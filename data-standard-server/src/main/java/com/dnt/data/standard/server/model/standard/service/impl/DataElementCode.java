package com.dnt.data.standard.server.model.standard.service.impl;

import lombok.Getter;

/**
 * @description: 数据元类型--数据元类型枚举值 <br>
 * @date: 2022/2/18 上午11:36 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public enum DataElementCode {
    //整数型
    INT(1,"整数型"),
    //浮点型
    FLOAT(2,"浮点型");

    @Getter
    private final int code;
    /**
     * 中文描述
     */
    @Getter
    private final String value;

    /**
     * 构 造 方 法
     */
    DataElementCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValue(int code) {
        for (DataElementCode c : DataElementCode.values()) {
            if (c.getCode() == code) {
                return c.value;
            }
        }
        return "";
    }


}
