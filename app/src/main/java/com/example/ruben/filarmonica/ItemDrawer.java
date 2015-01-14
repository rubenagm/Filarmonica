package com.example.ruben.filarmonica;

public class ItemDrawer 
{
	private int icono;
	private int icono2;
	
	public ItemDrawer(int icono) 
	{
		super();
		this.icono = icono;
	}

	public ItemDrawer(int icono, int icono2) 
	{
		this.icono = icono;
		this.icono2 = icono2;
	}

	public int getIcono() {
		return icono;
	}

	public void setIcono(int icono) {
		this.icono = icono;
	}

	public int getIcono2() {
		return icono2;
	}

	public void setIcono2(int icono2) {
		this.icono2 = icono2;
	}
	
	
}
