package com.couple.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.couple.app.common.BusinessException;
import com.couple.app.dto.UserLoginDTO;
import com.couple.app.dto.UserUpdateDTO;
import com.couple.app.dto.WxLoginDTO;
import com.couple.app.entity.User;
import com.couple.app.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Value("${wechat.appid}")
    private String wxAppId;

    @Value("${wechat.secret}")
    private String wxSecret;

    /**
     * 获取情侣双方的 userId 列表（用于共享数据查询）
     */
    public List<Long> getCoupleUserIds(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) return Arrays.asList(userId);
        if (user.getPartnerId() != null) {
            return Arrays.asList(userId, user.getPartnerId());
        }
        return Arrays.asList(userId);
    }

    public User login(UserLoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, dto.getOpenid())
        );
        if (user == null) {
            user = new User();
            user.setOpenid(dto.getOpenid());
            user.setNickname(dto.getNickname() != null ? dto.getNickname() : "");
            user.setAvatarUrl(dto.getAvatarUrl() != null ? dto.getAvatarUrl() : "");
            userMapper.insert(user);
        }
        return user;
    }

    @SuppressWarnings("unchecked")
    private String codeToOpenid(String code) {
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wxAppId, wxSecret, code
        );
        RestTemplate restTemplate = new RestTemplate();
        String json = restTemplate.getForObject(url, String.class);
        Map<String, Object> resp;
        try {
            resp = new ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            throw new BusinessException("微信登录失败: 响应解析错误");
        }
        if (resp.containsKey("errcode")) {
            throw new BusinessException("微信登录失败: " + resp.get("errmsg"));
        }
        return (String) resp.get("openid");
    }

    /**
     * 静默登录：用 code 换 openid，查到用户就返回，查不到返回 null
     */
    public User silentWxLogin(String code) {
        String openid = codeToOpenid(code);
        return silentWxLoginByOpenid(openid);
    }

    public User silentWxLoginByOpenid(String openid) {
        if (openid == null || openid.trim().isEmpty()) {
            return null;
        }
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
        );
    }

    public User wxLogin(WxLoginDTO dto) {
        String openid = codeToOpenid(dto.getCode());
        return wxLoginWithOpenid(openid, dto.getNickname(), dto.getAvatarUrl());
    }

    public User wxLoginWithOpenid(String openid, String nickname, String avatarUrl) {
        if (openid == null || openid.trim().isEmpty()) {
            throw new BusinessException("微信登录失败: openid 为空");
        }
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid)
        );
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(nickname != null ? nickname : "");
            user.setAvatarUrl(avatarUrl != null ? avatarUrl : "");
            userMapper.insert(user);
        } else {
            // 更新昵称和头像
            if (nickname != null && !nickname.isEmpty()) {
                user.setNickname(nickname);
            }
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                user.setAvatarUrl(avatarUrl);
            }
            userMapper.updateById(user);
        }
        return user;
    }

    public User getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    public void updateUserInfo(Long userId, UserUpdateDTO dto) {
        User user = getUserInfo(userId);
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        userMapper.updateById(user);
    }

    @Autowired
    private com.couple.app.mapper.AnniversaryMapper anniversaryMapper;

    public void updateStartDate(Long userId, LocalDate startDate) {
        User user = getUserInfo(userId);
        user.setStartDate(startDate);
        userMapper.updateById(user);
        // 同步创建/更新 Anniversary 记录
        List<Long> userIds = getCoupleUserIds(userId);
        com.couple.app.entity.Anniversary anniversary = anniversaryMapper.selectOne(
                new LambdaQueryWrapper<com.couple.app.entity.Anniversary>()
                        .in(com.couple.app.entity.Anniversary::getUserId, userIds).last("LIMIT 1")
        );
        if (anniversary == null) {
            anniversary = new com.couple.app.entity.Anniversary();
            anniversary.setUserId(userId);
            anniversary.setStartDate(startDate);
            anniversaryMapper.insert(anniversary);
        } else {
            anniversary.setStartDate(startDate);
            anniversaryMapper.updateById(anniversary);
        }
    }

    public void bindPartner(Long userId, String partnerOpenid) {
        User user = getUserInfo(userId);
        if (user.getPartnerId() != null) {
            throw new BusinessException("已经绑定了伴侣");
        }
        User partner = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, partnerOpenid)
        );
        if (partner == null) {
            throw new BusinessException("未找到该用户");
        }
        if (partner.getId().equals(userId)) {
            throw new BusinessException("不能绑定自己");
        }
        user.setPartnerId(partner.getId());
        partner.setPartnerId(userId);
        userMapper.updateById(user);
        userMapper.updateById(partner);
    }
}
