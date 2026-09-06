package com.hmdp.service.cursor;

/*
 * 现实业务背景：用户在热榜、点赞榜、作者博客或 Feed 中继续下拉时，客户端需要携带上次读取位置。
 * 实际触发：列表服务生成下一页时编码 cursor，下一次请求到达时再解码并校验类型；客户端只负责原样回传。
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.dto.CursorPayload;
import com.hmdp.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.Base64;

/**
 * 把“下一页从哪里继续”转换成前端可以保存并原样传回的字符串。
 *
 * 这个字符串叫游标。编码过程：游标内容先填进 {@link CursorPayload}（游标载荷 DTO——普通列表只用
 * type 类型标记、score 排序值、id 记录主键三个字段，个性化 Feed 才会用到 snapshotId、offset 等其余字段），
 * 再序列化成 JSON 字节，最后用“不带填充（withoutPadding）的 URL 安全 Base64”编码成字符串放进 nextCursor。
 * 例如第一页最后一条是“点赞数 20、博客 ID 100”，热榜游标就是
 * CursorPayload{type="blog-hot-v1", score=20, id=100} 先转 JSON、再 Base64 后得到的字符串；
 * 请求下一页时就从它后面继续查。
 *     1. 前端不需要理解内容：opaque cursor 的意思就是“不透明游标”，
 *     前端只保存并原样回传，不能自己拼点赞数、时间或数据库 ID。
 *     2. 不同列表不能混用：payload 中的 type 字段标记游标属于哪个列表
 *     （热榜是 blog-hot-v1、作者博客是 user-blog-v2、点赞榜是 blog-like-v2，Feed 是 feed-模式-v2），
 *     把热榜游标传给点赞榜会因 type 与预期不符直接报参数错误。
 *     3. Base64 不是加密：它只是把 JSON 变成适合放在 URL 中的字符串，用户仍可能修改内容，
 *     所以解码后（本类只校验 type）调用方必须继续检查 score、id 等位置和数值范围。
 * 
 */
@Component
public class CursorCodec {

    private final ObjectMapper objectMapper;

    public CursorCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String encode(CursorPayload payload) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(payload);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("生成分页游标失败", e);
        }
    }

    public CursorPayload decode(String cursor, String expectedType) {
        if (cursor == null || cursor.trim().isEmpty()) {
            return null;
        }
        try {
            byte[] json = Base64.getUrlDecoder().decode(cursor.getBytes(StandardCharsets.UTF_8));
            CursorPayload payload = objectMapper.readValue(json, CursorPayload.class);
            if (payload == null || !expectedType.equals(payload.getType())) {
                throw invalidCursor();
            }
            return payload;
        } catch (IllegalArgumentException | IOException e) {
            throw invalidCursor();
        }
    }

    private BusinessException invalidCursor() {
        return BusinessException.badRequest("INVALID_CURSOR", "分页游标无效或已损坏");
    }
}
