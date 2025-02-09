package com.monkey.monkeysearch.constant;

/**
 * @author: wusihao
 * @date: 2023/11/7 8:53
 * @version: 1.0
 * @description: elasticsearch索引常量类
 */
public class ESCreateIndexConstant {
    // 创建文字索引
    public static final String createArticleIndex = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"monkey-pingyin\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false,\n" +
            "          \"keep_none_chinese_in_joined_full_pinyin\": true\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userId\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"username\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userHeadImg\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userBrief\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"labelName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"title\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"profile\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"content\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"togetherSearch\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"photo\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"viewCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"likeCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"collectCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"commentCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    // 创建问答索引
    public static final String createQuestionIndex = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"monkey-pingyin\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false,\n" +
            "          \"keep_none_chinese_in_joined_full_pinyin\": true\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userId\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"username\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userHeadImg\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userBrief\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"labelName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"title\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"profile\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"togetherSearch\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"photo\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"viewCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"likeCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"collectCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"replyCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    // 创建课程索引
    public static final String createCourseIndex = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"monkey-pingyin\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false,\n" +
            "          \"keep_none_chinese_in_joined_full_pinyin\": true\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userId\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"username\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userHeadImg\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userBrief\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"labelName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"title\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"introduce\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"togetherSearch\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"picture\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"viewCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"collectCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"commentCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"sectionCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"studyCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"formTypeName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"score\": {\n" +
            "        \"type\": \"float\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    // 创建社区文章索引
    public static final String createCommunityArticleIndex = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"monkey-pingyin\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false,\n" +
            "          \"keep_none_chinese_in_joined_full_pinyin\": true\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userId\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"communityId\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"communityName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"username\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userHeadImg\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userBrief\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"title\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"brief\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"togetherSearch\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"content\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"picture\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"viewCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"likeCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"collectCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"commentCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"score\": {\n" +
            "        \"type\": \"float\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "       \"labelName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    // 创建社区索引
    public static final String createCommunityIndex = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"monkey-pingyin\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false,\n" +
            "          \"keep_none_chinese_in_joined_full_pinyin\": true\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"description\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"togetherSearch\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"photo\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"classificationName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"contentLabelName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"attributeLabelName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"memberCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"articleCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    // 创建资源索引
    public static final String createResourceIndex = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"monkey-pingyin\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false,\n" +
            "          \"keep_none_chinese_in_joined_full_pinyin\": true\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userId\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \n" +
            "      \"username\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userHeadImg\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userBrief\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"name\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"typeUrl\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"description\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"togetherSearch\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"resourceLabelName\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"resourceClassificationName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"formTypeName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "       \"score\": {\n" +
            "        \"type\": \"float\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"viewCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"likeCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"commentCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"collectCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"downCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"buyCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    // 创建用户索引
    public static final String createUserIndex = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"monkey-pingyin\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false,\n" +
            "          \"keep_none_chinese_in_joined_full_pinyin\": true\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \n" +
            "      \"username\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"userHeadImg\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userBrief\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"togetherSearch\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"viewCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"fansCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"opusCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"likeCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"collectCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"connectCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    // 创建全部搜索所有
    public static final String createAllIndex = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": { \n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"monkey-pingyin\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"monkey-pingyin\": { \n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false,\n" +
            "          \"keep_none_chinese_in_joined_full_pinyin\": true\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"username\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userHeadImg\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userBrief\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"title\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"profile\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"content\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"togetherSearch\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"communityId\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"communityName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"formTypeName\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"labelName\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"monkey-pingyin\",\n" +
            "        \"search_analyzer\": \"ik_max_word\",\n" +
            "        \"copy_to\": \"togetherSearch\"\n" +
            "      },\n" +
            "      \"type\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"viewCount\": {\n" +
            "        \"type\": \"long\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"commentCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"likeCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"collectCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
