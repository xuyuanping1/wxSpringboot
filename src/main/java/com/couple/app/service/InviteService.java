package com.couple.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.couple.app.common.BusinessException;
import com.couple.app.entity.Invite;
import com.couple.app.entity.User;
import com.couple.app.mapper.InviteMapper;
import com.couple.app.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InviteService {

    @Autowired
    private InviteMapper inviteMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /**
     * 创建邀请（一个用户同时只能有一个有效邀请）
     */
    public Invite createInvite(Long userId) {
        User user = userService.getUserInfo(userId);
        if (user.getPartnerId() != null) {
            throw new BusinessException("已经绑定了伴侣，无需重复邀请");
        }

        // 先查是否有未使用的邀请
        Invite existing = inviteMapper.selectOne(
                new LambdaQueryWrapper<Invite>()
                        .eq(Invite::getUserId, userId)
                        .eq(Invite::getStatus, "PENDING")
                        .orderByDesc(Invite::getCreatedAt)
                        .last("LIMIT 1")
        );
        if (existing != null) {
            return existing;
        }

        Invite invite = new Invite();
        invite.setUserId(userId);
        invite.setToken(UUID.randomUUID().toString().replace("-", ""));
        invite.setStatus("PENDING");
        inviteMapper.insert(invite);
        return invite;
    }

    /**
     * 通过 token 获取邀请信息（给被邀请方查看）
     */
    public Invite getInviteByToken(String token) {
        Invite invite = inviteMapper.selectOne(
                new LambdaQueryWrapper<Invite>().eq(Invite::getToken, token)
        );
        if (invite == null || !"PENDING".equals(invite.getStatus())) {
            throw new BusinessException("邀请无效或已过期");
        }
        return invite;
    }

    /**
     * 接受邀请，绑定情侣关系
     */
    @Transactional
    public User acceptInvite(Long currentUserId, String token) {
        Invite invite = getInviteByToken(token);
        Long inviterId = invite.getUserId();

        if (inviterId.equals(currentUserId)) {
            throw new BusinessException("不能绑定自己");
        }

        User inviter = userService.getUserInfo(inviterId);
        User acceptor = userService.getUserInfo(currentUserId);

        if (inviter.getPartnerId() != null) {
            throw new BusinessException("对方已绑定了伴侣");
        }
        if (acceptor.getPartnerId() != null) {
            throw new BusinessException("你已经绑定了伴侣");
        }

        // 双向绑定
        inviter.setPartnerId(currentUserId);
        acceptor.setPartnerId(inviterId);
        userMapper.updateById(inviter);
        userMapper.updateById(acceptor);

        // 标记邀请为已接受
        invite.setStatus("ACCEPTED");
        inviteMapper.updateById(invite);

        return inviter;
    }
}
