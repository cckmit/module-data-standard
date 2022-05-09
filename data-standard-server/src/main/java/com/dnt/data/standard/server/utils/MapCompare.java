package com.dnt.data.standard.server.utils;

import java.util.Map;

public class MapCompare {
    public static Integer comparingById(Map m) {
        return Integer.parseInt(m.get("id")+"");
    }
}