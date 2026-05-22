-- 情侣邀请表
USE `couple_app`;

CREATE TABLE `invite` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '邀请ID',
    `user_id`     BIGINT       NOT NULL                COMMENT '发起邀请的用户ID',
    `token`       VARCHAR(64)  NOT NULL                COMMENT '邀请令牌',
    `status`      VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/ACCEPTED/EXPIRED',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token` (`token`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='情侣邀请表';
