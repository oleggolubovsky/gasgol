--liquibase formatted sql
--changeset OlegGolubovsky:init_12052020223000_change_whatsapp_account_relation
ALTER TABLE whatsapp_accounts DROP COLUMN corporation_id;

ALTER TABLE corporations ADD COLUMN whatsapp_account_id bigint constraint fk_corporation_accont references whatsapp_accounts;

