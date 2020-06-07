--liquibase formatted sql
--changeset OlegGolubovsky:upadate_19052020160000_user_status_original_to_string
UPDATE users SET user_status = 'BLOCKED' WHERE user_status = '0';
UPDATE users SET user_status = 'ACTIVE' WHERE user_status = '1';
UPDATE users SET user_status = 'WAITING' WHERE user_status = '2';
UPDATE users SET user_status = 'INVITED' WHERE user_status = '3';
UPDATE users SET user_status = 'FIRST' WHERE user_status = '4';
UPDATE users SET user_status = 'REMOVED' WHERE user_status = '5';