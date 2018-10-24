package com.github.adminfaces.starter.model;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;

@Entity
public class Taf implements Serializable {
	
	private static final long serialVersionUID = 1L; 
	
	@Id		
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@Column	private String nome;
	@Column @Type (type="date")
	private Date data;
	@Column
	private String realizado = "N";
	
	@OneToMany(mappedBy="taf", cascade=CascadeType.ALL)
	private List<TafExercicio> tafexercicio;

	@Override
	public String toString() {
		return getNome() + " - " + getData();
	}
	
	public String getRealizado() {
		return realizado;
	}

	public void setRealizado(String realizado) {
		this.realizado = realizado;
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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public List<TafExercicio> getTafexercicio() {
		return tafexercicio;
	}

	public void setTafexercicio(List<TafExercicio> tafexercicio) {
		this.tafexercicio = tafexercicio;
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
		Taf other = (Taf) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
}
	