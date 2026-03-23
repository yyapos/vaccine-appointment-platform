package com.vaccine.vaccine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vaccine.vaccine.entity.Vaccine;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VaccineMapper extends BaseMapper<Vaccine> {
}