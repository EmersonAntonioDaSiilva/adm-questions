CREATE TABLE respostas (
	id_resposta		integer	not null,
	id_cliente		integer	not null,
	id_classe		integer	not null,
	descricao		text not null,
	definicao		text not null
);

ALTER TABLE public.respostas OWNER TO question_magna;

ALTER TABLE respostas ADD PRIMARY KEY (id_resposta);
ALTER TABLE respostas ADD CONSTRAINT respostas_classe FOREIGN KEY (id_classe) REFERENCES classes (id_classe) MATCH FULL;
ALTER TABLE respostas ADD CONSTRAINT respostas_cliente FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) MATCH FULL;


CREATE SEQUENCE sq_respostas
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_respostas OWNER TO question_magna;
ALTER SEQUENCE sq_respostas OWNED BY respostas.id_resposta;

SELECT pg_catalog.setval('sq_respostas', 0, true);


ALTER TABLE respostas ALTER COLUMN id_resposta SET DEFAULT nextval('sq_respostas'::regclass);

REVOKE ALL ON SEQUENCE sq_respostas FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_respostas FROM question_magna;
GRANT ALL ON SEQUENCE sq_respostas TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_respostas TO PUBLIC;