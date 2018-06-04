package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yinhuan on 2018/4/23.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("get_detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        return iProductService.getProductDetail(productId);
    }
    @RequestMapping("search_product.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchProduct(@RequestParam(value = "keyword",required = false) String keyword,
                                                  @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                                  @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                                                  @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.searchProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
    }


}
