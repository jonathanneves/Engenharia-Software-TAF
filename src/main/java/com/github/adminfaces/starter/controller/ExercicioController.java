package com.github.adminfaces.starter.controller;

import java.io.Serializable;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.github.adminfaces.starter.model.Exercicio;
import com.github.adminfaces.starter.model.TafExercicio;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean //Classe controladora
@ViewScoped
public class ExercicioController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Exercicio exercicio;
	private List<Exercicio> exercicios;
    
	@PostConstruct
	public void init() {
		exercicio = new Exercicio();
		listarTodas();
	}
	

	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.merge(exercicio);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
			t.commit();
			exercicio = new Exercicio();
			addMessage("Cadastro", "Exercicio cadastrado com sucesso");
			listarTodas();
		} catch (Exception e) {
			if(t!=null)
				t.rollback();
			throw(e);
		}finally{
			sessao.close();
		}
	}
	
	public void exclui ()  {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.delete(exercicio);			
			t.commit();
			exercicio = new Exercicio();
			addMessage("Exclusão", "Exercicio excluído com sucesso");
			listarTodas();
		} catch (Exception e) {
			if(t!=null)
				t.rollback();
			throw(e);
		}finally{
			sessao.close();
		}
	}
	
	public void listarTodas() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
		
			CriteriaQuery<Exercicio> cq = sessao.getCriteriaBuilder().createQuery(Exercicio.class);
			cq.from(Exercicio.class);
			exercicios = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			addErro("ERRO", "ERRO ao listar exercicios");
		} finally {
			sessao.close();
		}
	}

	public void edita(ActionEvent evt) {
		exercicio = (Exercicio)evt.getComponent().getAttributes().get("exercicioEdita");
	}
	
	public void excluir(ActionEvent evt)  {
		exercicio = (Exercicio)evt.getComponent().getAttributes().get("exercicioExclui");
		exclui();
	}
	
	public void addErro(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	public Exercicio getExercicio() {
		return exercicio;
	}
	public void setExercicio(Exercicio exercicio) {
		this.exercicio = exercicio;
	}
	public List<Exercicio> getExercicios() {
		return exercicios;
	}
	public void setExercicios(List<Exercicio> exercicios) {
		this.exercicios = exercicios;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}