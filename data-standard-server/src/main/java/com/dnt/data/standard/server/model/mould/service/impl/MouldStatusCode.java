package com.dnt.data.standard.server.model.mould.service.impl;

import lombok.Getter;

/**
 * @description: 模型状态--模型状态枚举值 <br>
 * @date: 2022/2/18 上午11:36 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public enum MouldStatusCode {
    /**未发布**/
    UNRELEASE(0,"未发布"),
    /**发布**/
    RELEASE(1,"已发布");

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
    MouldStatusCode(int code, String value) {
        this.code = code;
        this.value = value;
    }
    public static String getValue(int code) {
        for (MouldStatusCode c : MouldStatusCode.values()) {
            if (c.getCode() == code) {
                return c.value;
            }
        }
        return "";
    }


}
