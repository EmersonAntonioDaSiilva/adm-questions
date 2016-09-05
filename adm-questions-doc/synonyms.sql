--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.3
-- Dumped by pg_dump version 9.5.3

-- Started on 2016-09-02 12:55:49

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 203 (class 1259 OID 33748)
-- Name: synonyms; Type: TABLE; Schema: public; Owner: question_magna
--

CREATE TABLE synonyms (
    id_synonyms integer NOT NULL,
    id_cliente integer NOT NULL,
    descricao character varying(150) NOT NULL,
    mapeado boolean
);


ALTER TABLE synonyms OWNER TO question_magna;

--
-- TOC entry 204 (class 1259 OID 33758)
-- Name: sq_synonyms; Type: SEQUENCE; Schema: public; Owner: question_magna
--

CREATE SEQUENCE sq_synonyms
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sq_synonyms OWNER TO question_magna;

--
-- TOC entry 2168 (class 0 OID 0)
-- Dependencies: 204
-- Name: sq_synonyms; Type: SEQUENCE OWNED BY; Schema: public; Owner: question_magna
--

ALTER SEQUENCE sq_synonyms OWNED BY synonyms.id_synonyms;


--
-- TOC entry 2044 (class 2604 OID 33760)
-- Name: id_synonyms; Type: DEFAULT; Schema: public; Owner: question_magna
--

ALTER TABLE ONLY synonyms ALTER COLUMN id_synonyms SET DEFAULT nextval('sq_synonyms'::regclass);


--
-- TOC entry 2170 (class 0 OID 0)
-- Dependencies: 204
-- Name: sq_synonyms; Type: SEQUENCE SET; Schema: public; Owner: question_magna
--

SELECT pg_catalog.setval('sq_synonyms', 14, true);


--
-- TOC entry 2162 (class 0 OID 33748)
-- Dependencies: 203
-- Data for Name: synonyms; Type: TABLE DATA; Schema: public; Owner: question_magna
--

COPY synonyms (id_synonyms, id_cliente, descricao, mapeado) FROM stdin;
1	1	GB,gib,gigabyte,gigabytes	\N
2	1	MB,mib,megabyte,megabytes	\N
3	1	Televisão, Televisões, TV, TVs	\N
4	1	Termo,Termos,Frase,Frases,Texto,Textos	\N
5	1	Relevância,relevâncias,significado,significados	\N
6	1	Língua,Línguas,Idiomas,Idiomas	\N
7	1	Féria,Férias,Repouso,Gozo	\N
8	1	Acordo,Convenção,Permissão	\N
9	1	Pecuniário,Em dinheiro	\N
10	1	Abono,Garantia,Fiança	\N
11	1	Vender,Transferir,Negociar	\N
12	1	Pedir,Solicitar,Demandar,Requerer	\N
13	1	Acumular,Juntar,Reunir,Ligar,Ajuntar,Associar	\N
14	1	Fracionar,Dividir,Romper,Fragmentar,Desagregar,Desmembrar,Subdividir	\N
\.


--
-- TOC entry 2046 (class 2606 OID 33752)
-- Name: synonyms_pkey; Type: CONSTRAINT; Schema: public; Owner: question_magna
--

ALTER TABLE ONLY synonyms
    ADD CONSTRAINT synonyms_pkey PRIMARY KEY (id_synonyms);


--
-- TOC entry 2047 (class 2606 OID 33753)
-- Name: synonyms_cliente; Type: FK CONSTRAINT; Schema: public; Owner: question_magna
--

ALTER TABLE ONLY synonyms
    ADD CONSTRAINT synonyms_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) MATCH FULL;


--
-- TOC entry 2169 (class 0 OID 0)
-- Dependencies: 204
-- Name: sq_synonyms; Type: ACL; Schema: public; Owner: question_magna
--

REVOKE ALL ON SEQUENCE sq_synonyms FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_synonyms FROM question_magna;
GRANT ALL ON SEQUENCE sq_synonyms TO question_magna;
GRANT SELECT,UPDATE ON SEQUENCE sq_synonyms TO PUBLIC;


-- Completed on 2016-09-02 12:55:49

--
-- PostgreSQL database dump complete
--

