/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : localhost:3306
 Source Schema         : vaccine_platform

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 23/03/2026 16:48:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `child_id` bigint NOT NULL COMMENT '儿童ID',
  `vaccine_id` bigint NOT NULL COMMENT '疫苗ID',
  `dose_number` int NOT NULL COMMENT '剂次',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `time_slot` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '时间段（上午/下午/具体时间）',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0:待审核 1:通过 2:拒绝 3:已完成 4:已取消',
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消原因',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_child_id`(`child_id` ASC) USING BTREE,
  INDEX `idx_appointment_date`(`appointment_date` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (3, 3, 2, 3, '2026-03-17', NULL, 3, NULL, NULL, '2026-03-17 23:56:00', '2026-03-17 23:56:00', NULL, NULL, 0);
INSERT INTO `appointment` VALUES (4, 4, 2, 3, '2026-03-18', NULL, 3, NULL, NULL, '2026-03-18 16:58:56', '2026-03-18 16:58:56', NULL, NULL, 0);
INSERT INTO `appointment` VALUES (5, 5, 1, 1, '2026-03-04', NULL, 3, NULL, NULL, '2026-03-18 16:59:15', '2026-03-18 16:59:15', NULL, NULL, 0);
INSERT INTO `appointment` VALUES (6, 3, 1, 1, '2026-03-18', NULL, 3, NULL, NULL, '2026-03-19 16:07:45', '2026-03-19 16:07:47', NULL, NULL, 0);
INSERT INTO `appointment` VALUES (7, 3, 1, 1, '2026-03-19', NULL, 2, '不想让你接种', NULL, '2026-03-19 12:07:48', '2026-03-19 16:07:53', NULL, NULL, 0);
INSERT INTO `appointment` VALUES (9, 3, 1, 2, '2026-03-19', NULL, 3, NULL, NULL, '2026-03-19 16:07:59', '2026-03-19 16:08:00', NULL, NULL, 0);
INSERT INTO `appointment` VALUES (10, 4, 3, 1, '2026-04-19', NULL, 3, NULL, NULL, '2026-03-19 16:08:01', '2026-03-19 16:08:04', NULL, NULL, 0);
INSERT INTO `appointment` VALUES (11, 8, 1, 1, '2026-03-21', NULL, 3, NULL, NULL, '2026-03-20 10:07:20', '2026-03-20 10:07:20', NULL, NULL, 0);
INSERT INTO `appointment` VALUES (12, 6, 2, 1, '2026-03-21', NULL, 3, NULL, NULL, '2026-03-20 12:07:24', '2026-03-20 12:07:24', NULL, NULL, 0);

-- ----------------------------
-- Table structure for appointment_settings
-- ----------------------------
DROP TABLE IF EXISTS `appointment_settings`;
CREATE TABLE `appointment_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `advance_days` int NULL DEFAULT 7 COMMENT '预约提前天数',
  `max_daily_appointments` int NULL DEFAULT 100 COMMENT '每天最大预约数',
  `start_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '08:00' COMMENT '预约开始时间',
  `end_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '17:00' COMMENT '预约结束时间',
  `enabled` tinyint NULL DEFAULT 1 COMMENT '是否启用预约功能 0:禁用 1:启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of appointment_settings
-- ----------------------------
INSERT INTO `appointment_settings` VALUES (1, 7, 100, '08:00', '17:00', 1, '2026-03-18 10:00:00', '2026-03-18 10:00:00', 'system', NULL, 0);

-- ----------------------------
-- Table structure for basic_settings
-- ----------------------------
DROP TABLE IF EXISTS `basic_settings`;
CREATE TABLE `basic_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `system_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '社区儿童疫苗接种管理系统' COMMENT '系统名称',
  `system_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '为社区儿童提供疫苗接种预约和管理的平台' COMMENT '系统描述',
  `admin_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员邮箱',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '基本设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of basic_settings
-- ----------------------------
INSERT INTO `basic_settings` VALUES (1, '社区儿童疫苗接种管理系统', '为社区儿童提供疫苗接种预约和管理的平台', '2927824043@qq.com', '15512725316', '2026-03-18 10:00:00', '2026-03-18 10:00:00', 'system', NULL, 0);

-- ----------------------------
-- Table structure for child
-- ----------------------------
DROP TABLE IF EXISTS `child`;
CREATE TABLE `child`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '儿童ID',
  `parent_id` bigint NOT NULL COMMENT '家长ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint NULL DEFAULT 1 COMMENT '性别 1:男 0:女',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `is_key_child` tinyint NULL DEFAULT 0 COMMENT '是否重点儿童 1:是 0:否',
  `key_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '重点原因',
  `health_status` tinyint NULL DEFAULT 1 COMMENT '健康状况 1:良好 2:一般 3:特殊',
  `allergy_history` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '过敏史',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '病史',
  `family_medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '家族病史',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_birth_date`(`birth_date` ASC) USING BTREE,
  INDEX `idx_id_card`(`id_card` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '儿童表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of child
-- ----------------------------
INSERT INTO `child` VALUES (3, 4, '王晨宇', NULL, 1, '2024-03-18', NULL, 1, '脑瘫、双腿瘫痪', 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 0);
INSERT INTO `child` VALUES (4, 4, '林子康', '139274832294491351', 1, '2022-03-18', '唐城一品', 0, NULL, 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 0);
INSERT INTO `child` VALUES (5, 4, '小小娴', NULL, 2, '2023-03-19', NULL, 0, NULL, 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 1);
INSERT INTO `child` VALUES (6, 4, '肖子娴', NULL, 2, '2025-03-19', NULL, 0, NULL, 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, 0);
INSERT INTO `child` VALUES (7, 4, '测试', NULL, 1, '2024-01-01', NULL, 0, NULL, 1, NULL, NULL, NULL, 1, '2026-03-19 19:44:48', '2026-03-20 09:52:53', NULL, NULL, 1);
INSERT INTO `child` VALUES (8, 4, '测试', NULL, 1, '2024-03-20', NULL, 0, NULL, 1, NULL, NULL, NULL, 1, '2026-03-20 09:53:08', '2026-03-22 15:06:40', NULL, NULL, 1);
INSERT INTO `child` VALUES (9, 4, '测试', NULL, 1, '2024-03-22', NULL, 0, NULL, 1, NULL, NULL, NULL, 1, '2026-03-22 15:06:56', '2026-03-22 15:06:56', NULL, NULL, 0);

-- ----------------------------
-- Table structure for forum_announcement
-- ----------------------------
DROP TABLE IF EXISTS `forum_announcement`;
CREATE TABLE `forum_announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'normal' COMMENT '公告类型：normal-普通，urgent-紧急，notice-通知',
  `priority` int NULL DEFAULT 0 COMMENT '优先级，数字越大越靠前',
  `is_popup` tinyint NULL DEFAULT 0 COMMENT '是否弹窗显示 1:是 0:否',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始显示时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束显示时间',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum_announcement
-- ----------------------------
INSERT INTO `forum_announcement` VALUES (1, '关于疫苗接种预约系统的通知', '本平台已正式上线疫苗接种预约功能，家长可以通过小程序在线预约接种时间，减少排队等待。', 'notice', 100, 1, NULL, NULL, 1, '2026-03-16 19:43:57', '2026-03-16 19:43:57', 'admin', NULL, 0);
INSERT INTO `forum_announcement` VALUES (2, '紧急通知：疫苗库存更新', '近期乙肝疫苗库存紧张，请家长们合理安排接种时间。', 'urgent', 90, 1, NULL, NULL, 1, '2026-03-16 19:43:57', '2026-03-16 19:43:57', 'admin', NULL, 0);
INSERT INTO `forum_announcement` VALUES (3, '测试', 'ceshi', 'normal', 0, 1, NULL, NULL, 1, NULL, NULL, NULL, NULL, 0);
INSERT INTO `forum_announcement` VALUES (4, '预约时间通知', '家长可以提前7天预约，每天最多预约100个人，希望家长们提前预约，预约的时间是从早上8点到晚上8点，', 'normal', 0, 0, NULL, NULL, 1, '2026-03-20 10:30:04', '2026-03-20 10:30:14', NULL, NULL, 0);

-- ----------------------------
-- Table structure for forum_comment
-- ----------------------------
DROP TABLE IF EXISTS `forum_comment`;
CREATE TABLE `forum_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '评论者ID（关联parent表）',
  `user_type` tinyint NULL DEFAULT 1 COMMENT '用户类型 1:家长',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
  `reply_to_user_id` bigint NULL DEFAULT NULL COMMENT '回复的目标用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum_comment
-- ----------------------------
INSERT INTO `forum_comment` VALUES (1, 1, 2, 1, 0, NULL, '我家宝宝也是刚出生，我这边有一个疫苗接种时间表，可以分享一下', 0, 1, '2026-03-16 19:43:57', '2026-03-16 19:43:57', 0);
INSERT INTO `forum_comment` VALUES (2, 1, 1, 1, 1, 2, '谢谢！太需要了', 0, 1, '2026-03-16 19:43:57', '2026-03-16 19:43:57', 0);
INSERT INTO `forum_comment` VALUES (3, 3, 4, 1, 0, NULL, '测试', 0, 1, '2026-03-19 16:21:54', '2026-03-19 16:21:54', 0);
INSERT INTO `forum_comment` VALUES (4, 3, 4, 1, 0, NULL, '测试', 0, 1, '2026-03-19 17:23:39', '2026-03-19 17:23:39', 0);
INSERT INTO `forum_comment` VALUES (5, 5, 4, 1, 0, NULL, 'nihao', 0, 1, '2026-03-19 17:54:28', '2026-03-19 17:54:28', 0);
INSERT INTO `forum_comment` VALUES (6, 6, 4, 1, 0, NULL, '你好', 0, 1, '2026-03-22 15:08:03', '2026-03-22 15:08:03', 0);

-- ----------------------------
-- Table structure for forum_like
-- ----------------------------
DROP TABLE IF EXISTS `forum_like`;
CREATE TABLE `forum_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '点赞用户ID（关联parent表）',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标类型：post/comment',
  `target_id` bigint NOT NULL COMMENT '目标ID（帖子ID或评论ID）',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target`(`user_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum_like
-- ----------------------------
INSERT INTO `forum_like` VALUES (1, 4, 'post', 1, '2026-03-19 17:03:17');
INSERT INTO `forum_like` VALUES (6, 4, 'post', 3, '2026-03-19 17:23:00');
INSERT INTO `forum_like` VALUES (7, 4, 'post', 5, '2026-03-19 17:54:23');
INSERT INTO `forum_like` VALUES (9, 4, 'post', 6, '2026-03-22 15:07:57');

-- ----------------------------
-- Table structure for forum_post
-- ----------------------------
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '发布者ID（关联parent表）',
  `user_type` tinyint NULL DEFAULT 1 COMMENT '用户类型 1:家长',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `images` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片列表（JSON格式）',
  `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签（逗号分隔）',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶 1:是 0:否',
  `is_essence` tinyint NULL DEFAULT 0 COMMENT '是否精华 1:是 0:否',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:正常 0:禁用 2:审核中',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '论坛帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum_post
-- ----------------------------
INSERT INTO `forum_post` VALUES (1, 1, 1, '新生儿疫苗接种时间表', '我家宝宝刚出生，想了解一下疫苗接种的时间安排，有没有家长分享一下经验？', NULL, '新生儿,时间表', 121, 6, 3, 0, 0, 1, '2026-03-16 19:43:57', '2026-03-19 17:55:44', NULL, NULL, 1);
INSERT INTO `forum_post` VALUES (2, 2, 1, '宝宝打疫苗后发烧正常吗？', '我家宝宝接种乙肝疫苗后有点低烧，大约37.5度，请问这种情况正常吗？需要去医院吗？', NULL, '发烧,不良反应', 60, 2, 1, 0, 0, 1, '2026-03-16 19:43:57', '2026-03-19 17:55:45', NULL, NULL, 1);
INSERT INTO `forum_post` VALUES (3, 4, 1, '测试', '测试', NULL, '测试', 59, 1, 2, 0, 0, 1, '2026-03-19 16:21:40', '2026-03-19 16:21:40', NULL, NULL, 0);
INSERT INTO `forum_post` VALUES (4, 4, 1, '测试2', '测试2', NULL, '测试2', 1, 0, 0, 0, 0, 1, '2026-03-19 17:38:41', '2026-03-19 17:38:41', NULL, NULL, 0);
INSERT INTO `forum_post` VALUES (5, 4, 1, '测试3', '测试3', NULL, '测试3', 9, 1, 1, 0, 0, 1, '2026-03-19 17:43:08', '2026-03-19 17:43:08', NULL, NULL, 0);
INSERT INTO `forum_post` VALUES (6, 4, 1, '测试', '测试', NULL, '', 1, 1, 1, 0, 0, 1, '2026-03-22 15:07:48', '2026-03-22 15:07:48', NULL, NULL, 0);

-- ----------------------------
-- Table structure for parent
-- ----------------------------
DROP TABLE IF EXISTS `parent`;
CREATE TABLE `parent`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '家长ID',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '微信openid',
  `unionid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信unionid',
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信头像',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别 0-未知 1-男 2-女',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_openid`(`openid` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '家长表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parent
-- ----------------------------
INSERT INTO `parent` VALUES (4, 'o4aeC13cgQzhDmxallEoEljQzx7s', NULL, 'PGCHEF', 'http://tmp/SPBUWHX0RSZEee8fa6ba4c2075a866f6a2f0b203c4c8.jpeg', 1, '北京市', '15512725316', NULL, 1, '2026-03-18 22:08:26', NULL, '2026-03-18 21:50:55', '2026-03-18 21:50:55', NULL, NULL, 0);

-- ----------------------------
-- Table structure for reminder
-- ----------------------------
DROP TABLE IF EXISTS `reminder`;
CREATE TABLE `reminder`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提醒ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID（家长或系统用户）',
  `user_type` tinyint NULL DEFAULT 1 COMMENT '用户类型 1:家长 2:系统用户',
  `reminder_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提醒类型：VACCINATION-接种，APPOINTMENT-预约，PLAN-计划，ANNOUNCEMENT-公告',
  `target_id` bigint NULL DEFAULT NULL COMMENT '目标ID（预约ID、计划ID、公告ID等）',
  `child_id` bigint NULL DEFAULT NULL COMMENT '儿童ID',
  `vaccine_id` bigint NULL DEFAULT NULL COMMENT '疫苗ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '提醒标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提醒内容',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息内容',
  `remind_time` datetime NOT NULL COMMENT '提醒时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0:未发送 1:已发送 2:已读 3:已忽略',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
  `send_channel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发送渠道：WX-微信，SMS-短信，PUSH-推送',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_remind_time`(`remind_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '提醒记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reminder
-- ----------------------------
INSERT INTO `reminder` VALUES (1, 1, 1, 'APPOINTMENT', 1, NULL, NULL, '接种预约提醒', '您预约的脊髓灰质炎疫苗将于2026-03-20接种，请准时前往', NULL, '2026-03-19 09:00:00', 0, NULL, NULL, 'WX', NULL, NULL, '2026-03-16 19:43:57', '2026-03-16 19:43:57', 0);
INSERT INTO `reminder` VALUES (2, 2, 1, 'PLAN', 3, NULL, NULL, '接种计划提醒', '您的孩子小红计划于2026-03-25接种乙肝疫苗第二剂', NULL, '2026-03-24 09:00:00', 0, NULL, NULL, 'WX', NULL, NULL, '2026-03-16 19:43:57', '2026-03-16 19:43:57', 0);
INSERT INTO `reminder` VALUES (3, NULL, 1, '年龄到期', NULL, 3, 3, '年龄到期提醒', '【模拟短信】发送至13800138000：您好，您的孩子王晨宇已满2岁，可预约脊髓灰质炎疫苗疫苗，请尽快登录小程序预约~', '【模拟短信】发送至13800138000：您好，您的孩子王晨宇已满2岁，可预约脊髓灰质炎疫苗疫苗，请尽快登录小程序预约~', '2026-03-19 20:56:08', 1, '2026-03-19 20:56:08', NULL, 'SMS', '13800138000', NULL, '2026-03-19 20:56:08', '2026-03-19 20:56:08', 0);
INSERT INTO `reminder` VALUES (4, NULL, 1, '年龄到期', NULL, 7, 3, '年龄到期提醒', '【模拟短信】发送至13800138000：您好，您的孩子测试已满2岁，可预约脊髓灰质炎疫苗疫苗，请尽快登录小程序预约~', '【模拟短信】发送至13800138000：您好，您的孩子测试已满2岁，可预约脊髓灰质炎疫苗疫苗，请尽快登录小程序预约~', '2026-03-19 20:56:08', 1, '2026-03-19 20:56:08', NULL, 'SMS', '13800138000', NULL, '2026-03-19 20:56:08', '2026-03-19 20:56:08', 0);
INSERT INTO `reminder` VALUES (5, NULL, 1, '年龄到期', NULL, 8, 3, '年龄到期提醒', '【模拟短信】发送至13800138000：您好，您的孩子测试已满2岁，可预约脊髓灰质炎疫苗疫苗，请尽快登录小程序预约~', '【模拟短信】发送至13800138000：您好，您的孩子测试已满2岁，可预约脊髓灰质炎疫苗疫苗，请尽快登录小程序预约~', '2026-03-20 09:56:00', 1, '2026-03-20 09:56:00', NULL, 'SMS', '13800138000', NULL, '2026-03-20 09:56:00', '2026-03-20 09:56:00', 0);
INSERT INTO `reminder` VALUES (8, 4, 1, '预约提醒-1天', NULL, 8, 1, '年龄到期提醒', '【模拟短信】发送至15512725316：您好，您的孩子测试预约的乙肝疫苗疫苗将于1天后（2026-03-21）接种，请按时前往接种点~', '【模拟短信】发送至15512725316：您好，您的孩子测试预约的乙肝疫苗疫苗将于1天后（2026-03-21）接种，请按时前往接种点~', '2026-03-20 12:22:44', 2, '2026-03-20 12:22:44', NULL, 'SMS', '15512725316', NULL, '2026-03-20 12:22:44', '2026-03-20 14:30:50', 0);
INSERT INTO `reminder` VALUES (9, 4, 1, '预约提醒-1天', NULL, 6, 2, '年龄到期提醒', '【模拟短信】发送至15512725316：您好，您的孩子肖子娴预约的卡介苗疫苗将于1天后（2026-03-21）接种，请按时前往接种点~', '【模拟短信】发送至15512725316：您好，您的孩子肖子娴预约的卡介苗疫苗将于1天后（2026-03-21）接种，请按时前往接种点~', '2026-03-20 12:22:44', 2, '2026-03-20 12:22:44', NULL, 'SMS', '15512725316', NULL, '2026-03-20 12:22:44', '2026-03-20 14:30:50', 0);

-- ----------------------------
-- Table structure for reminder_settings
-- ----------------------------
DROP TABLE IF EXISTS `reminder_settings`;
CREATE TABLE `reminder_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '设置ID',
  `appointment_reminder` tinyint NULL DEFAULT 1 COMMENT '是否启用预约提醒 0:禁用 1:启用',
  `vaccination_reminder` tinyint NULL DEFAULT 1 COMMENT '是否启用接种提醒 0:禁用 1:启用',
  `sms_reminder` tinyint NULL DEFAULT 0 COMMENT '是否启用短信提醒 0:禁用 1:启用',
  `reminder_time` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '09:00' COMMENT '提醒时间',
  `reminder_days_before` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '3,1' COMMENT '提前提醒天数（逗号分隔）',
  `enabled` tinyint NULL DEFAULT 1 COMMENT '是否启用提醒功能 0:禁用 1:启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '提醒设置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of reminder_settings
-- ----------------------------
INSERT INTO `reminder_settings` VALUES (1, 1, 1, 1, '12:22', '5,3,1', 1, '2026-03-18 10:00:00', '2026-03-18 10:00:00', 'system', NULL, 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'admin123', '系统管理员', '13800138000', 'admin@vaccine.com', 1, NULL, '2026-03-16 19:43:57', '2026-03-16 19:43:57', 'system', NULL, 0);
INSERT INTO `sys_user` VALUES (2, 'staff001', 'admin123', '工作人员张三', '13800138003', 'staff001@test.com', 1, NULL, '2026-03-16 19:43:57', '2026-03-16 19:43:57', 'system', NULL, 0);
INSERT INTO `sys_user` VALUES (3, 'admin2', '1231232', '张三', '1273518234', '123@163.com', 1, NULL, '2026-03-16 22:24:17', '2026-03-16 22:24:19', NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (5, 'admin3', 'admin123', 'Ceshi ', '12312312312', '2927824043@qq.com', 1, NULL, '2026-03-16 22:24:21', '2026-03-16 22:24:22', NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (6, 'admin4', '123123', '测试2', '12212847712', '123123@qq.com', 1, NULL, '2025-02-16 22:24:23', '2026-03-16 22:24:29', NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (7, 'admin5', '123123', '测试3', '12221234212', '21354@qq.com', 1, NULL, '2026-03-17 17:50:22', '2026-03-17 17:50:23', NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (8, 'admin6', 'admin123', '大叔大婶', '12312312312', '2927824043@qq.com', 0, NULL, '2026-03-17 17:50:24', '2026-03-17 17:50:25', NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (9, 'admin12', 'admin123', '12', '12312312312', '12345789@123.cm', 1, NULL, '2026-03-17 17:50:35', '2026-03-17 17:50:37', NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (10, 'ceshi', 'admin123', '测试', '15512725316', 'ceshi@qq.com', 1, NULL, NULL, NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for vaccination_record
-- ----------------------------
DROP TABLE IF EXISTS `vaccination_record`;
CREATE TABLE `vaccination_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `appointment_id` bigint NULL DEFAULT NULL COMMENT '关联的预约ID',
  `child_id` bigint NOT NULL COMMENT '儿童ID',
  `vaccine_id` bigint NOT NULL COMMENT '疫苗ID',
  `dose_number` int NOT NULL COMMENT '剂次',
  `scheduled_date` date NULL DEFAULT NULL COMMENT '预约日期',
  `vaccinated_date` datetime NULL DEFAULT NULL COMMENT '接种时间',
  `vaccination_site` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '接种地点',
  `batch_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '批次号',
  `batch_id` bigint NULL DEFAULT NULL COMMENT '批次ID',
  `vaccinator_id` bigint NULL DEFAULT NULL COMMENT '接种人员ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 0:未接种 1:已接种 2:已取消',
  `reaction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '不良反应',
  `reaction_level` tinyint NULL DEFAULT NULL COMMENT '反应等级 0:无 1:轻微 2:中等 3:严重',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_child_id`(`child_id` ASC) USING BTREE,
  INDEX `idx_vaccine_id`(`vaccine_id` ASC) USING BTREE,
  INDEX `idx_appointment_id`(`appointment_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '接种记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vaccination_record
-- ----------------------------
INSERT INTO `vaccination_record` VALUES (1, NULL, 1, 1, 1, '2020-01-02', '2020-01-02 10:30:00', '社区卫生服务中心', '202401001', 1, NULL, 1, NULL, NULL, '接种顺利，无不良反应', '2026-03-16 19:43:57', '2026-03-16 19:43:57', 'system', NULL, 0);
INSERT INTO `vaccination_record` VALUES (2, NULL, 1, 2, 1, '2020-01-02', '2020-01-02 11:00:00', '社区卫生服务中心', '202401002', 2, NULL, 1, NULL, NULL, '接种顺利', '2026-03-16 19:43:57', '2026-03-16 19:43:57', 'system', NULL, 0);
INSERT INTO `vaccination_record` VALUES (3, NULL, 2, 1, 1, '2019-06-16', '2019-06-16 09:00:00', '社区卫生服务中心', '202401001', 1, NULL, 1, '局部红肿', NULL, '轻微红肿，24小时后消退', '2026-03-16 19:43:57', '2026-03-16 19:43:57', 'system', NULL, 0);

-- ----------------------------
-- Table structure for vaccine
-- ----------------------------
DROP TABLE IF EXISTS `vaccine`;
CREATE TABLE `vaccine`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '疫苗ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '疫苗名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '疫苗编码',
  `manufacturer` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生产厂家',
  `dose_number` int NULL DEFAULT 1 COMMENT '接种剂次',
  `interval_days` int NULL DEFAULT 0 COMMENT '间隔天数',
  `expiry_date` date NULL DEFAULT NULL COMMENT '有效期',
  `stock` int NULL DEFAULT 0 COMMENT '库存数量',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '支' COMMENT '单位',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疫苗类别（一类疫苗、二类疫苗）',
  `target_age` int NULL DEFAULT NULL COMMENT '可接种年龄（岁，整数，用于简化预约和提醒逻辑，例如：0表示出生时，1表示1岁，2表示2岁）',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌症',
  `adverse_reactions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '不良反应',
  `precautions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '注意事项',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '疫苗描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:正常 0:禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '疫苗表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vaccine
-- ----------------------------
INSERT INTO `vaccine` VALUES (1, '乙肝疫苗', 'HEPB', '北京生物制品研究所', 3, 28, '2025-12-31', 10, '支', 200.00, '一类疫苗', 0, NULL, NULL, NULL, '乙型肝炎疫苗，新生儿出生后24小时内接种', 1, '2026-03-16 19:43:57', '2026-03-19 12:30:22', NULL, NULL, 0);
INSERT INTO `vaccine` VALUES (2, '卡介苗', 'BCG', '上海生物制品研究所', 1, 0, '2025-12-31', 20, '支', NULL, '一类疫苗', 0, NULL, NULL, NULL, '卡介苗，新生儿出生后24小时内接种', 1, '2026-03-16 19:43:57', '2026-03-18 19:15:47', NULL, NULL, 0);
INSERT INTO `vaccine` VALUES (3, '脊髓灰质炎疫苗', 'IPV', '中国医学科学院医学生物学研究所', 4, 28, '2025-12-31', 5, '支', NULL, '一类疫苗', 2, NULL, NULL, NULL, '脊髓灰质炎灭活疫苗', 1, '2026-03-16 19:43:57', '2026-03-18 19:15:47', NULL, NULL, 0);
INSERT INTO `vaccine` VALUES (4, '百白破疫苗', 'DTP', '武汉生物制品研究所', 4, 28, '2025-12-31', 60, '支', NULL, '一类疫苗', 3, NULL, NULL, NULL, '百日咳、白喉、破伤风联合疫苗', 1, '2026-03-16 19:43:57', '2026-03-18 19:15:47', NULL, NULL, 0);
INSERT INTO `vaccine` VALUES (5, '麻疹疫苗', 'MV', '兰州生物制品研究所', 2, 180, '2025-12-31', 70, '支', NULL, '一类疫苗', 8, NULL, NULL, NULL, '麻疹减毒活疫苗', 1, '2026-03-16 19:43:57', '2026-03-18 19:15:47', NULL, NULL, 0);
INSERT INTO `vaccine` VALUES (6, '新冠疫苗', '12121', '河北沧州师范学院研究所', 1, 13, NULL, 0, '支', 15.00, '一类疫苗', 12, '心脏病', '头晕嗜睡', '忌酒忌烟', '新冠疫情专用疫苗', 1, NULL, '2026-03-18 19:15:47', NULL, NULL, 0);
INSERT INTO `vaccine` VALUES (7, '测试', 'ceshi', '测试商家', 1, 1, NULL, 0, '瓶', 20.00, '一类疫苗', 12, '测试', '测试', '测试', '测试', 1, NULL, NULL, NULL, NULL, 0);
INSERT INTO `vaccine` VALUES (8, '测试2', 'ceshi2', '测试商家2', 1, 0, NULL, 0, '瓶', 0.00, '二类疫苗', 0, '', '', '', '', 1, NULL, NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for vaccine_batch
-- ----------------------------
DROP TABLE IF EXISTS `vaccine_batch`;
CREATE TABLE `vaccine_batch`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `vaccine_id` bigint NOT NULL COMMENT '疫苗ID',
  `batch_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '批次号',
  `production_date` date NULL DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date NULL DEFAULT NULL COMMENT '有效期',
  `quantity` int NULL DEFAULT 0 COMMENT '数量',
  `used_quantity` int NULL DEFAULT 0 COMMENT '已使用数量',
  `available_quantity` int NULL DEFAULT 0 COMMENT '可用数量',
  `supplier` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '供应商',
  `trace_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '追溯码',
  `site_id` bigint NULL DEFAULT NULL COMMENT '分配的站点ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 1:正常 0:过期 2:用完',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NULL DEFAULT 0 COMMENT '删除标记 0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_batch_number`(`batch_number` ASC) USING BTREE,
  INDEX `idx_vaccine_id`(`vaccine_id` ASC) USING BTREE,
  INDEX `idx_site_id`(`site_id` ASC) USING BTREE,
  INDEX `idx_trace_code`(`trace_code` ASC) USING BTREE,
  INDEX `idx_expiry_date`(`expiry_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '疫苗批次表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vaccine_batch
-- ----------------------------
INSERT INTO `vaccine_batch` VALUES (1, 1, '202401001', '2024-01-01', '2025-12-31', 100, 0, 100, '北京生物制品研究所', 'VB000120260317145917', NULL, 2, NULL, '2026-03-16 19:43:57', '2026-03-17 14:59:17', 0);
INSERT INTO `vaccine_batch` VALUES (2, 2, '202401002', '2024-01-01', '2025-12-31', 50, 0, 50, '上海生物制品研究所', 'VB000220260317145917', NULL, 2, NULL, '2026-03-16 19:43:57', '2026-03-17 14:59:17', 0);

SET FOREIGN_KEY_CHECKS = 1;
