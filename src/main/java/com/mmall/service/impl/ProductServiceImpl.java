package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinhuan on 2018/4/22.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;



    public ServerResponse saveOrUpdateProduct(Product product){
        if (product != null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                product.setMainImage(product.getSubImages().split(",")[0]);
            }
            if (product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if (rowCount > 0){
                    return ServerResponse.createBySuccessMessgae("更新商品信息成功");
                }else {
                    return ServerResponse.createByErrorMessgae("更新商品信息失败");
                }
            }else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0){
                    return ServerResponse.createBySuccessMessgae("添加商品信息成功");
                }else {
                    return ServerResponse.createByErrorMessgae("添加商品信息失败");
                }
            }
        }else {
            return ServerResponse.createByErrorMessgae("新增或更新产品参数错误");
        }
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if (productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0){
            return ServerResponse.createBySuccessMessgae("更新销售状态信息成功");
        }
        return ServerResponse.createByErrorMessgae("更新销售状态信息失败");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorMessgae("参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessgae("商品已下架");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setName(product.getName());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());

        productDetailVo.setImageHosts(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null){
            productDetailVo.setParentCategroyId(0);
        }else {
            productDetailVo.setParentCategroyId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    public ServerResponse<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        //pageHelper使用说明
        //1.调用startPage--start
        //2.填充自己的sql查询逻辑
        //3.pageHelper--end
        PageHelper.startPage(pageNum,pageSize);
        List<ProductListVo> productListVo = Lists.newArrayList();
        List<Product> productList = productMapper.selectProductList();
        for (Product productItem :
                productList) {
            productListVo.add(assembleProductListVo(productItem));
        }
        PageInfo pageResult = new PageInfo(productListVo);
        pageResult.setList(productListVo);
        return ServerResponse.createBySuccess(pageResult);
    }
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHosts(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<ProductListVo> productListVo = Lists.newArrayList();
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        for (Product productItem :
                productList) {
            productListVo.add(assembleProductListVo(productItem));
        }
        PageInfo pageResult = new PageInfo(productListVo);
        pageResult.setList(productListVo);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorMessgae("参数错误");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessgae("商品已下架");
        }
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessgae("商品已下架或已删除");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> searchProductByKeywordCategory(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderBy){
        if (StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<>();
        if (categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)){
                //返回一个空的结果集，不报错,填写分页结构
                //pageHelper-->start
                PageHelper.startPage(pageNum,pageSize);
                //填写查询结果
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.getChildrenDeepCategory(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " +  orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem:
                productList) {
            productListVoList.add(assembleProductListVo(productItem));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
