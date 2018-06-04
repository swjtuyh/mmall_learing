package com.mmall.dao;

import com.mmall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByShippingIdUserID(@Param("userId")Integer userID,@Param("shippingId")Integer shippingId);

    int updateByShipping(Shipping shipping);

    Shipping selectByUserIdShippingId(@Param("userId")Integer userID,@Param("shippingId")Integer shippingId);

    List<Shipping> selectByUserId(@Param("userId")Integer userId);
}