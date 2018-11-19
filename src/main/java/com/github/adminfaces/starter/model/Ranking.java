package com.github.adminfaces.starter.model;

public class Ranking {
	
	private String posicao;
	private String texto;
	private int totalpontos;
	
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
	public String getPosicao() {
		return posicao;
	}
	public void setPosicao(String posicao) {
		this.posicao = posicao;
	}
	
}
