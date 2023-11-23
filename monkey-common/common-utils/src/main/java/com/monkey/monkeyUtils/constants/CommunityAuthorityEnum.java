package com.monkey.monkeyUtils.constants;


public enum CommunityAuthorityEnum {
    COMMUNITY_MANAGE("社区管理员", "admin:communityId = "),
    COMMUNITY_PRIME_MANAGE("社区超级管理员", "prime_admin:communityId = ")
    ;

    private String name;
    private String perm;

    CommunityAuthorityEnum(String name, String perm) {
        this.name = name;
        this.perm = perm;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerm() {
        return perm;
    }

    public void setPerm(String perm) {
        this.perm = perm;
    }
}
