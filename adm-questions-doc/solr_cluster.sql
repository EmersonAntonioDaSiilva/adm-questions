-- Table: public.solr_cluster

-- DROP TABLE public.solr_cluster;

CREATE TABLE public.solr_cluster
(
  id_solr_cluster integer NOT NULL DEFAULT nextval('sq_solr_cluster'::regclass),
  id_cliente integer NOT NULL,
  nome_cluster text NOT NULL,
  id_cluster text,
  unit_cluster integer,
  status_cluster text,
  datacadastro timestamp without time zone,
  nome_config text,
  nome_collection text,
  unidade integer,
  CONSTRAINT solr_cluster_pkey PRIMARY KEY (id_solr_cluster),
  CONSTRAINT solr_cluster_cliente FOREIGN KEY (id_cliente)
      REFERENCES public.clientes (id_cliente) MATCH FULL
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.solr_cluster
  OWNER TO question_magna;
