package com.couple.app.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginDTO {
    @NotBlank(message = "openid不能为空")
    private String openid;

    private String nickname;

    private String avatarUrl;
}
