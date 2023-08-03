package com.duck.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duck.reggie.common.CustomException;
import com.duck.reggie.entity.Category;
import com.duck.reggie.entity.Dish;
import com.duck.reggie.entity.Setmeal;
import com.duck.reggie.mapper.CategoryMapper;
import com.duck.reggie.service.CategoryService;
import com.duck.reggie.service.DishService;
import com.duck.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    //根據id刪除分類，刪除前 進行判斷
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //增加查詢條件 根據分類id進行查詢
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查詢當前分類是否關聯菜品，如果有關聯則拋出業務異常
        if (count1 > 0) {
            //關聯菜品
            throw new CustomException("當前分類已連動菜品，無法刪除");
        }

        //查詢當前分類是否關聯套餐，如果有關聯則拋出業務異常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if (count2 > 0) {
            //關聯套餐
            throw new CustomException("當前分類已連動套餐，無法刪除");
        }

        //正常刪除分類
        super.removeById(id);

    }
}
