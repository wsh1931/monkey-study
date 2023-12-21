package com.monkey.monkeyUtils.util;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wusihao
 * @create 2023-03-07 15:06
 */
@Slf4j
public class DateSelfUtils {
    /**
     * 得到两个日期之间的所有日期
     *
     * @param
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/18 19:55
     */
    public static List<Date> getBeenTwoDayAllDate(Date start, Date end) {
        // 示例 - 创建开始和结束日期
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        // 获取日期间所有日期的列表
        List<Date> dates = getDatesBetween(startCal.getTime(), endCal.getTime());
        return dates;
    }

    public static List<Date> getDatesBetween(Date start, Date end) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        while (calendar.getTime().before(end) || calendar.getTime().equals(end)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        return dates;
    }
    /**
     * 将当前格式的字符串转化为指定格式的日期
     *
     * @param strDate 日期字符串
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/13 16:43
     */

    public static Date stringToDate(String strDate, String pattern) {
        if (strDate == null || strDate.trim().isEmpty()) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(strDate, formatter);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    /**
     * 判断当前时间是否在指定时间之后
     * true 为当前时间在指定时间之后
     * false 当前时间在指定时间之前
     * @param specifiedTime 指定时间
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/18 15:00
     */
    public static boolean judgeNowTimeAndAssignment(Date specifiedTime) {
        if (specifiedTime == null) {
            return false;
        }
        Date currentDateTime = new Date();
        return currentDateTime.after(specifiedTime);
    }
    /**
     * 根据秒数得到 (时:分:秒的形式) 00:00:00
     *
     * @param seconds 传入的秒数
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/18 11:32
     */
    public static String getSpecialFormatBySeconds(Integer seconds) {
        String second = String.valueOf(seconds % 60) ;
        String hour = String.valueOf(seconds / 3600);
        seconds /= 3600;
        String minute = String.valueOf(seconds / 60);
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        if (second.length() == 1) {
            second = "0" + second;
        }
        if ("0".equals(hour)) {
            return minute + ":" + second;
        }
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        return hour + ":" + minute + ":" + second;
    }

    /**
     * 判断目前登录时间与指定的日期是否是同一天
     *
     * @param specifiedDate 指定的日期，格式为xxxx-xx-xx
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/17 10:50
     */
    public static boolean isSameDayAsSpecifiedDate(String specifiedDate) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 将指定的周期时间字符串解析为LocalDate对象
        LocalDate parsedDate = LocalDate.parse(specifiedDate, DateTimeFormatter.ISO_DATE);

        // 判断是否是同一天
        boolean isSameDay = currentDate.isEqual(parsedDate);

        return isSameDay;
    }
    /**
     * 获取指定日期的开始日期（即当天的 00:00:00）
     *
     * @param date 指定日期
     * @return 开始日期
     */
    public static Calendar getStartDate(Date date) {
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        return start;
    }

    /**
     * 获取指定日期的结束日期（即当天的 23:59:59）
     *
     * @param date 指定日期
     * @return 结束日期
     */
    public static Calendar getEndDate(Date date) {
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        return end;
    }


    /**
     * 获取两个日期之间所有日期
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> getMonthBetweenMonths(Date startDate, Date endDate) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        //设置开始和结束月份
        start.setTime(startDate);
        end.setTime(endDate);

        List<Date> months = getMonthsBetween(start, end);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        return months;

    }

    public static List<Date> getMonthsBetween(Calendar start, Calendar end) {
        List<Date> results = new ArrayList<>();
        Calendar temp = (Calendar) start.clone();
        temp.set(Calendar.DAY_OF_MONTH, 1);
        while (temp.before(end)) {
            results.add(temp.getTime());
            temp.add(Calendar.MONTH, 1);
        }
        results.add(end.getTime());
        return results;
    }


    /**
     * 获取两个日期之间的所有日期
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<LocalDate> getDatesBetweenUsingJava8(LocalDate startDate, LocalDate endDate) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
    }


    /**
     * 获取 获取某年某月 所有日期（yyyy-mm-dd格式字符串）
     *
     * @param month
     * @return
     */
    public static List<Date> getMonthFullDay(Date month) {
        List<Date> fullDayList = new ArrayList<>(32);
        // 获得当前日期对象
        Calendar cal = Calendar.getInstance();
        cal.clear();// 清除信息

        Date startDate = changeLocalDateToDate(getFirstDayOfMonth(month));
        // 结束日期为当前年拼接12月份
        Date endDate = changeLocalDateToDate(getLastDayOfMonth(month));
        // 设置calendar的开始日期
        cal.setTime(startDate);
        // 当前时间小于等于设定的结束时间
        while (cal.getTime().compareTo(endDate) <= 0) {
            fullDayList.add(cal.getTime());
            // 当前天数
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return fullDayList;
    }


    /**
     * 获取指定年份1月到12月的日期
     *
     * @param yearDate
     * @return
     */
    public static List<Date> getMonthByYear(String yearDate) {
        List<Date> data = new ArrayList<>();
        try {
            Calendar c = Calendar.getInstance();
            // 获取当前的年份
//            int year = c.get(Calendar.YEAR);
            // 定义时间格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            // 开始日期为当前年拼接1月份
            Date startDate = sdf.parse(yearDate + "-01");
            // 结束日期为当前年拼接12月份
            Date endDate = sdf.parse(yearDate + "-12");
            // 设置calendar的开始日期
            c.setTime(startDate);
            // 当前时间小于等于设定的结束时间
            while (c.getTime().compareTo(endDate) <= 0) {
                data.add(c.getTime());
                // 当前月份加1
                c.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 得到两个日期之间的分钟数

     * @return {@link null}
     * @author wusihao
     * @date 2023/8/21 17:47
     */
    public static int getMinutesBetweenDates(Date date1, Date date2) {
        long diffInMilliseconds = date2.getTime() - date1.getTime();
        int diffInMinutes = (int)(diffInMilliseconds / (60 * 1000));

        return diffInMinutes;
    }

    /**
     * 得到两个日期之间的秒数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/21 17:55
     */
    public static int getSecondsBetweenDates(Date date1, Date date2) {
        long diffInMilliseconds = date2.getTime() - date1.getTime();
        int diffInSeconds = (int)(diffInMilliseconds / 1000);

        return diffInSeconds;
    }


    /**
     * 获取两个日期相隔天数/小时数/分钟数
     *
     * @return
     * @throws ParseException
     */
    public static TimeVo getBetweenTime(String s1, String s2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String s1 = "2023-03-02 07:54:11";
//        String s2 = "2023-03-02 08:54:11";
        Date date1 = format.parse(s1);
        Date date2 = format.parse(s2);
        boolean sameDay = DateUtils.isSameDay(date1, date2);
        Integer days = null;
        Integer hours = null;
        Integer minutes = null;
        if (sameDay) {//同一天
            minutes = Math.abs(minuteBetween(date1, date2));
            minutes = minutes >= 60 ? null : minutes;
            hours = (minutes == null || minutes >= 60) ? Math.abs(daysBetween(date1, date2, 1)) : null;
        } else {//不同天
            format = new SimpleDateFormat("yyyy-MM-dd");
            date1 = format.parse(s1);
            date2 = format.parse(s2);
            days = daysBetween(date1, date2, 24);
        }

//        log.info("sameDay:{}", sameDay);
//        log.info("days:{}", days);
//        log.info("hours:{}", hours);
//        log.info("minute:{}", minutes);
        TimeVo timeVo = new TimeVo();
        timeVo.setDays(days);
        timeVo.setHours(hours);
        timeVo.setMinutes(minutes);
        return timeVo;
    }

    /**
     * JAVA计算两个日期相差多少天和小时(by date)
     *
     * @author zhengkai.blog.csdn.net
     */
    public static int daysBetween(Date date1, Date date2, Integer isDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * isDay);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * JAVA计算两个日期相差多少天和小时(by date)
     *
     * @author zhengkai.blog.csdn.net
     */
    public static int minuteBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 60 * 1);
        return Integer.parseInt(String.valueOf(between_days));
    }


    /**
     * 获取当年的第一天
     */
    public static Date getCurrentFirstOfYear(Calendar currCal) {
        int currentYear = currCal.get(Calendar.YEAR);
        return getFirstOfYear(currentYear);
    }

    /**
     * 获取当年的最后一天
     */
    public static Date getCurrentLastOfYear(Calendar currCal) {
        int currentYear = currCal.get(Calendar.YEAR);
        return getLastOfYear(currentYear);
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getFirstOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }


    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getLastOfYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }


    /**
     * 获取当月第一天日期
     *
     * @param currentDate 当天日期
     * @return
     */
    public static LocalDate getFirstDayOfMonth(Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDay = sdf.format(calendar.getTime());
//        log.info("firstDay:{}", firstDay);
        return LocalDate.parse(firstDay);

    }

    /**
     * 获取当月最后一天日期
     *
     * @param currentDate 当天日期
     * @return
     */
    public static LocalDate getLastDayOfMonth(Date currentDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = sdf.format(calendar.getTime());
        return LocalDate.parse(lastDay);

    }

    /**
     * 每周的所有日期  即周一到周日
     *
     * @param currentDate 当前日期
     * @return {@link List <LocalDate>}
     */
    public static List<LocalDate> allDaysOfWeek(LocalDate currentDate) {
        List<LocalDate> allDays = new ArrayList<>();
        Date date = new Date();
        allDays.add(currentDate.with(DayOfWeek.MONDAY));
        allDays.add(currentDate.with(DayOfWeek.TUESDAY));
        allDays.add(currentDate.with(DayOfWeek.WEDNESDAY));
        allDays.add(currentDate.with(DayOfWeek.THURSDAY));
        allDays.add(currentDate.with(DayOfWeek.FRIDAY));
        allDays.add(currentDate.with(DayOfWeek.SATURDAY));
        allDays.add(currentDate.with(DayOfWeek.SUNDAY));
        return allDays;
    }

    /**
     * 根据日期取得星期几
     *
     * @param date
     * @return
     */
    public static String getWeek(Date date) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 根据日期取得星期几
     *
     * @param date
     * @return
     */
    public static String getWeekDay(Date date) {
        String[] weeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }
    /**
     * 根据日期取得星期几(0表示星期日，1表示星期一，以此类推)
     *
     * @param date 指定的时间
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/17 11:00
     */

    public static Integer getWeekNumber(Date date) {
        String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return week_index;
    }

    /**
     * 将LocalDate转换成Date
     *
     * @param localDate
     */
    public static Date changeLocalDateToDate(LocalDate localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);

        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * 将Date转换成LocalDate
     *
     * @param startTime
     */
    public static LocalDate changeDateToLocalDate(Date startTime) {
        Instant instant = startTime.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        // atZone()方法返回在指定时区从此Instant生成的ZonedDateTime。
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }
}
