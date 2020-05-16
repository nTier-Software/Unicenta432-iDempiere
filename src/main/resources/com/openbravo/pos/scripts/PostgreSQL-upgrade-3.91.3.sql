--    uniCenta oPOS - Touch Friendly Point Of Sale
--    Copyright (C) 2009-2015 uniCenta
--    http://www.unicenta.net
--
--    This file is part of uniCenta oPOS.
--
--    uniCenta oPOS is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    uniCenta oPOS is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

-- Database upgrade script
-- v4.32 - v4.32.0

-- UPDATE App' version

--Creacion de tabla UOM
CREATE TABLE uom
(
  id character varying NOT NULL,
  name character varying NOT NULL,
  CONSTRAINT uom_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
--Creacion de tabla products_bundle

CREATE TABLE products_bundle
(
  id character varying NOT NULL,
  product character varying NOT NULL,
  product_bundle character varying NOT NULL,
  quantity double precision NOT NULL DEFAULT 0,
  CONSTRAINT products_bundle_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

CREATE UNIQUE INDEX pbundle_inx_prod ON products_bundle(product, product_bundle);

--Creacion de tabla VOUCHER
CREATE TABLE vouchers
(
  id character varying NOT NULL,
  voucher_number character varying,
  customer character varying,
  amount double precision NOT NULL DEFAULT 0,
  status character(1) DEFAULT 'A'::bpchar,
  CONSTRAINT vouchers_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

-- ERROR: no existe la columna «isconstant»
--ALTER TABLE products ADD COLUMN isconstant boolean DEFAULT false NOT NULL;
ALTER TABLE public.products RENAME iskitchen TO isconstant;
--ALTER TABLE products ALTER COLUMN isconstant SET DEFAULT false;
--ALTER TABLE products ALTER COLUMN isconstant SET NOT NULL;
-- ERROR: no existe la columna «printto»
ALTER TABLE products ADD COLUMN printto character varying;
ALTER TABLE products ALTER COLUMN printto SET DEFAULT '1'::character varying;
-- ERROR: no existe la columna «uom»
ALTER TABLE products ADD COLUMN uom character varying;
ALTER TABLE products ALTER COLUMN uom SET DEFAULT '0'::character varying;
-- ERROR: no existe la columna «catorder»
ALTER TABLE categories ADD COLUMN catorder character varying;
--INCOMPLETO CVSIMPORT
ALTER TABLE csvimport ADD COLUMN tax character varying;
ALTER TABLE csvimport ADD COLUMN searchkey character varying;
--INCOMPLETO CUSTOMERS
ALTER TABLE customers ADD COLUMN isvip boolean DEFAULT false NOT NULL;
--ALTER TABLE customers ALTER COLUMN isvip SET NOT NULL;
--ALTER TABLE customers ALTER COLUMN isvip SET DEFAULT false;

ALTER TABLE customers ADD COLUMN discount double precision DEFAULT 0 NOT NULL;
--ALTER TABLE customers ALTER COLUMN discount SET NOT NULL;
--ALTER TABLE customers ALTER COLUMN discount SET DEFAULT 0;

ALTER TABLE customers ADD COLUMN memodate timestamp without time zone;
ALTER TABLE customers ALTER COLUMN memodate SET DEFAULT '1900-01-01 00:00:01'::timestamp without time zone;
--INCOMPLETO payments
--ALTER TABLE payments ADD COLUMN returnmsg bytea;
--ALTER TABLE payments ADD COLUMN notes character varying;
ALTER TABLE payments ADD COLUMN voucher character varying;
--INCOMPLETO product
ALTER TABLE products ADD COLUMN memodate timestamp without time zone;
ALTER TABLE products ALTER COLUMN memodate SET DEFAULT '1900-01-01 00:00:01'::timestamp without time zone;

-- INCOMPLETO SharedTickets
ALTER TABLE sharedtickets ADD COLUMN locked character varying;
-- INCOMPLETO stockdiary 
ALTER TABLE stockdiary ADD COLUMN supplier character varying;
ALTER TABLE stockdiary ADD COLUMN supplierdoc character varying;
-- INCOMPLETO suppliers  
ALTER TABLE suppliers ADD COLUMN searchkey character varying;
ALTER TABLE suppliers ALTER COLUMN searchkey SET NOT NULL;
ALTER TABLE suppliers ADD COLUMN taxid character varying;
ALTER TABLE suppliers ADD COLUMN maxdebt double precision;
ALTER TABLE suppliers ALTER COLUMN maxdebt SET NOT NULL;
ALTER TABLE suppliers ALTER COLUMN maxdebt SET DEFAULT 0;
ALTER TABLE suppliers ADD COLUMN address2 character varying;
ALTER TABLE suppliers ADD COLUMN postal character varying;
ALTER TABLE suppliers ADD COLUMN city character varying;
ALTER TABLE suppliers ADD COLUMN region character varying;
ALTER TABLE suppliers ADD COLUMN country character varying;
ALTER TABLE suppliers ADD COLUMN firstname character varying;
ALTER TABLE suppliers ADD COLUMN lastname character varying;
ALTER TABLE suppliers ADD COLUMN phone2 character varying;
ALTER TABLE suppliers ADD COLUMN fax character varying;
ALTER TABLE suppliers ADD COLUMN notes character varying;
ALTER TABLE suppliers ADD COLUMN visible boolean;
ALTER TABLE suppliers ALTER COLUMN visible SET NOT NULL;
ALTER TABLE suppliers ALTER COLUMN visible SET DEFAULT true;
ALTER TABLE suppliers ADD COLUMN curdate timestamp without time zone;
ALTER TABLE suppliers ADD COLUMN curdebt double precision;
ALTER TABLE suppliers ALTER COLUMN curdebt SET DEFAULT 0;
ALTER TABLE suppliers ADD COLUMN vatid character varying;
INSERT INTO suppliers(id,searchkey,taxid,name,address,address2,phone,phone2,visible) VALUES('001','001','155648137-2-2017 DV 3','La Casa Del Software S.A','Punta Pacifica','Torre 1000 Ofic 35C','201-4000','6623-4016',true);
-- INCOMPLETO tickets  
-- ALTER TABLE tickets ADD COLUMN cuponno character varying;
INSERT INTO ROLES(id, name, permissions) VALUES('102', 'Administrador role 432', $FILE{/com/ghintech/fiscalprint/templates/Role.Administrator.xml} );
INSERT INTO ROLES(id, name, permissions) VALUES('104', 'Gerente role 432', $FILE{/com/openbravo/pos/templates/Role.Manager.xml} );
INSERT INTO ROLES(id, name, permissions) VALUES('103', 'Empleado role 432', $FILE{/com/openbravo/pos/templates/Role.Employee.xml} );
INSERT INTO ROLES(id, name, permissions) VALUES('105', 'Invitado role 432', $FILE{/com/openbravo/pos/templates/Role.Guest.xml} );

INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('101', 'Administrator432', NULL, '102', TRUE, NULL);

TRUNCATE TABLE sharedtickets;

UPDATE products set printto ='1';


UPDATE APPLICATIONS SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};
