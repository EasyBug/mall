package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerRespose;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDevimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @program: mmall
 * @description: 支付实现类
 * @author: BoWei
 * @create: 2018-04-11 13:55
 **/
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    //打印日志
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;


    /**
     * @Description: 创建订单
     * @Param: [userId, shippingId]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/20
     */
    public ServerRespose craeeteOrder(Integer userId, Integer shippingId) {

        //从购物车中获取数据
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        //计算订单总价
        ServerRespose serverRespose = this.getCartOrderItem(userId, cartList);
        if (!serverRespose.isSuccess()) {
            return serverRespose;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) serverRespose.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);
        if (order == null) {
            return ServerRespose.createByErrorMessage("生成订单失败");
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            return ServerRespose.createByErrorMessage("购物车为空");
        }
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        //mybaties 批量插入
        orderItemMapper.batchInsert(orderItemList);
        //生成成功，减少库存
        this.reduceProductStock(orderItemList);
        //清空购物车
        this.cleanCart(cartList);
        //返回数据
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return ServerRespose.createBySuccess(orderVo);
    }

    /**
     * @Description: 取消订单
     * @Param: [userId, orderNo]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/4/20
     */
    public ServerRespose<String> cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerRespose.createByErrorMessage("该用户此订单不存在");
        }
        //判断状态
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerRespose.createByErrorMessage("已付款，无法取消订单");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row > 0) {
            return ServerRespose.createBySuccess();
        }
        return ServerRespose.createByError();
    }

    /**
     * @Description: 获取勾选商品总价
     * @Param: [userId]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    public ServerRespose getOrderCartProduct(Integer userId) {
        OrderProductVo orderProductVo = new OrderProductVo();
        //从够购物车中获取数据
        List<Cart> cartList = cartMapper.selectCheckedByUserId(userId);
        //获取购物车中Item
        ServerRespose serverRespose = this.getCartOrderItem(userId, cartList);
        if (!serverRespose.isSuccess()) {
            return serverRespose;
        }
        //计算总价
        List<OrderItem> orderItemList = (List<OrderItem>) serverRespose.getData();

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDevimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerRespose.createBySuccess(orderProductVo);
    }

    /**
     * @Description: 获取订单详情接口
     * @Param: [userId, orderNo]
     * @return: com.mmall.common.ServerRespose<com.mmall.vo.OrderVo>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    public ServerRespose<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return ServerRespose.createBySuccess(orderVo);
        }
        return ServerRespose.createByErrorMessage("没有找到该订单");
    }

    /**
     * @Description: 个人中心查看订单List
     * @Param: [userId, pageNum, pageSize]
     * @return: com.mmall.common.ServerRespose<com.github.pagehelper.PageInfo>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    public ServerRespose<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
        //开始分页
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerRespose.createBySuccess(pageResult);
    }

    /**
     * @Description: ListOrder---->>>ListOrderVo
     * @Param: [orderList, userId]
     * @return: java.util.List<com.mmall.vo.OrderVo>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null) {
                //todo 管理员传时候不需要UserId
                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            } else {
                orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(), userId);
            }
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    /**
     * @Description: 返回前端数据
     * @Param: [order, orderItemList]
     * @return: com.mmall.vo.OrderVo
     * @Author: BoWei
     * @Date: 2018/4/19
     */
    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        //返回值包含订单信息以及订单明细信息还有收货地址信息
        OrderVo orderVo = new OrderVo();
        /*订单号*/
        orderVo.setOrderNo(order.getOrderNo());
        /*金额*/
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        //取到在线支付的文字描述
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeof(order.getPaymentType()).getValue());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeof(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    /**
     * @Description: 组装OrderItemVo
     * @Param: [orderItem]
     * @return: com.mmall.vo.OrderItemVo
     * @Author: BoWei
     * @Date: 2018/4/19
     */
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    /**
     * @Description: 组装Shipping
     * @Param: [shipping]
     * @return: com.mmall.vo.ShippingVo
     * @Author: BoWei
     * @Date: 2018/4/19
     */
    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        return shippingVo;
    }

    /**
     * @Description: 清空购物车
     * @Param: [cartList]
     * @return: void
     * @Author: BoWei
     * @Date: 2018/4/19
     */
    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * @Description: 减少库存
     * @Param: [orderItemList]
     * @return: void
     * @Author: BoWei
     * @Date: 2018/4/19
     */
    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * @Description: 生成订单
     * @Param: [userId, shippingId, payment]
     * @return: com.mmall.pojo.Order
     * @Author: BoWei
     * @Date: 2018/4/19
     */
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PayPlatformEnum.ALIPAY.getCode());
        //设置订单金额
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        //todo 发货时间等等
        //todo 付款时间
        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) {
            return order;
        }
        return null;
    }

    /**
     * @Description: 生成订单号
     * @Param: []
     * @return: long
     * @Author: BoWei
     * @Date: 2018/4/18
     */
    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    /**
     * @Description: 计算购物车总价
     * @Param: [orderItemList]
     * @return: java.math.BigDecimal
     * @Author: BoWei
     * @Date: 2018/4/18
     */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDevimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;

    }

    /**
     * @Description: 校验购物车商品情况毕竟算某商品总价
     * @Param: [userId, cartList]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/18
     */
    private ServerRespose getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerRespose.createByErrorMessage("购物车为空");
        }
        //校验购物车数据，包括产品状态和数量
        for (Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            //校验商品状态
            if (Const.ProductEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServerRespose.createByErrorMessage("商品" + product.getName() + "不是售卖状态");
            }
            //校验商品库存数量
            if (cartItem.getQuantity() > product.getStock()) {
                return ServerRespose.createByErrorMessage("商品" + product.getName() + "不是售卖状态");
            }
            //组装对象
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            //计算总价
            orderItem.setTotalPrice(BigDevimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return ServerRespose.createBySuccess(orderItemList);
    }

    /**
     * @Description: 支付方法
     * @Param: [orderNo, userId, path][订单号；用户ID；二维码路径]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/11
     */
    public ServerRespose pay(Long orderNo, Integer userId, String path) {

        Map<String, String> resultmap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if (order == null) {
            return ServerRespose.createByErrorMessage("用户没有该订单");
        }
        resultmap.put("orderNo", String.valueOf(order.getOrderNo()));

         /*以下代码来自于支付宝Demo的 Main.java */
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("mmall扫码支付订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单:").append(outTradeNo).append("购买商品共:").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";
        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");
        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";
        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        /*查询订单商品*/
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
        for (OrderItem orderItem : orderItemList) {
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDevimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(), orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                /*判断目录下是否存在，不存在就新建*/
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 需要修改为运行机器上的路径
                // 细节注意关于路径的反斜杠
                String qrPath = String.format(path + "/qr-%s.png", response.getOutTradeNo());//二维码的path
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常", e);
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultmap.put("qrUrl", qrUrl);
                return ServerRespose.createBySuccess(resultmap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerRespose.createByErrorMessage("支付宝预下单失败!!!");
            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerRespose.createByErrorMessage("系统异常，预下单状态未知!!!");
            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerRespose.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServerRespose aliCallback(Map<String, String> params) {
        //订单号
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        //支付宝交易号
        String tradeNo = params.get("trade_no");
        //交易状态
        String tradeStatus = params.get("trade_status");
        //查看订单号有效性
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return ServerRespose.createByErrorMessage("订单不存在");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerRespose.createBySuccess("支付宝重复调用");
        }
        //判断支付宝回调状态是否是成功
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            //订单付款时间
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_patment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        //支付方式
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        //支付宝交易号
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerRespose.createBySuccess();
    }

    /**
     * @Description: 查询订单状态
     * @Param: [userId, orgerNo]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/13
     */
    public ServerRespose queryOrderPayStatus(Integer userId, Long orgerNo) {

        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orgerNo);
        if (order == null) {
            return ServerRespose.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return ServerRespose.createBySuccess();
        }
        return ServerRespose.createByError();
    }


    //backend

    /**
     * @Description: 后台订单List页
     * @Param: [pageNum, pageSize]
     * @return: com.mmall.common.ServerRespose<com.github.pagehelper.PageInfo>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    public ServerRespose<PageInfo> manageList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
        PageInfo pageInfo = new PageInfo(orderVoList);
        pageInfo.setList(orderVoList);
        return ServerRespose.createBySuccess(pageInfo);
    }


    /**
     * @Description: 管理员查询订单详情
     * @Param: [orderNo]
     * @return: com.mmall.common.ServerRespose<com.mmall.vo.OrderVo>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    public ServerRespose<OrderVo> manageDetail(Long orderNo) {

        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return ServerRespose.createBySuccess(orderVo);
        }
        return ServerRespose.createByErrorMessage("订单不存在");
    }


    /**
     * @Description: 根据订单号查询订单详情，将来扩展为模糊查询
     * @Param: [orderNo, pageNum, pageSize]
     * @return: com.mmall.common.ServerRespose<com.github.pagehelper.PageInfo>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    public ServerRespose<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));
            pageResult.setList(Lists.newArrayList(orderVo));
            return ServerRespose.createBySuccess(pageResult);
        }
        return ServerRespose.createByErrorMessage("订单不存在");
    }


    /**
     * @Description: 订单发货
     * @Param: [orderNo]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/4/23
     */
    public ServerRespose<String> manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order != null) {
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()) {
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServerRespose.createBySuccess("发货成功");
            }
        }
        return ServerRespose.createByErrorMessage("订单不存在");
    }
}
