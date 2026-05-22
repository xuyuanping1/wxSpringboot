package com.couple.app.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class EventCreateDTO {
    @NotBlank(message = "事件名称不能为空")
    private String name;

    @NotNull(message = "目标日不能为空")
    private LocalDate targetDate;

    private String repeatType = "NONE";

    private String remark;

    private String backgroundImage;

    private Integer backgroundBlur;

    private Integer sortOrder = 0;
}
