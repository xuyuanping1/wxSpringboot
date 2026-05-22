package com.couple.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.couple.app.common.BusinessException;
import com.couple.app.entity.Event;
import com.couple.app.entity.EventPhoto;
import com.couple.app.mapper.EventMapper;
import com.couple.app.mapper.EventPhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class EventPhotoService {

    @Autowired
    private EventPhotoMapper eventPhotoMapper;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    public List<EventPhoto> listByEvent(Long userId, Long eventId) {
        Event event = getAndVerifyEvent(userId, eventId);
        return eventPhotoMapper.selectList(
                new LambdaQueryWrapper<EventPhoto>()
                        .eq(EventPhoto::getEventId, eventId)
                        .orderByDesc(EventPhoto::getOccurrenceDate)
                        .orderByAsc(EventPhoto::getUploadTime)
        );
    }

    public EventPhoto upload(Long userId, Long eventId, MultipartFile file, LocalDate occurrenceDate) throws IOException {
        Event event = getAndVerifyEvent(userId, eventId);

        if (occurrenceDate.isAfter(LocalDate.now())) {
            throw new BusinessException("不能上传未来日期的照片");
        }

        long count = eventPhotoMapper.selectCount(
                new LambdaQueryWrapper<EventPhoto>()
                        .eq(EventPhoto::getEventId, eventId)
                        .eq(EventPhoto::getOccurrenceDate, occurrenceDate)
        );
        if (count >= 4) {
            throw new BusinessException("该日期最多上传4张照片");
        }

        String url = fileService.upload(file);
        String thumbnailUrl = fileService.generateThumbnail(url);

        EventPhoto photo = new EventPhoto();
        photo.setEventId(eventId);
        photo.setOccurrenceDate(occurrenceDate);
        photo.setUrl(url);
        photo.setThumbnailUrl(thumbnailUrl);
        eventPhotoMapper.insert(photo);
        return photo;
    }

    public void delete(Long userId, Long photoId) {
        EventPhoto photo = eventPhotoMapper.selectById(photoId);
        if (photo == null) {
            throw new BusinessException("照片不存在");
        }
        getAndVerifyEvent(userId, photo.getEventId());
        fileService.deleteFile(photo.getUrl());
        fileService.deleteFile(photo.getThumbnailUrl());
        eventPhotoMapper.deleteById(photoId);
    }

    public void deleteByEventId(Long eventId) {
        List<EventPhoto> photos = eventPhotoMapper.selectList(
                new LambdaQueryWrapper<EventPhoto>()
                        .eq(EventPhoto::getEventId, eventId)
        );
        for (EventPhoto photo : photos) {
            fileService.deleteFile(photo.getUrl());
            fileService.deleteFile(photo.getThumbnailUrl());
        }
        if (!photos.isEmpty()) {
            eventPhotoMapper.delete(
                    new LambdaQueryWrapper<EventPhoto>()
                            .eq(EventPhoto::getEventId, eventId)
            );
        }
    }

    private Event getAndVerifyEvent(Long userId, Long eventId) {
        Event event = eventMapper.selectById(eventId);
        List<Long> userIds = userService.getCoupleUserIds(userId);
        if (event == null || !userIds.contains(event.getUserId())) {
            throw new BusinessException("事件不存在");
        }
        return event;
    }
}
