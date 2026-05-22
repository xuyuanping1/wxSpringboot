package com.couple.app.controller;

import com.couple.app.common.CurrentUser;
import com.couple.app.common.Result;
import com.couple.app.entity.Invite;
import com.couple.app.entity.User;
import com.couple.app.service.InviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invite")
public class InviteController {

    @Autowired
    private InviteService inviteService;

    @Autowired
    private com.couple.app.service.UserService userService;

    /**
     * 创建邀请（生成邀请 token）
     */
    @PostMapping("/create")
    public Result<Invite> createInvite() {
        return Result.success(inviteService.createInvite(CurrentUser.get()));
    }

    /**
     * 查看邀请详情（被邀请方打开链接时调用，需传 token）
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getInviteInfo(@RequestParam String token) {
        Invite invite = inviteService.getInviteByToken(token);
        User inviter = userService.getUserInfo(invite.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("token", invite.getToken());
        data.put("inviterId", invite.getUserId());
        data.put("inviterNickname", inviter.getNickname());
        data.put("createdAt", invite.getCreatedAt());
        return Result.success(data);
    }

    /**
     * 接受邀请，绑定情侣
     */
    @PostMapping("/accept")
    public Result<User> acceptInvite(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        User inviter = inviteService.acceptInvite(CurrentUser.get(), token);
        return Result.success(inviter);
    }
}
