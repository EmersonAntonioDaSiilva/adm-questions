CREATE TABLE campos_relacionados (
	id_campo	integer	not null,
	id_cliente		integer,
	id_topico		integer,
	typo_doc		text not null,
	campo			text not null,	
	data_cadastro		date
);

ALTER TABLE public.campos_relacionados OWNER TO question_magna;

ALTER TABLE campos_relacionados ADD PRIMARY KEY (id_campo);

CREATE SEQUENCE sq_campos_relacionados
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_campos_relacionados OWNER TO question_magna;
ALTER SEQUENCE sq_campos_relacionados OWNED BY campos_relacionados.id_campo;

SELECT pg_catalog.setval('sq_campos_relacionados', 0, true);


ALTER TABLE campos_relacionados ALTER COLUMN id_campo SET DEFAULT nextval('sq_campos_relacionados'::regclass);

REVOKE ALL ON SEQUENCE sq_campos_relacionados FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_campos_relacionados FROM question_magna;
GRANT ALL ON SEQUENCE sq_campos_relacionados TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_campos_relacionados TO PUBLIC;