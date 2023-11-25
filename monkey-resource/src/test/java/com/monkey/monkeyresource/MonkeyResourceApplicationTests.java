package com.monkey.monkeyresource;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyresource.mapper.ResourcesMapper;
import com.monkey.monkeyresource.pojo.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MonkeyResourceApplicationTests {
	@Resource
	private ResourcesMapper resourcesMapper;

	/**
	 * 测试lamadaQueryWrapper使用聚合函数
	 *
	 * @return {@link null}
	 * @author wusihao
	 * @date 2023/11/24 17:24
	 */
	@Test
	public void testLamadaQueryWrapper() throws Exception{
		QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
		resourcesQueryWrapper
				.groupBy("user_id")
				.select("user_id, Count(user_id) as userCount");
		List<Map<String, Object>> maps = resourcesMapper.selectMaps(resourcesQueryWrapper);
		int delete = resourcesMapper.delete(resourcesQueryWrapper);
		for (Map<String, Object> map : maps) {
			for (Map.Entry<String, Object> t : map.entrySet()) {
				System.err.println(t.getKey() + " " + t.getValue());
			}
		}

	}
	/**
	 * 测试解析阿里云返回结果测试类
	 *
	 * @return {@link null}
	 * @author wusihao
	 * @date 2023/10/21 14:51
	 */
	@Test
	public void testAliPayReturnResult() {
		System.err.println("测试");
		String str = "{\"alipay_trade_query_response\":{\"code\":\"10000\",\"msg\":\"Success\",\"buyer_logon_id\":\"nqh***@sandbox.com\",\"buyer_pay_amount\":\"0.00\",\"buyer_user_id\":\"2088722009652012\",\"buyer_user_type\":\"PRIVATE\",\"invoice_amount\":\"0.00\",\"out_trade_no\":\"68\",\"point_amount\":\"0.00\",\"receipt_amount\":\"0.00\",\"send_pay_date\":\"2023-10-21 10:39:34\",\"total_amount\":\"4.00\",\"trade_no\":\"2023102122001452010501222297\",\"trade_status\":\"TRADE_CLOSED\"},\"sign\":\"GEPqjj2NSTPI53ZxvtpaPHJimIG6PbaIXuV/O2DacRe+QKGE9ZRXV1ORvp1flBaNRqMW4cRfUhYTeWhCVv9InujRQ484z/BLf3MF+QCdElPdt78mlmPTdoDoAwJoiwN4J2Lw9HuSVnMeflmetzj+Gknh27/Xmnu6eDYQfsPImIGmXTQNPLduSJ8351nN577MAyP2Vsl5j1cegGc5XILNRdq5i8Juf04fQy6esRCce1fHvigfEK/smriS0hJaS1WyXGi+jc81jhUtY0c3QuiVxz0zY+VpG5gfiu2pm8CxqCCVKsu/A7OKfaDxqreVcFhwr40YbsmD8XJxQ2YzzxuNrQ==\"}";
		JSONObject jsonObject = JSONObject.parseObject(str);
		JSONObject alipay_trade_query_response = jsonObject.getJSONObject("alipay_trade_query_response");
		String trade_status = alipay_trade_query_response.getString("trade_status");
		Date send_pay_date = alipay_trade_query_response.getDate("send_pay_date");
		System.err.println(send_pay_date);
		System.out.println(trade_status);
		System.err.println(alipay_trade_query_response);
	}

}
