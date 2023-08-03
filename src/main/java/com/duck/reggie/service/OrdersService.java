package com.duck.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duck.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {

    //用戶下單
    public void submit(Orders orders);
}
