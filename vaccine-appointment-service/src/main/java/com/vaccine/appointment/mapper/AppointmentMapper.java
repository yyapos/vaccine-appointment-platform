package com.vaccine.appointment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vaccine.appointment.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}