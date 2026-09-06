package com.hmdp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 管理身份配置：application.yaml 的 hmdp.admin.user-ids 里列出的用户 ID 视为运营人员。
 *
 * 项目没有用户角色字段，管理权限用这个白名单表达：
 * 例如 user-ids 配置为 1,2 时，用户 1 和 2 可以调用秒杀券修改、店铺删除等管理写接口，
 * 其他用户调用会得到 403 语义的失败结果。
 */
@Configuration
@ConfigurationProperties(prefix = "hmdp.admin")
public class AdminProperties {

    /** 管理员用户 ID 白名单，默认为空表示没有管理员。 */
    private List<Long> userIds = new ArrayList<>();

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds == null ? new ArrayList<>() : userIds;
    }

    /**
     * 判断给定用户是否是管理员；未登录（userId 为 null）永远不是管理员。
     */
    public boolean isAdmin(Long userId) {
        return userId != null && userIds.contains(userId);
    }

    /**
     * 只读视图，避免外部代码误改白名单。
     */
    public List<Long> readOnlyUserIds() {
        return Collections.unmodifiableList(userIds);
    }
}
