-- ============================================================
-- 情侣纪念日与相册小程序 - 数据库初始化脚本
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS `couple_app` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `couple_app`;

-- ------------------------------------------------------------
-- 用户表
-- ------------------------------------------------------------
CREATE TABLE `user` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `openid`       VARCHAR(64)  NOT NULL                COMMENT '微信 openid',
    `unionid`      VARCHAR(64)  DEFAULT NULL            COMMENT '微信 unionid',
    `nickname`     VARCHAR(32)  DEFAULT ''               COMMENT '昵称',
    `avatar_url`   VARCHAR(512) DEFAULT ''               COMMENT '头像地址',
    `partner_id`   BIGINT       DEFAULT NULL            COMMENT '伴侣用户ID',
    `phone`        VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    `start_date`   DATE         DEFAULT NULL            COMMENT '在一起起始日',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`),
    KEY `idx_partner_id` (`partner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ------------------------------------------------------------
-- 纪念日主卡片表（在一起纪念日）
-- ------------------------------------------------------------
CREATE TABLE `anniversary` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主卡片ID',
    `user_id`         BIGINT       NOT NULL                COMMENT '用户ID',
    `start_date`      DATE         NOT NULL                COMMENT '在一起起始日',
    `background_image` VARCHAR(512) DEFAULT ''              COMMENT '背景图地址',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='纪念日主卡片表';

-- ------------------------------------------------------------
-- 事件纪念日表
-- ------------------------------------------------------------
CREATE TABLE `event` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '事件ID',
    `user_id`         BIGINT       NOT NULL                COMMENT '用户ID',
    `name`            VARCHAR(64)  NOT NULL                COMMENT '事件名称',
    `target_date`     DATE         NOT NULL                COMMENT '目标日',
    `repeat_type`     VARCHAR(16)  NOT NULL DEFAULT 'NONE' COMMENT '重复类型：NONE/YEARLY/MONTHLY',
    `background_image` VARCHAR(512) DEFAULT ''              COMMENT '背景图地址',
    `sort_order`      INT          NOT NULL DEFAULT 0      COMMENT '排序权重',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_target_date` (`user_id`, `target_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='事件纪念日表';

-- ------------------------------------------------------------
-- 相册表
-- ------------------------------------------------------------
CREATE TABLE `album` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '相册ID',
    `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
    `name`        VARCHAR(64)  NOT NULL                COMMENT '相册名称',
    `description` VARCHAR(256) DEFAULT ''               COMMENT '相册备注',
    `cover_image` VARCHAR(512) DEFAULT ''               COMMENT '封面图地址',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相册表';

-- ------------------------------------------------------------
-- 照片表
-- ------------------------------------------------------------
CREATE TABLE `photo` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '照片ID',
    `album_id`     BIGINT       NOT NULL                COMMENT '所属相册ID',
    `url`          VARCHAR(512) NOT NULL                COMMENT '原图地址',
    `thumbnail_url` VARCHAR(512) DEFAULT ''              COMMENT '缩略图地址',
    `upload_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    PRIMARY KEY (`id`),
    KEY `idx_album_id` (`album_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片表';

-- ------------------------------------------------------------
-- 插入示例数据
-- ------------------------------------------------------------

-- 示例用户
INSERT INTO `user` (`id`, `openid`, `nickname`, `avatar_url`, `partner_id`, `start_date`) VALUES
(1, 'wx_openid_001', '小明', '', NULL, '2024-02-14'),
(2, 'wx_openid_002', '小红', '', NULL, '2024-02-14');

-- 绑定伴侣关系
UPDATE `user` SET `partner_id` = 2 WHERE `id` = 1;
UPDATE `user` SET `partner_id` = 1 WHERE `id` = 2;

-- 纪念日主卡片
INSERT INTO `anniversary` (`user_id`, `start_date`, `background_image`) VALUES
(1, '2024-02-14', '');

-- 事件纪念日
INSERT INTO `event` (`user_id`, `name`, `target_date`, `repeat_type`, `sort_order`) VALUES
(1, '第一次旅行',  '2024-05-01', 'YEARLY',  1),
(1, '一周年纪念日', '2025-02-14', 'NONE',   2),
(1, '生日',        '2024-08-15', 'YEARLY',  3);

-- 相册
INSERT INTO `album` (`user_id`, `name`, `description`) VALUES
(1, '我们的日常', '记录甜蜜的每一天'),
(1, '旅行日记',  '一起去过的地方');

-- 照片
INSERT INTO `photo` (`album_id`, `url`, `thumbnail_url`) VALUES
(1, '/uploads/photo/001.jpg', '/uploads/photo/thumb/001.jpg'),
(1, '/uploads/photo/002.jpg', '/uploads/photo/thumb/002.jpg'),
(2, '/uploads/photo/003.jpg', '/uploads/photo/thumb/003.jpg');
