package com.couple.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("anniversary")
public class Anniversary {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private LocalDate startDate;

    private String backgroundImage;

    private Integer cardBgBlur;

    private String homeBgImage;

    private Integer homeBgBlur;

    private String customTitle;

    private String textColor;

    private String cardTextColor;

    private Integer cardBgPositionX;

    private Integer cardBgPositionY;

    private Integer cardBgScale;

    private Integer homeBgPositionX;

    private Integer homeBgPositionY;

    private Integer homeBgScale;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
