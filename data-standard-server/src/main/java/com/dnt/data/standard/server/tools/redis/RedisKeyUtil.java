package com.dnt.data.standard.server.tools.redis;

/**
 * @description: RedisKeyUtil <br>
 * @date: 2021/7/21 上午11:44 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public
class RedisKeyUtil {

    public final static String PREFIX = "dnt";

    public static final String SEPARATOR = ":";

    public static String buildKey(String... args) {
        if (args == null) {
            return null;
        }

        int iMax = args.length - 1;
        if (iMax == -1) {
            return null;
        }

        StringBuilder b = new StringBuilder();
        b.append(PREFIX);
        b.append(SEPARATOR);

        for (int i = 0; ; i++) {
            b.append(String.valueOf(args[i]));
            if (i == iMax) {
                return b.toString();
            }
            b.append(SEPARATOR);
        }
    }
}
