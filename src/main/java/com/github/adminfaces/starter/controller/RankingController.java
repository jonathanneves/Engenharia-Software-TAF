package com.github.adminfaces.starter.controller;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;

import com.github.adminfaces.starter.model.Grafico;
import com.github.adminfaces.starter.model.Ranking;
import com.github.adminfaces.starter.model.Taf;
import com.github.adminfaces.starter.model.TafAluno;
import com.github.adminfaces.starter.model.TafExercicio;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean
@ViewScoped
public class RankingController implements Serializable {

private static final long serialVersionUID = 1L;

	private TafAluno tafaluno;
 
	private Taf tafselecionado;
	private Ranking rankingSel;
	private TafExercicio exercicioSel;
	private String filtroSel;
	
	private List<Ranking> rankinglista;
	private List<TafAluno> tafalunos;
	private List<TafExercicio> tafexercicios;
	private List<Usuario> usuarios;

	private boolean desativadoPont = true;			//desativar Botao antes de selecionar 3 combo
	private boolean desativadoExer = true;

	@PostConstruct
	public void inicializa() {
		List<Taf> tafs = listarTafsRealizadas();
		if(!tafs.isEmpty())
			tafselecionado = listarTafsRealizadas().get(listarTafsRealizadas().size() -1);
		else
			tafselecionado = null;
		listarTafExercicios();
		rankinglista = listarTotalPontos();
	}
	
	//Listar apenas os TAFExercicio da taf selecionada
	public List<TafExercicio> listarTafExercicios() {
		List<TafExercicio> listaexercicios = new ArrayList<TafExercicio>();
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			if(tafselecionado != null) {
				CriteriaQuery<TafExercicio> cq = sessao.getCriteriaBuilder().createQuery(TafExercicio.class);
				cq.from(TafExercicio.class);
				listaexercicios = sessao.createQuery(cq).getResultList();
				listaexercicios.removeIf(s -> s.getTaf().getId() != tafselecionado.getId());
				tafexercicios = listaexercicios;	
			}else
				System.out.println("TAF SELECIONADO É NULO");
		} catch (Exception e) {
			addErro("ERRO", "Erro ao listar tafs exercicios");
		} finally {
			sessao.close();
		}
		return tafexercicios;
	}
	
	//Listar Taf realizadas	
	public List<Taf> listarTafsRealizadas() {
		List<Taf> tafsrealizadas = new ArrayList<Taf>();
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			tafsrealizadas = sessao.createQuery(cq).getResultList();
			tafsrealizadas.removeIf(s -> s.getRealizado().equals("N"));
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		tafsrealizadas.sort(Comparator.comparing(Taf::getData));
		return tafsrealizadas;
	}
	
	//Listar todos os alunos que participaram do TAF
	public List<TafAluno> filtrarAlunosTaf(){
		List<TafAluno> alunosparticipantes = new ArrayList<TafAluno>();
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			if(tafselecionado != null) {
				CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
				cq.from(TafAluno.class);
				alunosparticipantes = sessao.createQuery(cq).getResultList();
				alunosparticipantes.removeIf(s -> s.getTafexercicio().getTaf().getId() != tafselecionado.getId());
			} else
				System.out.println("Nenhuma taf selecionado");
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		return alunosparticipantes;
	}
	 
	//Listar apenas os TAFExercicio da taf selecionada
	public List<TafAluno> listarPontosExercicio() {
		List<TafAluno> listafiltrada = new ArrayList<TafAluno>();
		try {
			listafiltrada = filtrarAlunosTaf();
			listafiltrada.removeIf(s -> !s.getUsuario().getNome().equals(rankingSel.getTexto()));
		} catch (Exception e) {
			addErro("ERRO", "Erro ao listar pontos por exercício");
		} finally {
		}
		return listafiltrada;
	}
	
	//Filtrar todos os alunos que realizaram o exercicio
	public List<TafAluno> filtrarPorExercicio(){
		List<TafAluno> alunosporexercicio = new ArrayList<TafAluno>();
		try {
			if(tafselecionado != null) {
				alunosporexercicio = filtrarAlunosTaf();
				alunosporexercicio.removeIf(s -> s.getTafexercicio().getId() != exercicioSel.getId());
				alunosporexercicio.sort(Comparator.comparing(TafAluno::getPontuacao).reversed());
			} else
				System.out.println("Nenhuma taf selecionado");
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		}
		return alunosporexercicio;
	}
	 
	//Filtrar alunos por fraco forte e médio
	public List<TafAluno> filtrarAlunosClassificados(){
		List<TafAluno> alunosclassificados = new ArrayList<TafAluno>();
		try {
			alunosclassificados = filtrarAlunosTaf();
			alunosclassificados.removeIf(s -> s.getTafexercicio().getId() != exercicioSel.getId());
			if(filtroSel.equals("Fraco")) {
				if(exercicioSel.getModalidade().equals("1RM"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getUmrmFraco() < s.getPontuacao());
				if(exercicioSel.getModalidade().equals("MAX"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getRmFraco() < s.getPontuacao());
				if(exercicioSel.getModalidade().equals("VT"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getVtFraco() < s.getPontuacao());
			}else if(filtroSel.equals("Forte")) {
				if(exercicioSel.getModalidade().equals("1RM")) 
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getUmrmForte() > s.getPontuacao());
				if(exercicioSel.getModalidade().equals("MAX"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getRmForte() > s.getPontuacao());
				if(exercicioSel.getModalidade().equals("VT"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getVtForte() > s.getPontuacao());
			}else if(filtroSel.equals("Médio")) {
				if(exercicioSel.getModalidade().equals("1RM")) {
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getUmrmFraco() >= s.getPontuacao());
					alunosclassificados.removeIf(s-> s.getTafexercicio().getExercicio().getUmrmForte() <= s.getPontuacao());
				}
				if(exercicioSel.getModalidade().equals("MAX")) {
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getRmFraco() >= s.getPontuacao());
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getRmForte() <= s.getPontuacao());
				}
				if(exercicioSel.getModalidade().equals("VT")) {
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getVtFraco() >= s.getPontuacao());
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getVtForte() <= s.getPontuacao());
				}
			}
			alunosclassificados.sort(Comparator.comparing(TafAluno::getPontuacao).reversed());
		} catch (Exception e) {
			addErro("ERRO", "Erro ao classficar alunos");
		} 
		return alunosclassificados;
	}
	
	public List<Ranking> listarTotalPontos() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();

		String sql = "SELECT t.taf, a.usuario, SUM(a.pontuacao) as total FROM TafAluno a, TafExercicio t " + 
				"WHERE a.tafexercicio = t.id " +
				"AND t.taf.id = "+tafselecionado.getId() +
				"GROUP BY t.taf, a.usuario " +
				"ORDER BY t.taf, total DESC";
		
		Query query = sessao.createQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		List<Ranking> dados = new ArrayList<Ranking>();
		int pos = 1;
		for(Object[]c:resultados) {
			Ranking rank = new Ranking();
			rank.setPosicao(pos+"º");
			Usuario user = new Usuario();
			user = (Usuario) c[1];
			rank.setTexto(user.getNome());
			rank.setTotalpontos(Math.round((Long) c[2]));
			dados.add(rank);
			pos++;
		}		
		
		for(Ranking r : dados)
			System.out.println(r.getPosicao()+"-"+r.getTexto()+"-"+r.getTotalpontos());
		
		return dados;
	}
	  
	 /* public List<Ranking> ranqueandoAluno() {

		  Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
			
			String sql = "SELECT t.taf, a.usuario, SUM(a.pontuacao) as total FROM TafAluno a, TafExercicio t " + 
					"WHERE a.tafexercicio = t.id " +
					"AND t.taf.id = "+tafselecionado.getId() +
					"GROUP BY t.taf, a.usuario " +
					"ORDER BY t.taf, total DESC";
			
			Query query = sessao.createQuery(sql);
			@SuppressWarnings("unchecked")
			List<Object[]> resultados = query.getResultList();
			
			List<Ranking> dados = new ArrayList<Ranking>();
			int pos = 1;
			for(Object[]c:resultados) {
				Ranking rank = new Ranking();
				rank.setPosicao(pos+"º");
				Usuario user = new Usuario();
				user = (Usuario) c[1];
				rank.setTexto(user.getNome());
				rank.setTotalpontos(Math.round((Long) c[2]));
				dados.add(rank);
				pos++;
			}		
			for(Ranking r : dados)
				System.out.println(r.getPosicao()+"-"+r.getTexto()+"-"+r.getTotalpontos());
			
			return dados;
			
		  List<Ranking>corpo = new ArrayList<Ranking>();
		  String texto="";
		  int saida=0;
		  boolean novoAluno = true;
		  int total = 0; 
		  List<TafAluno> alunos = filtrarAlunosTaf();
		  Collections.sort(alunos, (o1, o2) -> o1.getUsuario().getId().compareTo(o2.getUsuario().getId()));
		  
		  for(TafAluno te : alunos) {
			  if(novoAluno) {
				  texto = te.getUsuario().getNome()+" |   ";
			  	  novoAluno = false;
			  }
			  texto += "   "+te.getPontuacao()+"   |   ";
			  total += te.getPontuacao();
			  saida++;
			  if(tafexercicios.size() == saida) {
				  Ranking rank = new Ranking();
				  rank.setTexto(texto);
				  rank.setTotalpontos(total);
				  corpo.add(rank);
				  novoAluno = true;
				  total=0; saida=0;
			  }
		  }
		  corpo.sort(Comparator.comparing(Ranking::getTotalpontos).reversed());
		  int pos = 0;
		  for(Ranking m : corpo) {
			  pos++;
			  m.setPosicao(pos+"º "); 
		  }
		  return corpo;
	  }
	  
	public String criarCabecalho() {
	   
		String cab = "          Aluno  |";	       
	   for(TafExercicio te : tafexercicios) {
	    		cab +=  " "+te.getExercicio().getNome()+"-"+te.getModalidade()+" | ";
	    }
	   cab += " Total";
	   return cab;
	} */
	    
	public void manterTaf() {
		System.out.println("Nome: "+getTafselecionado().getNome() +"  Data: "+ getTafselecionado().getData());
		listarTafExercicios();
		rankinglista = listarTotalPontos();
	}

	public void manterExercicio() {
		System.out.println("Nome: "+getExercicioSel().getExercicio().getNome() +"  Modalidade: "+ getExercicioSel().getModalidade());
		setDesativadoExer(false);
	}
	
	public void manterFiltro() {
		System.out.println("FILTRO: "+filtroSel);
		setDesativadoPont(false);
	}
	
	public void selecionarRanking(ActionEvent evt){
		rankingSel = (Ranking)evt.getComponent().getAttributes().get("rankingSeleciona");
	}
	
	public List<Ranking> getRankinglista() {
		return rankinglista;
	}

	public void setRankinglista(List<Ranking> rankinglista) {
		this.rankinglista = rankinglista;
	}

	public Ranking getRankingSel() {
		return rankingSel;
	}

	public void setRankingSel(Ranking rankingSel) {
		this.rankingSel = rankingSel;
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

	public void addErro(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String getFiltroSel() {
		return filtroSel;
	}

	public void setFiltroSel(String filtroSel) {
		this.filtroSel = filtroSel;
	}

	public boolean isDesativadoPont() {
		return desativadoPont;
	}

	public void setDesativadoPont(boolean desativadoPont) {
		this.desativadoPont = desativadoPont;
	}

	public boolean isDesativadoExer() {
		return desativadoExer;
	}

	public void setDesativadoExer(boolean desativadoExer) {
		this.desativadoExer = desativadoExer;
	}

	public List<TafExercicio> getTafexercicios() {
		return tafexercicios;
	}

	public void setTafexercicios(List<TafExercicio> tafexercicios) {
		this.tafexercicios = tafexercicios;
	}
}