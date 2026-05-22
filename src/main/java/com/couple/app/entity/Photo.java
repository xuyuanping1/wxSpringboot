package com.couple.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("photo")
public class Photo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long albumId;

    private String url;

    private String thumbnailUrl;

    private LocalDateTime uploadTime;

    private Integer sortOrder;
}
