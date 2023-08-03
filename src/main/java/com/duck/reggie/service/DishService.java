package com.duck.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duck.reggie.dto.DishDto;
import com.duck.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同時插入菜品對應的口味數據
    //需要操作兩張表 ： dish / dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根據id查詢
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品，同時更新對應的口味
    public void updateWithFlavor(DishDto dishDto);

    //根據id刪除菜品
    public void deleteWithFlavor(Long ids);
}
