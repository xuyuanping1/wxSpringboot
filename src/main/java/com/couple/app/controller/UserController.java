package com.couple.app.controller;

import com.couple.app.common.CurrentUser;
import com.couple.app.common.Result;
import com.couple.app.dto.UserLoginDTO;
import com.couple.app.dto.UserUpdateDTO;
import com.couple.app.dto.WxLoginDTO;
import com.couple.app.entity.User;
import com.couple.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody UserLoginDTO dto) {
        User user = userService.login(dto);
        return Result.success(user);
    }

    @PostMapping("/wx-login")
    public Result<User> wxLogin(@RequestHeader(value = "x-wx-openid", required = false) String wxOpenid,
                                @RequestBody WxLoginDTO dto) {
        if (wxOpenid != null && !wxOpenid.trim().isEmpty()) {
            User user = userService.wxLoginWithOpenid(wxOpenid, dto.getNickname(), dto.getAvatarUrl());
            return Result.success(user);
        }
        User user = userService.wxLogin(dto);
        return Result.success(user);
    }

    @PostMapping("/silent-wx-login")
    public Result<User> silentWxLogin(@RequestHeader(value = "x-wx-openid", required = false) String wxOpenid,
                                      @RequestBody(required = false) Map<String, String> body) {
        if (wxOpenid != null && !wxOpenid.trim().isEmpty()) {
            User user = userService.silentWxLoginByOpenid(wxOpenid);
            return Result.success(user);
        }
        String code = body != null ? body.get("code") : null;
        if (code != null && !code.trim().isEmpty()) {
            User user = userService.silentWxLogin(code);
            return Result.success(user);
        }
        return Result.error("缺少 openid 或 code");
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        return Result.success(userService.getUserInfo(CurrentUser.get()));
    }

    @GetMapping("/partner-info")
    public Result<User> getPartnerInfo() {
        User user = userService.getUserInfo(CurrentUser.get());
        if (user.getPartnerId() == null) {
            return Result.success(null);
        }
        User partner = userService.getUserInfo(user.getPartnerId());
        if (partner != null) {
            partner.setOpenid(null);
            partner.setUnionid(null);
            partner.setPhone(null);
        }
        return Result.success(partner);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody UserUpdateDTO dto) {
        userService.updateUserInfo(CurrentUser.get(), dto);
        return Result.success();
    }

    @PutMapping("/start-date")
    public Result<Void> updateStartDate(@RequestBody Map<String, String> body) {
        LocalDate startDate = LocalDate.parse(body.get("startDate"));
        userService.updateStartDate(CurrentUser.get(), startDate);
        return Result.success();
    }

    @PostMapping("/bind-partner")
    public Result<Void> bindPartner(@RequestBody Map<String, String> body) {
        userService.bindPartner(CurrentUser.get(), body.get("partnerOpenid"));
        return Result.success();
    }
}
