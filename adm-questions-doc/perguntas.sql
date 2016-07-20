CREATE TABLE perguntas (
	id_pergunta		integer	not null,
	id_resposta		integer	not null,
	descricao		text not null
);

ALTER TABLE public.perguntas OWNER TO question_magna;

ALTER TABLE perguntas ADD PRIMARY KEY (id_pergunta);
ALTER TABLE perguntas ADD CONSTRAINT perguntas_resposta FOREIGN KEY (id_resposta) REFERENCES respostas (id_resposta) MATCH FULL;

CREATE SEQUENCE sq_perguntas
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_perguntas OWNER TO question_magna;
ALTER SEQUENCE sq_perguntas OWNED BY perguntas.id_pergunta;

SELECT pg_catalog.setval('sq_perguntas', 0, true);


ALTER TABLE perguntas ALTER COLUMN id_pergunta SET DEFAULT nextval('sq_perguntas'::regclass);

REVOKE ALL ON SEQUENCE sq_perguntas FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_perguntas FROM question_magna;
GRANT ALL ON SEQUENCE sq_perguntas TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_perguntas TO PUBLIC;