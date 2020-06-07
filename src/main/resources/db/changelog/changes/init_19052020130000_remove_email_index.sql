--liquibase formatted sql
--changeset OlegGolubovsky:init_19052020130000_remove_email_index
ALTER TABLE users DROP CONSTRAINT email_unique;

