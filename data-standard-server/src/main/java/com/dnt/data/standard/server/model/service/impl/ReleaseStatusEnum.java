package com.dnt.data.standard.server.model.service.impl;

import lombok.Getter;

/**
 * @description: 数据元发布状态--枚举值 <br>
 * @date: 2022/4/25 上午11:36 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public enum ReleaseStatusEnum {
    //未发布
    UNRELEASE(0,"未发布"),
    //已发布
    RELEASE(1,"已发布"),
    //已更新
    RELEASEUPDATE(2,"已更新");

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
    ReleaseStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static String getValue(int code) {
        for (ReleaseStatusEnum c : ReleaseStatusEnum.values()) {
            if (c.getCode() == code) {
                return c.value;
            }
        }
        return "";
    }


}
