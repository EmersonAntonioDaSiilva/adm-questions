--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.3
-- Dumped by pg_dump version 9.5.1

-- Started on 2016-09-20 18:22:45

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
-- TOC entry 185 (class 1259 OID 57699)
-- Name: perguntas; Type: TABLE; Schema: public; Owner: user_watson_ibm_ead_admin
--

CREATE TABLE perguntas (
    id_pergunta integer NOT NULL,
    id_resposta integer NOT NULL,
    descricao text NOT NULL
);


ALTER TABLE perguntas OWNER TO user_watson_ibm_ead_admin;

--
-- TOC entry 193 (class 1259 OID 57729)
-- Name: sq_perguntas; Type: SEQUENCE; Schema: public; Owner: user_watson_ibm_ead_admin
--

CREATE SEQUENCE sq_perguntas
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sq_perguntas OWNER TO user_watson_ibm_ead_admin;

--
-- TOC entry 2168 (class 0 OID 0)
-- Dependencies: 193
-- Name: sq_perguntas; Type: SEQUENCE OWNED BY; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER SEQUENCE sq_perguntas OWNED BY perguntas.id_pergunta;


--
-- TOC entry 2044 (class 2604 OID 57767)
-- Name: id_pergunta; Type: DEFAULT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY perguntas ALTER COLUMN id_pergunta SET DEFAULT nextval('sq_perguntas'::regclass);


--
-- TOC entry 2162 (class 0 OID 57699)
-- Dependencies: 185
-- Data for Name: perguntas; Type: TABLE DATA; Schema: public; Owner: user_watson_ibm_ead_admin
--

COPY perguntas (id_pergunta, id_resposta, descricao) FROM stdin;
1	1	Qual o objetivo do curso?
2	1	Do que se trata o curso?
3	1	Qual a visão geral do curso?
4	2	Que áreas podem se beneficiar mais da computação cognitiva?
5	2	Onde o Watson pode ser utilizado?
6	2	As áreas que podem utilizar o Watson?
\.


--
-- TOC entry 2170 (class 0 OID 0)
-- Dependencies: 193
-- Name: sq_perguntas; Type: SEQUENCE SET; Schema: public; Owner: user_watson_ibm_ead_admin
--

SELECT pg_catalog.setval('sq_perguntas', 6, true);


--
-- TOC entry 2046 (class 2606 OID 57786)
-- Name: perguntas_pkey; Type: CONSTRAINT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY perguntas
    ADD CONSTRAINT perguntas_pkey PRIMARY KEY (id_pergunta);


--
-- TOC entry 2047 (class 2606 OID 57810)
-- Name: perguntas_resposta; Type: FK CONSTRAINT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY perguntas
    ADD CONSTRAINT perguntas_resposta FOREIGN KEY (id_resposta) REFERENCES respostas(id_resposta) MATCH FULL;


--
-- TOC entry 2169 (class 0 OID 0)
-- Dependencies: 193
-- Name: sq_perguntas; Type: ACL; Schema: public; Owner: user_watson_ibm_ead_admin
--

REVOKE ALL ON SEQUENCE sq_perguntas FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_perguntas FROM user_watson_ibm_ead_admin;
GRANT ALL ON SEQUENCE sq_perguntas TO user_watson_ibm_ead_admin;
GRANT SELECT,UPDATE ON SEQUENCE sq_perguntas TO PUBLIC;


-- Completed on 2016-09-20 18:22:45

--
-- PostgreSQL database dump complete
--

