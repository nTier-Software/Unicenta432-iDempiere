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
-- v3.91.2 - v3.91.3
--Add Fiscal print reports
--UPDATE RESOURCES SET CONTENT=$FILE{/com/ghintech/fiscalprint/templates/ticket.bematech.bsh} WHERE ID = '0';
--UPDATE ROLES SET CONTENT=$FILE{/com/ghintech/fiscalprint/templates/Role.Administrator.xml} WHERE ID = '0';
ALTER TABLE public.tickets ADD COLUMN cuponno character varying;
-- UPDATE App' version
UPDATE APPLICATIONS SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};
