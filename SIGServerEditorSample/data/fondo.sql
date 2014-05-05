--
-- PostgreSQL database dump
--

-- Started on 2012-07-23 19:57:59 UTC

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 252 (class 1259 OID 18181)
-- Dependencies: 4100 4101 4102 10 1718
-- Name: agm_fondo; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE agm_fondo (
    id integer NOT NULL,
    the_geom geometry NOT NULL,
    nombre character varying(100) NOT NULL,
    CONSTRAINT enforce_dims_the_geom CHECK ((ndims(the_geom) = 2)),
    CONSTRAINT enforce_geotype_the_geom CHECK (((geometrytype(the_geom) = 'POLYGON'::text) OR (the_geom IS NULL))),
    CONSTRAINT enforce_srid_the_geom CHECK ((srid(the_geom) = 32721))
);


--
-- TOC entry 4105 (class 0 OID 18181)
-- Dependencies: 252
-- Data for Name: agm_fondo; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO agm_fondo (id, the_geom, nombre) VALUES (1, '0103000020D17F0000010000000500000000000000804F124100000000B881564100000000A0F0294100000000B881564100000000A0F0294100000000A8CB584100000000804F124100000000A8CB584100000000804F124100000000B8815641', 'Rio de la Plata');


--
-- TOC entry 4104 (class 2606 OID 29554)
-- Dependencies: 252 252
-- Name: fond_agm_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY agm_fondo
    ADD CONSTRAINT fond_agm_pk PRIMARY KEY (id);


-- Completed on 2012-07-23 19:57:59 UTC

--
-- PostgreSQL database dump complete
--

