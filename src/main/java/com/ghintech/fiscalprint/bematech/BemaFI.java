/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ghintech.fiscalprint.bematech;

import com.sun.jna.Library;

public interface BemaFI extends Library {
    public int Bematech_FI_ProgramaAlicuota(String var1, int var2);

    public int Bematech_FI_ProgramaRedondeo();

    public int Bematech_FI_ProgramaTruncamiento();

    public int Bematech_FI_ActivaDesactivaReporteZAutomatico(int var1);

    public int Bematech_FI_ActivaDesactivaCuponAdicional(int var1);

    public int Bematech_FI_ActivaDesactivaVinculadoComprobanteNoFiscal(int var1);

    public int Bematech_FI_ActivaDesactivaImpresionBitmapMA(int var1);

    public int Bematech_FI_HoraLimiteReporteZ(String var1);

    public int Bematech_FI_ProgramaCliche(String var1);

    public int Bematech_FI_AbreComprobanteDeVenta(String var1, String var2);

    public int Bematech_FI_AbreComprobanteDeVentaEx(String var1, String var2, String var3);

    public int Bematech_FI_VendeArticulo(String var1, String var2, String var3, String var4, String var5, int var6, String var7, String var8, String var9);

    public int Bematech_FI_AnulaArticuloAnterior();

    public int Bematech_FI_AnulaCupon();

    public int Bematech_FI_CierraCupon(String var1, String var2, String var3, String var4, String var5, String var6);

    public int Bematech_FI_CierraCuponReducido(String var1, String var2);

    public int Bematech_FI_IniciaCierreCupon(String var1, String var2, String var3);

    public int Bematech_FI_EfectuaFormaPago(String var1, String var2);

    public int Bematech_FI_FinalizarCierreCupon(String var1);

    public int Bematech_FI_DevolucionArticulo(String var1, String var2, String var3, String var4, String var5, int var6, String var7, String var8, String var9);

    public int Bematech_FI_AbreNotaDeCredito(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, String var11);

    public int Bematech_FI_AbreFacturaFis(String var1, String var2, String var3, String var4, String var5);
    
    public int Bematech_FI_Abre_Nota_Cred_Fis(String var1, String var2, String var3, String var4, String var5);

    public int Bematech_FI_LecturaX();

    public int Bematech_FI_LecturaXSerial();

    public int Bematech_FI_ReduccionZ(String var1, String var2);

    public int Bematech_FI_InformeGerencial(String var1);

    public int Bematech_FI_CierraInformeGerencial();

    public int Bematech_FI_FlagFiscalesIII(int var1);

    public int Bematech_FI_InformeTransacciones(String var1, String var2, String var3, String var4);

    public int Bematech_FI_RecebimientoNoFiscal(String var1, String var2, String var3);

    public int Bematech_FI_AbreComprobanteNoFiscalVinculado(String var1, String var2, String var3);

    public int Bematech_FI_ImprimeComprobanteNoFiscalVinculado(String var1);

    public int Bematech_FI_CierraComprobanteNoFiscalVinculado();

    public int Bematech_FI_Sangria(String var1);

    public int Bematech_FI_Provision(String var1, String var2);

    public int Bematech_FI_AbreInformeGerencialMFD(String var1);

    public int Bematech_FI_Agregado(String[] var1);

    public int Bematech_FI_Cancelamientos(String[] var1);

    public int Bematech_FI_DatosUltimaReduccion(String[] var1);

    public int Bematech_FI_Descuentos(String[] var1);

    public int Bematech_FI_NumeroCuponesAnulados(String[] var1);

    public int Bematech_FI_RetornoAlicuotas(String[] var1);

    public int Bematech_FI_ClavePublica(String var1);

    public int Bematech_FI_ContadorSecuencial(String var1);

    public int Bematech_FI_VentaBrutaDiaria(String var1);

    public int Bematech_FI_BaudrateProgramado(String var1);

    public int Bematech_FI_FlagActivacionAlineamientoIzquierda(String var1);

    public int Bematech_FI_FlagSensores(int var1);

    public int Bematech_FI_ImprimeClavePublica();

    public int Bematech_FI_AccionaGaveta();

    public int Bematech_FI_Autenticacion();

    public int Bematech_FI_ProgramaCaracterAutenticacion(String var1);

    public int Bematech_FI_VerificaEstadoGaveta(int var1);

    public int Bematech_FI_AbrePuertaSerial();

    public int Bematech_FI_CierraPuertaSerial();

    public int Bematech_FI_RetornoImpresora(int var1, int var2, int var3);
    
    public int Bematech_FI_LecturaMemoriaFiscalFecha(String FechaInicial,String FechaFinal);
    
    public int Bematech_FI_LecturaMemoriaFiscalReduccion(String ReduccionInicial, String ReduccionFinal);
    
    public int Bematech_FI_LecturaMemoriaFiscalSerialFecha(String FechaInicial,String FechaFinal);
    
    public int Bematech_FI_LecturaMemoriaFiscalSerialReduccion(String ReduccionInicial, String ReduccionFinal);
    
    public int Bematech_FI_VerificaEstadoImpresora(int ACK, int ST1,int ST2);
    
    public int Bematech_FI_VerificaImpresoraPrendida();
    
    public int Bematech_FI_NumeroCupon(String NumeroCupon);
    
    public int Bematech_FI_NumeroComprobanteFiscal(String NumeroComprobanteFiscal);
    
    public int Bematech_FI_CopiaUltimaFactura();    
    
    public int Bematech_FI_ImpressaoFitaDetalhe( String tipo, String dadoInicial, String dadoFinal, String usuario );        
}