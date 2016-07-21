CREATE TABLE usuario_perfil (
	id_usuario_perfil	integer	not null,
	client_id		text,
	conversation_id		text,
	dialog_id		text,
	nome			text,
	email			text,	
	data_nascimento		date,
	data_admissao		date
);

ALTER TABLE public.usuario_perfil OWNER TO question_magna;

ALTER TABLE usuario_perfil ADD PRIMARY KEY (id_usuario_perfil);

CREATE SEQUENCE sq_usuario_perfil
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_usuario_perfil OWNER TO question_magna;
ALTER SEQUENCE sq_usuario_perfil OWNED BY usuario_perfil.id_usuario_perfil;

SELECT pg_catalog.setval('sq_usuario_perfil', 0, true);


ALTER TABLE usuario_perfil ALTER COLUMN id_usuario_perfil SET DEFAULT nextval('sq_usuario_perfil'::regclass);

REVOKE ALL ON SEQUENCE sq_usuario_perfil FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_usuario_perfil FROM question_magna;
GRANT ALL ON SEQUENCE sq_usuario_perfil TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_usuario_perfil TO PUBLIC;