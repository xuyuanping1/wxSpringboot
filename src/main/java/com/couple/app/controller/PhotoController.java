package com.couple.app.controller;

import com.couple.app.common.CurrentUser;
import com.couple.app.common.Result;
import com.couple.app.dto.PhotoOrderDTO;
import com.couple.app.entity.Photo;
import com.couple.app.service.FileService;
import com.couple.app.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public Result<?> upload(@RequestParam("file") MultipartFile file,
                            @RequestParam(value = "albumId", required = false) Long albumId) throws IOException {
        if (albumId != null) {
            return Result.success(photoService.upload(CurrentUser.get(), albumId, file));
        }
        // 仅上传文件，不创建 Photo 记录（用于封面等场景）
        String url = fileService.upload(file);
        java.util.Map<String, String> result = new java.util.HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        photoService.delete(CurrentUser.get(), id);
        return Result.success();
    }

    @PutMapping("/reorder")
    public Result<Void> reorder(@RequestBody List<PhotoOrderDTO> orders) {
        photoService.reorder(CurrentUser.get(), orders);
        return Result.success();
    }
}
