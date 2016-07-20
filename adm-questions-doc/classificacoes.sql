CREATE TABLE classificacoes (
	id_classificacao	integer	not null,
	id_cliente		integer	not null,
	pergunta		text not null,
	resposta		text not null,
	confidence		double precision not null,
	dataCadastro 		timestamp without time zone
);

ALTER TABLE public.classificacoes OWNER TO question_magna;

ALTER TABLE classificacoes ADD PRIMARY KEY (id_classificacao);
ALTER TABLE classificacoes ADD CONSTRAINT classificacoes_cliente FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) MATCH FULL;

CREATE SEQUENCE sq_classificacoes
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_classificacoes OWNER TO question_magna;
ALTER SEQUENCE sq_classificacoes OWNED BY classificacoes.id_classificacao;

SELECT pg_catalog.setval('sq_classificacoes', 0, true);


ALTER TABLE classificacoes ALTER COLUMN id_classificacao SET DEFAULT nextval('sq_classificacoes'::regclass);

REVOKE ALL ON SEQUENCE sq_classificacoes FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_classificacoes FROM question_magna;
GRANT ALL ON SEQUENCE sq_classificacoes TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_classificacoes TO PUBLIC;