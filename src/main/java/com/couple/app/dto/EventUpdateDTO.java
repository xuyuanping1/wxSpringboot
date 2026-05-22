package com.couple.app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EventUpdateDTO {
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
}
