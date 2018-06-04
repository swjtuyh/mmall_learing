package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * Created by yinhuan on 2018/5/3.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount == 0){
            return ServerResponse.createByErrorMessgae("新建地址失败");
        }
        Map result = Maps.newHashMap();
        result.put("shippingId",shipping.getId());
        return ServerResponse.createBySuccess("新建地址成功",result);
    }

    public ServerResponse<String> delete(Integer userId, Integer shippingId){
        int rowCount = shippingMapper.deleteByShippingIdUserID(userId,shippingId);
        if (rowCount == 0){
            return ServerResponse.createByErrorMessgae("删除地址失败");
        }
        return ServerResponse.createBySuccessMessgae("删除地址成功");
    }

    public ServerResponse<String> update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount == 0){
            return ServerResponse.createByErrorMessgae("更新地址失败");
        }
        return ServerResponse.createBySuccessMessgae("更新地址成功");
    }

    public ServerResponse<Shipping> search(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByUserIdShippingId(userId,shippingId);
        if (shipping == null){
            return ServerResponse.createByErrorMessgae("获取地址失败");
        }
        return ServerResponse.createBySuccess("获取地址成功",shipping);
    }

    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }








}
