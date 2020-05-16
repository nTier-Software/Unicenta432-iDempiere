--    uniCenta oPOS - Touch Friendly Point Of Sale
--    Copyright (C) 2009-2017 uniCenta
--    http://sourceforge.net/projects/unicentaopos
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

-- Database create script for PostgreSQL
-- v4.1.4

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE applications (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    version VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE roles (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    permissions BYTEA,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX ROLES_NAME_INX ON roles(name);
INSERT INTO ROLES(id, name, permissions) VALUES('102', 'Administrador role', $FILE{/com/ghintech/pos/templates/Role.Administrator.xml} );
INSERT INTO ROLES(id, name, permissions) VALUES('104', 'Gerente role', $FILE{/com/openbravo/pos/templates/Role.Manager.xml} );
INSERT INTO ROLES(id, name, permissions) VALUES('103', 'Empleado role', $FILE{/com/openbravo/pos/templates/Role.Employee.xml} );
INSERT INTO ROLES(id, name, permissions) VALUES('105', 'Invitado role', $FILE{/com/openbravo/pos/templates/Role.Guest.xml} );

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE people (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    apppassword VARCHAR,
    card VARCHAR,
    role VARCHAR NOT NULL,
    visible BOOLEAN NOT NULL,
    image BYTEA,
    updated timestamp default '1900-01-01 00:00:01',
    PRIMARY KEY (id),
    CONSTRAINT PEOPLE_FK_1 FOREIGN KEY (ROLE) REFERENCES roles(id)
);
CREATE UNIQUE INDEX PEOPLE_NAME_INX ON people(name);
CREATE INDEX PEOPLE_CARD_INX ON people(card);
INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('101', 'Administrator', NULL, '102', TRUE, NULL);
INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('103', 'Manager', NULL, '104', TRUE, NULL);
INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('102', 'Employee', NULL, '103', TRUE, NULL);
INSERT INTO people(id, name, apppassword, role, visible, image) VALUES ('104', 'Guest', NULL, '105', TRUE, NULL);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE resources (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    restype INTEGER NOT NULL,
    content BYTEA,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX RESOURCES_NAME_INX ON resources(name);

-- ADD resources --
-- MENU
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('0', 'Menu.Root', 0, $FILE{/com/ghintech/pos/templates/Menu.Root.txt});

-- IMAGES
INSERT INTO resources(id, name, restype, content) VALUES('1', 'coin.01', 1, $FILE{/com/openbravo/pos/templates/coin.01.png});
INSERT INTO resources(id, name, restype, content) VALUES('2', 'coin.02', 1, $FILE{/com/openbravo/pos/templates/coin.02.png});
INSERT INTO resources(id, name, restype, content) VALUES('3', 'coin.05', 1, $FILE{/com/openbravo/pos/templates/coin.05.png});
INSERT INTO resources(id, name, restype, content) VALUES('4', 'coin.1', 1, $FILE{/com/openbravo/pos/templates/coin.1.png});
INSERT INTO resources(id, name, restype, content) VALUES('5', 'coin.10', 1, $FILE{/com/openbravo/pos/templates/coin.10.png});
INSERT INTO resources(id, name, restype, content) VALUES('6', 'coin.2', 1, $FILE{/com/openbravo/pos/templates/coin.2.png});
INSERT INTO resources(id, name, restype, content) VALUES('7', 'coin.20', 1, $FILE{/com/openbravo/pos/templates/coin.20.png});
INSERT INTO resources(id, name, restype, content) VALUES('8', 'coin.50', 1, $FILE{/com/openbravo/pos/templates/coin.50.png});
INSERT INTO resources(id, name, restype, content) VALUES('9', 'img.cash', 1, $FILE{/com/openbravo/pos/templates/img.cash.png});
INSERT INTO resources(id, name, restype, content) VALUES('10', 'img.cashdrawer', 1, $FILE{/com/openbravo/pos/templates/img.cashdrawer.png});
INSERT INTO resources(id, name, restype, content) VALUES('11', 'img.discount', 1, $FILE{/com/openbravo/pos/templates/img.discount.png});
INSERT INTO resources(id, name, restype, content) VALUES('12', 'img.discount_b', 1, $FILE{/com/openbravo/pos/templates/img.discount_b.png});
INSERT INTO resources(id, name, restype, content) VALUES('13', 'img.empty', 1, $FILE{/com/openbravo/pos/templates/img.empty.png});
INSERT INTO resources(id, name, restype, content) VALUES('14', 'img.heart', 1, $FILE{/com/openbravo/pos/templates/img.heart.png});
INSERT INTO resources(id, name, restype, content) VALUES('15', 'img.keyboard_32', 1, $FILE{/com/openbravo/pos/templates/img.keyboard_32.png});
INSERT INTO resources(id, name, restype, content) VALUES('16', 'img.kit_print', 1, $FILE{/com/openbravo/pos/templates/img.kit_print.png});
INSERT INTO resources(id, name, restype, content) VALUES('17', 'img.no_photo', 1, $FILE{/com/openbravo/pos/templates/img.no_photo.png});
INSERT INTO resources(id, name, restype, content) VALUES('18', 'img.refundit', 1, $FILE{/com/openbravo/pos/templates/img.refundit.png});
INSERT INTO resources(id, name, restype, content) VALUES('19', 'img.run_script', 1, $FILE{/com/openbravo/pos/templates/img.run_script.png});
INSERT INTO resources(id, name, restype, content) VALUES('20', 'img.ticket_print', 1, $FILE{/com/openbravo/pos/templates/img.ticket_print.png});
INSERT INTO resources(id, name, restype, content) VALUES('21', 'img.user', 1, $FILE{/com/openbravo/pos/templates/img.user.png});
INSERT INTO resources(id, name, restype, content) VALUES('22', 'note.50', 1, $FILE{/com/openbravo/pos/templates/note.50.png});
INSERT INTO resources(id, name, restype, content) VALUES('23', 'note.20', 1, $FILE{/com/openbravo/pos/templates/note.20.png});
INSERT INTO resources(id, name, restype, content) VALUES('24', 'note.10', 1, $FILE{/com/openbravo/pos/templates/note.10.png});
INSERT INTO resources(id, name, restype, content) VALUES('25', 'note.5', 1, $FILE{/com/openbravo/pos/templates/note.5.png});

-- PRINTER
INSERT INTO resources(id, name, restype, content) VALUES('26', 'Printer.CloseCash.Preview', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.Preview.xml});
INSERT INTO resources(id, name, restype, content) VALUES('27', 'Printer.CloseCash', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.xml});
INSERT INTO resources(id, name, restype, content) VALUES('28', 'Printer.CustomerPaid', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid.xml});
INSERT INTO resources(id, name, restype, content) VALUES('29', 'Printer.CustomerPaid2', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('30', 'Printer.FiscalTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.FiscalTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('31', 'Printer.Inventory', 0, $FILE{/com/openbravo/pos/templates/Printer.Inventory.xml});
INSERT INTO resources(id, name, restype, content) VALUES('32', 'Printer.OpenDrawer', 0, $FILE{/com/openbravo/pos/templates/Printer.OpenDrawer.xml});
INSERT INTO resources(id, name, restype, content) VALUES('33', 'Printer.PartialCash', 0, $FILE{/com/openbravo/pos/templates/Printer.PartialCash.xml});
INSERT INTO resources(id, name, restype, content) VALUES('34', 'Printer.PrintLastTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.PrintLastTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('35', 'Printer.Product', 0, $FILE{/com/openbravo/pos/templates/Printer.Product.xml});
INSERT INTO resources(id, name, restype, content) VALUES('36', 'Printer.ReprintTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.ReprintTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('37', 'Printer.Start', 0, $FILE{/com/openbravo/pos/templates/Printer.Start.xml});
INSERT INTO resources(id, name, restype, content) VALUES('38', 'Printer.Ticket.P1', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P1.xml});
INSERT INTO resources(id, name, restype, content) VALUES('39', 'Printer.Ticket.P2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('40', 'Printer.Ticket.P3', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P3.xml});
INSERT INTO resources(id, name, restype, content) VALUES('41', 'Printer.Ticket.P4', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P4.xml});
INSERT INTO resources(id, name, restype, content) VALUES('42', 'Printer.Ticket.P5', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P5.xml});
INSERT INTO resources(id, name, restype, content) VALUES('43', 'Printer.Ticket.P6', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P6.xml});
INSERT INTO resources(id, name, restype, content) VALUES('44', 'Printer.Ticket', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('45', 'Printer.Ticket2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('46', 'Printer.TicketClose', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketClose.xml});
INSERT INTO resources(id, name, restype, content) VALUES('47', 'Printer.TicketRemote', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketRemote.xml});
INSERT INTO resources(id, name, restype, content) VALUES('48', 'Printer.TicketLine', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});
INSERT INTO resources(id, name, restype, content) VALUES('49', 'Printer.TicketNew', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});
INSERT INTO resources(id, name, restype, content) VALUES('50', 'Printer.TicketPreview', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketPreview.xml});
INSERT INTO resources(id, name, restype, content) VALUES('51', 'Printer.TicketTotal', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketTotal.xml});
INSERT INTO resources(id, name, restype, content) VALUES('52', 'Printer.Ticket.Logo', 1, $FILE{/com/openbravo/pos/templates/printer.ticket.logo.png});

-- SCRIPTS
INSERT INTO resources(id, name, restype, content) VALUES('53', 'script.AddLineNote', 0, $FILE{/com/openbravo/pos/templates/script.AddLineNote.txt});
INSERT INTO resources(id, name, restype, content) VALUES('54', 'script.Event.Total', 0, $FILE{/com/openbravo/pos/templates/script.Event.Total.txt});
INSERT INTO resources(id, name, restype, content) VALUES('55', 'script.Keyboard', 0, $FILE{/com/openbravo/pos/templates/script.Keyboard.txt});
INSERT INTO resources(id, name, restype, content) VALUES('56', 'script.linediscount', 0, $FILE{/com/openbravo/pos/templates/script.linediscount.txt});
INSERT INTO resources(id, name, restype, content) VALUES('57', 'script.ReceiptConsolidate', 0, $FILE{/com/openbravo/pos/templates/script.ReceiptConsolidate.txt});
INSERT INTO resources(id, name, restype, content) VALUES('58', 'script.Refundit', 0, $FILE{/com/openbravo/pos/templates/script.Refundit.txt});
INSERT INTO resources(id, name, restype, content) VALUES('59', 'script.SendOrder', 0, $FILE{/com/openbravo/pos/templates/script.SendOrder.txt});
INSERT INTO resources(id, name, restype, content) VALUES('60', 'script.ServiceCharge', 0, $FILE{/com/openbravo/pos/templates/script.script.ServiceCharge.txt});
INSERT INTO resources(id, name, restype, content) VALUES('61', 'script.SetPerson', 0, $FILE{/com/openbravo/pos/templates/script.SetPerson.txt});
INSERT INTO resources(id, name, restype, content) VALUES('62', 'script.StockCurrentAdd', 0, $FILE{/com/ghintech/pos/templates/script.StockCurrentAdd.bsh});
INSERT INTO resources(id, name, restype, content) VALUES('63', 'script.StockCurrentSet', 0, $FILE{/com/ghintech/pos/templates/script.StockCurrentSet.bsh});
INSERT INTO resources(id, name, restype, content) VALUES('64', 'script.totaldiscount', 0, $FILE{/com/openbravo/pos/templates/script.totaldiscount.txt});

-- SYSTEM
INSERT INTO resources(id, name, restype, content) VALUES('65', 'payment.cash', 0, $FILE{/com/ghintech/pos/templates/payment.cash.txt});
INSERT INTO resources(id, name, restype, content) VALUES('66', 'ticket.addline', 0, $FILE{/com/openbravo/pos/templates/ticket.addline.txt});
INSERT INTO resources(id, name, restype, content) VALUES('67', 'ticket.change', 0, $FILE{/com/openbravo/pos/templates/ticket.change.txt});
INSERT INTO resources(id, name, restype, content) VALUES('68', 'Ticket.Buttons', 0, $FILE{/com/ghintech/fiscalprint/templates/Ticket.Buttons.xml});
INSERT INTO resources(id, name, restype, content) VALUES('69', 'Ticket.Close', 0, $FILE{/com/openbravo/pos/templates/Ticket.Close.xml});
INSERT INTO resources(id, name, restype, content) VALUES('70', 'Ticket.Discount', 0, $FILE{/com/openbravo/pos/templates/Ticket.Discount.xml});
INSERT INTO resources(id, name, restype, content) VALUES('71', 'Ticket.Line', 0, $FILE{/com/openbravo/pos/templates/Ticket.Line.xml});
INSERT INTO resources(id, name, restype, content) VALUES('72', 'ticket.removeline', 0, $FILE{/com/openbravo/pos/templates/ticket.removeline.txt});
INSERT INTO resources(id, name, restype, content) VALUES('73', 'ticket.setline', 0, $FILE{/com/openbravo/pos/templates/ticket.setline.txt});
INSERT INTO resources(id, name, restype, content) VALUES('74', 'Ticket.TicketLineTaxesIncluded', 0, $FILE{/com/openbravo/pos/templates/Ticket.TicketLineTaxesIncluded.xml});
INSERT INTO resources(id, name, restype, content) VALUES('75', 'Window.Logo', 1, $FILE{/com/openbravo/pos/templates/window.logo.png});
INSERT INTO resources(id, name, restype, content) VALUES('76', 'Window.Title', 0, $FILE{/com/openbravo/pos/templates/Window.Title.txt});

-- LPA
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('100', 'script.posapps', 0, $FILE{/com/openbravo/pos/templates/script.posapps.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('101', 'img.posapps', 1, $FILE{/com/openbravo/pos/templates/img.posapps.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('102', 'erp.properties', 0, $FILE{/com/ghintech/sync/templates/erp.properties.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('103', 'img.linediscount', 1, $FILE{/com/openbravo/pos/templates/img.linediscount.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('104', 'script.linediscountoverprice', 0, $FILE{/com/ghintech/pos/templates/script.linediscountoverprice.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('105', 'card.type', 0, $FILE{/com/ghintech/pos/templates/card.type.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('106', 'pointofsale.name', 0, $FILE{/com/ghintech/pos/templates/pointofsale.name.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('107', 'openbravo2.properties', 0, $FILE{/com/openbravo/pos/erp/sync/templates/openbravo.properties.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('108', 'fiscalprint.properties', 0, $FILE{/com/ghintech/fiscalprint/templates/fiscalprint.properties.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('109', 'ticket.bematech', 0, $FILE{/com/ghintech/fiscalprint/templates/ticket.bematech.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('110', 'ticket.thefactory', 0, $FILE{/com/ghintech/fiscalprint/templates/ticket.thefactory.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('111', 'event.total', 0, $FILE{/com/ghintech/fiscalprint/templates/event.total.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('112', 'ticket.hasar', 0, $FILE{/com/ghintech/fiscalprint/templates/ticket.hasar.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('113', 'ticket.thefactory.preview', 0, $FILE{/com/ghintech/fiscalprint/templates/ticket.thefactory.preview.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('114', 'event.save', 0, $FILE{/com/openbravo/pos/templates/event.save.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('115', 'event.removeline', 0, $FILE{/com/ghintech/fiscalprint/templates/event.removeline.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('116', 'event.totalcancel', 0, $FILE{/com/openbravo/pos/templates/event.totalcancel.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('117', 'script.totaldiscount2', 0, $FILE{/com/openbravo/pos/templates/script.totaldiscount.bsh});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('118', 'Printer.TicketKitchen', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketKitchen.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('119', 'code.quotation', 0, $FILE{/com/ghintech/fiscalprint/templates/code.quotation.txt});
INSERT INTO resources(id, name, restype, content) VALUES('120', 'img.retirediscount', 1, $FILE{/com/openbravo/pos/templates/img.retirediscount.png});
INSERT INTO resources(id, name, restype, content) VALUES('121', 'script.retirediscount', 0, $FILE{/com/ghintech/fiscalprint/templates/script.retirediscount.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('122', 'ticket.print', 0, $FILE{/com/ghintech/fiscalprint/templates/ticket.print.bsh});

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('123', 'note.100', 1, $FILE{/com/openbravo/pos/templates/note.100.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('124', 'coin.050', 1, $FILE{/com/openbravo/pos/templates/coin.050.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('125', 'coin.025', 1, $FILE{/com/openbravo/pos/templates/coin.025.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('126', 'coin.010', 1, $FILE{/com/openbravo/pos/templates/coin.010.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('127', 'note.1', 1, $FILE{/com/openbravo/pos/templates/note.1.png});


/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE taxcustcategories (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX TAXCUSTCAT_NAME_INX ON taxcustcategories(name);
--INSERT INTO TAXCUSTCATEGORIES(ID, NAME) VALUES ('000', 'EXENTO');

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE CUSTOMERS (
	id varchar NOT NULL,
	searchkey varchar NOT NULL,
	taxid varchar default NULL,
	name varchar NOT NULL,
	taxcategory varchar default NULL,
	card varchar default NULL,
	maxdebt double precision default 0 NOT NULL,
	address varchar default NULL,
	address2 varchar default NULL,
	postal varchar default NULL,
	city varchar default NULL,
	region varchar default NULL,
	country varchar default NULL,
	firstname varchar default NULL,
	lastname varchar default NULL,
	email varchar default NULL,
	phone varchar default NULL,
	phone2 varchar default NULL,
	fax varchar default NULL,
	notes varchar default NULL,
	visible BOOLEAN NOT NULL DEFAULT TRUE,
	curdate timestamp default NULL,
	curdebt double precision default 0,
	image bytea,
	isvip BOOLEAN NOT NULL DEFAULT FALSE,
	discount double precision default 0 NOT NULL,
	memodate timestamp default '1900-01-01 00:00:01',
	updated timestamp default '1900-01-01 00:00:01',
    PRIMARY KEY (id),
    CONSTRAINT CUSTOMERS_TAXCAT FOREIGN KEY (taxcategory) REFERENCES taxcustcategories(id)
);
CREATE UNIQUE INDEX CUSTOMERS_SKEY_INX ON customers(searchkey);
CREATE INDEX CUSTOMERS_TAXID_INX ON customers(taxid);
CREATE INDEX CUSTOMERS_NAME_INX ON customers(name);
CREATE INDEX CUSTOMERS_CARD_INX ON customers(card);
INSERT INTO CUSTOMERS(ID,SEARCHKEY,TAXID, NAME) VALUES ('000', 'CONTADO', 'CONTADO', 'CONTADO');

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE categories (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    parentid VARCHAR,
    image BYTEA,
    texttip VARCHAR DEFAULT NULL,
    catshowname BOOLEAN NOT NULL DEFAULT TRUE,
    catorder varchar default NULL,
    PRIMARY KEY (id),
    CONSTRAINT categories_fk_1 FOREIGN KEY (parentid) REFERENCES categories(id)
);
CREATE UNIQUE INDEX CATEGORIES_NAME_INX ON categories(name);
--INSERT INTO categories(id, name) VALUES ('000', 'Category Standard');
--INSERT INTO categories(id, name) VALUES ('xxx999', '***');

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE taxcategories (
	id varchar NOT NULL,
	name varchar NOT NULL,
	PRIMARY KEY  ( id )
);
CREATE UNIQUE INDEX TAXCAT_NAME_INX ON taxcategories(name);
--INSERT INTO taxcategories(id, name) VALUES ('000', 'EXENTO');
--INSERT INTO taxcategories(id, name) VALUES ('001', 'Standard');
--INSERT INTO taxcategories(id, name) VALUES ('002', 'LICORES');
--INSERT INTO taxcategories(id, name) VALUES ('003', 'TABACO');

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE taxes (
	id varchar NOT NULL,
	name varchar NOT NULL,
	category varchar NOT NULL,
	custcategory varchar default NULL,
	parentid varchar default NULL,
	rate double precision default 0 NOT NULL,
	ratecascade BOOLEAN NOT NULL DEFAULT FALSE,
	rateorder integer default NULL,
    PRIMARY KEY (id),
    CONSTRAINT TAXES_CAT_FK FOREIGN KEY (category) REFERENCES taxcategories(id),
    CONSTRAINT TAXES_CUSTCAT_FK FOREIGN KEY (custcategory) REFERENCES taxcustcategories(id),
    CONSTRAINT TAXES_TAXES_FK FOREIGN KEY (parentid) REFERENCES taxes(id)
);
CREATE UNIQUE INDEX TAXES_NAME_INX ON taxes(name);
--INSERT INTO TAXES(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('000', 'Exento', '000', NULL, NULL, 0, FALSE, NULL);
--INSERT INTO TAXES(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('001', 'Standard', '001', NULL, NULL, 0.07, FALSE, NULL);
--INSERT INTO TAXES(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('002', 'Licores', '002', NULL, NULL, 0.10, FALSE, NULL);
--INSERT INTO TAXES(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('003', 'Tabaco', '003', NULL, NULL, 0.15, FALSE, NULL);
--INSERT INTO TAXES(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('004', 'Exento Cliente', '000','000', NULL, 0, FALSE, NULL);
--INSERT INTO TAXES(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('005', 'ITBMS Exento Cliente', '001','000', NULL, 0, FALSE, NULL);
--INSERT INTO TAXES(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('006', 'Licores Exento Cliente', '002','000', NULL, 0, FALSE, NULL);
--INSERT INTO TAXES(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('007', 'Tabaco Exento Cliente', '003','000', NULL, 0, FALSE, NULL);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE attribute (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE attributevalue (
    id VARCHAR NOT NULL,
    attribute_id VARCHAR NOT NULL,
    value VARCHAR,
    PRIMARY KEY (id),
    CONSTRAINT attval_att FOREIGN KEY (attribute_id) REFERENCES attribute(id) ON DELETE CASCADE
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE attributeset (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE attributeuse (
    id VARCHAR NOT NULL,
    attributeset_id VARCHAR NOT NULL,
    attribute_id VARCHAR NOT NULL,
    lineno INTEGER,
    PRIMARY KEY (id),
    CONSTRAINT attuse_set FOREIGN KEY (attributeset_id) REFERENCES attributeset(id) ON DELETE CASCADE,
    CONSTRAINT attuse_att FOREIGN KEY (attribute_id) REFERENCES attribute(id)
);
CREATE UNIQUE INDEX attuse_line ON attributeuse(attributeset_id, lineno);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE attributesetinstance (
    id VARCHAR NOT NULL,
    attributeset_ID VARCHAR NOT NULL,
    description VARCHAR,
    PRIMARY KEY (id),
    CONSTRAINT attsetinst_set FOREIGN KEY (attributeset_id) REFERENCES attributeset(id) ON DELETE CASCADE
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE attributeinstance (
    id VARCHAR NOT NULL,
    attributesetinstance_id VARCHAR NOT NULL,
    attribute_id VARCHAR NOT NULL,
    value VARCHAR,
    PRIMARY KEY (id),
    constraint attinst_set foreign key (attributesetinstance_id) references attributesetinstance(id) on delete cascade,
    constraint attinst_att foreign key (attribute_id) references attribute(id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE products (
	id varchar NOT NULL,
	reference varchar NOT NULL,
	code varchar NOT NULL,
	codetype varchar default NULL,
	name varchar NOT NULL,
	pricebuy double precision default 0 NOT NULL,
	pricesell double precision default 0 NOT NULL,
	category varchar NOT NULL,
	taxcat varchar NOT NULL,
	attributeset_id varchar default NULL,
	stockcost double precision default 0 NOT NULL,
	stockvolume double precision default 0 NOT NULL,
	image bytea default NULL,
	iscom BOOLEAN NOT NULL DEFAULT FALSE,
	isscale BOOLEAN NOT NULL DEFAULT FALSE,
	isconstant BOOLEAN NOT NULL DEFAULT FALSE,
	printkb BOOLEAN NOT NULL DEFAULT FALSE,
	sendstatus BOOLEAN NOT NULL DEFAULT FALSE,
	isservice BOOLEAN NOT NULL DEFAULT FALSE,
	attributes bytea default NULL,
	display varchar default NULL,
	isvprice BOOLEAN NOT NULL DEFAULT FALSE,
	isverpatrib BOOLEAN NOT NULL DEFAULT FALSE,
	texttip varchar default NULL,
	warranty BOOLEAN NOT NULL DEFAULT FALSE,
	stockunits double precision default 0 NOT NULL,
	printto varchar default '1',
	supplier varchar default NULL,
        uom varchar default '0',
        memodate timestamp default '1900-01-01 00:00:01',
	updated timestamp default '1900-01-01 00:00:01',
    PRIMARY KEY (ID),
    CONSTRAINT PRODUCTS_FK_1 FOREIGN KEY (category) REFERENCES categories(id),
    CONSTRAINT PRODUCTS_TAXCAT_FK FOREIGN KEY (taxcat) REFERENCES taxcategories(id),
    CONSTRAINT PRODUCTS_ATTRSET_FK FOREIGN KEY (attributeset_id) REFERENCES attributeset(id)
);
CREATE UNIQUE INDEX PRODUCTS_INX_0 ON products(reference);
CREATE UNIQUE INDEX PRODUCTS_INX_1 ON products(code);
CREATE INDEX PRODUCTS_NAME_INX ON products(name);

-- ADD PRODUCTS
--INSERT INTO products(id, reference, code, name, category, taxcat, isservice, display, printto) 
--VALUES ('xxx999_999xxx_x9x9x9', 'xxx999', 'xxx999', '***', '000', '001', false, '<html><center>***', '1');
---INSERT INTO products(id, reference, code, name, category, taxcat, isservice, display, printto) 
--VALUES ('xxx998_998xxx_x8x8x8', 'xxx998', 'xxx998', '****', '000', '001', true, '<html><center>****', '1');
--INSERT INTO PRODUCTS(ID, REFERENCE, CODE, NAME, PRICEBUY, PRICESELL, CATEGORY, TAXCAT, ISSERVICE,  ISVPRICE, ISVERPATRIB, TEXTTIP, WARRANTY) 
--VALUES ('000', '000', '000', 'Servicio', 0.00, 0.00, '000', '000', TRUE, FALSE, FALSE, '',FALSE);

CREATE TABLE products_bundle (
    id varchar NOT NULL,
    product VARCHAR NOT NULL,
    product_bundle VARCHAR NOT NULL,
    quantity double precision default 0 NOT NULL,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX pbundle_inx_prod ON products_bundle(product , product_bundle);
/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE products_cat (
    product VARCHAR NOT NULL,
    catorder INTEGER,
    PRIMARY KEY (product),
    CONSTRAINT PRODUCTS_CAT_FK_1 FOREIGN KEY (product) REFERENCES products(id)
);
CREATE INDEX PRODUCTS_CAT_INX_1 ON products_cat(catorder);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE products_com (
    id VARCHAR NOT NULL,
    product VARCHAR NOT NULL,
    product2 VARCHAR NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT PRODUCTS_COM_FK_1 FOREIGN KEY (product) REFERENCES products(id),
    CONSTRAINT PRODUCTS_COM_FK_2 FOREIGN KEY (product2) REFERENCES products(id)
);
CREATE UNIQUE INDEX PCOM_INX_PROD ON products_com(product, product2);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE locations (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    address VARCHAR,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX LOCATIONS_NAME_INX ON locations(name);
INSERT INTO locations(id, name, address) VALUES('0', 'General', NULL);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE stockdiary (
    id varchar NOT NULL,
    datenew timestamp NOT NULL,
    reason integer NOT NULL,
    location varchar NOT NULL,
    product varchar NOT NULL,
    attributesetinstance_id varchar,
    units double precision default 0 NOT NULL,
    price double precision default 0 NOT NULL,
    appuser varchar,
    status integer default 0,
    supplier varchar default NULL,
    supplierdoc varchar default NULL,
    i_movement_id NUMERIC,
    hostsync VARCHAR,
    PRIMARY KEY (id),
    CONSTRAINT STOCKDIARY_FK_1 FOREIGN KEY (product) REFERENCES products(id),
    CONSTRAINT STOCKDIARY_ATTSETINST FOREIGN KEY (attributesetinstance_id) REFERENCES attributesetinstance(id),
    CONSTRAINT STOCKDIARY_FK_2 FOREIGN KEY (location) REFERENCES locations(id)
);
CREATE INDEX STOCKDIARY_INX_1 ON stockdiary(datenew);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE stocklevel (
    id varchar NOT NULL,
    location varchar NOT NULL,
    product varchar NOT NULL,
    stocksecurity double precision default 0 NOT NULL,
    stockmaximum double precision default 0 NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT STOCKLEVEL_PRODUCT FOREIGN KEY (product) REFERENCES products(id),
    CONSTRAINT STOCKLEVEL_LOCATION FOREIGN KEY (location) REFERENCES locations(id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE stockcurrent (
	location varchar NOT NULL,
	product varchar NOT NULL,
	attributesetinstance_id varchar default NULL,
	units double precision default 0 NOT NULL,
    CONSTRAINT STOCKCURRENT_FK_1 FOREIGN KEY (product) REFERENCES products(id),
    CONSTRAINT STOCKCURRENT_ATTSETINST FOREIGN KEY (attributesetinstance_id) REFERENCES attributesetinstance(id),
    CONSTRAINT STOCKCURRENT_FK_2 FOREIGN KEY (location) REFERENCES locations(id)
);
CREATE UNIQUE INDEX STOCKCURRENT_INX ON stockcurrent(location, product, attributesetinstance_id);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE closedcash (
    money varchar NOT NULL,
    host varchar NOT NULL,
    hostsequence INTEGER NOT NULL,
    datestart TIMESTAMP NOT NULL,
    dateend TIMESTAMP,
    nosales INTEGER DEFAULT 0 NOT NULL,
    PRIMARY KEY(money)
);
CREATE INDEX CLOSEDCASH_INX_1 ON closedcash(datestart);
CREATE UNIQUE INDEX CLOSEDCASH_INX_SEQ ON closedcash(host, hostsequence);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE RECEIPTS (
    id VARCHAR NOT NULL,
    MONEY VARCHAR NOT NULL,
    datenew TIMESTAMP NOT NULL,
    attributes BYTEA,
    person VARCHAR,
    PRIMARY KEY (id),
CONSTRAINT RECEIPTS_FK_MONEY FOREIGN KEY (money) REFERENCES closedcash(money)
);
CREATE INDEX RECEIPTS_INX_1 ON receipts(datenew);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE tickets (
    id VARCHAR NOT NULL,
    tickettype INTEGER DEFAULT 0 NOT NULL,
    ticketid INTEGER NOT NULL,
    person VARCHAR NOT NULL,
    customer VARCHAR DEFAULT NULL,
    status INTEGER DEFAULT 0 NOT NULL,
    hostsync VARCHAR DEFAULT NULL,
    fiscalnumber VARCHAR DEFAULT NULL,
    cuponno VARCHAR DEFAULT NULL,
    fiscalserial VARCHAR DEFAULT NULL,
    notes VARCHAR DEFAULT NULL,
    ticketrefundnumber VARCHAR DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT TICKETS_FK_ID FOREIGN KEY (id) REFERENCES receipts(id),
    CONSTRAINT TICKETS_FK_2 FOREIGN KEY (PERSON) REFERENCES people(id),
    CONSTRAINT TICKETS_CUSTOMERS_FK FOREIGN KEY (customer) REFERENCES customers(id)
);
CREATE INDEX TICKETS_TICKETID ON tickets(tickettype, ticketid);

CREATE SEQUENCE customer_searchkey_seq START WITH 1;

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE ticketlines (
    ticket VARCHAR NOT NULL,
    line INTEGER NOT NULL,
    product VARCHAR,
    attributesetinstance_id VARCHAR,
    units double precision default 0 NOT NULL,
    price double precision default 0 NOT NULL,
    taxid VARCHAR NOT NULL,
    attributes BYTEA,
    i_order_id INTEGER,
    PRIMARY KEY (ticket, line),
    CONSTRAINT TICKETLINES_FK_TICKET FOREIGN KEY (ticket) REFERENCES tickets(id),
    CONSTRAINT TICKETLINES_FK_2 FOREIGN KEY (product) REFERENCES products(id),
    CONSTRAINT TICKETLINES_ATTSETINST FOREIGN KEY (attributesetinstance_id) REFERENCES attributesetinstance(id),
    CONSTRAINT TICKETLINES_FK_3 FOREIGN KEY (taxid) REFERENCES taxes(id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
 CREATE TABLE lineremoved (
  removeddate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name VARCHAR DEFAULT NULL,
  ticketid VARCHAR DEFAULT NULL,
  productid VARCHAR DEFAULT NULL,
  productname VARCHAR DEFAULT NULL,
  units double precision default 0 NOT NULL
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE payments (
	id varchar NOT NULL,
	receipt varchar NOT NULL,
	payment varchar NOT NULL,
	total double precision default 0 NOT NULL,
	tip double precision default 0 NOT NULL,
	transid varchar default NULL,
	isprocessed BOOLEAN NOT NULL DEFAULT FALSE,
	returnmsg bytea default NULL,
	notes varchar default NULL,
	tendered double precision default 0 NOT NULL,
	cardname varchar default NULL,
        voucher varchar default NULL,
    PRIMARY KEY (id),
    CONSTRAINT PAYMENTS_FK_RECEIPT FOREIGN KEY (receipt) REFERENCES receipts(id)
);
CREATE INDEX PAYMENTS_INX_1 ON payments(payment);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE taxlines (
    id VARCHAR NOT NULL,
    receipt VARCHAR NOT NULL,
    taxid VARCHAR NOT NULL,
    base double precision default 0 NOT NULL, 
    amount double precision default 0 NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT TAXLINES_TAX FOREIGN KEY (taxid) REFERENCES taxes(id),
    CONSTRAINT TAXLINES_RECEIPT FOREIGN KEY (receipt) REFERENCES receipts(id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE floors (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    image BYTEA,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX FLOORS_NAME_INX ON floors(name);
INSERT INTO floors(id, name, image) VALUES ('0', 'Restaurant floor', $FILE{/com/openbravo/pos/templates/restaurant_floor.png});

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE places (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    floor VARCHAR NOT NULL,
    customer VARCHAR,
    waiter VARCHAR,
    ticketid VARCHAR,
    tablemoved BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT PLACES_FK_1 FOREIGN KEY (floor) REFERENCES floors(id)
);
CREATE UNIQUE INDEX PLACES_NAME_INX ON places(name);
INSERT INTO places(id, name, x, y, floor) VALUES ('1', 'Table 1', 80, 70, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('2', 'Table 2', 250, 75, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('3', 'Table 3', 400, 75, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('4', 'Table 4', 80, 200, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('5', 'Table 5', 260, 210, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('6', 'Table 6', 430, 210, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('7', 'Table 7', 80, 330, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('8', 'Table 8', 190, 350, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('9', 'Table 9', 295, 350, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('10', 'Table 10', 430, 345, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('11', 'Table 11', 550, 135, '0');
INSERT INTO places(id, name, x, y, floor) VALUES ('12', 'Table 12', 550, 290, '0');

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE reservations (
    id VARCHAR NOT NULL,
    created TIMESTAMP NOT NULL,
    datenew TIMESTAMP DEFAULT '2016-01-01 00:00:00' NOT NULL,
    title VARCHAR NOT NULL,
    chairs INTEGER NOT NULL,
    isdone BOOLEAN NOT NULL,
    description VARCHAR,
    PRIMARY KEY (id)
);
CREATE INDEX RESERVATIONS_INX_1 ON reservations(datenew);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE reservation_customers (
    id VARCHAR NOT NULL,
    customer VARCHAR NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT RES_CUST_FK_1 FOREIGN KEY (id) REFERENCES reservations(id),
    CONSTRAINT RES_CUST_FK_2 FOREIGN KEY (customer) REFERENCES customers(id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE thirdparties (
    id VARCHAR NOT NULL,
    cif VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    address VARCHAR,
    concatcomm VARCHAR,
    contactfact VARCHAR,
    payrule VARCHAR,
    faxnumber VARCHAR,
    phonenumber VARCHAR,
    mobilenumber VARCHAR,
    email VARCHAR,
    webpage VARCHAR,
    notes VARCHAR,
    PRIMARY KEY (id)
);
CREATE UNIQUE INDEX THIRDPARTIES_CIF_INX ON thirdparties(cif);
CREATE UNIQUE INDEX THIRDPARTIES_NAME_INX ON thirdparties(name);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE sharedtickets (
    id varchar NOT NULL,
    name varchar NOT NULL,
    content bytea default NULL,
    appuser varchar default NULL,
    pickupid integer NOT NULL default '0',
    locked varchar default NULL,
    PRIMARY KEY (id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE shifts (
    id VARCHAR NOT NULL,
    startshift TIMESTAMP NOT NULL,
    endshift TIMESTAMP,
    pplid VARCHAR NOT NULL,
    PRIMARY KEY (id)
);
INSERT INTO shifts(id, startshift, endshift, pplid) VALUES ('0', '2016-01-01 00:00:00.001', '2016-01-01 00:00:00.002','0');

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE leaves (
    id VARCHAR NOT NULL,
    pplid VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    startdate TIMESTAMP NOT NULL,
    enddate TIMESTAMP NOT NULL,
    notes VARCHAR,
    PRIMARY KEY (ID),
    CONSTRAINT lEAVES_PPLID FOREIGN KEY (pplid) REFERENCES people(id)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE breaks (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    notes VARCHAR,
    visible BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);
INSERT INTO breaks(id, name, visible, notes) VALUES ('0', 'Lunch Break', TRUE, NULL);
INSERT INTO breaks(id, name, visible, notes) VALUES ('1', 'Tea Break', TRUE, NULL);
INSERT INTO breaks(id, name, visible, notes) VALUES ('2', 'Mid Break', TRUE, NULL);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE shift_breaks (
  id VARCHAR NOT NULL,
  shiftid VARCHAR NOT NULL,
  breakid VARCHAR NOT NULL,
  starttime TIMESTAMP NOT NULL,
  endtime TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT SHIFT_BREAKS_BREAKID FOREIGN KEY (breakid) REFERENCES breaks(id),
  CONSTRAINT SHIFT_BREAKS_SHIFTID FOREIGN KEY (shiftid) REFERENCES shifts(id)
);
INSERT INTO shift_breaks(id, shiftid, breakid, starttime, endtime) VALUES ('0', '0', '0', '2016-01-01 00:00:00.003', '2016-01-01 00:00:00.004');

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE moorers (
  vesselname VARCHAR,
  size INTEGER,
  days INTEGER,
  power BOOLEAN NOT NULL DEFAULT FALSE
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE csvimport (
    id VARCHAR NOT NULL,
    rownumber VARCHAR,
    csverror VARCHAR,
    reference VARCHAR,
    code VARCHAR,
    name VARCHAR,
    pricebuy double precision,
    pricesell double precision,
    previousbuy double precision,
    previoussell double precision,
    category VARCHAR,
    tax VARCHAR,
    searchkey VARCHAR,
    PRIMARY KEY (ID)
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE suppliers (
    id varchar NOT NULL,
    searchkey varchar NOT NULL,
    taxid varchar default NULL,
    name varchar NOT NULL,
    maxdebt double precision default 0 NOT NULL,
    address varchar default NULL,
    address2 varchar default NULL,
    postal varchar default NULL,
    city varchar default NULL,
    region varchar default NULL,
    country varchar default NULL,
    firstname varchar default NULL,
    lastname varchar default NULL,
    email varchar default NULL,
    phone varchar default NULL,
    phone2 varchar default NULL,
    fax varchar default NULL,
    notes varchar default NULL,
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    curdate timestamp default NULL,
    curdebt double precision default 0,
    vatid varchar default NULL,
    PRIMARY KEY  ( id )
);
CREATE UNIQUE INDEX SUPPLIERS_SKEY_INX ON suppliers(searchkey);
CREATE INDEX SUPPLIERS_NAME_INX ON suppliers(name);
INSERT INTO suppliers(id,searchkey,taxid,name,address,address2,phone,phone2,visible) VALUES('001','001','155648137-2-2017 DV 3','La Casa Del Software S.A','Punta Pacifica','Torre 1000 Ofic 35C','201-4000','6623-4016',true);
/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE uom (
    id VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    PRIMARY KEY ( id )
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE TABLE vouchers (
    id VARCHAR NOT NULL,
    voucher_number VARCHAR DEFAULT NULL,
    customer VARCHAR DEFAULT NULL,
    amount double precision default 0 NOT NULL,
    status CHAR(1) DEFAULT 'A',
    PRIMARY KEY ( id )
);

/* Header line. Object: vouchers. Script date: 08/05/2016 18:00:00. */
CREATE SEQUENCE ticketsnum START WITH 1;
CREATE SEQUENCE ticketsnum_refund START WITH 1;
CREATE SEQUENCE ticketsnum_payment START WITH 1;
CREATE SEQUENCE pickup_number START WITH 1;

CREATE TABLE draweropened (
    opendate TIMESTAMP DEFAULT NOW(),
    name VARCHAR,
    ticketid VARCHAR
);

INSERT INTO APPLICATIONS(ID, NAME, VERSION) VALUES($APP_ID{}, $APP_NAME{}, $APP_VERSION{});

CREATE TABLE GIFTCARD
(
  id SERIAL NOT NULL,
  serial VARCHAR NOT NULL,
  amount DOUBLE PRECISION,
  status INTEGER NOT NULL DEFAULT 0,
  hostsync VARCHAR,
  giftcard_id INTEGER,
  updated TIMESTAMP,
  CONSTRAINT GIFTCARD_PKEY PRIMARY KEY (ID)
);
