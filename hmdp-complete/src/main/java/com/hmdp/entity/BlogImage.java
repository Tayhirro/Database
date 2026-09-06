package com.hmdp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_blog_image")
public class BlogImage implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_TEMP = "TEMP";
    public static final String STATUS_BOUND = "BOUND";
    public static final String STATUS_DELETING = "DELETING";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long blogId;

    private String storageKey;

    private String publicUrl;

    private String contentType;

    private Long fileSize;

    private Integer width;

    private Integer height;

    private String status;

    private Integer sortOrder;

    private LocalDateTime bindTime;

    private Integer retryCount;

    private String lastError;

    private LocalDateTime nextRetryTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
