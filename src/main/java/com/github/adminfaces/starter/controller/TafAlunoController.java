package com.github.adminfaces.starter.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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
	private TafExercicio exercicioSel;
	
	private List<TafAluno> tafalunos;
	
	private List<TafExercicio> tafexercicios;
	private List<Usuario> usuarios;
	private List<TafAluno> exerciciosfiltrados;

	public List<String> listapontos = new ArrayList<String>();
	private String pontos;
	private List<Integer> somaTotais;

	UsuarioController tu = new UsuarioController();
	private List<Usuario> alunosfiltrados;
	
	@PostConstruct
	public void inicializa() {
		tafaluno = new TafAluno(); 
		//listarTodas();
		listarTafExercicios();
	}
	
	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
			try {
				alunosfiltrados = tu.filtrarAlunos();
				tafselecionado.setRealizado("S");
				System.out.println(listapontos.get(0));
				for(Usuario u : alunosfiltrados) {
					if(listapontos.get(0) != null) {
						t = sessao.beginTransaction();
						tafaluno.setPontuacao(Integer.parseInt(listapontos.get(0)));
						listapontos.remove(0);
						tafaluno.setUsuario(u);
						tafaluno.setTafexercicio(exercicioSel);
						sessao.merge(tafaluno);	
						sessao.merge(tafselecionado);
						t.commit();
						tafaluno = new TafAluno();
					}
				}
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
			addErro("ERRO", "Erro ao listar alunos  participantes");
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
	
	public void editar(ActionEvent evt) {
		tafaluno = (TafAluno)evt.getComponent().getAttributes().get("tafalunoEdita");
		System.out.println("editado?"+tafaluno.getId());
	}
	
/*	public void somaTotal(TafExercicio tafSel) {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		CriteriaBuilder builder = sessao.getCriteriaBuilder();
		try {
			CriteriaQuery<Object[]> cq = builder.createQuery(Object[].class);
			Root<TafAluno> root = cq.from(TafAluno.class);
			cq.multiselect(root.get("usuario") , builder.sum(root.get("pontuacao")));
			cq.groupBy(root.get("usuario"));
			Query<Object[]> query = sessao.createQuery(cq);
			List<Object[]> list = query.getResultList();
			for (Object[] objects : list) {
				Usuario usuario = (Usuario) objects[0];
				long sum = (Long) objects[1];
				System.out.println("Usuario: " + usuario.getNome() + "	Pontuação: " + sum);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
	
	public List<TafAluno> somaTotal2(Taf tafAux) {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			if(tafAux != null) {
				CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
				cq.from(TafAluno.class);
				exerciciosfiltrados = sessao.createQuery(cq).getResultList();
				exerciciosfiltrados.removeIf(s -> s.getTafexercicio().getTaf().getId() != tafAux.getId());
				Map<Usuario, Integer> result = exerciciosfiltrados.stream().collect(Collectors.groupingBy(TafAluno::getUsuario,  Collectors.summingInt(TafAluno::getPontuacao)));
				System.out.println(result);
			} else
				System.out.println("Sem Taf selecionado");	
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		return exerciciosfiltrados;
	}
	
	public void manterTaf() {
		somaTotal2(getTafselecionado());
		System.out.println("Nome: "+getTafselecionado().getNome() +"		Data: "+ getTafselecionado().getData());
	}
	
	public void manterPontos() {
		listapontos.add(getPontos());
	}
		
	

	public Taf getTafselecionado() {
		return tafselecionado;
	}

	public void setTafselecionado(Taf tafselecionado) {
		this.tafselecionado = tafselecionado;
	}

	public void selecionarExercicio(ActionEvent evt){
		exercicioSel = (TafExercicio)evt.getComponent().getAttributes().get("exercicioSeleciona");
		System.out.println("Exercicio: "+ exercicioSel.getExercicio().getNome());
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
	
	public TafExercicio getExercicioSel() {
		return exercicioSel;
	}

	public void setExercicioSel(TafExercicio exercicioSel) {
		this.exercicioSel = exercicioSel;
	}

	public String getPontos() {
		return pontos;
	}
	
	public List<Integer> getSomaTotais() {
		return somaTotais;
	}

	public void setSomaTotais(List<Integer> somaTotais) {
		this.somaTotais = somaTotais;
	}

	public void setPontos(String pontos) {
		this.pontos = pontos;
	}

	public List<Usuario> getAlunosfiltrados() {
		return alunosfiltrados;
	}

	public void setAlunosfiltrados(List<Usuario> alunosfiltrados) {
		this.alunosfiltrados = alunosfiltrados;
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