CREATE TABLE IF NOT EXISTS users
(
    user_id                                     BIGINT       NOT NULL AUTO_INCREMENT,
    nickname                                    VARCHAR(255) NOT NULL,
    email                                       VARCHAR(255) NOT NULL,
    profile_image_url                           TEXT         NULL,
    now_max_consecutive_participation_day_count BIGINT       NULL     DEFAULT 0,
    refresh_token                               VARCHAR(255) NULL,
    created_at                                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                                  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at                                  TIMESTAMP    NULL,

    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS challenge_categories
(
    challenge_category_id BIGINT       NOT NULL AUTO_INCREMENT,
    title                 VARCHAR(255) NOT NULL,
    description           TEXT         NOT NULL,
    image_url             TEXT         NULL,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at            TIMESTAMP    NULL,

    PRIMARY KEY (challenge_category_id)
);

CREATE TABLE IF NOT EXISTS challenges
(
    challenge_id                     BIGINT       NOT NULL AUTO_INCREMENT,
    challenge_category_id            BIGINT       NOT NULL,
    title                            VARCHAR(255) NOT NULL,
    pre_description                  TEXT         NOT NULL,
    description                      TEXT         NOT NULL,
    certification_method_description TEXT         NOT NULL,
    main_image_url                   TEXT         NULL,
    certification_example_image_url  TEXT         NULL,
    created_at                       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at                       TIMESTAMP    NULL,

    PRIMARY KEY (challenge_id),
    FOREIGN KEY (challenge_category_id) REFERENCES challenge_categories (challenge_category_id)
);

CREATE TABLE IF NOT EXISTS user_challenges
(
    user_challenge_id                       BIGINT      NOT NULL AUTO_INCREMENT,
    user_id                                 BIGINT      NOT NULL,
    challenge_id                            BIGINT      NOT NULL,
    status                                  VARCHAR(50) NOT NULL DEFAULT 'running',
    participant_days                        INTEGER     NOT NULL,
    ice_count                               TINYINT     NOT NULL DEFAULT 0,
    now_consecutive_participation_day_count BIGINT      NOT NULL DEFAULT 0,
    max_consecutive_participation_day_count BIGINT      NOT NULL DEFAULT 0,
    total_participation_day_count           BIGINT      NOT NULL,
    report_message                          TEXT        NULL,
    created_at                              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at                              TIMESTAMP   NULL,

    PRIMARY KEY (user_challenge_id, user_id),
    FOREIGN KEY (challenge_id) REFERENCES challenges (challenge_id)
);

CREATE TABLE IF NOT EXISTS user_challenge_histories
(
    user_challenge_history_id BIGINT      NOT NULL AUTO_INCREMENT,
    user_challenge_id         BIGINT      NOT NULL,
    date                      DATE        NOT NULL,
    status                    VARCHAR(50) NOT NULL DEFAULT 'fail',
    certificated_image_url    TEXT        NULL,
    created_at                TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at                TIMESTAMP   NULL,

    PRIMARY KEY (user_challenge_history_id),
    FOREIGN KEY (user_challenge_id) REFERENCES user_challenges (user_challenge_id)
);

CREATE TABLE IF NOT EXISTS feeds
(
    feed_id           BIGINT    NOT NULL AUTO_INCREMENT,
    user_challenge_id BIGINT    NOT NULL,
    user_id           BIGINT    NOT NULL,
    image_url         TEXT      NOT NULL,
    content           TEXT      NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMP NULL,

    PRIMARY KEY (feed_id)
);

CREATE TABLE IF NOT EXISTS feed_projections
(
    feed_id                                          BIGINT       NOT NULL,
    challenge_category_TITLE                         VARCHAR(255) NOT NULL,
    challenge_title                                  VARCHAR(255) NOT NULL,
    user_name                                        VARCHAR(255) NOT NULL,
    user_profile_image_url                           TEXT         NULL,
    user_now_max_consecutive_participation_day_count BIGINT       NULL,
    feed_image_url                                   TEXT         NOT NULL,
    feed_content                                     TEXT         NULL,
    created_at                                       TIMESTAMP    NOT NULL,
    updated_at                                       TIMESTAMP    NOT NULL,
    deleted_at                                       TIMESTAMP    NULL,

    PRIMARY KEY (feed_id),
    FOREIGN KEY (feed_id) REFERENCES feeds (feed_id)
);

CREATE TABLE IF NOT EXISTS notifications
(
    notification_id BIGINT      NOT NULL AUTO_INCREMENT,
    user_id         BIGINT      NOT NULL,
    type            VARCHAR(50) NOT NULL,
    payload         TEXT        NULL,
    status          VARCHAR(50) NOT NULL DEFAULT 'FAIL',
    created_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMP   NULL,

    PRIMARY KEY (notification_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS notification_settings
(
    user_id               BIGINT       NOT NULL,
    pre_daily_enabled     boolean      NOT NULL DEFAULT false,
    end_challenge_enabled boolean      NOT NULL DEFAULT false,
    fcm_token             VARCHAR(512) NULL,

    PRIMARY KEY (user_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);
