package org.xxpay.common.util;

import org.apache.commons.beanutils.Converter;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by admin on 2016/5/10.
 */
public class DateTimeConverter implements Converter {

    private static final String DATE = "yyyy-MM-dd";
    private static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final String TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";

    @Override
    public Object convert(Class type, Object value) {
        return toDate(type, value);
    }

    private static Object toDate(Class type, Object value) {
        if (value == null || "".equals(value))
            return null;
        if (value instanceof String) {
            String dateValue = value.toString().trim();
            int length = dateValue.length();
            if (type.equals(Date.class)) {
                try {
                    String pattern;
                    if (length <= 10) {
                        pattern = DATE;
                    } else if (length <= 19) {
                        pattern = DATETIME;
                    } else {
                        pattern = TIMESTAMP;
                    }
                    DateFormat formatter = new SimpleDateFormat(pattern, new DateFormatSymbols(Locale.CHINA));
                    return formatter.parse(dateValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
