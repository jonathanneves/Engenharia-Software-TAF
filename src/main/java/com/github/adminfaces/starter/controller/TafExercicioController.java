package com.github.adminfaces.starter.controller;

import java.io.Serializable;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.github.adminfaces.starter.model.TafExercicio;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean
@ViewScoped
public class TafExercicioController implements Serializable {

private static final long serialVersionUID = 1L;
	
	private TafExercicio tafexercicio;
	private List<TafExercicio> tafexercicios;
	
	private List<Exercicio> exercicios;
	private List<Taf> tafs;
	
	public TafController tc = new TafController();
	
	//private List<TafExercicio> tafsfiltradas;
	public List<TafExercicio> exerciciosfiltrados;
	
	@PostConstruct
	public void inicializa() {
		tafexercicio = new TafExercicio(); 
		listarTodas();
		listarExercicios();	
		listarTafs();
	//	filtarTafs();
	}
	
	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
			try {	
				Taf tx = tc.ultimoTAF();
				if (exercicios.isEmpty())
					throw new NullPointerException();
				for(Exercicio e : exercicios) {	
					if(e.getModalidades().isEmpty())
						throw new NullPointerException();
					for(String m : e.getModalidades()) {
						if(m.equals("rm")) 
							tafexercicio.setModalidade("1RM");
						else if(m.equals("max")) 
							tafexercicio.setModalidade("MAX");
						else if(m.equals("vt")) 
							tafexercicio.setModalidade("VT");		
						System.out.println("salvou");
						t = sessao.beginTransaction();
						tafexercicio.setExercicio(e);
						tafexercicio.setTaf(tx);
						sessao.merge(tafexercicio);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
						t.commit();
						tc.setDesativado(true);
						tafexercicio = new TafExercicio();
					}
				}
				addMessage("TAF", "Cadastrado com sucesso");		
			} catch(ArrayIndexOutOfBoundsException exception) {
				addErro("ERRO", "Não foi possível cadastrar");
			} catch(NullPointerException nullExc) {
				addErro("ERRO", "Selecione pelo menos um exercicio e uma modalidade");
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
			sessao.delete(tafexercicio);		
			t.commit();
			tafexercicio = new TafExercicio();
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
			CriteriaQuery<TafExercicio> cq = sessao.getCriteriaBuilder().createQuery(TafExercicio.class);
			cq.from(TafExercicio.class);
			tafexercicios = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			addErro("ERRO", "Erro ao listar tafs exercicios");
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
			addErro("ERRO", "Erro ao listar exercicios");
		} finally {
			sessao.close();
		}
	}
	
	public void listarTafs() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			tafs = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			addErro("ERRO", "Erro ao listar tafs de tafsexericios");
		} finally {
			sessao.close();
		}
	}
	
	public List<TafExercicio> filtrarExercicios(Taf tafAux) {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			if(tafAux != null) {
				System.out.println("OLHA: "+tafAux.getNome());
				CriteriaQuery<TafExercicio> cq = sessao.getCriteriaBuilder().createQuery(TafExercicio.class);
				cq.from(TafExercicio.class);
				exerciciosfiltrados = sessao.createQuery(cq).getResultList();
				exerciciosfiltrados.removeIf(s -> s.getTaf().getId() != tafAux.getId());
			} else
				System.out.println("Sem Taf selecionado");	
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		return exerciciosfiltrados;
	}

	
	public void editar(ActionEvent evt) {
		tafexercicio = (TafExercicio)evt.getComponent().getAttributes().get("tafEdita");
	}
	
	public TafExercicio getTafexercicio() {
		return tafexercicio;
	}

	public void setTafexercicio(TafExercicio tafexercicio) {
		this.tafexercicio = tafexercicio;
	}

	public List<TafExercicio> getTafexercicios() {
		return tafexercicios;
	}

	public void setTafexercicios(List<TafExercicio> tafexercicios) {
		this.tafexercicios = tafexercicios;
	}

	public List<Exercicio> getExercicios() {
		return exercicios;
	}

	public void setExercicios(List<Exercicio> exercicios) {
		this.exercicios = exercicios;
	}

	public List<Taf> getTafs() {
		return tafs;
	}

	public void setTafs(List<Taf> tafs) {
		this.tafs = tafs;
	}

	public List<TafExercicio> getExerciciosfiltrados() {
		return exerciciosfiltrados;
	}

	public void setExerciciosfiltrados(List<TafExercicio> exerciciosfiltrados) {
		this.exerciciosfiltrados = exerciciosfiltrados;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void addErro(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}