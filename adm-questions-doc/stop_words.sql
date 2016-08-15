CREATE TABLE stop_words (
	id_stop_words		integer	not null,
	id_cliente		integer	not null,
	descricao		varchar(50) not null
	
);

ALTER TABLE public.stop_words OWNER TO question_magna;

ALTER TABLE stop_words ADD PRIMARY KEY (id_stop_words);
ALTER TABLE stop_words ADD CONSTRAINT stop_words_cliente FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) MATCH FULL;

CREATE SEQUENCE sq_stop_words
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_stop_words OWNER TO question_magna;
ALTER SEQUENCE sq_stop_words OWNED BY stop_words.id_stop_words;

SELECT pg_catalog.setval('sq_stop_words', 0, true);


ALTER TABLE stop_words ALTER COLUMN id_stop_words SET DEFAULT nextval('sq_stop_words'::regclass);

REVOKE ALL ON SEQUENCE sq_stop_words FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_stop_words FROM question_magna;
GRANT ALL ON SEQUENCE sq_stop_words TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_stop_words TO PUBLIC;