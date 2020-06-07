--liquibase formatted sql
--changeset OlegGolubovsky:init_14052020233000_add_user_to_contact
ALTER TABLE contacts ADD COLUMN user_id bigint constraint fk_contact_user references users;

