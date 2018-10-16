package com.github.adminfaces.starter.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class Exercicio implements Serializable {
	
	private static final long serialVersionUID = 1L; 
	
	@Id		
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column	private String nome;
	@Column private String descricao;
	
	@Column	private int umrmFraco;
	@Column private int umrmForte;
		
	@Column	private int rmFraco;
	@Column private int rmForte;
	
	@Column	private int vtFraco;
	@Column private int vtForte;

	@OneToMany(mappedBy="exercicio", cascade=CascadeType.ALL)
	private List<TafExercicio> tafexercicio;	
	
	@Transient
	private List<String> modalidades;
	
	public List<String> getModalidades() {
		return modalidades;
	}
	public void setModalidades(List<String> modalidades) {
		System.out.println("TA SALVANDO?");
		//for(String m : modalidades)
		//	System.out.println(m);
		this.modalidades = modalidades;
	}
	
	public int getUmrmFraco() {
		return umrmFraco;
	}
	public void setUmrmFraco(int umrmFraco) {
		this.umrmFraco = umrmFraco;
	}
	public int getUmrmForte() {
		return umrmForte;
	}
	
	public void setUmrmForte(int umrmForte) {
		this.umrmForte = umrmForte;
	}
	public int getRmFraco() {
		return rmFraco;
	}
	public void setRmFraco(int rmFraco) {
		this.rmFraco = rmFraco;
	}
	
	public List<TafExercicio> getTafexercicio() {
		return tafexercicio;
	}
	public void setTafexercicio(List<TafExercicio> tafexercicio) {
		this.tafexercicio = tafexercicio;
	}
	public int getRmForte() {
		return rmForte;
	}
	public void setRmForte(int rmForte) {
		this.rmForte = rmForte;
	}
	public int getVtFraco() {
		return vtFraco;
	}
	public void setVtFraco(int vtFraco) {
		this.vtFraco = vtFraco;
	}
	
	public int getVtForte() {
		return vtForte;
	}
	public void setVtForte(int vtForte) {
		this.vtForte = vtForte;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Exercicio other = (Exercicio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}