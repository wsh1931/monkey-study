package com.monkey.monkeysearch.pojo;

/**
 * @author: wusihao
 * @date: 2023/11/29 11:16
 * @version: 1.0
 * @description:
 */
public class Achievement {
        Long userId;
        String keyword;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public Achievement(Long userId, String keyword) {
            this.userId = userId;
            this.keyword = keyword;
        }
}
