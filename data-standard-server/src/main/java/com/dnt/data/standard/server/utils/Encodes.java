package com.dnt.data.standard.server.utils;


import com.dnt.data.standard.server.web.ServiceException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * <pre>
 * 封装各种格式的编码解码工具类.
 * 	1.Commons-codec的 hex/base64 编码
 * 	2.自制的base62 编码
 * 	3.Commons-lang的xml/html escape
 * 	4.JDK提供的URLEncoder
 * </pre>
 */
public class Encodes {

    /**
     * base
     */
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final String CHARSET_UTF_8="UTF-8";


    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String part) {
        try {
            return URLEncoder.encode(part, CHARSET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Encodes-->urlEncode:URL 编码异常");
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String urlDecode(String part) {

        try {
            return URLDecoder.decode(part, CHARSET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Encodes-->urlDecode: URL 解码异常");
        }
    }
}
