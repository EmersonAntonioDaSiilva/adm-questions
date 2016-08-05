CREATE TABLE dados_watson (
	id_watson		integer	not null,
	id_cliente		integer,
	id_topico		integer,
	typo_servico		text not null,
	usuario			text not null,
	senha			text not null,	
	data_cadastro		date
);

ALTER TABLE public.dados_watson OWNER TO question_magna;

ALTER TABLE dados_watson ADD PRIMARY KEY (id_watson);

CREATE SEQUENCE sq_dados_watson
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_dados_watson OWNER TO question_magna;
ALTER SEQUENCE sq_dados_watson OWNED BY dados_watson.id_watson;

SELECT pg_catalog.setval('sq_dados_watson', 0, true);


ALTER TABLE dados_watson ALTER COLUMN id_watson SET DEFAULT nextval('sq_dados_watson'::regclass);

REVOKE ALL ON SEQUENCE sq_dados_watson FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_dados_watson FROM question_magna;
GRANT ALL ON SEQUENCE sq_dados_watson TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_dados_watson TO PUBLIC;