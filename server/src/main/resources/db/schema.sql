CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT AUTO_INCREMENT,
                                     nickname VARCHAR(20) NOT NULL,
    profile_image_url VARCHAR(255) NULL,
    device_token VARCHAR(255) NOT NULL,
    email VARCHAR(50) NOT NULL,
    refresh_token VARCHAR(255) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,

    PRIMARY KEY (user_id),
    CONSTRAINT email_unique UNIQUE (email)
    );