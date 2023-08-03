package com.duck.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duck.reggie.common.R;
import com.duck.reggie.entity.Category;
import com.duck.reggie.entity.Dish;
import com.duck.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//分類管理
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    //新增分類
    @PostMapping
    public R<String> add(@RequestBody Category category) { //JSON格式資料
        log.info("category:{}", category);
        categoryService.save(category);
        return R.success("分類新增成功");
    }

    //分頁查詢
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //分頁構造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //條件構造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //增加排序條件 根據sort
        queryWrapper.orderByAsc(Category::getSort);

        //分頁查詢
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }

    //根據id刪除分類
    @DeleteMapping
    public R<String> delete(Long id) {
        log.info("刪除分類，id為: {}", id);
        //categoryService.removeById(id);
        categoryService.remove(id);
        return R.success("分類資料刪除成功");
    }

    //根據id修改分類
    @PutMapping
    public R<String> update(@RequestBody Category category) { //Json格式
        log.info("修改分類信息:{}", category);
        categoryService.updateById(category);
        return R.success("分類修改成功");
    }


    //根據條件查詢分類數據
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //條件構造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //條件
        queryWrapper.
                eq(category.getType() != null, Category::getType, category.getType());
        //條件，查詢狀態為1(販售中)
//        queryWrapper.eq(Dish::getStatus,1);
        //排序條件
        queryWrapper.orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }


}
