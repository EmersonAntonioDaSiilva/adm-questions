--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.3
-- Dumped by pg_dump version 9.5.1

-- Started on 2016-09-20 18:23:29

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
-- TOC entry 184 (class 1259 OID 57693)
-- Name: dados_watson; Type: TABLE; Schema: public; Owner: user_watson_ibm_ead_admin
--

CREATE TABLE dados_watson (
    id_watson integer NOT NULL,
    id_cliente integer,
    id_topico integer,
    type_servico text NOT NULL,
    usuario text NOT NULL,
    senha text NOT NULL,
    data_cadastro date
);


ALTER TABLE dados_watson OWNER TO user_watson_ibm_ead_admin;

--
-- TOC entry 192 (class 1259 OID 57727)
-- Name: sq_dados_watson; Type: SEQUENCE; Schema: public; Owner: user_watson_ibm_ead_admin
--

CREATE SEQUENCE sq_dados_watson
    START WITH 0
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sq_dados_watson OWNER TO user_watson_ibm_ead_admin;

--
-- TOC entry 2167 (class 0 OID 0)
-- Dependencies: 192
-- Name: sq_dados_watson; Type: SEQUENCE OWNED BY; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER SEQUENCE sq_dados_watson OWNED BY dados_watson.id_watson;


--
-- TOC entry 2044 (class 2604 OID 57766)
-- Name: id_watson; Type: DEFAULT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY dados_watson ALTER COLUMN id_watson SET DEFAULT nextval('sq_dados_watson'::regclass);


--
-- TOC entry 2161 (class 0 OID 57693)
-- Dependencies: 184
-- Data for Name: dados_watson; Type: TABLE DATA; Schema: public; Owner: user_watson_ibm_ead_admin
--

COPY dados_watson (id_watson, id_cliente, id_topico, type_servico, usuario, senha, data_cadastro) FROM stdin;
3	1	1	DOCUMENT_CONVERSION	f7ea56e9-3830-4afa-812a-d97b108604d0	wlHwtFzpdFia	2016-08-05
2	1	1	DIALOG	1365890f-4afa-45c2-89c9-b3026c218e20	1BDytZLDRtRc	2016-08-05
1	1	1	NATURAL_LANGUAGE_CLASSIFIER	78526cde-454f-416e-bac5-c86a6f672df0	iBEJcNxX4nvf	2016-08-05
4	1	1	RETRIEVE_AND_RANK	e5dd04f3-e1e3-452b-ab6e-f44b4e3fc437	6O5MerleH8kf	2016-08-05
\.


--
-- TOC entry 2169 (class 0 OID 0)
-- Dependencies: 192
-- Name: sq_dados_watson; Type: SEQUENCE SET; Schema: public; Owner: user_watson_ibm_ead_admin
--

SELECT pg_catalog.setval('sq_dados_watson', 4, true);


--
-- TOC entry 2046 (class 2606 OID 57784)
-- Name: dados_watson_pkey; Type: CONSTRAINT; Schema: public; Owner: user_watson_ibm_ead_admin
--

ALTER TABLE ONLY dados_watson
    ADD CONSTRAINT dados_watson_pkey PRIMARY KEY (id_watson);


--
-- TOC entry 2168 (class 0 OID 0)
-- Dependencies: 192
-- Name: sq_dados_watson; Type: ACL; Schema: public; Owner: user_watson_ibm_ead_admin
--

REVOKE ALL ON SEQUENCE sq_dados_watson FROM PUBLIC;
REVOKE ALL ON SEQUENCE sq_dados_watson FROM user_watson_ibm_ead_admin;
GRANT ALL ON SEQUENCE sq_dados_watson TO user_watson_ibm_ead_admin;
GRANT SELECT,UPDATE ON SEQUENCE sq_dados_watson TO PUBLIC;


-- Completed on 2016-09-20 18:23:29

--
-- PostgreSQL database dump complete
--

