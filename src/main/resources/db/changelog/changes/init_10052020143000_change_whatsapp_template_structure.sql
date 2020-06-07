--liquibase formatted sql
--changeset OlegGolubovsky:10052020143000_change_whatsapp_template_structure
ALTER TABLE whatsapp_templates DROP COLUMN body;
ALTER TABLE whatsapp_templates DROP COLUMN lang;

ALTER TABLE whatsapp_templates ADD COLUMN status character varying (10);
ALTER TABLE whatsapp_templates ADD COLUMN "json_content" json;
ALTER TABLE whatsapp_templates ADD COLUMN created_at timestamp;
ALTER TABLE whatsapp_templates ADD COLUMN user_id bigint not null constraint fk_template_user references users;

