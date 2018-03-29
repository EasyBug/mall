package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @program: mmall
 * @description: 处理时间类
 * @author: BoWei
 * @create: 2018-03-23 09:51
 * 主要用到joda-time;完成两件事str->date和date->str
 **/
public class DateTimeUtil {

    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * @Description: 时间字符串转换为时间格式
     * @Param: [dateTimeStr, formtStr]
     * @return: java.util.Date
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    public static Date strToDate(String dateTimeStr, String formtStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formtStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * @Description: 将时间格式转换为字符串格式
     * @Param: [date, formatStr]
     * @return: java.lang.String
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    public static String dateToStr(Date date, String formatStr) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }

    /**
     * @Description:重载函数
     * @Param: [dateTimeStr]
     * @return: java.util.Date
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    public static Date strToDate(String dateTimeStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * @Description: 重载函数
     * @Param: [date]
     * @return: java.lang.String
     * @Author: BoWei
     * @Date: 2018/3/23
     */
    public static String dateToStr(Date date) {
        if (date == null) {
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
