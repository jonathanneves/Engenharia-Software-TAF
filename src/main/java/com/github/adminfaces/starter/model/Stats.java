package com.github.adminfaces.starter.model;

import java.io.Serializable;

import com.github.adminfaces.starter.model.TafRanking;

public class Stats implements Serializable {
     
	private String posicao;	
	private String aluno;     
    private String exercicio1;    
    private String exercicio2;    
    private String exercicio3;    
    private int total;
    

	public Stats() {}
 
    public Stats(String posicao, String aluno, String exercicio1, String exercicio2, String exercicio3, int total) {
    	this.posicao = posicao;
        this.aluno = aluno;
        this.exercicio1 = exercicio1;
        this.exercicio2 = exercicio2;
        this.exercicio3 = exercicio3;
        this.total = total;
    
    }
 
    public String getExercicio1() {
        return exercicio1;
    }
 
    public void setExercicio1(String exercicio1) {
        this.exercicio1 = exercicio1;
    }
 
    public String getExercicio2() {
        return exercicio2;
    }
 
    public void setExercicio2(String exercicio2) {
        this.exercicio2 = exercicio2;
    }
 
    public String getExercicio3() {
		return exercicio3;
	}

	public void setExercicio3(String exercicio3) {
		this.exercicio3 = exercicio3;
	}
    
    public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getAluno() {
        return aluno;
    }
 
    public void setAluno(String aluno) {
        this.aluno = aluno;
    }

	public String getPosicao() {
		return posicao;
	}

	public void setPosicao(String posicao) {
		this.posicao = posicao;
	}
    
    
}