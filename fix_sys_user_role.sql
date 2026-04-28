-- 为 sys_user 表添加 role 字段
ALTER TABLE `sys_user` 
ADD COLUMN `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色：admin-管理员，staff-工作人员' 
AFTER `status`;

-- 为现有用户设置角色
-- 将admin设置为管理员角色
UPDATE `sys_user` SET `role` = 'admin' WHERE `username` = 'admin';

-- 将其他用户设置为工作人员角色
UPDATE `sys_user` SET `role` = 'staff' WHERE `username` != 'admin' AND `role` IS NULL;