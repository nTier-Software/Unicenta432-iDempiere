/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.fiscalprint;

import java.io.File;

/**
 *
 * @author egil
 */
public class Hasar {

	
	////////////////////////////////
	//CONSTANTES DE IMPRESION
	/////////////////////
	public String NamePort = "",  StatusError = "";
	public boolean IndPort = false;

    /**
     * PrintLineItem - Imprimir ítem<br/>
     * $PI = "B" ASCII 66<br/>
     * Estado no válido* -> ERROR_0<br/>
     * Error sintáctico en alguno de los parámetros 1 a 5 -> ERROR_1 a ERROR_5<br/>
     * Tasa no coincide con las programadas -> ERROR_4<br/>
     * Error en límites (> máximo del documento o < 0) -> ERROR_0**<br/>
     * estados no válidos: todos menos SELL_DF y PRINT_TEXT_DF<br/>
     * *modifica status fiscal: comando no válido (bit 5) y desborde total (bit 6)<br/>
     */
	public char PI = 66;

    /**
     * OpenFiscalReceipt - Abrir comprobante fiscal<br/>
     * $DF = "@" ASCII 64<br/>
     * Estado no válido* -> ERROR_0<br/>
     * Error sintáctico en algunos de los parámetros 1 a 7 -> ERROR_1 a ERROR_7<br/>
     * *estados no válidos: todos menos INI_JF y EN_JF<br/>
     */
	public char DF = 64;

    /**
     * SetHeader - Texto Encabezado Documentos
     * $TH = "]" ASCII 93
     */
	public char TH = 93;

    /**
     * Separador de campos del comando
     * FS ASCII 28
     */
	public char FS = 28;

    /**
     * Caracter de nueva line NEWLINE
     * ASCII 10
     */
	public char LF = 10;

    /**
     * Backspace, DEL, SUPR
     * ASCII 127
     */
	
	public char BS = 127; 
	
	public String FD = "";
	public char TD = 'A';
	
	public char CED=156;
	public char DOCF=94;
	
	public String file = "";
	public String NR = "1FHS310000740";
	public String X = "9" + FS + "X" + LF;
	public String Z = "9" + FS + "Z" + LF;
	

    ///////////////////////////////////////////////////////////////////////
    // ESTADO DE IMPRESORA 
    ///////////////////////////////////////////////////////////////////////
    
    /** Impresora ocupada. (Bit 0 del estado de impresora) */
    public int PST_PRINTER_BUSY               = 0x0001;
    /** Error de impresora. (Bit 2 del estado de impresora) */
    public int PST_PRINTER_ERROR              = 0x0004;
    /** Impresora fuera de línea. No ha podido comunicarse con la impresora dentro del per�odo de tiempo establecido. (Bit 3 del estado de impresora) */
    public int PST_PRINTER_OFFLINE            = 0x0008;
    /** Falta papel del diario. (Bit 4 del estado de impresora) */
    public int PST_JOURNAL_PAPER_OUT          = 0x0010;
    /** Falta papel de tickets. (Bit 5 del estado de impresora) */
    public int PST_TICKET_PAPER_OUT           = 0x0020;
    /** Buffer de impresión lleno. Cualquier comando que se envíe 
     * cuando este bit está en 1 no se ejecuta y debe ser reenviado 
     * por la aplicación. (Bit 6 del estado de impresora) */
    public int PST_PRINT_BUFFER_FULL          = 0x0040;
    /** Buffer de impresión vacío. Todos los comandos fueron 
     * enviados a la impresora. (Bit 7 del estado de impresora) */
    public int PST_PRINT_BUFFER_EMPTY         = 0x0080;
    /** Tapa de impresora abierta. (Bit 8 del estado de impresora) */
    public int PST_PRINTER_COVER_OPEN         = 0x0100;

    /** Cajón de dinero cerrado o ausente. (Bit 14 del estado de impresora) */
    public int PST_MONEY_DRAWER_CLOSED        = 0x4000;
    /** Suma lógica (OR) de los bits 2 a 5, 8 y 14. Este bit se 
     * encuentra en 1 siempre que alguno de los bits mencionados 
     * se encuentre en 1. (Bit 15 del estado de impresora) */
    public int PST_BITWISE_OR                 = 0x8000;

    ///////////////////////////////////////////////////////////////////////
    // ESTADO FISCAL 
    ///////////////////////////////////////////////////////////////////////
    
    /** Error en chequeo de memoria fiscal. Al encenderse la 
     * impresora se produjo un error en el checksum. La impresora no 
     * funcionará. (Bit 0 de estado fiscal) */
    public int FST_FISCAL_MEMORY_CRC_ERROR    = 0x0001;
    /** Error en chequeo de memoria de trabajo. Al encenderse la impresora 
     * se produjo un error en el checksum. La impresora no funcionará. 
     * (Bit 1 de estado fiscal) */
    public int FST_WORKING_MEMORY_CRC_ERROR   = 0x0002;
    /** Comando desconocido. El comando recibido no fue reconocido. 
     * (Bit 3 de estado fiscal) */
    public int FST_UNKNOWN_COMMAND            = 0x0008;
    /** Datos inválidos en un campo. Uno de los campos del comando recibido 
     * tiene datos no válidos (por ejemplo, datos no numéricos en un campo 
     * numérico). (Bit 4 de estado fiscal) */
    public int FST_INVALID_DATA_FIELD         = 0x0010;
    /** Comando inválido para el estado fiscal actual. Se ha recibido un 
     * comando que no es válido en el estado actual del controlador (por 
     * ejemplo, abrir un documento no fiscal cuando se encuentra abierto un 
     * documento fiscal). (Bit 5 de estado fiscal) */
    public int FST_INVALID_COMMAND            = 0x0020;
    /** Desborde de acumulador. El acumulador de una transacción, del total 
     * diario o del IVA se desbordará a raíz de un comando recibido. El comando 
     * no es ejecutado. Este bit debe ser monitoreado por la aplicación para 
     * emitir el correspondiente aviso. (Bit 6 de estado fiscal) */
    public int FST_ACCUMULATOR_OVERFLOW       = 0x0040;
    /** Memoria fiscal llena, bloqueada o dada de baja. No se permite abrir un 
     * comprobante fiscal. (Bit 7 de estado fiscal) */
    public int FST_FISCAL_MEMORY_FULL         = 0x0080;
    /** Memoria fiscal a punto de llenarse. La memoria fiscal tiene 30 o menos 
     * registros libres. Este bit debe ser monitoreado por la aplicación para 
     * emitir el correspondiente aviso. (Bit 8 de estado fiscal) */
    public int FST_FISCAL_MEMORY_ALMOST_FULL  = 0x0100;
    /** Terminal fiscal certificada. Indica que la impresora ha sido 
     * inicializada. (Bit 9 de estado fiscal) */
    public int FST_DEVICE_CERTIFIED           = 0x0200;
    /** Terminal fiscal fiscalizada. Indica que la impresora ha sido 
     * inicializada. (Bit 10 de estado fiscal) */
    public int FST_DEVICE_FISCALIZED          = 0x0400;
    /** Error en ingreso de fecha. Se ha ingresado una fecha inválida. 
     * Para volver el bit a 0 debe ingresarse una fecha válida. 
     * (Bit 11 de estado fiscal) */
    public int FST_DATE_ERROR                = 0x0800;
    /** Documento fiscal abierto. Este bit se encuentra en 1 siempre que un 
     * documento fiscal (factura, recibo oficial o nota de crédito) se encuentra 
     * abierto. (Bit 12 de estado fiscal) */
    public int FST_FISCAL_DOCUMENT_OPEN       = 0x1000;
    /** Documento abierto (solo impresoras que soportan STATPRN). Este bit se 
     * encuentra en 1 siempre que un documento (fiscal, no fiscal o no fiscal 
     * homologado) se encuentra abierto. (Bit 13 de estado fiscal) */
    public int FST_DOCUMENT_OPEN              = 0x2000;
    /** Estado intermedio (STATPRN) activo (solo impresoras que soportan 
     * STATPRN). Este bit se encuentra en 1 cuando se intenta enviar un 
     * comando estando activado el STATPRN. El comando es rechazado. 
     * (Bit 14 de estado fiscal) */
    public int FST_STATPRN_ACTIVE             = 0x4000;
    /** Documento abierto en impresora Ticket (solo impresoras que no soportan 
     * STATPRN). (Bit 13 de estado fiscal) */
    public int FST_DOCUMENT_OPEN_TICKET       = 0x2000;
    /** Documento abierto en impresora Slip (solo impresoras que no 
     * soportan STATPRN). (Bit 14 de estado fiscal) */
    public int FST_DOCUMENT_OPEN_SLIP         = 0x4000;
    /** Suma lógica (OR) de los bits 0 a 8. Este bit se encuentra en 1 
     * siempre que alguno de los bits mencionados se encuentre en 1. 
     * (Bit 15 de estado fiscal) */
    public int FST_BITWISE_OR                 = 0x8000;

    public void emptyInvoiceFolder(String invoiceFolder){
        File folder = new File(invoiceFolder);
        File[] files = folder.listFiles();
        if(files!=null) { 
            for(File f: files) {
                f.delete();
            }
        } 
    }
}
