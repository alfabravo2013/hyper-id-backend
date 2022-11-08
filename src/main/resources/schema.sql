CREATE TABLE hyper_user (
    username VARCHAR(64) NOT NULL UNIQUE PRIMARY KEY,
    name VARCHAR(64) NOT NULL DEFAULT '',
    surname VARCHAR(64) NOT NULL DEFAULT '',
    password VARCHAR(128) NOT NULL,
    access_token VARCHAR(64)
);
