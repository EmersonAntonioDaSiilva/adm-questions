CREATE TABLE prot_words (
	id_prot_words		integer	not null,
	id_cliente		integer	not null,
	descricao		varchar(50) not null
	
);

ALTER TABLE public.prot_words OWNER TO question_magna;

ALTER TABLE prot_words ADD PRIMARY KEY (id_prot_words);
ALTER TABLE prot_words ADD CONSTRAINT prot_words_cliente FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) MATCH FULL;

CREATE SEQUENCE sq_prot_words
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_prot_words OWNER TO question_magna;
ALTER SEQUENCE sq_prot_words OWNED BY prot_words.id_prot_words;

SELECT pg_catalog.setval('sq_prot_words', 0, true);


ALTER TABLE prot_words ALTER COLUMN id_prot_words SET DEFAULT nextval('sq_prot_words'::regclass);

REVOKE ALL ON SEQUENCE sq_prot_words FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_prot_words FROM question_magna;
GRANT ALL ON SEQUENCE sq_prot_words TO question_magna;
GRANT SELECT, UPDATE ON SEQUENCE sq_prot_words TO PUBLIC;




INSERT INTO prot_words (id_cliente, descricao) VALUES (1, 'força');
INSERT INTO prot_words (id_cliente, descricao) VALUES (1, 'destruir');
INSERT INTO prot_words (id_cliente, descricao) VALUES (1, 'falar');
INSERT INTO prot_words (id_cliente, descricao) VALUES (1, 'buscar');
INSERT INTO prot_words (id_cliente, descricao) VALUES (1, 'anexar');