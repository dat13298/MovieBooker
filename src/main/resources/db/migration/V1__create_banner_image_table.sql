CREATE TABLE banner_images
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_url   VARCHAR(255) NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    user_id     BIGINT       NOT NULL,
    CONSTRAINT fk_banner_user FOREIGN KEY (user_id) REFERENCES users (id)
);
