--liquibase formatted sql
--changeset OlegGolubovsky:init_18052020123000_add_role_to_user
ALTER TABLE users ADD COLUMN role character varying (10);

