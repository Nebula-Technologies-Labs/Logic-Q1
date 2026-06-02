CREATE TABLE watch_list
(
    id           UUID NOT NULL,
    name         VARCHAR(255),
    email        VARCHAR(255),
    phone_number VARCHAR(255),
    CONSTRAINT pk_watch_list PRIMARY KEY (id)
);