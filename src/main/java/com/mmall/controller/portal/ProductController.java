package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerRespose;
import com.mmall.service.IproductService;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(("/product/"))
public class ProductController {

    @Autowired
    private IproductService iproductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerRespose<ProductDetailVo> detail(Integer productId) {
        return iproductService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerRespose<PageInfo> list(@RequestParam(value = "keyboard", required = false) String keyboard,
                                        @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                        @RequestParam(value = "odorBy", defaultValue = "") String odorBy) {
        return iproductService.getProductByKeywordCategory(keyboard, categoryId, pageNum, pageSize, odorBy);

    }
}
