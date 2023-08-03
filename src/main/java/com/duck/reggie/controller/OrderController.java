package com.duck.reggie.controller;

import com.duck.reggie.common.R;
import com.duck.reggie.entity.Orders;
import com.duck.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrdersService ordersService;

    //用戶下單
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("訂單數據 : {}", orders);
        ordersService.submit(orders);
        return R.success("下單成功");
    }

}
