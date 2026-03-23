package com.vaccine.parent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vaccine.parent.entity.Parent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParentMapper extends BaseMapper<Parent> {
}