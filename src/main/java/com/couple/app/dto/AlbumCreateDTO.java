package com.couple.app.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AlbumCreateDTO {
    @NotBlank(message = "相册名称不能为空")
    private String name;

    private String description;

    private String coverImage;

    private Integer coverBlur;
}
