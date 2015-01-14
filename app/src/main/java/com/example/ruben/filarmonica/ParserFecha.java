package com.example.ruben.filarmonica;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import android.util.Log;
import android.widget.TextView;

public class ParserFecha 
{
	private String fecha;
	private String hora_cadena;
	private TextView lblReloj;
	
	private int anio;
	private int mes;
	private int dia;
	private int hora;
	private int minuto;
	
	//Fechas.
	private Calendar fecha_actual = Calendar.getInstance();
	private Calendar fecha_faltante = Calendar.getInstance();
	private Calendar fecha_proximo_concierto = Calendar.getInstance();
	
	//Variables que se usar�n para crear el reloj.
	private int dia_efectivo;
	private int hora_efectiva;
	private int minuto_efectivo;
	private int segundo_efectivo;
	
	ParserFecha(ArrayList<String> arregloFecha, TextView lblReloj)
	{
		this.lblReloj = lblReloj;
		
		fecha = arregloFecha.get(0);
		hora_cadena  = arregloFecha.get(1);
		Log.i("frank", "Fecha: " + fecha);
		Log.i("frank", "Hora: " + hora_cadena);
		//Limpiamos las cadenas.
		fecha = fecha.trim();
		hora_cadena = hora_cadena.trim();
		
		//Separamos la fecha por espacios.
		StringTokenizer tokenizer_fecha = new StringTokenizer (fecha, "-");
		
		//La fecha tiene el siguiente formato:
		// Viernes 28 de Noviembre del 2014.
		//Necesitaremos el elemento 1, 3, 5. (impares).
		for(int i = 0; tokenizer_fecha.hasMoreTokens(); i++)
		{
			//Obtenemos el token.
			String token = tokenizer_fecha.nextToken();
			token = token.trim();
			Log.i("frank", "Toke Fecha: " + i + " - " + token);
			//Si el token est� en una posici�n impar lo mandamos al respectivo parser.
			
			switch(i)
			{
				//A�o
				case 0:
				{
					parsearAnio(token);
					break;
				}
				
				//Mes
				case 1:
				{
					parsearMes(token);
					break;
				}
				
				//A�o
				case 2:
				{
					parsearDia(token);
					break;
				}
				
				default:
				{
					//ERROR.
					lblReloj.setText("ERROR FECHA.");
				}
			}	
		}
		
		//Separamos la hora por ":".
		StringTokenizer tokenizer_hora = new StringTokenizer(hora_cadena, ":");
		
		//La hora tiene el siguiente formato.
		//20:00
		for(int i = 0; tokenizer_hora.hasMoreTokens(); i++)
		{
			//Obtenemos el token.
			String token = tokenizer_hora.nextToken();
			token = token.trim();
			Log.i("frank", "Toke Hora: " + i + " - " + token);
			switch(i)
			{
				//Hora
				case 0:
				{
					parsearHora(token);
					break;
				}
				
				//Minuto
				case 1:
				{
					parsearMinuto(token);
					break;
				}
				
				default:
				{
					lblReloj.setText("ERROR HORA.");
				}
			}
		}
		
		//Colocamos la fecha del pr�ximo concierto.
		fecha_proximo_concierto.set(anio, mes, dia, hora, minuto, 0);
		
		//La fecha faltante para el pr�ximo concierto es igual a fecha del pr�ximo concierto
		//menos la fecha actual.
		fecha_faltante.set(anio, mes, dia, hora, minuto, 0);
		fecha_faltante.add(Calendar.DAY_OF_MONTH,  - fecha_actual.get(Calendar.DAY_OF_MONTH));
		fecha_faltante.add(Calendar.HOUR_OF_DAY,  - fecha_actual.get(Calendar.HOUR_OF_DAY));
		fecha_faltante.add(Calendar.MINUTE,  - fecha_actual.get(Calendar.MINUTE));
		fecha_faltante.add(Calendar.SECOND,  - fecha_actual.get(Calendar.SECOND));
		
		//Ya que tenemos los campos parseados obtenemos los d�as, horas y minutos efectivos.
		dia_efectivo = fecha_faltante.get(Calendar.DAY_OF_MONTH);
		hora_efectiva = fecha_faltante.get(Calendar.HOUR_OF_DAY);
		minuto_efectivo = fecha_faltante.get(Calendar.MINUTE);
		segundo_efectivo = fecha_faltante.get(Calendar.SECOND);
	}
	
	public void parsearAnio(String anio)
	{
		this.anio = Integer.parseInt(anio);
	}
	
	public void parsearMes(String mes)
	{	
		this.mes = Integer.parseInt(mes);
	}
	
	public void parsearDia(String dia)
	{
		this.dia = Integer.parseInt(dia);
	}
	
	public void parsearHora(String hora)
	{
		this.hora = Integer.parseInt(hora);
	}
	
	public void parsearMinuto(String minuto)
	{		
		this.minuto = Integer.parseInt(minuto);
	}
	
	public String getFecha() {
		return fecha;
	}

	public String getHora_cadena() {
		return hora_cadena;
	}

	public TextView getLblReloj() {
		return lblReloj;
	}

	public int getAnio() {
		return anio;
	}

	public int getMes() {
		return mes;
	}

	public int getDia() {
		return dia;
	}

	public int getHora() {
		return hora;
	}

	public int getMinuto() {
		return minuto;
	}
	
	//Getter de las variables efectivas.
	public int getDiaEfectivo()
	{
		return dia_efectivo;
	}
	
	public int getHoraEfectiva()
	{
		return hora_efectiva;
	}
	
	public int getMinutoEfectivo()
	{
		return minuto_efectivo;
	}
	
	public int getSegundoEfectivo()
	{
		return segundo_efectivo;
	}
}
