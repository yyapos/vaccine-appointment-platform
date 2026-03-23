package com.vaccine.appointment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vaccine.appointment.entity.VaccinationRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VaccinationRecordMapper extends BaseMapper<VaccinationRecord> {
}