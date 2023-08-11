package com.duck.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.duck.reggie.common.R;
import com.duck.reggie.entity.User;
import com.duck.reggie.service.UserService;
import com.duck.reggie.utils.ValidateCodeUtils;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    //發送手機驗證碼簡訊
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {//json格式資料
        //獲取手機號碼
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成隨機四位數字
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("cod=:{}", code);

            //調用短信服務api完成發送
            //SMSUtils.sendMessage("","",phone,code);

            //將生成的驗證碼保存到Session
            //session.setAttribute(phone, code);

            //TODO:改造 將生成的驗證碼緩存到redis中，並設置有效時間五分鐘
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);

            return R.success("手機驗證碼發送成功");
        }
        return R.error("簡訊發送失敗");
    }


    //移動端用戶登入
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //獲取手機號碼
        String phone = map.get("phone").toString();
        //獲取驗證碼
        String code = map.get("code").toString();
        //從session中獲取保存的驗證碼
        //Object codeInSession = session.getAttribute(phone);

        //TODO:改造 從redis中獲取緩存的驗證碼
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //驗證碼比對（session / 使用者提交）
        if (codeInSession != null && codeInSession.equals(code)) {
            //比對成功 -> 登入

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //判斷當前用戶是否為新用戶，新用戶 -> 自動完成註冊
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);

                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            //TODO 改造 用戶刪除成功，刪除redis中緩存的驗證碼
            redisTemplate.delete(phone);
            return R.success(user);

        }
        return R.error("登入失敗");
    }

}
