package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.vo.OrderVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by yinhuan on 2018/5/5.
 */
public interface IOrderService {

    ServerResponse pay(Integer userId, Long orderNo, String path);

    ServerResponse<Boolean> aliCallback(Map<String,String> params);

    ServerResponse queryOrderPayStatus(Integer userId,Long orderNo);

    ServerResponse create(Integer userId,Integer shippingId);

    ServerResponse  cancel(Integer userId,Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId,Integer pageSize, Integer pageNum);

    ServerResponse<PageInfo> manageList(int pageNum,int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo,Integer pageNum,Integer pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
