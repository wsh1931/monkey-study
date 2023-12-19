package com.monkey.monkeyarticle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.util.DateSelfUtils;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeyarticle.mapper.ArticleStatisticsMapper;
import com.monkey.monkeyarticle.pojo.Article;
import com.monkey.monkeyarticle.pojo.ArticleStatistics;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.monkey.monkeyUtils.util.DateSelfUtils.getBeenTwoDayAllDate;

/**
 * @author: wusihao
 * @date: 2023/12/18 11:06
 * @version: 1.0
 * @description:
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MonkeyArticleTest {

    @Resource
    private ArticleStatisticsMapper articleStatisticsMapper;
    // 得到一周内用户文章数
    @Test
    public void getUserArticleCountByWeek() {

// userId 是我们想要查询的作者的ID
        Long authorId = 10L;// 某个特定作者的ID
                Date sevenDaysAgo = Date.from(Instant.now().minus(7, ChronoUnit.DAYS));

// 创建 QueryWrapper 实例
        QueryWrapper<ArticleStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select("DATE_FORMAT(create_time, '%Y-%m-%d') as date", "COUNT(*) as articleCount") // 使用 DATE_FORMAT 来格式化日期，并计数
                .eq("userId", authorId) // 筛选指定的作者
                .ge("create_time", sevenDaysAgo) // 筛选最近7天内的数据
                .groupBy("DATE_FORMAT(create_time, '%Y-%m-%d')") // 按天分组
                .orderByAsc("DATE_FORMAT(create_time, '%Y-%m-%d')"); // 按日期排序

// 执行查询
        List<Map<String, Object>> results = articleStatisticsMapper.selectMaps(queryWrapper);

// 这里的 results 是一个包含 Map 的 List，每个 Map 都有两个键：date 和 articleCount
// date 是每天的日期，articleCount 是对应日期的文章数
        for (Map<String, Object> result : results) {
            System.out.println("Date: " + result.get("date") + ", Article count: " + result.get("articleCount"));
        }
    }
    /**
     * 得到一周内的所有日期
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/18 11:09
     */
    @Test
    public void getWeekAllDate() {
        String format = DateUtils.format(DateUtils.stringToDate("2023-11-01 23:24:01", DateUtils.DATE_TIME_PATTERN));
        System.out.println(format);
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
}
