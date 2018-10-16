package com.github.adminfaces.starter.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

@Entity
public class TafExercicio implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	@Id		
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
		
	@ManyToOne
	@JoinColumn(nullable=false)
	private Exercicio exercicio;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Taf taf;
	
	@Column private String modalidade;
		
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Exercicio getExercicio() {
		return exercicio;
	}
	
	public void setExercicio(Exercicio exercicio) {
		this.exercicio = exercicio;
	}
	
	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public Taf getTaf() {
		return taf;
	}

	public void setTaf(Taf taf) {
		this.taf = taf;
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
		TafExercicio other = (TafExercicio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}