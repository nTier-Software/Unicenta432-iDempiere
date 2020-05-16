
-- UPDATE App' version 4.3.3
ALTER TABLE tickets ADD COLUMN notes character varying;
ALTER TABLE tickets ADD COLUMN ticketrefundnumber character varying;
UPDATE RESOURCES SET ID = '1000000', NAME = 'Menu.Root.RESP.4.3.2' WHERE NAME='Menu.Root';
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('0', 'Menu.Root', 0, $FILE{/com/ghintech/pos/templates/Menu.Root.txt});
UPDATE RESOURCES SET ID = '1000001', NAME = 'ticket.print.RESP.4.3.2' WHERE NAME='ticket.print';
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('122', 'ticket.print', 0, $FILE{/com/ghintech/fiscalprint/templates/ticket.print.bsh});
UPDATE RESOURCES SET ID = '1000002', NAME = 'fiscalprint.properties.RESP.4.3.2' WHERE NAME='fiscalprint.properties';
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('108', 'fiscalprint.properties', 0, $FILE{/com/ghintech/fiscalprint/templates/fiscalprint.properties.xml});
INSERT INTO ROLES(id, name, permissions) VALUES('1000000', 'Administrador role 4.3.3', $FILE{/com/ghintech/pos/templates/Role.Administrator.xml} );
UPDATE PEOPLE SET ROLE = '1000000' WHERE ID = '101';
UPDATE APPLICATIONS SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};
