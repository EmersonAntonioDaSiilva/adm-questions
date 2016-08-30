CREATE TABLE synonyms (
	id_synonyms		integer	not null,
	id_cliente		integer	not null,
	descricao		varchar(150) not null
	
);

ALTER TABLE public.synonyms OWNER TO question_magna;

ALTER TABLE synonyms ADD PRIMARY KEY (id_synonyms);
ALTER TABLE synonyms ADD CONSTRAINT synonyms_cliente FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) MATCH FULL;

CREATE SEQUENCE sq_synonyms
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_synonyms OWNER TO question_magna;
ALTER SEQUENCE sq_synonyms OWNED BY synonyms.id_synonyms;

SELECT pg_catalog.setval('sq_synonyms', 0, true);


ALTER TABLE synonyms ALTER COLUMN id_synonyms SET DEFAULT nextval('sq_synonyms'::regclass);

REVOKE ALL ON SEQUENCE sq_synonyms FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_synonyms FROM question_magna;
GRANT ALL ON SEQUENCE sq_synonyms TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_synonyms TO PUBLIC;
