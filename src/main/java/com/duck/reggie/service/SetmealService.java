package com.duck.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duck.reggie.dto.SetmealDto;
import com.duck.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐，同時保存套餐和菜品的關聯關係
    public void saveWithDish(SetmealDto setmealDto);

    //刪除套餐，同時刪除套餐和菜品的關聯數據
    public void removeWithDish(List<Long> ids);
}
