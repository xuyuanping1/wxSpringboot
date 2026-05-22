package com.couple.app.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("invite")
public class Invite {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String token;

    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
