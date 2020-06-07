

create table if not exists corporations
(
	id integer not null
		constraint corporations_pkey
			primary key,
	name character varying (100) not null,
	create_at timestamp
);

alter table corporations owner to postgres;

create table if not exists phones
(
	number character varying (12) not null
		constraint phones_pkey
			primary key
);

alter table phones owner to postgres;

create table if not exists contacts
(
	id integer not null
		constraint contact_pkey
			primary key,
	name character varying (100),
	created_at timestamp,
	updated_at timestamp,
	phone_number character varying (100) not null
		constraint fk_contact_phone
			references phones
);

alter table contacts owner to postgres;

create table if not exists users
(
	email character varying (100) not null
		constraint email_unique
			unique,
	first_name character varying (100),
	last_name character varying (100),
	password character varying (100),
	id bigint not null
		constraint user_pkey
			primary key,
	register_link character varying (36),
	user_status character varying (15),
	temp_token character varying (36),
	login_date timestamp,
	created_at timestamp,
	updated_at timestamp,
	corporation_id integer
		constraint fk_corporation_user
			references corporations
);

alter table users owner to postgres;

create table if not exists contact_lists
(
	id bigint not null
		constraint contact_list_pkey
			primary key,
	user_id bigint not null
		constraint fk_contact_list_user
			references users,
	name character varying (100) not null,
	created_at timestamp not null,
	updated_at timestamp
);

alter table contact_lists owner to postgres;

create table if not exists contact_to_contact_list
(
	contact_list_id integer
		constraint fk_contact_list
			references contact_lists,
	contact_id integer
		constraint contact
			references contacts
);

alter table contact_to_contact_list owner to postgres;

create table if not exists whatsapp_accounts
(
	id integer not null
		constraint whatsapp_accounts_pkey
			primary key,
	message_api_url character varying (100),
	app_number character varying (100),
	"app_template_namespace" character varying (100),
	corporation_id integer not null
		constraint fk_wa_acoount
			references corporations
);

alter table whatsapp_accounts owner to postgres;

create table if not exists whatsapp_templates
(
	id integer not null
		constraint whatsapp_templates_pkey
			primary key,
	body text,
	name character varying (100),
	lang character varying (10)
);

alter table whatsapp_templates owner to postgres;

create table if not exists campaigns
(
	id integer not null
		constraint campaigns_pkey
			primary key,
	contact_list_id integer
		constraint fk_to_cm
			references contact_lists,
	wa_template_id integer
		constraint campaign_fk
			references whatsapp_templates,
	user_id integer
		constraint user_cm_fk
			references users,
	created_at timestamp not null,
	sent_at timestamp,
	name character varying (100),
	status character varying (10),
	"json_content" json
);

alter table campaigns owner to postgres;

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 1000000000000
    CACHE 1;

ALTER TABLE hibernate_sequence OWNER TO postgres;

