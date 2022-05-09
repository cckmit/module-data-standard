package com.dnt.data.standard.server.web;

import com.baomidou.mybatisplus.extension.api.R;

/**
 * 响应结果生成工具
 */
public class Result {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final String DEFAULT_FAIL_MESSAGE = "FAIL";

    public static R ok() {
        return new R()
                .setCode(ResultCode.SUCCESS.code())
                .setMsg(DEFAULT_SUCCESS_MESSAGE)
                .setData("操作成功");
    }

    public static <T> R<T> ok(T data) {
        return new R()
                .setCode(ResultCode.SUCCESS.code())
                .setMsg(DEFAULT_SUCCESS_MESSAGE)
                .setData(data);
    }

    public static R fail() {
        return new R()
                .setCode(ResultCode.FAIL.code())
                .setMsg(DEFAULT_FAIL_MESSAGE)
                .setData("操作失败");
    }
    public static R fail(String message) {
        return new R()
                .setCode(ResultCode.FAIL.code())
                .setMsg(DEFAULT_FAIL_MESSAGE)
                .setData(message);
    }
}
