package com.couple.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("event_photo")
public class EventPhoto {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long eventId;

    private LocalDate occurrenceDate;

    private String url;

    private String thumbnailUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime uploadTime;
}
