package com.dnt.data.standard.server.web;

/**
 * 响应码枚举，参考HTTP状态码的语义
 */
public enum ResultCode {
    //成功
    SUCCESS(200),
    //失败
    FAIL(400),
    //未认证（签名错误）
    UNAUTHORIZED(401),
    //接口不存在
    NOT_FOUND(404),
    //服务器内部错误
    INTERNAL_SERVER_ERROR(500);

    private final long code;

    ResultCode(long code) {
        this.code = code;
    }

    public long code() {
        return code;
    }
}
