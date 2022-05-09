package com.dnt.data.standard.server.model.mould.service.impl;

import lombok.Getter;

/**
 * @description: 模型类型--模型状态枚举值 <br>
 * @date: 2022/2/18 上午11:36 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public enum MouldTypeCode {
    /**手工建模**/
    MANUAL(1,"手工建模"),
    /**DDL建模**/
    DDL(2,"DDL建模");

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
    MouldTypeCode(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValue(int code) {
        for (MouldTypeCode c : MouldTypeCode.values()) {
            if (c.getCode() == code) {
                return c.value;
            }
        }
        return "";
    }


}
