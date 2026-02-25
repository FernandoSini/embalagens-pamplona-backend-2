CREATE TABLE IF NOT EXISTS `users`
(
    `id`             BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT UNIQUE,
    `name`           varchar(200)     NOT NULL,
    `last_name`      VARCHAR(50)      NOT NULL,
    `email`          VARCHAR(150)     NOT NULL UNIQUE,
    `gender`         VARCHAR(20)      NULL,
    `birthday`       VARCHAR(10)      NULL,
    `cpf_cnpj`       VARCHAR(14)      NULL,
    `phone`          VARCHAR(11)      NOT NULL,
    `password`       VARCHAR(200)     NOT NULL,
    `email_verified` BOOLEAN          NOT NULL,
    `active`         BOOLEAN          NOT NULL
)