package com.vaccine.notify.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vaccine.appointment.entity.Appointment;
import com.vaccine.child.entity.Child;
import com.vaccine.common.entity.ReminderSettings;
import com.vaccine.common.result.Result;
import com.vaccine.notify.entity.Reminder;
import com.vaccine.notify.feign.AppointmentClient;
import com.vaccine.notify.feign.ChildClient;
import com.vaccine.notify.feign.ParentClient;
import com.vaccine.notify.feign.VaccineClient;
import com.vaccine.notify.service.ReminderService;
import com.vaccine.parent.entity.Parent;
import com.vaccine.vaccine.entity.Vaccine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 极简版疫苗接种提醒定时任务
 * 只实现两个核心场景：
 * 1. 年龄到期提醒：孩子年龄到了疫苗可接种年龄
 * 2. 预约后提醒：预约审核通过后，提前3天+1天提醒接种
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VaccinationReminderScheduler {

    private final AppointmentClient appointmentClient;
    private final ReminderService reminderService;
    private final ChildClient childClient;
    private final VaccineClient vaccineClient;
    private final ParentClient parentClient;
    private final com.vaccine.notify.feign.SettingsClient settingsClient;

//    /**
//     * 测试定时任务 - 每10秒执行一次
//     * 用于验证定时任务是否正常工作
//     */
//    @Scheduled(fixedRate = 10000)
//    public void testScheduler() {
//        log.info("定时任务测试 - 当前时间: {}", java.time.LocalDateTime.now());
//    }
//
//    /**
//     * 每分钟检查一次提醒设置的时间，如果到了设置的时间就执行提醒扫描
//     * 这样可以支持后台动态修改提醒时间
//     */
    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkAllReminders() {
        log.info("检查提醒设置开始 - 当前时间: {}", java.time.LocalDateTime.now());
        try {
            // 获取提醒设置
            Result<ReminderSettings> result = settingsClient.getReminderSettings();
            
            // 检查接口调用是否成功
            if (result == null || result.getCode() != 200) {
                log.error("获取提醒设置失败，接口返回异常: {}", result != null ? result.getMessage() : "null");
                return;
            }
            
            com.vaccine.common.entity.ReminderSettings reminderSettings = result.getData();
            
            // 检查提醒设置是否存在，如果不存在则使用默认设置
            if (reminderSettings == null) {
                log.warn("提醒设置不存在，使用默认设置：启用提醒功能");
                // 继续执行，使用默认配置
            }
            
            // 检查是否启用提醒功能（处理 null 值）
            Integer enabled = reminderSettings != null ? reminderSettings.getEnabled() : null;
            if (enabled != null && enabled == 0) {
                log.info("提醒功能已关闭，跳过提醒扫描");
                return; // 提醒功能已关闭，静默返回
            }

            // 获取当前时间
            java.time.LocalTime currentTime = java.time.LocalTime.now();
            String currentTimeStr = currentTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

            // 检查是否到了设置的提醒时间
            String reminderTime = reminderSettings.getReminderTime();
            if (reminderTime == null || reminderTime.isEmpty()) {
                reminderTime = "01:00"; // 默认凌晨1点
            }

            log.info("当前时间: {}, 设置的提醒时间: {}", currentTimeStr, reminderTime);

            // 如果当前时间不等于设置的提醒时间，跳过
            if (!currentTimeStr.equals(reminderTime)) {
                log.info("时间未到，跳过提醒扫描");
                return;
            }

            // 到了提醒时间，执行提醒扫描
            log.info("到了设置的提醒时间({}), 开始执行疫苗接种提醒扫描", reminderTime);

            // 场景1：年龄到期提醒（接种提醒）
            if (reminderSettings.getVaccinationReminder() == 1) {
                checkAgeDueReminders(reminderSettings);
            } else {
                log.info("接种提醒已关闭，跳过年龄到期提醒");
            }

            // 场景2：预约后提醒（预约提醒）
            if (reminderSettings.getAppointmentReminder() == 1) {
                checkAppointmentReminders(reminderSettings);
            } else {
                log.info("预约提醒已关闭，跳过预约后提醒");
            }

            log.info("疫苗接种提醒扫描完成");
        } catch (Exception e) {
            log.error("检查提醒设置失败", e);
        }

    }

    /**
     * 场景1：年龄到期提醒
     * 触发条件：孩子当前年龄 = 某疫苗的「可接种年龄」
     */
    private void checkAgeDueReminders(com.vaccine.common.entity.ReminderSettings reminderSettings) {
        log.info("开始检查年龄到期提醒");

        try {
            // 获取所有儿童（实际项目中应该分页）
            List<Child> allChildren = getAllChildren();

            // 获取所有疫苗（实际项目中应该分页）
            List<Vaccine> allVaccines = getAllVaccines();

            int currentYear = LocalDate.now().getYear();

            for (Child child : allChildren) {
                if (child.getBirthDate() == null) {
                    continue;
                }

                // 极简逻辑：计算孩子当前年龄（岁）= 今年 - 出生年
                int birthYear = child.getBirthDate().getYear();
                int childAgeYears = currentYear - birthYear;

                for (Vaccine vaccine : allVaccines) {
                    if (vaccine.getTargetAge() == null) {
                        continue;
                    }

                    // 触发条件：孩子当前年龄 = 疫苗可接种年龄
                    if (childAgeYears == vaccine.getTargetAge()) {
                        // 检查是否已经提醒过（避免重复提醒）
                        if (hasAlreadyReminded(child.getId(), vaccine.getId(), "年龄到期")) {
                            continue;
                        }

                        // 检查孩子是否已经预约过该疫苗
                        if (hasAlreadyAppointed(child.getId(), vaccine.getId())) {
                            continue;
                        }

                        // 生成提醒
                        sendAgeDueReminder(child, vaccine, reminderSettings);
                    }
                }
            }

            log.info("年龄到期提醒检查完成");
        } catch (Exception e) {
            log.error("检查年龄到期提醒失败", e);
        }
    }

    /**
     * 场景2：预约后提醒
     * 触发条件：预约状态为「已通过」，且距离接种日期还有3天 / 还有1天
     */
    private void checkAppointmentReminders(com.vaccine.common.entity.ReminderSettings reminderSettings) {
        log.info("开始检查预约后提醒");

        try {
            // 获取所有已通过的预约
            List<Appointment> approvedAppointments = appointmentClient.listAppointments(
                1, // status: 已通过
                null, // childId
                null, // vaccineId
                LocalDate.now().toString() // appointmentDate >= today
            );

            LocalDate today = LocalDate.now();

            // 解析提醒天数设置（例如："3,1"）
            String[] reminderDays = reminderSettings.getReminderDaysBefore().split(",");

            // 获取所有有效的儿童和疫苗ID，用于过滤无效预约
            List<Child> allChildren = childClient.getAllChildren().getData();
            List<Vaccine> allVaccines = vaccineClient.getAllVaccines().getData();
            
            // 创建有效的ID集合（排除已删除的）
            java.util.Set<Long> validChildIds = allChildren.stream()
                .map(Child::getId)
                .collect(java.util.stream.Collectors.toSet());
            java.util.Set<Long> validVaccineIds = allVaccines.stream()
                .map(Vaccine::getId)
                .collect(java.util.stream.Collectors.toSet());

            log.info("有效儿童数量: {}, 有效疫苗数量: {}", validChildIds.size(), validVaccineIds.size());
            log.info("有效儿童ID列表: {}", validChildIds);
            log.info("有效疫苗ID列表: {}", validVaccineIds);

            for (Appointment appointment : approvedAppointments) {
                // 检查 childId 和 vaccineId 是否为 null
                if (appointment.getChildId() == null || appointment.getVaccineId() == null) {
                    log.warn("预约记录数据不完整，跳过提醒: appointmentId={}, childId={}, vaccineId={}",
                        appointment.getId(), appointment.getChildId(), appointment.getVaccineId());
                    continue;
                }

                // 检查 childId 和 vaccineId 是否有效（未被删除）
                if (!validChildIds.contains(appointment.getChildId())) {
                    log.warn("预约记录关联的儿童已被删除，跳过提醒: appointmentId={}, childId={}",
                        appointment.getId(), appointment.getChildId());
                    continue;
                }

                if (!validVaccineIds.contains(appointment.getVaccineId())) {
                    log.warn("预约记录关联的疫苗已被删除，跳过提醒: appointmentId={}, vaccineId={}",
                        appointment.getId(), appointment.getVaccineId());
                    continue;
                }

                // 计算距离接种日期的天数
                long daysUntil = today.until(appointment.getAppointmentDate()).getDays();

                // 检查是否在提醒天数范围内
                for (String dayStr : reminderDays) {
                    int days = Integer.parseInt(dayStr.trim());
                    if (daysUntil == days) {
                        // 检查是否已经提醒过
                        String reminderType = "预约提醒-" + days + "天";
                        if (hasAlreadyReminded(appointment.getChildId(), appointment.getVaccineId(), reminderType)) {
                            continue;
                        }

                        // 生成提醒
                        sendAppointmentReminder(appointment, days, reminderSettings);
                        break; // 避免同一天发送多次提醒
                    }
                }
            }

            log.info("预约后提醒检查完成");
        } catch (Exception e) {
            log.error("检查预约后提醒失败", e);
        }
    }

    /**
     * 发送年龄到期提醒
     */
    private void sendAgeDueReminder(Child child, Vaccine vaccine,ReminderSettings reminderSettings) {
        try {
            // 获取家长电话（实际项目中应该调用家长服务）
            String phone = getParentPhone(child.getId());
            if (phone == null || phone.isEmpty()) {
                // 如果获取不到真实电话，生成基于childId的模拟号码
                phone = generateMockPhone(child.getId());
                log.warn("使用模拟电话: childId={}, phone={}", child.getId(), maskPhone(phone));
            }

            // 构建提醒消息
            String message = String.format(
                "【模拟短信】发送至%s：您好，您的孩子%s已满%d岁，可预约%s疫苗，请尽快登录小程序预约~",
                phone, child.getName(), vaccine.getTargetAge(), vaccine.getName()
            );

            // 根据设置决定是否模拟发送短信
            if (reminderSettings.getSmsReminder() == 1) {
                // 模拟发送短信（控制台打印）
                log.info(message);
            } else {
                log.info("短信提醒已关闭，仅记录提醒: childId={}, vaccineId={}", child.getId(), vaccine.getId());
            }

            // 记录提醒到数据库
            recordReminder(
                child.getId(),
                vaccine.getId(),
                "年龄到期",
                message,
                phone
            );

            log.info("年龄到期提醒发送成功: childId={}, vaccineId={}", child.getId(), vaccine.getId());
        } catch (Exception e) {
            log.error("发送年龄到期提醒失败: childId={}, vaccineId={}", child.getId(), vaccine.getId(), e);
        }
    }

    /**
     * 发送预约后提醒
     */
    private void sendAppointmentReminder(Appointment appointment, long daysUntil, com.vaccine.common.entity.ReminderSettings reminderSettings) {
        try {
            log.info("开始处理预约提醒: appointmentId={}, childId={}, vaccineId={}",
                appointment.getId(), appointment.getChildId(), appointment.getVaccineId());

            // 获取儿童信息
            Child child = null;
            try {
                Result<Child> childResult = childClient.getChildById(appointment.getChildId());
                if (childResult != null && childResult.getCode() == 200 && childResult.getData() != null) {
                    child = childResult.getData();
                }
                log.info("获取儿童信息: childId={}, result={}", appointment.getChildId(),
                    child != null ? child.getName() : "null");
            } catch (Exception e) {
                log.error("获取儿童信息失败: childId={}", appointment.getChildId(), e);
            }

            if (child == null) {
                log.warn("儿童信息为空，跳过提醒: appointmentId={}, childId={}",
                    appointment.getId(), appointment.getChildId());
                return;
            }

            // 获取疫苗信息
            Vaccine vaccine = null;
            try {
                Result<Vaccine> vaccineResult = vaccineClient.getVaccineById(appointment.getVaccineId());
                if (vaccineResult != null && vaccineResult.getCode() == 200 && vaccineResult.getData() != null) {
                    vaccine = vaccineResult.getData();
                }
                log.info("获取疫苗信息: vaccineId={}, result={}", appointment.getVaccineId(),
                    vaccine != null ? vaccine.getName() : "null");
            } catch (Exception e) {
                log.error("获取疫苗信息失败: vaccineId={}", appointment.getVaccineId(), e);
            }

            if (vaccine == null) {
                log.warn("疫苗信息为空，跳过提醒: appointmentId={}, vaccineId={}",
                    appointment.getId(), appointment.getVaccineId());
                return;
            }

            // 获取家长电话（实际项目中应该调用家长服务）
            String phone = getParentPhone(child.getId());
            if (phone == null || phone.isEmpty()) {
                // 如果获取不到真实电话，生成基于childId的模拟号码
                phone = generateMockPhone(child.getId());
                log.warn("使用模拟电话: childId={}, phone={}", child.getId(), maskPhone(phone));
            }

            // 构建提醒消息
            String message = String.format(
                "【模拟短信】发送至%s：您好，您的孩子%s预约的%s疫苗将于%d天后（%s）接种，请按时前往接种点~",
                phone, child.getName(), vaccine.getName(), daysUntil, appointment.getAppointmentDate()
            );

            // 根据设置决定是否模拟发送短信
            if (reminderSettings.getSmsReminder() == 1) {
                // 模拟发送短信（控制台打印）
                log.info(message);
            } else {
                log.info("短信提醒已关闭，仅记录提醒: appointmentId={}, daysUntil={}", appointment.getId(), daysUntil);
            }

            // 记录提醒到数据库
            recordReminder(
                child.getId(),
                vaccine.getId(),
                "预约提醒-" + daysUntil + "天",
                message,
                phone
            );

            log.info("预约后提醒发送成功: appointmentId={}, daysUntil={}", appointment.getId(), daysUntil);
        } catch (Exception e) {
            log.error("发送预约后提醒失败: appointmentId={}", appointment.getId(), e);
        }
    }

    /**
     * 记录提醒到数据库
     */
    private void recordReminder(Long childId, Long vaccineId, String reminderType, String message, String phone) {
        try {
            // 检查 childId 和 vaccineId 是否为 null
            if (childId == null || vaccineId == null) {
                log.error("记录提醒失败，参数为空: childId={}, vaccineId={}, reminderType={}", childId, vaccineId, reminderType);
                return;
            }

            // 获取儿童信息
            Child child = childClient.getChildById(childId).getData();
            if (child == null) {
                log.error("儿童不存在: childId={}", childId);
                return;
            }

            // 🔍 添加日志输出，检查 parentId 是否正确获取
            log.info("获取到儿童信息: childId={}, childName={}, parentId={}", 
                child.getId(), child.getName(), child.getParentId());

            // 获取疫苗信息
            Vaccine vaccine = vaccineClient.getVaccineById(vaccineId).getData();
            if (vaccine == null) {
                log.error("疫苗不存在: vaccineId={}", vaccineId);
                return;
            }

            Reminder reminder = new Reminder();
            Long parentId = child.getParentId();
            reminder.setUserId(parentId); // 家长ID

            // 🔍 添加日志输出，确认 userId 是否正确设置
            log.info("设置提醒用户ID: userId={}, childId={}, vaccineId={}",
                parentId, childId, vaccineId);
            reminder.setUserType(1); // 1:家长
            reminder.setReminderType(reminderType);
            reminder.setTargetId(null); // 目标ID

            reminder.setChildId(childId);
            reminder.setVaccineId(vaccineId);

            // 根据 reminder_type 动态设置标题
            String title;
            if (reminderType.startsWith("预约提醒")) {
                title = "预约提醒";
            } else if (reminderType.startsWith("年龄到期")) {
                title = "年龄到期提醒";
            } else {
                title = "接种提醒";
            }
            reminder.setTitle(title); // 提醒标题

            reminder.setContent(message); // 提醒内容
            reminder.setMessage(message); // 消息内容
            reminder.setRemindTime(java.time.LocalDateTime.now()); // 提醒时间
            reminder.setSendTime(java.time.LocalDateTime.now()); // 发送时间
            reminder.setStatus(1); // 1:已发送
            reminder.setSendChannel("SMS"); // 发送渠道：短信
            reminder.setPhone(phone); // 手机号
            reminder.setEmail(null); // 邮箱
            reminder.setCreateTime(java.time.LocalDateTime.now()); // 创建时间
            reminder.setUpdateTime(java.time.LocalDateTime.now()); // 更新时间
            reminder.setDeleted(0); // 删除标记

            // 🔍 保存前日志：打印即将插入的数据
            log.info("准备插入提醒记录: userId={}, childId={}, vaccineId={}, reminderType={}",
                reminder.getUserId(), reminder.getChildId(), reminder.getVaccineId(), reminder.getReminderType());

            boolean saveResult = reminderService.save(reminder);

            // 🔍 保存后日志：确认插入结果
            if (saveResult) {
                log.info("✓ 提醒记录插入成功: reminderId={}, userId={}, childId={}, vaccineId={}",
                    reminder.getId(), reminder.getUserId(), reminder.getChildId(), reminder.getVaccineId());
            } else {
                log.error("✗ 提醒记录插入失败: userId={}, childId={}, vaccineId={}",
                    reminder.getUserId(), reminder.getChildId(), reminder.getVaccineId());
            }
        } catch (Exception e) {
            log.error("记录提醒失败: childId={}, vaccineId={}", childId, vaccineId, e);
        }
    }

    /**
     * 检查是否已经提醒过
     */
    private boolean hasAlreadyReminded(Long childId, Long vaccineId, String reminderType) {
        try {
            LambdaQueryWrapper<Reminder> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Reminder::getChildId, childId)
            .eq(Reminder::getVaccineId, vaccineId)
                    .eq(Reminder::getReminderType, reminderType)
                    .eq(Reminder::getStatus, 1)
                    .ge(Reminder::getSendTime, java.time.LocalDateTime.now().minusDays(7));
            long count = reminderService.count(
               queryWrapper
            );
            return count > 0;
        } catch (Exception e) {
            log.error("检查提醒记录失败", e);
            return false;
        }
    }

    /**
     * 检查孩子是否已经预约过该疫苗
     */
    private boolean hasAlreadyAppointed(Long childId, Long vaccineId) {
        try {
            AppointmentClient.AppointmentQueryRequest request = new AppointmentClient.AppointmentQueryRequest();
            request.setChildId(childId);
            request.setVaccineId(vaccineId);
            // 注意：这里只能查询到一个状态的预约数量，无法同时查询多个状态
            // 由于接口限制，这里简化为只查询已通过的预约
            request.setStatus(1);
            long count = appointmentClient.countAppointments(request);
            return count > 0;
        } catch (Exception e) {
            log.error("检查预约记录失败", e);
            return false;
        }
    }

    /**
     * 获取所有儿童（实际项目中应该分页）
     */
    private List<Child> getAllChildren() {
        try {
            Result<List<Child>> result = childClient.getAllChildren();
            if (result == null || result.getCode() != 200) {
                log.error("获取所有儿童失败，接口返回异常: {}", result != null ? result.getMessage() : "null");
                return List.of();
            }
            List<Child> children = result.getData();
            if (children == null) {
                log.warn("获取所有儿童返回空数据");
                return List.of();
            }
            log.info("成功获取儿童列表，数量: {}", children.size());
            return children;
        } catch (Exception e) {
            log.error("获取所有儿童失败", e);
            return List.of();
        }
    }

    /**
     * 获取所有疫苗（实际项目中应该分页）
     */
    private List<Vaccine> getAllVaccines() {
        try {
            Result<List<Vaccine>> result = vaccineClient.getAllVaccines();
            if (result == null || result.getCode() != 200) {
                log.error("获取所有疫苗失败，接口返回异常: {}", result != null ? result.getMessage() : "null");
                return List.of();
            }
            List<Vaccine> vaccines = result.getData();
            if (vaccines == null) {
                log.warn("获取所有疫苗返回空数据");
                return List.of();
            }
            log.info("成功获取疫苗列表，数量: {}", vaccines.size());
            return vaccines;
        } catch (Exception e) {
            log.error("获取所有疫苗失败", e);
            return List.of();
        }
    }

    /**
     * 获取家长电话号码
     */
    private String getParentPhone(Long childId) {
        try {
            // 通过childId获取儿童信息，再获取家长信息
            Child child = null;
            try {
                Result<Child> childResult = childClient.getChildById(childId);
                if (childResult != null && childResult.getCode() == 200 && childResult.getData() != null) {
                    child = childResult.getData();
                }
            } catch (Exception e) {
                log.error("获取儿童信息失败: childId={}", childId, e);
            }

            if (child == null || child.getParentId() == null) {
                log.warn("无法获取家长ID: childId={}", childId);
                return null;
            }

            // 调用家长服务获取家长信息
            try {
                Parent parent = parentClient.getParentById(child.getParentId()).getData();
                if (parent != null && parent.getPhone() != null && !parent.getPhone().isEmpty()) {
                    log.info("获取到家长电话: parentId={}, phone={}", parent.getId(), maskPhone(parent.getPhone()));
                    return parent.getPhone();
                } else {
                    log.warn("家长电话为空: parentId={}", child.getParentId());
                    return null;
                }
            } catch (Exception e) {
                log.error("获取家长信息失败: parentId={}", child.getParentId(), e);
                return null;
            }
        } catch (Exception e) {
            log.error("获取家长联系方式失败: childId={}", childId, e);
            return null;
        }
    }

    /**
     * 脱敏电话号码
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 生成模拟电话号码（基于childId）
     */
    private String generateMockPhone(Long childId) {
        if (childId == null) {
            log.error("childId为null，无法生成模拟电话");
            return "13800000000"; // 默认值
        }
        // 基于childId生成一个固定的模拟号码，避免使用固定值
        long base = 13800000000L;
        long suffix = (childId % 9000000) + 1000000;
        return String.valueOf(base + suffix);
    }
}