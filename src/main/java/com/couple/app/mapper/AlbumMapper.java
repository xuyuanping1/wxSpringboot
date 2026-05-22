package com.couple.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.couple.app.entity.Album;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlbumMapper extends BaseMapper<Album> {
}
