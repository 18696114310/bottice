package com.ecofresh.bottice.order.service;

import org.springframework.data.domain.Page;

import com.ecofresh.bottice.order.entities.Order;
import com.ecofresh.bottice.order.form.OrderForm;

public interface OrderService
{
	/***
     * 根据用户分页查询订单信息
     * @param id
     * @return
     */
	public Page<Order> find(int page, int rows, String userId,String startDateTime,String endDateTime,String orderType);
    
    /***
     * 增加积分信息
     * @param dto
     * @return
     */
    public boolean insert(OrderForm dto,String storeid,String userId);
}
