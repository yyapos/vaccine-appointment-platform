package com.vaccine.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vaccine.forum.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ForumCommentMapper extends BaseMapper<ForumComment> {
}