package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerRespose;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.PayInfo;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @program: mmall
 * @description: 地址实现接口
 * @author: BoWei
 * @create: 2018-04-08 13:12
 **/
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {


    @Autowired
    private ShippingMapper shippingMapper;


    /**
     * @Description: 新增地址
     * @Param: [userId, shipping]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/8
     */
    public ServerRespose add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        /*useGeneratedKeys="true" keyProperty="id" 在XML中配置这两个可以拿到插入后的相应字段*/
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0) {
            Map result = Maps.newHashMap();
            result.put("shippingId", shipping.getId());
            return ServerRespose.createBySuccess("创建地址成功", result);
        }
        return ServerRespose.createByErrorMessage("创建地址失败");
    }

    /**
     * @Description: 删除商品(注意横向越权问题)
     * @Param: [userId, shippingId]
     * @return: com.mmall.common.ServerRespose<java.lang.String>
     * @Author: BoWei
     * @Date: 2018/4/8
     */
    public ServerRespose<String> del(Integer userId, Integer shippingId) {
       /* int resultCount = shippingMapper.deleteByPrimaryKey(shippingId);
       * 横向越权会发生，因为没有将地址和UserID关联
       * */
        int resultCount = shippingMapper.deleteByShippingIdUserId(userId, shippingId);
        if (resultCount > 0) {
            return ServerRespose.createBySuccessMessage("删除地址成功");
        }
        return ServerRespose.createByErrorMessage("删除地址失败");
    }

    /**
     * @Description: 更新信息（注意横向越权问题）
     * @Param: [userId, shippingId]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/4/8
     */
    public ServerRespose update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0) {
            return ServerRespose.createBySuccess("更新成功");
        }
        return ServerRespose.createByErrorMessage("创建失败");
    }

    /**
     * @Description: 查询相应的地址信息
     * @Param: [userId, shippingId]
     * @return: com.mmall.common.ServerRespose<com.mmall.pojo.Shipping>
     * @Author: BoWei
     * @Date: 2018/4/8
     */
    public ServerRespose<Shipping> select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
        if (shipping == null) {
            return ServerRespose.createByErrorMessage("没有查到该地址");
        }
        return ServerRespose.createBySuccess("已经查到该地址", shipping);
    }

    /**
     * @Description: 分页查询
     * @Param: [userId, pageNum, pageSize]
     * @return: com.mmall.common.ServerRespose<com.github.pagehelper.PageInfo>
     * @Author: BoWei
     * @Date: 2018/4/8
     */
    public ServerRespose<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerRespose.createBySuccess(pageInfo);
    }

}
