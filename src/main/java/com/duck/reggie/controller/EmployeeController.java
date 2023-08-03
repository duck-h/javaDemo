package com.duck.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.duck.reggie.common.R;
import com.duck.reggie.entity.Employee;
import com.duck.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    //員工登入
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) { //接收json值
        //1.將頁面提交的密碼password進行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根據頁面提交的用戶名username查詢資料庫
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果沒有查詢到則返回登入失敗結果
        if (emp == null) {
            return R.error("登入失敗");
        }

        //4.密碼比對 不一致則返回登入失敗結果
        if (!emp.getPassword().equals(password)) {
            return R.error("登入失敗");
        }

        //5.查看員工status 如果為停用狀態 則返回員工已被停用
        if (emp.getStatus() == 0) {
            return R.error("該帳號已被停用");
        }

        //6.登入成功 將員工id存入Session並返回登入成功結果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    //登出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {

        //清除Session
        request.getSession().removeAttribute("employee");
        return R.success("已登出");

    }

    //新增員工
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) { //接收過來的資料是JSON格式 ->requestBody
        log.info("新增員工，員工信息: {}", employee.toString());

        //設置初始密碼123456, 並進行md5加密處理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //獲得當前登入用戶的id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增員工成功");

    }

    //員工信息分頁查詢
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);

        //分頁構造器
        Page<Employee> pageInfo = new Page<Employee>(page, pageSize);
        //條件構造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //增加過濾條件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //排序條件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //執行查詢
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    //修改
    //根據員工id修改員工資料
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());

        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("員工資料修改成功");
    }

    //根據id查詢員工資料
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根據id查詢員工資料...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        } else {
            return R.error("查無資料");
        }
    }


}
