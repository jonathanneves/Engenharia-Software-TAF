package com.github.adminfaces.starter.model;

public class Ranking {
	
	private String texto;
	private int totalpontos;
	
	public Ranking(String texto, int totalpontos) {
		this.texto = texto;
		this.totalpontos = totalpontos;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public int getTotalpontos() {
		return totalpontos;
	}
	public void setTotalpontos(int totalpontos) {
		this.totalpontos = totalpontos;
	}

	
	
}
