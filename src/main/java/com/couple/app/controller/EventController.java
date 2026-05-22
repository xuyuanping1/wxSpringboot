package com.couple.app.controller;

import com.couple.app.common.CurrentUser;
import com.couple.app.common.Result;
import com.couple.app.dto.EventCreateDTO;
import com.couple.app.dto.EventUpdateDTO;
import com.couple.app.entity.Event;
import com.couple.app.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/list")
    public Result<List<Event>> list() {
        return Result.success(eventService.listByUser(CurrentUser.get()));
    }

    @PostMapping
    public Result<Event> create(@Valid @RequestBody EventCreateDTO dto) {
        return Result.success(eventService.create(CurrentUser.get(), dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody EventUpdateDTO dto) {
        eventService.update(CurrentUser.get(), id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        eventService.delete(CurrentUser.get(), id);
        return Result.success();
    }
}
