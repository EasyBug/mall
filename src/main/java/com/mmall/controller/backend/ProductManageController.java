package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerRespose;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IUserService;
import com.mmall.service.IproductService;
import com.mmall.util.PropertiesUtil;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IproductService iproductService;
    @Autowired
    private IFileService iFileService;

    /**
     * @Description: 保存商品
     * @Param: [session, product]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerRespose productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户尚未登陆,请登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iproductService.saveOrUpdateProduct(product);
        } else {
            return ServerRespose.createByErrorMessage("该用户无权限");
        }
    }

    /**
     * @Description: 商品状态
     * @Param: [session, productId, status]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerRespose setSaleStatus(HttpSession session, Integer productId, Integer status) {

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户尚未登陆,需要登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iproductService.setSaleStatus(productId, status);
        } else {
            return ServerRespose.createByErrorMessage("该用户无权限");
        }
    }

    /**
     * @Description: 商品详情
     * @Param: [session, prodectId]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    @RequestMapping("get_detail.do")
    @ResponseBody
    public ServerRespose getDetail(HttpSession session, Integer prodectId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户尚未登陆,请登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iproductService.manageProductDetail(prodectId);
        } else {
            return ServerRespose.createByErrorMessage("该用户无权限");
        }
    }


    /**
     * @Description: 商品列表
     * @Param: [session, pageNumber, pageSize]
     * @return: com.mmall.common.ServerRespose
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerRespose getList(HttpSession session, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iproductService.getProductList(pageNumber, pageSize);
        } else {
            return ServerRespose.createByErrorMessage("该用户无权限");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerRespose productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iproductService.searchProduct(productName, productId, pageNumber, pageSize);
        } else {
            return ServerRespose.createByErrorMessage("该用户无权限");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerRespose upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerRespose.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            /*封装结果*/
            Map filemap = Maps.newHashMap();
            filemap.put("uri", targetFileName);
            filemap.put("url", url);
            return ServerRespose.createBySuccess(filemap);
        } else {
            return ServerRespose.createByErrorMessage("该用户无权限");
        }
    }

    @RequestMapping("rich_text_img_Upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            /*获取Tomcat服务器目录下的upload文件夹*/
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            /*修改返回的header*/
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权操作");
            return resultMap;
        }
    }
}
