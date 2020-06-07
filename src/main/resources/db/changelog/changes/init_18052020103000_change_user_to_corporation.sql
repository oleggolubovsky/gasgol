--liquibase formatted sql
--changeset OlegGolubovsky:init_18052020103000_change_user_to_corporation
ALTER TABLE whatsapp_templates DROP COLUMN user_id;

ALTER TABLE whatsapp_templates ADD COLUMN corporation_id bigint constraint fk_template_corporation references corporations;

