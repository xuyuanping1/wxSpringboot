-- ============================================================
-- 微信云托管 MySQL 初始化脚本 (couple_app)
-- ============================================================

CREATE DATABASE IF NOT EXISTS `couple_app` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `couple_app`;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `openid` VARCHAR(100) NOT NULL,
  `unionid` VARCHAR(100),
  `nickname` VARCHAR(50),
  `avatar_url` VARCHAR(500),
  `partner_id` BIGINT,
  `phone` VARCHAR(20),
  `start_date` DATE,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_partner_id` (`partner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 纪念日主卡片表
CREATE TABLE IF NOT EXISTS `anniversary` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `start_date` DATE,
  `background_image` VARCHAR(500),
  `card_bg_blur` INT DEFAULT 0,
  `home_bg_image` VARCHAR(500),
  `home_bg_blur` INT DEFAULT 0,
  `custom_title` VARCHAR(100),
  `text_color` VARCHAR(20) DEFAULT '',
  `card_text_color` VARCHAR(20) DEFAULT '',
  `card_bg_position_x` INT DEFAULT 50,
  `card_bg_position_y` INT DEFAULT 50,
  `card_bg_scale` INT DEFAULT 100,
  `home_bg_position_x` INT DEFAULT 50,
  `home_bg_position_y` INT DEFAULT 50,
  `home_bg_scale` INT DEFAULT 100,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 事件纪念日表
CREATE TABLE IF NOT EXISTS `event` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `target_date` DATE NOT NULL,
  `repeat_type` VARCHAR(20) DEFAULT 'NONE',
  `remark` VARCHAR(500),
  `background_image` VARCHAR(500),
  `background_blur` INT DEFAULT 0,
  `sort_order` INT DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 相册表
CREATE TABLE IF NOT EXISTS `album` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(500),
  `cover_image` VARCHAR(500),
  `cover_blur` INT DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 照片表
CREATE TABLE IF NOT EXISTS `photo` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `album_id` BIGINT NOT NULL,
  `url` VARCHAR(500) NOT NULL,
  `thumbnail_url` VARCHAR(500),
  `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `sort_order` INT DEFAULT 0,
  KEY `idx_album_id` (`album_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. 事件照片表
CREATE TABLE IF NOT EXISTS `event_photo` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `event_id` BIGINT NOT NULL,
  `occurrence_date` DATE NOT NULL,
  `url` VARCHAR(500) NOT NULL,
  `thumbnail_url` VARCHAR(500),
  `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_event_occurrence` (`event_id`, `occurrence_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. 伴侣邀请表
CREATE TABLE IF NOT EXISTS `invite` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `token` VARCHAR(64) NOT NULL,
  `status` VARCHAR(20) DEFAULT 'PENDING',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_token` (`token`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
