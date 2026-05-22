package com.couple.app.dto;

import lombok.Data;

@Data
public class WxLoginDTO {
    private String code;
    private String nickname;
    private String avatarUrl;
}
