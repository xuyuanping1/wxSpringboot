package com.couple.app.controller;

import com.couple.app.common.CurrentUser;
import com.couple.app.common.Result;
import com.couple.app.dto.AlbumCreateDTO;
import com.couple.app.entity.Album;
import com.couple.app.entity.Photo;
import com.couple.app.service.AlbumService;
import com.couple.app.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;

    @GetMapping("/list")
    public Result<List<Album>> list() {
        return Result.success(albumService.listByUser(CurrentUser.get()));
    }

    @PostMapping
    public Result<Album> create(@Valid @RequestBody AlbumCreateDTO dto) {
        return Result.success(albumService.create(CurrentUser.get(), dto));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody AlbumCreateDTO dto) {
        albumService.update(CurrentUser.get(), id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        albumService.delete(CurrentUser.get(), id);
        return Result.success();
    }

    @GetMapping("/{id}/photos")
    public Result<List<Photo>> listPhotos(@PathVariable Long id) {
        return Result.success(photoService.listByAlbum(CurrentUser.get(), id));
    }
}
