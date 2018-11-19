package com.github.adminfaces.starter.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

import com.github.adminfaces.starter.model.Taf;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean //Classe controladora
@ViewScoped
public class TafController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Taf taf;
	private List<Taf> tafs;
    
	private boolean desativado = false;
	
	@PostConstruct
	public void init() {
		taf = new Taf();
		listarTodas();
		setDesativado(false);
	}
	

	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.merge(taf);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
			t.commit();
			addMessage("Cadastro", "Selecione os exercícios desejados e suas modalidades");
			setDesativado(true);
			listarTodas();
			taf = new Taf();
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
			sessao.delete(taf);			
			t.commit();
			taf = new Taf();
			addMessage("Exclusão", "Taf excluído com sucesso");
			listarTodas();
		} catch (Exception e) {
			if(t!=null)
				t.rollback();
			throw(e);
		}finally{
			sessao.close();
		}
	}
	
	public Taf ultimoTAF() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			tafs = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			addErro("ERRO", "ERRO ao listar tafs");
		} finally {
			sessao.close();
		}
		Taf tf = tafs.get(tafs.size()-1);
		return tf;
	}
	
	public void listarTodas() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
		
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			tafs = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			addErro("ERRO", "ERRO ao listar tafs");
		} finally {
			sessao.close();
		}
	}

	public List<Taf> tafsNaoRealizadas() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		List<Taf> tafsNaoRealizadas = new ArrayList<Taf>();
		try {
		
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			tafsNaoRealizadas = sessao.createQuery(cq).getResultList();
			tafsNaoRealizadas.removeIf(s -> s.getRealizado().equals("S"));
		} catch (Exception e) {
			addErro("ERRO", "ERRO ao listar tafs");
		} finally {
			sessao.close();
		}
		return tafsNaoRealizadas;
	}

	public void foiRealizado(Taf tafx) {
		for(Taf tf : tafs) {
			if(tafx == tf)
				taf.setRealizado("S");
		}
	}
	public void edita(ActionEvent evt) {
		taf = (Taf)evt.getComponent().getAttributes().get("tafEdita");
	}
	
	public void excluir(ActionEvent evt)  {
		taf = (Taf)evt.getComponent().getAttributes().get("tafExclui");
		exclui();
	}
	
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void addErro(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public boolean isDesativado() {
		return desativado;
	}

	public void setDesativado(boolean desativado) {
		this.desativado = desativado;
	}

	public Taf getTaf() {
		return taf;
	}

	public void setTaf(Taf taf) {
		this.taf = taf;
	}


	public List<Taf> getTafs() {
		return tafs;
	}


	public void setTafs(List<Taf> tafs) {
		this.tafs = tafs;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}