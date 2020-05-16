/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.fiscalprint;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author alara
 * Code adapted from adempiere Fiscal-Print-Adapter
 */
public class HandlerSpooler {
        Runtime 				runT					= 	null;
	/**	Puerto							*/
	private String			port					=	null;
	/**	Velocidad						*/
	private int				speed					=	0;
	/**	Ruta del Archivo de Registro	*/
	private String 			pathFileLog				= 	null;
	/**	Nombre del directorio donde va 
	 * a guardar los archivos temporales
	 */
	private String 			pathNameTmp				= 	null;
	/**	Path Spooler					*/
	private String 			pathSpooler				= 	null;
	/**	Arguments						*/
	private StringBuffer 	args 					= 	null;
	/**	Message							*/
	private StringBuffer 	msg 					=	null;
	/**	OS CMD							*/
	private String 			os_Cmd					=	"";
	/**	Separator						*/
	String 					cSeparator				=	null;
	/**	Code Prionter					*/
	String 					cPrinter				=	null;
	/**	Constador de lineas del Encabezdo	*/
	private int				lineHeader				=	0;
	/**	Contador de lineas del Pie de Pagina*/
	private int				lineTrailer				=	0;
	/**	Cmd Port				*/
	private final String CMD_M_PORT__P				=	"-p";
	/**	Cmd Speed				*/
	private final String CMD_O_SPEED__V				=	"-v";
	/**	Cmd	Search Speed		*/
	private final String CMD_O_SEARCH_SPEED__T		= 	"-t";
	/**	Cmd Protocolo Log File	*/
	private final String CMD_O_PROT_LOG__L			=	"-l";
	/**	Cmd	Path Name File Log	*/
	private final String CMD_O_PATH_LOG__D			=	"-d";
	/**	Cmd	Retrieved File		*/
	private final String CMD_O_ID_FILE__N			=	"-n";
	/**	Cmd	Not Message			*/
	private final String CMD_O_N_MSG__M				=	"-m";
	/**	Cmd Path File Tmp		*/
	private final String CMD_O_PH_TMP__B			=	"-b";
	/**	Not Cancel Journal		*/
	private final String CMD_O_NC_JOURNAL__B		=	"-b";
	/**	Cmd Command				*/
	private final String CMD_M_CMD__C				=	"-c";
	
	/**	Param P					*/
	private final String P__V_Z						=	"z";
	
	/**	Space					*/
	private final String C_SPACE					=	" ";
	/**	Quotes					*/
	private final String C_QUOTE					=	"\"";
	/**	X Report				*/
	private final String CMD_S_X					=	"9X";
	/**	Z Report				*/
	private final String CMD_S_Z					=	"9Z";
        
        /**Encabezado de Comprobante*/
	//"@Yamel SenihV-20237661120401093310A"
	//"BCoca-Cola25.10.7.M123456"
        	/**
	 * 
	 * *** Constructor de la Clase ***
	 * @author Yamel Senih 04/02/2012, 09:09:09
	 * @param port
	 * @param speed
	 * @param pathSpooler
	 * @param pathFileLog
	 * @param pathNameTmp
	 * @param os_Cmd
	 * @param cPrinter
	 * @param cSeparator
	 */
	public HandlerSpooler(String port, 
			int speed,
			String pathSpooler,
			String pathFileLog,
			String pathNameTmp, 
			String os_Cmd,
			String cPrinter, 
			int cSeparator){
		runT = Runtime.getRuntime();
		this.port = port;
		this.speed = speed;
		this.pathSpooler = pathSpooler;
		this.pathFileLog = pathFileLog;
		this.pathNameTmp = pathNameTmp;
		if(os_Cmd != null)
			this.os_Cmd = os_Cmd;
		this.cPrinter = cPrinter;
		this.cSeparator = Character.toString((char)cSeparator);
		
		//	Arguments
		args = new StringBuffer();
		//	Path Spooler
		args.append(pathSpooler);
		//	Space
		args.append(C_SPACE);
		//	Param
		//args.append(CMD_M_PORT__P);
		//	Space
		//args.append(C_SPACE);
		//	Port
		args.append("-"+port);
		//	Speed
		if(speed != 0){
			args.append(C_SPACE);
			args.append(CMD_O_SPEED__V + speed);
		}
		//	Parth Log File
		if(pathFileLog != null 
				&& pathFileLog.length() != 0){
			args.append(C_SPACE);
			args.append(CMD_O_PATH_LOG__D + 
					C_SPACE + 
					pathFileLog);
		}
		//	Path Tmp Files
		if(pathNameTmp != null 
				&& pathNameTmp.length() != 0){
			args.append(C_SPACE);
			args.append(CMD_O_PH_TMP__B + 
					C_SPACE + 
					pathNameTmp);
		}
		
	}
	        
	public HandlerSpooler(String port,
			String pathSpooler, String cPrinter, int cSeparator){
		this(port, 0, pathSpooler, null, null, null, cPrinter, cSeparator);
	}
        
/**
	 * 
	 * *** Constructor de la Clase ***
	 * @author Yamel Senih 04/02/2012, 09:10:07
	 * @param port
	 * @param pathSpooler
	 * @param os_Cmd
	 * @param cPrinter
	 * @param cSeparator
	 */
	public HandlerSpooler(String port, 
			String pathSpooler,
			String os_Cmd,
			String cPrinter, 
			int cSeparator){
		this(port, 0, pathSpooler, null, null, os_Cmd, cPrinter, cSeparator);
	}
        
	public void printCommand(String cmd) throws Exception {
    	
        Process process = null;
        
        System.out.println(
        		os_Cmd + 
        		C_SPACE + 
        		args.toString() + 
        		C_SPACE + 
        		CMD_O_PROT_LOG__L + 
        		P__V_Z + 
        		C_SPACE + 
        		CMD_M_CMD__C + 
        		C_SPACE + 
        		C_QUOTE +
        		cmd + 
        		C_QUOTE);
        
        process = runT.exec(
        		os_Cmd + 
        		C_SPACE + 
        		args.toString() + 
        		C_SPACE + 
        		CMD_O_PROT_LOG__L + 
        		P__V_Z + 
        		C_SPACE + 
        		CMD_M_CMD__C + 
        		C_SPACE + 
        		C_QUOTE +
        		cmd + 
        		C_QUOTE);
        
        int exitVal = process.waitFor();
        System.err.println(exitVal);
	}
        public void OpenNonFiscalDocument() throws Exception {
    	
        	printCommand("H" + 
			cSeparator + 
			"C" );
	}
        public void PrintNonFiscalText(String text) throws Exception {
    	
        	printCommand("I" + 
			cSeparator + 
			text );
	}
        public void CloseNonFiscalReceipt() throws Exception {
    	
        	printCommand("J" );
	}
        
        
	
	public void printCommand(String[] cmd) throws Exception{
		for (int i = 0; i < cmd.length; i++) {
			printCommand(cmd[i]);
		}
	}
	
	public void printLine(String product, String price, String units,
			String taxRate, String facProd, String codProduct) throws Exception{
                    NumberFormat f = NumberFormat.getInstance(Locale.ENGLISH);
        f.setMinimumFractionDigits(1);
        if (f instanceof DecimalFormat) {
            ((DecimalFormat) f).setDecimalSeparatorAlwaysShown(true);
        }
		printCommand("B" + 
			cSeparator + 
			product + 
			cSeparator + 
			units + 
			cSeparator + 
			price + 
			cSeparator + 
			taxRate + 
			cSeparator + 
			(facProd != null? facProd: "M") + 
			cSeparator + 
			codProduct
			);
	}

        public void printPaymentForm(String payment, String price, int type
                        ) throws Exception{
		printCommand("D" + 
			cSeparator + 
			payment + 
			cSeparator + 
			price + 
			cSeparator + 
			"T" + 
			cSeparator + 
			type
			);
	}
        
        public void printTextHeader(String sheader) throws Exception {
		if(lineHeader == 0)
			printCommand("]" + cSeparator + lineHeader++ + cSeparator + sheader);
		if(lineHeader <= 4)
			printCommand("]" + cSeparator + lineHeader++ + cSeparator + sheader);
	}
	        
	public void printMessage(String smessage) throws Exception{
		printCommand("A" + cSeparator + smessage + cSeparator);
	}

	public void printTextTrailer(String strailer) throws Exception {
		if(lineTrailer == 0)
			printCommand("^" + cSeparator + lineTrailer++ + cSeparator + strailer);
		if(lineTrailer <= 4)
			printCommand("^" + cSeparator + lineTrailer++ + cSeparator + strailer);
	}        

        public void printZReport() throws Exception{
		printCommand("9" + cSeparator + "Z" + cSeparator);
	}

	public void printXReport() throws Exception{
		printCommand("9" + cSeparator + "X" + cSeparator);
	}
        
        public void get_g_Status() throws Exception{
		printCommand("g");
	}
        
        public void get_Status() throws Exception{
		printCommand("*");
	}
        
	public void printHeader(String nameBP, String rfc, String nroVoucher,
			String codPrinter, String dateVoucher, String timeVoucher, String docType)
			throws Exception {
		StringBuffer cmd = new StringBuffer();
		cmd.append("@");
		cmd.append(cSeparator);
		//	Name Business Partner
		if(nameBP != null)
			cmd.append(nameBP);
		cmd.append(cSeparator);
		//	RFC
		if(rfc != null)
			cmd.append(rfc);
		cmd.append(cSeparator);
		//	Nro Voucher
		if(nroVoucher != null)
			cmd.append(nroVoucher);
		cmd.append(cSeparator);
		//	Code Printer
		if(codPrinter != null)
			cmd.append(codPrinter);
		cmd.append(cSeparator);
		//	date Voucher
		if(dateVoucher != null)
			cmd.append(dateVoucher);
		cmd.append(cSeparator);
		//	time Voucher
		if(timeVoucher != null)
			cmd.append(timeVoucher);
		cmd.append(cSeparator);
		//	docType
		if(docType != null)
			cmd.append(docType);
		cmd.append(cSeparator);
		cmd.append(cSeparator);
		
		printCommand(cmd.toString());
		
	}

	public void printFiscalMemReport(Timestamp dateFrom, Timestamp dateTo,
			String type) throws Exception {
		if(type == null)
			throw new Exception("@Type@ = Null");
		//	
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		//	String
		String strFrom = sdf.format(new Date(dateFrom.getTime()));
		String strTo = sdf.format(new Date(dateTo.getTime()));
		
		printCommand(":" + cSeparator + strFrom + cSeparator + strTo + cSeparator + type);
	}        

	public void printFiscalMemReport(int zFrom, int zTo, String type)
			throws Exception {
		if(type == null)
			throw new Exception("@Type@ = Null");
		//	
		printCommand(";" + cSeparator + zFrom + cSeparator + zTo + cSeparator + type);
	}
}
