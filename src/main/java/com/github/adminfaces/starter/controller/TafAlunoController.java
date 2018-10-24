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
import com.github.adminfaces.starter.model.TafAluno;
import com.github.adminfaces.starter.model.TafExercicio;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean
@ViewScoped
public class TafAlunoController implements Serializable {

private static final long serialVersionUID = 1L;

	private TafAluno tafaluno;
	private Taf tafselecionado;
	
	private List<TafAluno> tafalunos;
	
	private List<TafExercicio> tafexercicios;
	private List<Usuario> usuarios;
	
	private List<Exercicio> exercicios;
	
	@PostConstruct
	public void inicializa() {
		tafaluno = new TafAluno(); 
		listarTodas();
		listarTafExercicios();
	}
	
	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
			try{
				for(TafExercicio te : tafexercicios) {
					if(te.getTaf() == tafselecionado) {
						tafaluno.setTafexercicio(te);
						tafaluno.setPontuacao(te.getPontuacao());
					}
				}
				t = sessao.beginTransaction();
				sessao.merge(tafaluno);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
				t.commit();
				tafaluno = new TafAluno();
				addMessage("Pontuação", "Pontos registrado com sucesso");		
			} catch(ArrayIndexOutOfBoundsException exception) {
				addErro("ERRO", "Não foi possível registrar");
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
			sessao.delete(tafaluno);		
			t.commit();
			tafaluno = new TafAluno();
			addMessage("Exclusão", "Pontuação do aluno excluído com sucesso");
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
			CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
			cq.from(TafAluno.class);
			tafalunos = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			addErro("ERRO", "Erro ao listar alunos");
		} finally {
			sessao.close();
		}
	}
	
	public void listarTafExercicios() {
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
	public void manterTaf() {
		System.out.println("Nome: "+getTafselecionado().getNome() +"		Data: "+ getTafselecionado().getData());
	}

	public Taf getTafselecionado() {
		return tafselecionado;
	}

	public void setTafselecionado(Taf tafselecionado) {
		this.tafselecionado = tafselecionado;
	}

	public void selecionarAluno(ActionEvent evt){
		tafaluno.setUsuario((Usuario)evt.getComponent().getAttributes().get("alunoSeleciona"));
		System.out.println(tafaluno.getUsuario().getNome());
	}
		
	public List<TafExercicio> getTafexercicios() {
		return tafexercicios;
	}

	public void setTafexercicios(List<TafExercicio> tafexercicios) {
		this.tafexercicios = tafexercicios;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public TafAluno getTafaluno() {
		return tafaluno;
	}

	public void setTafaluno(TafAluno tafaluno) {
		this.tafaluno = tafaluno;
	}

	public List<TafAluno> getTafalunos() {
		return tafalunos;
	}

	public void setTafalunos(List<TafAluno> tafalunos) {
		this.tafalunos = tafalunos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public List<Exercicio> getExercicios() {
		return exercicios;
	}

	public void setExercicios(List<Exercicio> exercicios) {
		this.exercicios = exercicios;
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