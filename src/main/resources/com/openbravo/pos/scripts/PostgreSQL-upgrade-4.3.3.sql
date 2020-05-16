
-- UPDATE App' version 4.3.4
UPDATE RESOURCES SET ID = '1000003', NAME = 'Menu.Root.RESP.4.3.3' WHERE NAME='Menu.Root';
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('0', 'Menu.Root', 0, $FILE{/com/ghintech/pos/templates/Menu.Root.txt});
INSERT INTO ROLES(id, name, permissions) VALUES('1000001', 'Administrador role 4.3.4', $FILE{/com/ghintech/pos/templates/Role.Administrator.xml} );
UPDATE PEOPLE SET ROLE = '1000001' WHERE ROLE = '1000000' OR ROLE = '102';
UPDATE APPLICATIONS SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};
