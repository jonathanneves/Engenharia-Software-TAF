package com.github.adminfaces.starter.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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
	
	private List<Taf> tafsfiltradas;
	public List<Taf> exerciciosfiltrados;
	
	@PostConstruct
	public void inicializa() {
		taf = new Taf(); 
		listarTodas();
		listarExercicios();	
		filtarTafs();
	}
	
	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
			try {		
				boolean primeiraVez = true;
				String nomeAUX = "";
				Date dataAUX = null;
				for(Exercicio e : exercicios) {	
					if(primeiraVez) {
						nomeAUX = taf.getNome();
						dataAUX = taf.getData();
						primeiraVez = false;
					}
					for(String m : e.getModalidades()) {
						if(m.equals("rm")) 
							taf.setModalidade("1RM");
						else if(m.equals("max")) 
							taf.setModalidade("MAX");
						else if(m.equals("vt")) 
							taf.setModalidade("VT");						
					t = sessao.beginTransaction();
					taf.setNome(nomeAUX);
					taf.setData(dataAUX);
					taf.setExercicio(e);
					sessao.merge(taf);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
					t.commit();
					taf = new Taf();
					}
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
	
	public void filtarTafs() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			tafsfiltradas = sessao.createQuery(cq).getResultList();
			Set<Date> datasVistas = new HashSet<>();
			tafsfiltradas.removeIf(p -> !datasVistas.add(p.getData()));
		} catch (Exception e) {
			addMessage("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
	}
	
	public List<Taf> filtrarExercicios(Taf tafAux) {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			exerciciosfiltrados = sessao.createQuery(cq).getResultList();
			exerciciosfiltrados.removeIf(s -> !s.getData().equals(tafAux.getData()));
		} catch (Exception e) {
			addMessage("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		return exerciciosfiltrados;
	}
	
	/*public List<Taf> filtrarExercicios(Taf taf) {
		System.out.println(taf.getNome() + " - " + taf.getData());
		for(Taf t : tafs) {
			if(t.getData().equals(taf.getData())){
				System.out.println(t.getExercicio().getId() + " - "  + t.getModalidade());
				getExerciciosfiltrados().add(t);										
			}
		}
		return exerciciosfiltrados;
	}	
	*/
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

	public List<Taf> getTafsfiltradas() {
		return tafsfiltradas;
	}

	public void setTafsfiltradas(List<Taf> tafsfiltradas) {
		this.tafsfiltradas = tafsfiltradas;
	}

	public List<Taf> getExerciciosfiltrados() {
		return exerciciosfiltrados;
	}

	public void setExerciciosfiltrados(List<Taf> exerciciosfiltrados) {
		this.exerciciosfiltrados = exerciciosfiltrados;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}