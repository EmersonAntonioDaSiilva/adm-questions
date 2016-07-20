CREATE TABLE classes (
	id_classe		integer	not null,
	id_cliente		integer	not null,
	descricao		text not null
	
);

ALTER TABLE public.classes OWNER TO question_magna;

ALTER TABLE classes ADD PRIMARY KEY (id_classe);
ALTER TABLE classes ADD CONSTRAINT classes_cliente FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) MATCH FULL;

CREATE SEQUENCE sq_classes
    START WITH 0
    INCREMENT BY 1
    NO MAXVALUE
    MINVALUE 0
    CACHE 1;


ALTER TABLE public.sq_classes OWNER TO question_magna;
ALTER SEQUENCE sq_classes OWNED BY classes.id_classe;

SELECT pg_catalog.setval('sq_classes', 0, true);


ALTER TABLE classes ALTER COLUMN id_classe SET DEFAULT nextval('sq_classes'::regclass);

REVOKE ALL ON SEQUENCE sq_classes FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_classes FROM question_magna;
GRANT ALL ON SEQUENCE sq_classes TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_classes TO PUBLIC;