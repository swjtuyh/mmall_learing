package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by yinhuan on 2018/4/22.
 */
public class DateTimeUtil {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * string to date
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr,String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }
    /**
     * date to string
     * @param date
     * @return
     */
    public static String dateToStr(Date date,String formatStr){
        if (date == null){
            return StringUtils.EMPTY;
        }else{
            DateTime dateTime = new DateTime(date);
            return dateTime.toString(formatStr);
        }
    }

    /**
     * string to date
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DEFAULT_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * date to string
     * @param date
     * @return
     */
    public static String dateToStr(Date date){
        if (date == null){
            return StringUtils.EMPTY;
        }else{
            DateTime dateTime = new DateTime(date);
            return dateTime.toString(DEFAULT_FORMAT);
        }
    }
}
