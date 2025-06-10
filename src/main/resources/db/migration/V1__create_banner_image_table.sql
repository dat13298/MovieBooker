CREATE TABLE banner_image
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url   VARCHAR(255),
    title       VARCHAR(255),
    description TEXT,
    user_id     BIGINT NOT NULL,
    CONSTRAINT fk_banner_user FOREIGN KEY (user_id) REFERENCES users (id)
);
