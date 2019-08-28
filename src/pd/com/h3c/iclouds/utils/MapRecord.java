package com.h3c.iclouds.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yKF7317 on 2017/1/9.
 */
public class MapRecord {

    private static Map<String, Map<String, Object>> fwMap = new HashMap<>();

    private static Map<String, Map<String, Object>> rtMap = new HashMap<>();

    private static Map<String, Map<String, Object>> ntMap = new HashMap<>();

    private static Map<String, Map<String, Object>> vpMap = new HashMap<>();

    public static Map<String, Map<String, Object>> getFwMap() {
        return fwMap;
    }

    public static void setFwMap(Map<String, Map<String, Object>> fwMap) {
        MapRecord.fwMap = fwMap;
    }

    public static Map<String, Map<String, Object>> getRtMap() {
        return rtMap;
    }

    public static void setRtMap(Map<String, Map<String, Object>> rtMap) {
        MapRecord.rtMap = rtMap;
    }

    public static Map<String, Map<String, Object>> getNtMap() {
        return ntMap;
    }

    public static void setNtMap(Map<String, Map<String, Object>> ntMap) {
        MapRecord.ntMap = ntMap;
    }

    public static Map<String, Map<String, Object>> getVpMap() {
        return vpMap;
    }

    public static void setVpMap(Map<String, Map<String, Object>> vpMap) {
        MapRecord.vpMap = vpMap;
    }

}
