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

@Entity
public class TafExercicio implements Serializable {

	private static final long serialVersionUID = 1L; 
	
	
	@Id		
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column private String nome;
	@Column	private Date data;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Exercicio exercicio;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Taf taf;
	
	@Column (name="modalidade1RM", nullable = false,  columnDefinition = "varchar(1) default 'N'") 
	private String modalidade1RM;
	@Column (name="modalidadeMAX", nullable = false,  columnDefinition = "varchar(1) default 'N'") 
	private String modalidadeMAX;
	@Column (name="modalidadeVT", nullable = false,  columnDefinition = "varchar(1) default 'N'") 
	private String modalidadeVT;
	
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
	
	public Taf getTaf() {
		return taf;
	}
	
	public void setTaf(Taf taf) {
		this.taf = taf;
	}
	
	public String getModalidade1RM() {
		return modalidade1RM;
	}

	public void setModalidade1RM(String modalidade1rm) {
		modalidade1RM = modalidade1rm;
	}

	public String getModalidadeMAX() {
		return modalidadeMAX;
	}

	public void setModalidadeMAX(String modalidadeMAX) {
		this.modalidadeMAX = modalidadeMAX;
	}

	public String getModalidadeVT() {
		return modalidadeVT;
	}

	public void setModalidadeVT(String modalidadeVT) {
		this.modalidadeVT = modalidadeVT;
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