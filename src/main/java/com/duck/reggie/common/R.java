package com.duck.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
* 通用返回結果
* 服務端響應的數據最終都會封裝成此對象
* */

@Data
public class R<T>  implements Serializable {

    private Integer code; //編碼：1成功，0和其他數字為失敗

    private String msg; //錯誤信息

    private T data; //數據

    private Map map = new HashMap(); //動態數據

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
