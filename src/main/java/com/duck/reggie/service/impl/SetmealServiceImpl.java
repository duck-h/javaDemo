package com.duck.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duck.reggie.common.CustomException;
import com.duck.reggie.dto.SetmealDto;
import com.duck.reggie.entity.Setmeal;
import com.duck.reggie.entity.SetmealDish;
import com.duck.reggie.mapper.SetmealMapper;
import com.duck.reggie.service.SetmealDishService;
import com.duck.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    //新增套餐，同時保存套餐和菜品的關聯關係
    @Transactional //加入事務 確保一致性
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息 操作setmeal表 執行insert
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的關聯 操作setmeal_dish表 執行insert
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    //刪除套餐，同時刪除套餐和菜品的關聯數據
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1;
        //查詢套餐狀態，確定是否可以刪除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids).eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0) {
            //包含販售中，無法刪除
            //如果不能刪除，拋出一個業務異常
            throw new CustomException("套餐正在販售中，無法刪除");
        }
        //可以刪除，先刪除套餐表中的數據 setmeal
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);

        //刪除關係表中的數據 setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);

    }
}
