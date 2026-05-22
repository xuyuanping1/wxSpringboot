package com.couple.app.controller;

import com.couple.app.common.CurrentUser;
import com.couple.app.common.Result;
import com.couple.app.entity.Anniversary;
import com.couple.app.service.AnniversaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/anniversary")
public class AnniversaryController {

    @Autowired
    private AnniversaryService anniversaryService;

    @GetMapping
    public Result<Anniversary> getAnniversary() {
        return Result.success(anniversaryService.getAnniversary(CurrentUser.get()));
    }

    @PutMapping
    public Result<Void> update(@RequestBody Map<String, String> body) {
        anniversaryService.update(CurrentUser.get(), body);
        return Result.success();
    }
}
