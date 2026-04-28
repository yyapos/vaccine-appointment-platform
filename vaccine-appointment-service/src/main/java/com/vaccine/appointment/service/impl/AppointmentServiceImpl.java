package com.vaccine.appointment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vaccine.appointment.entity.Appointment;
import com.vaccine.appointment.mapper.AppointmentMapper;
import com.vaccine.appointment.service.AppointmentService;
import com.vaccine.appointment.vo.AppointmentVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    private final com.vaccine.appointment.feign.ChildClient childClient;
    private final com.vaccine.appointment.feign.VaccineClient vaccineClient;
    private final com.vaccine.appointment.feign.SettingsClient settingsClient;
    private final com.vaccine.appointment.feign.ParentClient parentClient;

    public AppointmentServiceImpl(
            com.vaccine.appointment.feign.ChildClient childClient,
            com.vaccine.appointment.feign.VaccineClient vaccineClient,
            com.vaccine.appointment.feign.SettingsClient settingsClient,
            com.vaccine.appointment.feign.ParentClient parentClient) {
        this.childClient = childClient;
        this.vaccineClient = vaccineClient;
        this.settingsClient = settingsClient;
        this.parentClient = parentClient;
    }

    @Override
    public AppointmentVO createAppointment(Appointment appointment) {
        System.out.println("========== 开始创建预约 ==========");
        System.out.println("预约信息: " + appointment);
        System.out.println("childId: " + appointment.getChildId());
        System.out.println("vaccineId: " + appointment.getVaccineId());
        System.out.println("appointmentDate: " + appointment.getAppointmentDate());

        // 获取儿童信息
        com.vaccine.common.result.Result<com.vaccine.child.entity.Child> childResult = childClient.getChildById(appointment.getChildId());
        System.out.println("获取到的儿童结果: " + childResult);

        if (childResult == null || childResult.getCode() != 200 || childResult.getData() == null) {
            throw new RuntimeException("儿童不存在");
        }

        com.vaccine.child.entity.Child child = childResult.getData();
        System.out.println("获取到的儿童信息: " + child);

        System.out.println("儿童出生日期: " + child.getBirthDate());
        System.out.println("儿童出生日期类型: " + (child.getBirthDate() != null ? child.getBirthDate().getClass().getName() : "null"));

        // 获取疫苗信息
        com.vaccine.common.result.Result<com.vaccine.vaccine.entity.Vaccine> vaccineResult = vaccineClient.getVaccineById(appointment.getVaccineId());
        if (vaccineResult == null || vaccineResult.getCode() != 200 || vaccineResult.getData() == null) {
            throw new RuntimeException("疫苗不存在");
        }
        com.vaccine.vaccine.entity.Vaccine vaccine = vaccineResult.getData();

        // 校验出生日期
        if (child.getBirthDate() == null) {
            System.out.println("错误：儿童出生日期为null");
            System.out.println("儿童完整信息: " + child);
            throw new RuntimeException("儿童出生日期信息不完整");
        }

        // 获取预约设置
        com.vaccine.common.result.Result<com.vaccine.common.entity.AppointmentSettings> settingsResult = settingsClient.getAppointmentSettings();
        if (settingsResult == null || settingsResult.getCode() != 200 || settingsResult.getData() == null) {
            throw new RuntimeException("预约功能已关闭，请稍后再试");
        }
        com.vaccine.common.entity.AppointmentSettings appointmentSettings = settingsResult.getData();
        if (appointmentSettings.getEnabled() == 0) {
            throw new RuntimeException("预约功能已关闭，请稍后再试");
        }

        // 计算孩子当前年龄（岁）= 今年 - 出生年
        java.time.LocalDate birthDate = child.getBirthDate();
        int currentYear = java.time.LocalDate.now().getYear();
        int birthYear = birthDate.getYear();
        int childAgeYears = currentYear - birthYear;

        System.out.println("当前年龄计算: 当前年份=" + currentYear + ", 出生年份=" + birthYear + ", 年龄=" + childAgeYears + "岁");

        // 校验孩子年龄 >= 疫苗可接种年龄
        if (vaccine.getTargetAge() != null && childAgeYears < vaccine.getTargetAge()) {
            throw new RuntimeException("年龄不符合接种要求，该疫苗要求至少" + vaccine.getTargetAge() + "岁");
        }

        // 校验预约日期是否在允许的提前天数范围内
        java.time.LocalDate appointmentDate = appointment.getAppointmentDate();
        long daysBetween = java.time.LocalDate.now().until(appointmentDate).getDays();
        if (daysBetween < 0) {
            throw new RuntimeException("预约日期不能早于今天");
        }
        if (daysBetween > appointmentSettings.getAdvanceDays()) {
            throw new RuntimeException("预约日期超出允许范围，最多只能提前" + appointmentSettings.getAdvanceDays() + "天预约");
        }

        // 校验当天的预约数量是否超过最大预约数
        long todayAppointmentCount = this.count(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getAppointmentDate, appointmentDate)
                .notIn(Appointment::getStatus, 2, 4)); // 不包括已拒绝、已取消的预约

        if (todayAppointmentCount >= appointmentSettings.getMaxDailyAppointments()) {
            throw new RuntimeException("当天预约数量已达上限，请选择其他日期");
        }

        // 预约状态：待审核(0)、已通过(1)、已拒绝(2)、已完成(3)、已取消(4)
        appointment.setStatus(0); // 待审核
        
        // 如果前端没有传递doseNumber，默认为1
        if (appointment.getDoseNumber() == null) {
            appointment.setDoseNumber(1);
        }
        
        this.save(appointment);
        System.out.println("========== 预约创建成功 ==========");
        
        // 返回包含完整信息的 AppointmentVO
        com.vaccine.appointment.vo.AppointmentVO vo = new com.vaccine.appointment.vo.AppointmentVO();
        vo.setId(appointment.getId());
        vo.setChildId(appointment.getChildId());
        vo.setVaccineId(appointment.getVaccineId());
        vo.setDoseNumber(appointment.getDoseNumber());
        vo.setAppointmentDate(appointment.getAppointmentDate());
        vo.setTimeSlot(appointment.getTimeSlot());
        vo.setStatus(appointment.getStatus());
        vo.setCancelReason(appointment.getCancelReason());
        vo.setNotes(appointment.getNotes());
        vo.setCreateTime(appointment.getCreateTime());
        vo.setUpdateTime(appointment.getUpdateTime());
        
        // 关联查询儿童信息
        try {
            if (child != null) {
                vo.setChildName(child.getName());
                vo.setChildGender(child.getGender());
                vo.setChildBirthDate(child.getBirthDate());
                vo.setIsKeyChild(child.getIsKeyChild());
                vo.setParentId(child.getParentId());
            }
        } catch (Exception e) {
            System.err.println("查询儿童信息失败: " + e.getMessage());
        }
        
        // 关联查询疫苗信息
        try {
            if (vaccine != null) {
                vo.setVaccineName(vaccine.getName());
                vo.setManufacturer(vaccine.getManufacturer());
            }
        } catch (Exception e) {
            System.err.println("查询疫苗信息失败: " + e.getMessage());
        }
        
        // 关联查询家长信息
        try {
            if (child != null && child.getParentId() != null) {
                com.vaccine.common.result.Result<com.vaccine.parent.entity.Parent> parentResult = parentClient.getParentById(child.getParentId());
                if (parentResult != null && parentResult.getCode() == 200 && parentResult.getData() != null) {
                    vo.setParentPhone(parentResult.getData().getPhone());
                    vo.setParentName(parentResult.getData().getNickname());
                }
            }
        } catch (Exception e) {
            System.err.println("查询家长信息失败: " + e.getMessage());
        }
        
        return vo;
    }

    @Override
    public boolean cancelAppointment(Long id, Long parentId, String reason) {
        Appointment appointment = this.getById(id);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (parentId == null) {
            throw new RuntimeException("无权限取消该预约");
        }
        com.vaccine.common.result.Result<com.vaccine.child.entity.Child> childResult = childClient.getChildById(appointment.getChildId());
        if (childResult == null || childResult.getCode() != 200 || childResult.getData() == null) {
            throw new RuntimeException("预约关联的儿童不存在");
        }
        Long appointmentParentId = childResult.getData().getParentId();
        if (appointmentParentId == null || !appointmentParentId.equals(parentId)) {
            throw new RuntimeException("无权限取消该预约");
        }
        if (appointment.getStatus() != 0 && appointment.getStatus() != 1) {
            throw new RuntimeException("只能取消待审核或已通过的预约");
        }
        appointment.setStatus(4); // 已取消
        appointment.setCancelReason(reason);
        return this.updateById(appointment);
    }

    @Override
    public boolean approveAppointment(Long id) {
        Appointment appointment = this.getById(id);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (appointment.getStatus() != 0) {
            throw new RuntimeException("只能审核待审核的预约");
        }
        appointment.setStatus(1); // 审核通过
        return this.updateById(appointment);
    }

    @Override
    public boolean rejectAppointment(Long id, String reason) {
        Appointment appointment = this.getById(id);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (appointment.getStatus() != 0) {
            throw new RuntimeException("只能审核待审核的预约");
        }
        appointment.setStatus(2); // 审核拒绝
        appointment.setCancelReason(reason);
        return this.updateById(appointment);
    }

    @Override
    public boolean completeAppointment(Long id) {
        Appointment appointment = this.getById(id);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (appointment.getStatus() != 1) {
            throw new RuntimeException("只能完成已通过的预约");
        }
        appointment.setStatus(3); // 已接种（改为状态3）
        return this.updateById(appointment);
    }

    @Override
    public List<com.vaccine.appointment.vo.AppointmentVO> getAppointmentsByChildId(Long childId) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getChildId, childId);
        wrapper.orderByDesc(Appointment::getCreateTime);
        List<Appointment> appointments = this.list(wrapper);

        // 转换为VO对象，并关联查询儿童、疫苗和家长信息
        return appointments.stream().map(appointment -> {
            com.vaccine.appointment.vo.AppointmentVO vo = new com.vaccine.appointment.vo.AppointmentVO();

            // 复制基础字段
            vo.setId(appointment.getId());
            vo.setChildId(appointment.getChildId());
            vo.setVaccineId(appointment.getVaccineId());
            vo.setDoseNumber(appointment.getDoseNumber());
            vo.setTimeSlot(appointment.getTimeSlot());
            vo.setStatus(appointment.getStatus());
            vo.setCancelReason(appointment.getCancelReason());
            vo.setNotes(appointment.getNotes());
            vo.setAppointmentDate(appointment.getAppointmentDate());
            vo.setCreateTime(appointment.getCreateTime());
            vo.setUpdateTime(appointment.getUpdateTime());

            // 设置状态文本
            vo.setStatusText(getStatusText(appointment.getStatus()));

            // 关联查询儿童信息
            try {
                com.vaccine.common.result.Result<com.vaccine.child.entity.Child> childResult = childClient.getChildById(appointment.getChildId());
                if (childResult != null && childResult.getCode() == 200 && childResult.getData() != null) {
                    com.vaccine.child.entity.Child child = childResult.getData();
                    vo.setChildName(child.getName());
                    vo.setChildGender(child.getGender());
                    vo.setChildBirthDate(child.getBirthDate());
                    vo.setIsKeyChild(child.getIsKeyChild());
                    vo.setParentId(child.getParentId());
                }
            } catch (Exception e) {
                System.err.println("查询儿童信息失败: " + e.getMessage());
            }

            // 关联查询疫苗信息
            try {
                com.vaccine.common.result.Result<com.vaccine.vaccine.entity.Vaccine> vaccineResult = vaccineClient.getVaccineById(appointment.getVaccineId());
                if (vaccineResult != null && vaccineResult.getCode() == 200 && vaccineResult.getData() != null) {
                    vo.setVaccineName(vaccineResult.getData().getName());
                    vo.setManufacturer(vaccineResult.getData().getManufacturer());
                }
            } catch (Exception e) {
                System.err.println("查询疫苗信息失败: " + e.getMessage());
            }

            // 关联查询家长信息
            try {
                if (vo.getParentId() != null) {
                    com.vaccine.common.result.Result<com.vaccine.parent.entity.Parent> parentResult = parentClient.getParentById(vo.getParentId());
                    if (parentResult != null && parentResult.getCode() == 200 && parentResult.getData() != null) {
                        vo.setParentPhone(parentResult.getData().getPhone());
                        vo.setParentName(parentResult.getData().getNickname());
                    }
                }
            } catch (Exception e) {
                System.err.println("查询家长信息失败: " + e.getMessage());
            }

            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待审核";
            case 1: return "已通过";
            case 2: return "已拒绝";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }

    @Override
    public List<Appointment> getPendingReviewAppointments() {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getStatus, 0); // 待审核
        wrapper.orderByAsc(Appointment::getAppointmentDate);
        return this.list(wrapper);
    }

    @Override
    public Map<String, Object> getAppointmentPage(Integer page, Integer pageSize, Integer status, Long childId, String childName, String vaccineName, String createTimeStart, String createTimeEnd) {
        Page<Appointment> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        if (childId != null) {
            wrapper.eq(Appointment::getChildId, childId);
        }

        // 添加时间过滤
        if (createTimeStart != null && !createTimeStart.isEmpty()) {
            wrapper.ge(Appointment::getCreateTime, java.time.LocalDateTime.parse(createTimeStart + "T00:00:00"));
        }
        if (createTimeEnd != null && !createTimeEnd.isEmpty()) {
            wrapper.le(Appointment::getCreateTime, java.time.LocalDateTime.parse(createTimeEnd + "T23:59:59"));
        }

        wrapper.orderByDesc(Appointment::getCreateTime);

        Page<Appointment> resultPage = this.page(pageParam, wrapper);

        // 转换为VO对象，并关联查询儿童、疫苗和家长信息
        List<com.vaccine.appointment.vo.AppointmentVO> voList = resultPage.getRecords().stream().map(appointment -> {
            com.vaccine.appointment.vo.AppointmentVO vo = new com.vaccine.appointment.vo.AppointmentVO();

            // 复制基础字段
            vo.setId(appointment.getId());
            vo.setChildId(appointment.getChildId());
            vo.setVaccineId(appointment.getVaccineId());
            vo.setDoseNumber(appointment.getDoseNumber());
            vo.setTimeSlot(appointment.getTimeSlot());
            vo.setStatus(appointment.getStatus());
            vo.setCancelReason(appointment.getCancelReason());
            vo.setNotes(appointment.getNotes());
            vo.setAppointmentDate(appointment.getAppointmentDate());
            vo.setCreateTime(appointment.getCreateTime());
            vo.setUpdateTime(appointment.getUpdateTime());

            // 设置状态文本
            vo.setStatusText(getStatusText(appointment.getStatus()));

            // 关联查询儿童信息
            try {
                com.vaccine.common.result.Result<com.vaccine.child.entity.Child> childResult = childClient.getChildById(appointment.getChildId());
                if (childResult != null && childResult.getCode() == 200 && childResult.getData() != null) {
                    com.vaccine.child.entity.Child child = childResult.getData();
                    vo.setChildName(child.getName());
                    vo.setChildGender(child.getGender());
                    vo.setChildBirthDate(child.getBirthDate());
                    vo.setIsKeyChild(child.getIsKeyChild());
                    vo.setParentId(child.getParentId());
                }
            } catch (Exception e) {
                System.err.println("查询儿童信息失败: " + e.getMessage());
            }

            // 关联查询疫苗信息
            try {
                com.vaccine.common.result.Result<com.vaccine.vaccine.entity.Vaccine> vaccineResult = vaccineClient.getVaccineById(appointment.getVaccineId());
                if (vaccineResult != null && vaccineResult.getCode() == 200 && vaccineResult.getData() != null) {
                    vo.setVaccineName(vaccineResult.getData().getName());
                    vo.setManufacturer(vaccineResult.getData().getManufacturer());
                }
            } catch (Exception e) {
                System.err.println("查询疫苗信息失败: " + e.getMessage());
            }

            // 关联查询家长信息
            try {
                if (vo.getParentId() != null) {
                    com.vaccine.common.result.Result<com.vaccine.parent.entity.Parent> parentResult = parentClient.getParentById(vo.getParentId());
                    if (parentResult != null && parentResult.getCode() == 200 && parentResult.getData() != null) {
                        vo.setParentPhone(parentResult.getData().getPhone());
                        vo.setParentName(parentResult.getData().getNickname());
                    }
                }
            } catch (Exception e) {
                System.err.println("查询家长信息失败: " + e.getMessage());
            }

            return vo;
        }).collect(java.util.stream.Collectors.toList());

        // 按儿童姓名和疫苗名称过滤
        if (childName != null && !childName.trim().isEmpty()) {
            voList = voList.stream()
                    .filter(vo -> vo.getChildName() != null && vo.getChildName().contains(childName))
                    .collect(java.util.stream.Collectors.toList());
        }
        if (vaccineName != null && !vaccineName.trim().isEmpty()) {
            voList = voList.stream()
                    .filter(vo -> vo.getVaccineName() != null && vo.getVaccineName().contains(vaccineName))
                    .collect(java.util.stream.Collectors.toList());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("records", voList);
        data.put("total", voList.size());  // 注意：这里改为过滤后的总数
        data.put("current", pageParam.getCurrent());
        data.put("size", pageParam.getSize());
        data.put("pages", (int) Math.ceil((double) voList.size() / pageParam.getSize()));

        return data;
    }
}