package com.couple.app.controller;

import com.couple.app.common.CurrentUser;
import com.couple.app.common.Result;
import com.couple.app.entity.EventPhoto;
import com.couple.app.service.EventPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/event-photo")
public class EventPhotoController {

    @Autowired
    private EventPhotoService eventPhotoService;

    @GetMapping("/{eventId}")
    public Result<List<EventPhoto>> listByEvent(@PathVariable Long eventId) {
        return Result.success(eventPhotoService.listByEvent(CurrentUser.get(), eventId));
    }

    @PostMapping("/upload")
    public Result<EventPhoto> upload(@RequestParam("file") MultipartFile file,
                                     @RequestParam("eventId") Long eventId,
                                     @RequestParam("occurrenceDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate occurrenceDate) throws IOException {
        return Result.success(eventPhotoService.upload(CurrentUser.get(), eventId, file, occurrenceDate));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        eventPhotoService.delete(CurrentUser.get(), id);
        return Result.success();
    }
}
