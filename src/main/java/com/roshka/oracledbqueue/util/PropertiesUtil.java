package com.roshka.oracledbqueue.util;

import java.util.Properties;

public class PropertiesUtil {

    public static Integer getIntegerProperty(Properties props, String key, Integer defaultValue) {
        if (props.containsKey(key)) {
            try {
                return Integer.valueOf(props.getProperty(key));
            } catch (Throwable t) {
                // do nothing, just return default
            }
        }
        return defaultValue;
    }

    public static Integer getIntegerProperty(Properties props, String key) {
        return PropertiesUtil.getIntegerProperty(props, key, null);
    }

}
