package com.couple.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.couple.app.common.BusinessException;
import com.couple.app.entity.Anniversary;
import com.couple.app.mapper.AnniversaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AnniversaryService {

    @Autowired
    private AnniversaryMapper anniversaryMapper;

    @Autowired
    private UserService userService;

    public Anniversary getAnniversary(Long userId) {
        java.util.List<Long> userIds = userService.getCoupleUserIds(userId);
        Anniversary anniversary = anniversaryMapper.selectOne(
                new LambdaQueryWrapper<Anniversary>().in(Anniversary::getUserId, userIds).last("LIMIT 1")
        );
        if (anniversary == null) {
            throw new BusinessException("尚未设置纪念日");
        }
        return anniversary;
    }

    public Anniversary createIfAbsent(Long userId, LocalDate startDate) {
        java.util.List<Long> userIds = userService.getCoupleUserIds(userId);
        Anniversary anniversary = anniversaryMapper.selectOne(
                new LambdaQueryWrapper<Anniversary>().in(Anniversary::getUserId, userIds).last("LIMIT 1")
        );
        if (anniversary == null) {
            anniversary = new Anniversary();
            anniversary.setUserId(userId);
            anniversary.setStartDate(startDate);
            anniversaryMapper.insert(anniversary);
        } else if (startDate != null) {
            anniversary.setStartDate(startDate);
            anniversaryMapper.updateById(anniversary);
        }
        return anniversary;
    }

    public void update(Long userId, java.util.Map<String, String> body) {
        Anniversary anniversary = getAnniversary(userId);
        if (body.containsKey("backgroundImage")) {
            anniversary.setBackgroundImage(body.get("backgroundImage"));
        }
        if (body.containsKey("cardBgBlur")) {
            String v = body.get("cardBgBlur");
            anniversary.setCardBgBlur(v != null && !v.isEmpty() ? Integer.parseInt(v) : 0);
        }
        if (body.containsKey("homeBgImage")) {
            anniversary.setHomeBgImage(body.get("homeBgImage"));
        }
        if (body.containsKey("homeBgBlur")) {
            String v = body.get("homeBgBlur");
            anniversary.setHomeBgBlur(v != null && !v.isEmpty() ? Integer.parseInt(v) : 0);
        }
        if (body.containsKey("customTitle")) {
            anniversary.setCustomTitle(body.get("customTitle"));
        }
        if (body.containsKey("textColor")) {
            anniversary.setTextColor(body.get("textColor"));
        }
        if (body.containsKey("cardTextColor")) {
            anniversary.setCardTextColor(body.get("cardTextColor"));
        }
        if (body.containsKey("cardBgPositionX")) {
            String v = body.get("cardBgPositionX");
            anniversary.setCardBgPositionX(v != null && !v.isEmpty() ? Integer.parseInt(v) : 50);
        }
        if (body.containsKey("cardBgPositionY")) {
            String v = body.get("cardBgPositionY");
            anniversary.setCardBgPositionY(v != null && !v.isEmpty() ? Integer.parseInt(v) : 50);
        }
        if (body.containsKey("cardBgScale")) {
            String v = body.get("cardBgScale");
            anniversary.setCardBgScale(v != null && !v.isEmpty() ? Integer.parseInt(v) : 100);
        }
        if (body.containsKey("homeBgPositionX")) {
            String v = body.get("homeBgPositionX");
            anniversary.setHomeBgPositionX(v != null && !v.isEmpty() ? Integer.parseInt(v) : 50);
        }
        if (body.containsKey("homeBgPositionY")) {
            String v = body.get("homeBgPositionY");
            anniversary.setHomeBgPositionY(v != null && !v.isEmpty() ? Integer.parseInt(v) : 50);
        }
        if (body.containsKey("homeBgScale")) {
            String v = body.get("homeBgScale");
            anniversary.setHomeBgScale(v != null && !v.isEmpty() ? Integer.parseInt(v) : 100);
        }
        anniversaryMapper.updateById(anniversary);
    }
}
