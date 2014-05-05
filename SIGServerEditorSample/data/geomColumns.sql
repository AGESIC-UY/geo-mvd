--
-- PostgreSQL database dump
--

-- Started on 2012-07-23 19:55:28 UTC

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- TOC entry 4102 (class 0 OID 16749)
-- Dependencies: 146
-- Data for Name: geometry_columns; Type: TABLE DATA; Schema: public; Owner: nucleo
--



INSERT INTO geometry_columns VALUES ('', 'public', 'agm_fondo', 'the_geom', 2, 32721, 'POLYGON');
INSERT INTO geometry_columns VALUES ('', 'public', 'sig_departamentos', 'the_geom', 2, 32721, 'POLYGON');
INSERT INTO geometry_columns VALUES ('', 'public', 'mdg_tramos_vias', 'the_geom', 2, 32721, 'LINESTRING');
INSERT INTO geometry_columns VALUES ('', 'public', 'mdg_manzanas', 'the_geom', 2, 32721, 'POLYGON');
INSERT INTO geometry_columns VALUES ('', 'public', 'uptu_wifi', 'the_geom', 2, 32721, 'POINT');



-- Completed on 2012-07-23 19:55:28 UTC

--
-- PostgreSQL database dump complete
--

