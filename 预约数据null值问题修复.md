# 预约数据 null 值问题修复方案

## 问题描述

定时任务在执行预约后提醒时，出现以下错误：

```
【模拟短信】发送至13800138000：您好，您的孩子null预约的null疫苗将于1天后（2027-07-20）接种，请按时前往接种点~
```

```
feign.FeignException$NotFound: [404] during [GET] to [http://vaccine-child-service/child/] [ChildClient#getChildById(Long)]
```

**根本原因：**
数据库中存在 `child_id` 或 `vaccine_id` 为 null 的预约记录。

## 已修复的代码

### 1. 添加空值检查

**文件：** `vaccine-notify-service/src/main/java/com/vaccine/notify/scheduled/VaccinationReminderScheduler.java`

**修改内容：**
```java
for (Appointment appointment : approvedAppointments) {
    // 检查 childId 和 vaccineId 是否为 null
    if (appointment.getChildId() == null || appointment.getVaccineId() == null) {
        log.warn("预约记录数据不完整，跳过提醒: appointmentId={}, childId={}, vaccineId={}",
            appointment.getId(), appointment.getChildId(), appointment.getVaccineId());
        continue;
    }

    // 后续处理...
}
```

**效果：**
- ✅ 避免处理无效的预约记录
- ✅ 记录警告日志，便于排查问题
- ✅ 不影响其他正常的预约提醒

## 需要执行的修复步骤

### 第一步：查询有问题的预约记录

**执行SQL脚本：** `查询并修复预约数据.sql`

```bash
mysql -u root -p vaccine_platform < "C:\Users\29278\Desktop\防控平台毕业设计\查询并修复预约数据.sql"
```

**查询结果：**
查看是否有 `child_id` 或 `vaccine_id` 为 null 的记录。

### 第二步：修复有问题的预约数据

**方式一：删除无效记录（推荐）**

```sql
-- 删除 child_id 或 vaccine_id 为 null 的预约记录
DELETE FROM appointment
WHERE (child_id IS NULL OR vaccine_id IS NULL)
  AND deleted = 0;
```

**方式二：更新为有效值（如果知道正确的ID）**

```sql
-- 如果知道正确的儿童ID和疫苗ID，可以更新
UPDATE appointment
SET child_id = [正确的儿童ID],
    vaccine_id = [正确的疫苗ID]
WHERE id = [预约记录ID];
```

### 第三步：重启通知服务

```bash
# 停止服务（如果正在运行）
# Ctrl + C

# 重新启动
cd C:\Users\29278\Desktop\防控平台毕业设计\vaccine-notify-service
mvn spring-boot:run
```

### 第四步：验证修复结果

**1. 查看定时任务日志：**
```bash
# 应该看到：
# - ✅ 开始检查预约后提醒
# - ✅ 【模拟短信】发送至...（正确的儿童名和疫苗名）
# - ✅ 预约后提醒发送成功
# - ⚠️ 预约记录数据不完整，跳过提醒（如果还有无效记录）
# - ✅ 预约后提醒检查完成
```

**2. 查看提醒记录：**
```sql
SELECT
    id,
    user_id,
    child_id,
    vaccine_id,
    title,
    content,
    reminder_type,
    send_time,
    status,
    create_time
FROM reminder
ORDER BY create_time DESC
LIMIT 10;
```

**3. 查看预约数据：**
```sql
-- 确认没有 null 值
SELECT id, child_id, vaccine_id, appointment_date, status
FROM appointment
WHERE (child_id IS NULL OR vaccine_id IS NULL)
  AND deleted = 0;
```

**预期结果：**
- ✅ 查询结果为空（没有 null 值）
- ✅ 提醒消息中显示正确的儿童名和疫苗名
- ✅ 不再出现 404 错误

## 数据完整性检查

### 检查所有表的外键关系

```sql
-- 检查 appointment 表的 child_id 是否都存在于 child 表
SELECT a.id AS appointment_id, a.child_id
FROM appointment a
LEFT JOIN child c ON a.child_id = c.id
WHERE a.child_id IS NOT NULL
  AND c.id IS NULL
  AND a.deleted = 0;

-- 检查 appointment 表的 vaccine_id 是否都存在于 vaccine 表
SELECT a.id AS appointment_id, a.vaccine_id
FROM appointment a
LEFT JOIN vaccine v ON a.vaccine_id = v.id
WHERE a.vaccine_id IS NOT NULL
  AND v.id IS NULL
  AND a.deleted = 0;

-- 检查 reminder 表的 child_id 是否都存在于 child 表
SELECT r.id AS reminder_id, r.child_id
FROM reminder r
LEFT JOIN child c ON r.child_id = c.id
WHERE r.child_id IS NOT NULL
  AND c.id IS NULL
  AND r.deleted = 0;

-- 检查 reminder 表的 vaccine_id 是否都存在于 vaccine 表
SELECT r.id AS reminder_id, r.vaccine_id
FROM reminder r
LEFT JOIN vaccine v ON r.vaccine_id = v.id
WHERE r.vaccine_id IS NOT NULL
  AND v.id IS NULL
  AND r.deleted = 0;
```

### 添加数据库约束（可选）

**为 appointment 表添加外键约束：**

```sql
-- 删除已存在的外键约束（如果有）
ALTER TABLE appointment DROP FOREIGN KEY IF EXISTS fk_appointment_child;
ALTER TABLE appointment DROP FOREIGN KEY IF EXISTS fk_appointment_vaccine;

-- 添加外键约束
ALTER TABLE appointment
ADD CONSTRAINT fk_appointment_child
FOREIGN KEY (child_id) REFERENCES child(id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE appointment
ADD CONSTRAINT fk_appointment_vaccine
FOREIGN KEY (vaccine_id) REFERENCES vaccine(id)
ON DELETE CASCADE
ON UPDATE CASCADE;
```

**注意：**
- 添加外键约束前，需要先清理无效数据
- 外键约束会影响插入和更新操作的性能
- 如果业务逻辑复杂，建议在应用层做数据校验

## 预防措施

### 1. 应用层校验

在预约服务中添加数据校验：

```java
// 在创建或更新预约时，检查 child_id 和 vaccine_id 是否有效
if (appointment.getChildId() == null || appointment.getVaccineId() == null) {
    throw new IllegalArgumentException("儿童ID和疫苗ID不能为空");
}

// 检查儿童是否存在
Child child = childService.getById(appointment.getChildId());
if (child == null) {
    throw new IllegalArgumentException("儿童不存在");
}

// 检查疫苗是否存在
Vaccine vaccine = vaccineService.getById(appointment.getVaccineId());
if (vaccine == null) {
    throw new IllegalArgumentException("疫苗不存在");
}
```

### 2. 数据库层校验

修改表结构，添加 NOT NULL 约束：

```sql
-- 将 child_id 和 vaccine_id 设为非空
ALTER TABLE appointment
MODIFY COLUMN child_id bigint NOT NULL COMMENT '儿童ID';

ALTER TABLE appointment
MODIFY COLUMN vaccine_id bigint NOT NULL COMMENT '疫苗ID';
```

### 3. 数据清理脚本

定期运行数据清理脚本：

```sql
-- 定期清理无效数据
DELETE FROM appointment
WHERE child_id IS NULL
   OR vaccine_id IS NULL
   OR child_id NOT IN (SELECT id FROM child WHERE deleted = 0)
   OR vaccine_id NOT IN (SELECT id FROM vaccine WHERE deleted = 0);
```

## 问题总结

**根本原因：**
1. 数据库中存在数据不完整的预约记录（child_id 或 vaccine_id 为 null）
2. 应用层没有添加数据完整性校验
3. 数据库表没有添加非空约束或外键约束

**解决方法：**
1. ✅ 在定时任务中添加空值检查，避免处理无效数据
2. ⏳ 清理数据库中的无效记录
3. ⏳ 在应用层添加数据校验
4. ⏳ 考虑添加数据库约束（可选）

**经验教训：**
1. 在应用层要添加完整的数据校验
2. 重要字段要添加 NOT NULL 约束
3. 要定期检查数据完整性
4. 定时任务要做好异常处理，避免因为一条无效数据影响整个任务
5. 要记录详细的日志，便于排查问题

## 相关文件

- ✅ `vaccine-notify-service/src/main/java/com/vaccine/notify/scheduled/VaccinationReminderScheduler.java` - 已添加空值检查
- ⏳ `查询并修复预约数据.sql` - 待执行
- 📋 `定时任务问题修复方案.md` - 之前的修复方案

## 执行清单

- [ ] 执行 `查询并修复预约数据.sql` 查询有问题的记录
- [ ] 删除或更新有问题的预约记录
- [ ] 重启通知服务
- [ ] 验证定时任务日志
- [ ] 检查提醒记录是否正确生成
- [ ] （可选）添加数据库约束
- [ ] （可选）在预约服务中添加数据校验