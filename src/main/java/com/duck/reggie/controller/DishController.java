package com.duck.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duck.reggie.common.R;
import com.duck.reggie.dto.DishDto;
import com.duck.reggie.entity.Category;
import com.duck.reggie.entity.Dish;
import com.duck.reggie.entity.DishFlavor;
import com.duck.reggie.service.CategoryService;
import com.duck.reggie.service.DishFlavorService;
import com.duck.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//菜品管理

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;


    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) { //接收Json參數
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    //菜品信息分頁查詢
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //構造分頁構造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //條件構造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //過濾條件
        queryWrapper.like(name != null, Dish::getName, name);
        //排序條件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //執行分頁查詢
        dishService.page(pageInfo, queryWrapper);

        //對象拷貝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分類id
            //根據id查詢分類對象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    //根據id查詢對應的菜品及口味
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    //修改
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) { //接收Json參數
        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    //刪除
    @DeleteMapping
    public R<String> delete(Long ids) {
        log.info("刪除菜品，菜品id為：{}", ids);
        dishService.deleteWithFlavor(ids);

        return R.success("刪除菜品成功");
    }

    //根據條件查詢對應的菜品數據
   /* @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        //構建查詢條件對象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(dish.getCategoryId() != null,
                        Dish::getCategoryId, dish.getCategoryId());
        //排序條件
        queryWrapper
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //構建查詢條件對象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(dish.getCategoryId() != null,
                        Dish::getCategoryId, dish.getCategoryId());
        //排序條件
        queryWrapper
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分類id
            //根據id查詢分類對象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //當前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL: select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }
}
