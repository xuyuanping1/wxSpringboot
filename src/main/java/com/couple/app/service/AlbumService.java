package com.couple.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.couple.app.common.BusinessException;
import com.couple.app.dto.AlbumCreateDTO;
import com.couple.app.entity.Album;
import com.couple.app.entity.Photo;
import com.couple.app.mapper.AlbumMapper;
import com.couple.app.mapper.PhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlbumService {

    @Autowired
    private AlbumMapper albumMapper;

    @Autowired
    private PhotoMapper photoMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    public List<Album> listByUser(Long userId) {
        List<Long> userIds = userService.getCoupleUserIds(userId);
        List<Album> albums = albumMapper.selectList(
                new LambdaQueryWrapper<Album>()
                        .in(Album::getUserId, userIds)
                        .orderByDesc(Album::getCreatedAt)
        );
        // 没有封面的相册，自动使用第一张照片作为封面
        for (Album album : albums) {
            if (album.getCoverImage() == null || album.getCoverImage().isEmpty()) {
                Photo first = photoMapper.selectOne(
                        new LambdaQueryWrapper<Photo>()
                                .eq(Photo::getAlbumId, album.getId())
                                .orderByAsc(Photo::getUploadTime)
                                .last("LIMIT 1")
                );
                if (first != null) {
                    album.setCoverImage(first.getThumbnailUrl() != null ? first.getThumbnailUrl() : first.getUrl());
                }
            }
        }
        return albums;
    }

    public Album create(Long userId, AlbumCreateDTO dto) {
        Album album = new Album();
        album.setUserId(userId);
        album.setName(dto.getName());
        album.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        album.setCoverImage(dto.getCoverImage() != null ? dto.getCoverImage() : "");
        album.setCoverBlur(dto.getCoverBlur() != null ? dto.getCoverBlur() : 0);
        albumMapper.insert(album);
        return album;
    }

    public void update(Long userId, Long albumId, AlbumCreateDTO dto) {
        Album album = getAlbum(userId, albumId);
        if (dto.getName() != null) album.setName(dto.getName());
        if (dto.getDescription() != null) album.setDescription(dto.getDescription());
        if (dto.getCoverImage() != null) album.setCoverImage(dto.getCoverImage());
        if (dto.getCoverBlur() != null) album.setCoverBlur(dto.getCoverBlur());
        albumMapper.updateById(album);
    }

    @Transactional
    public void delete(Long userId, Long albumId) {
        Album album = getAlbum(userId, albumId);
        // 删除相册内所有照片文件
        List<Photo> photos = photoMapper.selectList(
                new LambdaQueryWrapper<Photo>().eq(Photo::getAlbumId, albumId)
        );
        for (Photo photo : photos) {
            fileService.deleteFile(photo.getUrl());
            fileService.deleteFile(photo.getThumbnailUrl());
        }
        photoMapper.delete(new LambdaQueryWrapper<Photo>().eq(Photo::getAlbumId, albumId));
        albumMapper.deleteById(albumId);
    }

    public Album getAlbum(Long userId, Long albumId) {
        Album album = albumMapper.selectById(albumId);
        List<Long> userIds = userService.getCoupleUserIds(userId);
        if (album == null || !userIds.contains(album.getUserId())) {
            throw new BusinessException("相册不存在");
        }
        return album;
    }
}
