--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.3
-- Dumped by pg_dump version 9.5.1

-- Started on 2016-09-20 18:23:01

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
-- TOC entry 187 (class 1259 OID 57708)
-- Name: respostas; Type: TABLE; Schema: public; Owner: user_watson_ibm_ead_admin
--

CREATE TABLE respostas (
    id_resposta integer NOT NULL,
    id_cliente integer NOT NULL,
    id_topico integer NOT NULL,
    titulo text NOT NULL,
    definicao text NOT NULL
);


ALTER TABLE respostas OWNER TO user_watson_ibm_ead_admin;

--
-- TOC entry 195 (class 1259 OID 57733)
-- Name: sq_respostas; Type: SEQUENCE; Schema: public; Owner: user_watson_ibm_ead_admin
--

CREATE SEQUENCE sq_respostas
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sq_respostas OWNER TO user_watson_ibm_ead_admin;

--
-- TOC entry 2170 (class 0 OID 0)
-- Dependencies: 195
-- Name: sq_respostas; Type: SEQUENCE OWNED BY; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER SEQUENCE sq_respostas OWNED BY respostas.id_resposta;


--
-- TOC entry 2044 (class 2604 OID 57769)
-- Name: id_resposta; Type: DEFAULT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY respostas ALTER COLUMN id_resposta SET DEFAULT nextval('sq_respostas'::regclass);


--
-- TOC entry 2164 (class 0 OID 57708)
-- Dependencies: 187
-- Data for Name: respostas; Type: TABLE DATA; Schema: public; Owner: user_watson_ibm_ead_admin
--

COPY respostas (id_resposta, id_cliente, id_topico, titulo, definicao) FROM stdin;
1	1	1	Sobre_Curso	Esse curso tem como objetivo dar uma visão geral das API's do Watson
2	1	1	Aplicacao	O Watson tem sido aplicado em diversos tipos de indústrias. Na saúde, por exemplo, ele é treinado em dados médicos para que possa colaborar determinando diagnósticos.
\.


--
-- TOC entry 2172 (class 0 OID 0)
-- Dependencies: 195
-- Name: sq_respostas; Type: SEQUENCE SET; Schema: public; Owner: user_watson_ibm_ead_admin
--

SELECT pg_catalog.setval('sq_respostas', 2, true);


--
-- TOC entry 2046 (class 2606 OID 57790)
-- Name: respostas_pkey; Type: CONSTRAINT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY respostas
    ADD CONSTRAINT respostas_pkey PRIMARY KEY (id_resposta);


--
-- TOC entry 2047 (class 1259 OID 57799)
-- Name: unique_titulo; Type: INDEX; Schema: public; Owner: user_watson_ibm_ead_admin
--

CREATE UNIQUE INDEX unique_titulo ON respostas USING btree (titulo);


--
-- TOC entry 2048 (class 2606 OID 57820)
-- Name: respostas_classe; Type: FK CONSTRAINT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY respostas
    ADD CONSTRAINT respostas_classe FOREIGN KEY (id_topico) REFERENCES topicos(id_topico) MATCH FULL;


--
-- TOC entry 2049 (class 2606 OID 57825)
-- Name: respostas_cliente; Type: FK CONSTRAINT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY respostas
    ADD CONSTRAINT respostas_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) MATCH FULL;


--
-- TOC entry 2171 (class 0 OID 0)
-- Dependencies: 195
-- Name: sq_respostas; Type: ACL; Schema: public; Owner: user_watson_ibm_ead_admin
--

REVOKE ALL ON SEQUENCE sq_respostas FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_respostas FROM user_watson_ibm_ead_admin;
GRANT ALL ON SEQUENCE sq_respostas TO user_watson_ibm_ead_admin;
GRANT SELECT,UPDATE ON SEQUENCE sq_respostas TO PUBLIC;


-- Completed on 2016-09-20 18:23:01

--
-- PostgreSQL database dump complete
--

