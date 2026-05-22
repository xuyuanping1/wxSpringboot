package com.couple.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.couple.app.common.BusinessException;
import com.couple.app.dto.PhotoOrderDTO;
import com.couple.app.entity.Photo;
import com.couple.app.mapper.PhotoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PhotoService {

    @Autowired
    private PhotoMapper photoMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private AlbumService albumService;

    public List<Photo> listByAlbum(Long userId, Long albumId) {
        albumService.getAlbum(userId, albumId);
        return photoMapper.selectList(
                new LambdaQueryWrapper<Photo>()
                        .eq(Photo::getAlbumId, albumId)
                        .orderByAsc(Photo::getSortOrder)
                        .orderByDesc(Photo::getUploadTime)
        );
    }

    public void reorder(Long userId, List<PhotoOrderDTO> orders) {
        for (PhotoOrderDTO order : orders) {
            Photo photo = photoMapper.selectById(order.getId());
            if (photo == null) continue;
            albumService.getAlbum(userId, photo.getAlbumId());
            photo.setSortOrder(order.getSortOrder());
            photoMapper.updateById(photo);
        }
    }

    public Photo upload(Long userId, Long albumId, MultipartFile file) throws IOException {
        albumService.getAlbum(userId, albumId);

        String url = fileService.upload(file);
        String thumbnailUrl = fileService.generateThumbnail(url);

        Photo photo = new Photo();
        photo.setAlbumId(albumId);
        photo.setUrl(url);
        photo.setThumbnailUrl(thumbnailUrl);
        photo.setUploadTime(LocalDateTime.now());
        photoMapper.insert(photo);

        return photo;
    }

    public void delete(Long userId, Long photoId) {
        Photo photo = photoMapper.selectById(photoId);
        if (photo == null) {
            throw new BusinessException("照片不存在");
        }
        albumService.getAlbum(userId, photo.getAlbumId());
        fileService.deleteFile(photo.getUrl());
        fileService.deleteFile(photo.getThumbnailUrl());
        photoMapper.deleteById(photoId);
    }
}
