package com.couple.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("`event`")
public class Event {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private LocalDate targetDate;

    private String repeatType;

    private String remark;

    private String backgroundImage;

    private Integer backgroundBlur;

    private Integer backgroundPositionX;

    private Integer backgroundPositionY;

    private Integer backgroundScale;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
