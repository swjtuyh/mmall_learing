package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
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

/**
 * Created by yinhuan on 2018/4/22.
 */

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping(value = "product_save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            if (iUserService.checkAdminRole(user).isSuccess()){
                return iProductService.saveOrUpdateProduct(product);
            }else{
                return  ServerResponse.createByErrorMessgae("无权限操作,请用管理员账户登录");
            }
        }else {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    @RequestMapping(value = "set_sale_status.do")
    @ResponseBody
    public ServerResponse<String> setSaleStatus(HttpSession session,Integer productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            if (iUserService.checkAdminRole(user).isSuccess()){
                return iProductService.setSaleStatus(productId,status);
            }else{
                return ServerResponse.createByErrorMessgae("无操作权限，请用管理员账户登录");
            }
        }else {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    @RequestMapping(value = "get_detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            if (iUserService.checkAdminRole(user).isSuccess()){
                //填充业务逻辑
                return iProductService.manageProductDetail(productId);
            }else{
                return ServerResponse.createByErrorMessgae("无操作权限，请用管理员账户登录");
            }
        }else {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    @RequestMapping(value = "get_list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            if (iUserService.checkAdminRole(user).isSuccess()){
                //填充业务逻辑
                return iProductService.getProductList(pageNum,pageSize);
            }else{
                return ServerResponse.createByErrorMessgae("无操作权限，请用管理员账户登录");
            }
        }else {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    @RequestMapping(value = "search_product.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session, String productName,Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            if (iUserService.checkAdminRole(user).isSuccess()){
                //填充业务逻辑
                return iProductService.searchProduct(productName,productId,pageNum,pageSize);
            }else{
                return ServerResponse.createByErrorMessgae("无操作权限，请用管理员账户登录");
            }
        }else {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    @RequestMapping(value = "upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            if (iUserService.checkAdminRole(user).isSuccess()){
                //填充业务逻辑
                String path = request.getSession().getServletContext().getRealPath("upload");
                String targetFileName = iFileService.upload(file,path);
                String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
                Map fileMap = Maps.newHashMap();
                fileMap.put("uri",targetFileName);
                fileMap.put("url",url);
                return ServerResponse.createBySuccess(fileMap);
            }else{
                return ServerResponse.createByErrorMessgae("无操作权限，请用管理员账户登录");
            }
        }else {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    @RequestMapping(value = "richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null){
            if (iUserService.checkAdminRole(user).isSuccess()){
                String path = request.getSession().getServletContext().getRealPath("upload");
                String targetFileName = iFileService.upload(file,path);
                if (StringUtils.isBlank(targetFileName)){
                    resultMap.put("success","false");
                    resultMap.put("msg","上传失败");
                    return resultMap;
                }
                String url = PropertiesUtil.getProperty("http://img.happymmall.com/") + targetFileName;
                resultMap.put("success","true");
                resultMap.put("msg","上传成功");
                resultMap.put("file_path",url);
                response.setHeader("Access-Control-Allow-Headers","X-File-Name");
                return resultMap;
            }else{
                resultMap.put("success","false");
                resultMap.put("msg","无操作权限");
                return resultMap;
            }
        }else {
            resultMap.put("success","false");
            resultMap.put("msg","用户未登陆，请用管理员账户登录");
            return resultMap;
        }
    }







}
