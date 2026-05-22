package com.couple.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("album")
public class Album {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String description;

    private String coverImage;

    private Integer coverBlur;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
