package com.couple.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.couple.app.common.BusinessException;
import com.couple.app.dto.EventCreateDTO;
import com.couple.app.dto.EventUpdateDTO;
import com.couple.app.entity.Event;
import com.couple.app.mapper.EventMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private EventPhotoService eventPhotoService;

    public List<Event> listByUser(Long userId) {
        List<Long> userIds = userService.getCoupleUserIds(userId);
        return eventMapper.selectList(
                new LambdaQueryWrapper<Event>()
                        .in(Event::getUserId, userIds)
                        .orderByAsc(Event::getTargetDate)
        );
    }

    public Event create(Long userId, EventCreateDTO dto) {
        Event event = new Event();
        BeanUtils.copyProperties(dto, event);
        event.setUserId(userId);
        eventMapper.insert(event);
        return event;
    }

    public void update(Long userId, Long eventId, EventUpdateDTO dto) {
        Event event = eventMapper.selectById(eventId);
        List<Long> userIds = userService.getCoupleUserIds(userId);
        if (event == null || !userIds.contains(event.getUserId())) {
            throw new BusinessException("事件不存在");
        }
        if (dto.getName() != null) event.setName(dto.getName());
        if (dto.getTargetDate() != null) event.setTargetDate(dto.getTargetDate());
        if (dto.getRepeatType() != null) event.setRepeatType(dto.getRepeatType());
        if (dto.getRemark() != null) event.setRemark(dto.getRemark());
        if (dto.getBackgroundImage() != null) event.setBackgroundImage(dto.getBackgroundImage());
        if (dto.getBackgroundBlur() != null) event.setBackgroundBlur(dto.getBackgroundBlur());
        if (dto.getBackgroundPositionX() != null) event.setBackgroundPositionX(dto.getBackgroundPositionX());
        if (dto.getBackgroundPositionY() != null) event.setBackgroundPositionY(dto.getBackgroundPositionY());
        if (dto.getBackgroundScale() != null) event.setBackgroundScale(dto.getBackgroundScale());
        if (dto.getSortOrder() != null) event.setSortOrder(dto.getSortOrder());
        eventMapper.updateById(event);
    }

    public void delete(Long userId, Long eventId) {
        Event event = eventMapper.selectById(eventId);
        List<Long> userIds = userService.getCoupleUserIds(userId);
        if (event == null || !userIds.contains(event.getUserId())) {
            throw new BusinessException("事件不存在");
        }
        eventPhotoService.deleteByEventId(eventId);
        eventMapper.deleteById(eventId);
    }
}
