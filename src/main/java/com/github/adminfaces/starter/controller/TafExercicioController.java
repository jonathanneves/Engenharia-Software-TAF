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
	
	private List<String> modalidades;
	
	@PostConstruct
	public void inicializa() {
		tafexercicio = new TafExercicio(); 
		listarTodas();
		listarTafs();
		listarExercicios();		
	}
	
	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		Taf Taux = tafexercicio.getTaf();
			try {				
				for(Exercicio e : exercicios) {	
					for(String m : modalidades) {
						System.out.println("Modalidade: "+ m + " Exercicio: "+e.getNome());
						if(m.equals("rm")) 
							tafexercicio.setModalidade1RM("S");
						else
							tafexercicio.setModalidade1RM("N");
						if(m.equals("max")) 
							tafexercicio.setModalidadeMAX("S");
						else
							tafexercicio.setModalidadeMAX("N");
						if(m.equals("vt")) 
							tafexercicio.setModalidadeVT("S");
						else
							tafexercicio.setModalidadeVT("N");
						
					}
					t = sessao.beginTransaction();
					tafexercicio.setExercicio(e);
					tafexercicio.setTaf(Taux);
					sessao.merge(tafexercicio);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
					t.commit();
					tafexercicio = new TafExercicio();
				}
				addMessage("TAF", "Cadastrado con sucesso");
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
			listarTafs();
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
			addMessage("ERRO", "Erro ao listar tafs");
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
		tafexercicio = (TafExercicio)evt.getComponent().getAttributes().get("tafEdita");
	}
	
	public void excluir(ActionEvent evt) {
		tafexercicio = (TafExercicio)evt.getComponent().getAttributes().get("tafExclui");
		exclui();
	}
	
	public void selecionaTaf(ActionEvent evt) {
		tafexercicio.setTaf((Taf) (evt.getComponent().getAttributes().get("tafSelecionado")));
		System.out.println("Taf: "+tafexercicio.getTaf().getNome());
	}

	public List<String> getModalidades() {
		return modalidades;
	}

	public void setModalidades(List<String> modalidades) {
		this.modalidades = modalidades;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
