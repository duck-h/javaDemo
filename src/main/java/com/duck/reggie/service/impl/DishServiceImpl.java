package com.duck.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duck.reggie.common.R;
import com.duck.reggie.dto.DishDto;
import com.duck.reggie.entity.Dish;
import com.duck.reggie.entity.DishFlavor;
import com.duck.reggie.mapper.DishMapper;
import com.duck.reggie.service.DishFlavorService;
import com.duck.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品，同時保存對應的口味數據
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表 dish
        this.save(dishDto);

        Long dishId = dishDto.getId(); //菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味數據到口味表 dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    //根據id查詢菜品及口味
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查詢口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);

        //清理當前菜品對應口味數據 - dish_flavor 操作 delete()
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加當前提交的口味數據 - dish_flavor 操作 insert()
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void deleteWithFlavor(Long ids) {
        //刪除dish表
        this.removeById(ids);

        //根據菜品id刪除dish_flavor表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper);
    }
}
