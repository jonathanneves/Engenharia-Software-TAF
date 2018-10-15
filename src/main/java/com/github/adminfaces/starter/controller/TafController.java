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
import com.github.adminfaces.starter.model.Taf;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean
@ViewScoped
public class TafController implements Serializable {

private static final long serialVersionUID = 1L;
	
	private Taf taf;
	private List<Taf> tafs;
	
	private List<Exercicio> exercicios;
	
	@PostConstruct
	public void inicializa() {
		taf = new Taf(); 
		listarTodas();
		listarExercicios();		
	}
	
	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
			try {		
				for(Exercicio e : exercicios) {	
					taf.setModalidade1RM("N");
					taf.setModalidadeMAX("N");
					taf.setModalidadeVT("N");
						for(String m : e.getModalidades()) {
							System.out.println(m);
							if(m.equals("rm")) 
								taf.setModalidade1RM("S");
							if(m.equals("max")) 
								taf.setModalidadeMAX("S");
							if(m.equals("vt")) 
								taf.setModalidadeVT("S");
						}
					t = sessao.beginTransaction();
					taf.setExercicio(e);
					sessao.merge(taf);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
					t.commit();
					taf = new Taf();
				}
				addMessage("TAF", "Cadastrado com sucesso");				
			} catch (Exception ex) {
				if(t!=null)
					t.rollback();
				throw(ex);
			}finally{
				sessao.close();
			}
		}
	
	public void exclui () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.delete(taf);		
			t.commit();
			taf = new Taf();
			addMessage("Exclusão", "TAF excluído com sucesso");
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
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			tafs = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			addMessage("ERRO", "Erro ao listar tafs");
		} finally {
			sessao.close();
		}
	}
	
	public void listarExercicios() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<Exercicio> cq = sessao.getCriteriaBuilder().createQuery(Exercicio.class);
			cq.from(Exercicio.class);
			exercicios = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			addMessage("ERRO", "Erro ao listar exercicios");
		} finally {
			sessao.close();
		}
	}
			
	public void editar(ActionEvent evt) {
		taf = (Taf)evt.getComponent().getAttributes().get("tafEdita");
	}
	
	public void excluir(ActionEvent evt) {
		taf = (Taf)evt.getComponent().getAttributes().get("tafExclui");
		exclui();
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

	public List<Exercicio> getExercicios() {
		return exercicios;
	}

	public void setExercicios(List<Exercicio> exercicios) {
		this.exercicios = exercicios;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}