package com.monkey.monkeysearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.monkey.monkeysearch.constant.IndexConstant;
import com.monkey.monkeysearch.pojo.ESAllIndex;
import com.monkey.monkeysearch.pojo.ESArticleIndex;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeysearch.pojo.ESCommunityArticleIndex;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MonkeySearchApplicationTests {
//	private RestHighLevelClient restClient;
//
//
//	private static final String serverUrl = "http://192.168.133.138:9200";
//
//	@BeforeEach
//	void setUp() {
//		restClient = new RestHighLevelClient(RestClient.builder(
//				HttpHost.create(serverUrl)
//		));
//	}
//	@Test
//	void testElasticSearch() throws IOException {
//		System.err.println(restClient);
//
//	}

	@Resource
	private ElasticsearchClient elasticsearchClient;

	// 聚合函数sum的使用
	@Test
	void aggregationSum() throws Exception {
		SearchResponse<ESCommunityArticleIndex> response = elasticsearchClient.search(search -> search
				.index(IndexConstant.communityArticle)
				.query(query -> query
						.match(match -> match
								.field("communityId")
								.query(String.valueOf(1))))
				.aggregations("userId", ag -> ag.terms(term -> term
						.field("userId"))
						.aggregations("likeCount", aggregation -> aggregation
								.sum(sum -> sum
										.field("likeCount")))
						.aggregations("viewCount", aggregation -> aggregation
								.sum(sum -> sum
										.field("viewCount")))
						.aggregations("CollectCount", aggregation -> aggregation
								.sum(sum -> sum
										.field("CollectCount")))
						.aggregations("CommentCount", aggregation -> aggregation
								.sum(sum -> sum
										.field("CommentCount"))))
				, ESCommunityArticleIndex.class);

		Map<String, Aggregate> aggregations = response.aggregations();
		System.out.println(aggregations.size());
		aggregations.entrySet().stream().forEach(map -> {
			String key = map.getKey();
			System.err.println(key);
			Aggregate value = map.getValue();
			System.out.println(value);
			LongTermsAggregate lterms = value.lterms();
			System.out.println("lterms = " + lterms);
			Buckets<LongTermsBucket> buckets = lterms.buckets();
			System.out.println("buckets => " + buckets);
			List<LongTermsBucket> array = buckets.array();
			array.stream().forEach(arr ->  {
				long key1 = arr.key();
				System.out.println(key1);
				Map<String, Aggregate> aggregations1 = arr.aggregations();
				for (Map.Entry<String, Aggregate> aggregateEntry : aggregations1.entrySet()) {
					System.err.println(aggregateEntry.getKey() + " == " + BigDecimal.valueOf(aggregateEntry.getValue().sum().value()).longValue());
				}
			});
		});

//		response.hits().hits().stream().forEach(hit -> {
//			ESCommunityArticleIndex source = hit.source();
//			System.err.println(source);
//		});
	}
	// 删除文章文档
	@Test
	void deleteArticleDocument() throws Exception {
		DeleteResponse delete = elasticsearchClient.delete(d -> d
				.index(IndexConstant.article)
				.id("1"));

		System.out.println(delete.result());
	}


	// 批量插入文档
	@Test
	void bulkInsertDocument() throws Exception {
		ESArticleIndex article = new ESArticleIndex();
		article.setId("1");
		article.setContent("测试文章内容");
		List<String> labels = new ArrayList<>();
		labels.add("java");
		labels.add("python");
		article.setLabelName(labels);
		article.setTitle("吴思豪标题");
		article.setPhoto("吴思豪的简介");
		article.setCreateTime(new Date());

		ESArticleIndex article1 = new ESArticleIndex();
		article1.setId("2");
		article1.setContent("测试文章内容");
		List<String> labels1 = new ArrayList<>();
		labels.add("java");
		labels.add("python");
		article1.setLabelName(labels1);
		article1.setTitle("吴思豪标题");
		article1.setPhoto("吴思豪的简介");
		article1.setCreateTime(new Date());
		List<ESArticleIndex> esArticleIndices = new ArrayList<>();
		esArticleIndices.add(article);
		esArticleIndices.add(article1);

		BulkRequest.Builder br = new BulkRequest.Builder();
		for (ESArticleIndex esArticleIndex : esArticleIndices) {
			BulkRequest.Builder operations = br.operations(op -> op
					.index(idx -> idx
							.index(IndexConstant.article)
							.id(esArticleIndex.getId())
							.document(esArticleIndex)));
		}

		BulkResponse bulk = elasticsearchClient.bulk(br.build());
		System.out.println(bulk);
	}

	// 查询文章文档
	@Test
	void queryArticleDocumentById() throws Exception {
		GetResponse<ESArticleIndex> response = elasticsearchClient.get(s -> s
						.index(IndexConstant.article)
						.id("1")
				, ESArticleIndex.class);

		ESArticleIndex source = response.source();
		System.out.println(source);
	}

	// 插入文章文档
	@Test
	void insertArticleDocument() throws Exception {
		ESArticleIndex article = new ESArticleIndex();
		article.setId("1");
		article.setContent("测试文章内容");
		List<String> labels = new ArrayList<>();
		labels.add("java");
		labels.add("python");
		article.setLabelName(labels);
		article.setTitle("吴思豪标题");
		article.setPhoto("吴思豪的简介");
		article.setCreateTime(new Date());
		IndexResponse response = elasticsearchClient.index(s ->
				s.index("article")
						.id(article.getId())
						.document(article));

		System.out.println(response);
	}

	// 得到文章索引库
	@Test
	void getArticleIndex() throws Exception{
		GetIndexResponse response = elasticsearchClient.indices().get(s -> s.index("article"));
		System.out.println(response);
	}

	// 删除文章索引
	@Test
	void deleteArticleIndex() throws Exception {
		DeleteIndexResponse article = elasticsearchClient.indices().delete(s -> s.index("article"));
		System.out.println(article);
	}

	@Test
	void getToken() throws Exception {
		String jwt = JwtUtil.createJWT("10");
		System.out.println(jwt);
	}
//	// 测试注入客户端内容
//	@Test
//	void testConfiguration() throws Exception{
//		SearchRequest request = new SearchRequest("monkey");
//		request.source().query(QueryBuilders.matchQuery("all", "java"));
//		request.source().highlighter(new HighlightBuilder()
//				.field("info")
//				.requireFieldMatch(false));
//
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		// 得到查找结果
//		SearchHit[] hits = response.getHits().getHits();
//		for (SearchHit searchHit : hits) {
//			Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
//			HighlightField info = highlightFields.get("info");
//			if (info != null) {
//				Text fragment = info.getFragments()[0];
//				System.out.println(info);
//				System.err.println(fragment);
//				System.out.println();
//			}
//		}
//	}
//	// 聚合功能
//	@Test
//	void queryAggregation() throws Exception{
//		SearchRequest request = new SearchRequest("monkey");
//
//		request.source().size(0);
//		request.source().aggregation(AggregationBuilders
//				.terms("emailAgg")
//				.field("age")
//				.size(10)
//				);
//
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		Terms emailAgg = response.getAggregations().get("emailAgg");
//		List<? extends Terms.Bucket> buckets = emailAgg.getBuckets();
//		for (Terms.Bucket bucket : buckets) {
//			String keyAsString = bucket.getKeyAsString();
//			System.err.println(keyAsString);
//		}
//
//	}
//
//	// 查询高亮数据
//	@Test
//	void queryHighlight() throws Exception{
//		SearchRequest request = new SearchRequest("monkey");
//		request.source().query(QueryBuilders.matchQuery("all", "java"));
//		request.source().highlighter(new HighlightBuilder()
//				.field("info")
//				.requireFieldMatch(false));
//
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		// 得到查找结果
//		SearchHit[] hits = response.getHits().getHits();
//		for (SearchHit searchHit : hits) {
//			Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
//			HighlightField info = highlightFields.get("info");
//			if (info != null) {
//				Text fragment = info.getFragments()[0];
//				System.out.println(info);
//				System.err.println(fragment);
//				System.out.println();
//			}
//		}
//	}
//
//	// 查询数据分页并排序
//	@Test
//	void testPageAndSort() throws Exception {
//		SearchRequest request = new SearchRequest("monkey");
//		request.source()
//				.query(QueryBuilders.matchAllQuery())
//				.from(0)
//				.size(3)
//				.sort("age", SortOrder.ASC);
//
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		// 得到查找结果
//		SearchHit[] hits = response.getHits().getHits();
//		for (SearchHit searchHit : hits) {
//			System.err.println(searchHit.getSourceAsString());
//		}
//
//	}
//
//	// 查询Boolean字段数据(Boolean)
//	@Test
//	void testBooleanMatch() throws Exception {
//		SearchRequest request = new SearchRequest("monkey");
//
//		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//		boolQueryBuilder.must(QueryBuilders.termQuery("brief", "Java"))
//				.filter(QueryBuilders.rangeQuery("age")
//				.lte(20));
//
//		request.source().query(boolQueryBuilder);
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		// 得到查找结果
//		SearchHit[] hits = response.getHits().getHits();
//		for (SearchHit searchHit : hits) {
//			System.err.println(searchHit.getSourceAsString());
//		}
//	}
//
//	// 查询范围字段数据(range)
//	@Test
//	void testRangeMatch() throws Exception {
//		SearchRequest request = new SearchRequest("monkey");
//		request.source().query(QueryBuilders.rangeQuery("age")
//				.gte(10)
//				.lte(20));
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		// 得到查找结果
//		SearchHit[] hits = response.getHits().getHits();
//		for (SearchHit searchHit : hits) {
//			System.err.println(searchHit.getSourceAsString());
//		}
//	}
//
//	// 查询多个字段数据(match)
//	@Test
//	void testMutiMatch() throws Exception {
//		SearchRequest request = new SearchRequest("monkey");
//		request.source().query(QueryBuilders.multiMatchQuery("吴思豪", "info", "brief"));
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		// 得到查找结果
//		SearchHit[] hits = response.getHits().getHits();
//		for (SearchHit searchHit : hits) {
//			System.err.println(searchHit.getSourceAsString());
//		}
//	}
//
//	// 查询单个字段数据(match)
//	@Test
//	void testMatch() throws Exception {
//		SearchRequest request = new SearchRequest("monkey");
//		request.source().query(QueryBuilders.matchQuery("all", "吴思豪"));
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		// 得到查找结果
//		SearchHit[] hits = response.getHits().getHits();
//		for (SearchHit searchHit : hits) {
//			System.err.println(searchHit.getSourceAsString());
//		}
//	}
//
//	// 查询所有数据(match_all)
//	@Test
//	void testMatchAll() throws Exception {
//		SearchRequest request = new SearchRequest("monkey");
//		request.source().query(QueryBuilders.matchAllQuery());
//		SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
//
//		// 得到查找结果
//		SearchHit[] hits = response.getHits().getHits();
//		for (SearchHit searchHit : hits) {
//			System.err.println(searchHit.getSourceAsString());
//		}
//	}
//	// 批量新增文档信息
//	@Test
//	void testBulk() throws Exception {
//		BulkRequest request = new BulkRequest();
//		for (int i = 1; i <= 1; i ++ ) {
//			request.add(new IndexRequest("monkey")
//					.id("" + (i + 21))
//					.source("{\n" +
//							"  \"age\": 3,\n" +
//							"  \"info\": \"Python傻逼\",\n" +
//							"  \"email\": \"吴思豪的邮箱1931\",\n" +
//							"  \"name\": {\n" +
//							"    \"firstName\": \"吴\",\n" +
//							"    \"secondName\": \"思豪\"\n" +
//							"  },\n" +
//							"  \"brief\": \"吴思豪的Java在哪里呢\"\n" +
//							"}", XContentType.JSON));
//		}
//
//		restClient.bulk(request, RequestOptions.DEFAULT);
//	}
//	// 删除文档
//	@Test
//	void testDeleteDocumentById() throws IOException {
//		DeleteRequest deleteRequest = new DeleteRequest("monkey", "1");
//		restClient.delete(deleteRequest, RequestOptions.DEFAULT);
//	}
//
//	// 修改文档
//	@Test
//	void testUpdateDocumentById() throws IOException {
//		UpdateRequest request = new UpdateRequest("monkey", "1");
//		request.doc(
//		"age", 13,
//				  "info", "啊哈哈哈"
//		);
//		restClient.update(request, RequestOptions.DEFAULT);
//	}
//
//	// 通过id查询文档
//	@Test
//	void testGetDocumentById() throws IOException {
//		GetRequest request = new GetRequest("monkey", "21");
//		GetResponse documentFields = restClient.get(request, RequestOptions.DEFAULT);
//		String sourceAsString = documentFields.getSourceAsString();
//		System.err.println(sourceAsString);
//	}
//
//	// 新增文档
//	@Test
//	void testAddDocument() throws IOException {
//		IndexRequest request = new IndexRequest("monkey").id("10");
//		request.source("{\n" +
//				"  \"age\": 18,\n" +
//				"  \"info\": \"吴思豪太强了\",\n" +
//				"  \"email\": \"1931\",\n" +
//				"  \"name\": {\n" +
//				"    \"firstName\": \"吴\",\n" +
//				"    \"secondName\": \"思豪\"\n" +
//				"  }\n" +
//				"}\n", XContentType.JSON);
//		restClient.index(request, RequestOptions.DEFAULT);
//	}
//
//	@Test
//	void createHotelIndex() throws IOException {
//		CreateIndexRequest request = new CreateIndexRequest("monkey");
//		request.source("{\n" +
//				"  \"mappings\": {\n" +
//				"    \"properties\": {\n" +
//				"      \"info\": {\n" +
//				"        \"type\": \"text\",\n" +
//				"        \"analyzer\": \"ik_max_word\"\n" +
//				"      },\n" +
//				"      \"email\": {\n" +
//				"        \"type\": \"keyword\",\n" +
//				"        \"index\": false\n" +
//				"      },\n" +
//				"      \"name\": {\n" +
//				"        \"type\": \"object\",\n" +
//				"        \"properties\": {\n" +
//				"          \"firstName\": {\n" +
//				"            \"type\": \"keyword\",\n" +
//				"            \"index\": true\n" +
//				"          },\n" +
//				"          \"secondName\": {\n" +
//				"            \"type\": \"keyword\",\n" +
//				"            \"index\": true\n" +
//				"          }\n" +
//				"        }\n" +
//				"      }\n" +
//				"    }\n" +
//				"  }\n" +
//				"}", XContentType.JSON);
//		restClient.indices().create(request, RequestOptions.DEFAULT);
//	}
//
//	// 判断是否存在索引库并查询
//	@Test
//	void judgeIsExistAndQuery() throws IOException {
//		GetIndexRequest request = new GetIndexRequest("monkey");
//
//		boolean exists = restClient.indices().exists(request, RequestOptions.DEFAULT);
//		if (exists) {
//			GetIndexResponse response = restClient.indices().get(request, RequestOptions.DEFAULT);
//			System.err.println(response);
//		}
//	}
//
//	// 删除索引库
//	@Test
//	void deleteIndex() throws Exception{
//		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("monkey");
//		restClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
//	}
//
//	@AfterEach
//	void tearDown() throws Exception {
//		this.restClient.close();
//	}
}
