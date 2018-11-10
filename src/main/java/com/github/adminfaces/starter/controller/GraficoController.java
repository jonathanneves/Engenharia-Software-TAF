package com.github.adminfaces.starter.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;

import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.github.adminfaces.starter.model.Grafico;
import com.github.adminfaces.starter.model.Ranking;
import com.github.adminfaces.starter.model.Taf;
import com.github.adminfaces.starter.model.TafAluno;
import com.github.adminfaces.starter.model.TafExercicio;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean
@ViewScoped
public class GraficoController implements Serializable {

private static final long serialVersionUID = 1L;


	private TafAluno tafaluno;
	private Usuario alunoselecionado;
	
	private List<TafAluno> tafalunos;
	String nomeAluno = "";
	private List<TafExercicio> tafexercicios;
	private List<Usuario> usuarios;
	
	private BarChartModel barModel;
	
	@PostConstruct
	public void inicializa() {
		listarTodas();
		createBarModels();	
		//listarTotalPontos()  DESBLOQUEIA ISSO E VEJA A MAGICA ACONTECER CORRIJA OS ERROS
	}

	public void listarTodas() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
			cq.from(TafAluno.class);
			tafalunos = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			addErro("ERRO", "Erro ao listar alunos participantes");
		} finally {
			sessao.close();
		}
	}
	
	//filtrar determinado aluno
	public List<TafAluno> filtrarAlunosTaf(){
		List<TafAluno> tafsdoaluno = new ArrayList<TafAluno>();
		try {
			if(alunoselecionado != null) {
				tafsdoaluno = tafalunos;
				tafsdoaluno.removeIf(s -> s.getUsuario().getNome() != alunoselecionado.getNome());
			} else
				System.out.println("Nenhum aluno selecionado");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return tafsdoaluno;
	}

	//Listar apenas os TAFExercicio da taf selecionada
	public List<TafExercicio> listarTafExercicios(Taf tafAtual) {
		List<TafExercicio> listaexercicios = new ArrayList<TafExercicio>();
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			if(tafAtual != null) {
				CriteriaQuery<TafExercicio> cq = sessao.getCriteriaBuilder().createQuery(TafExercicio.class);
				cq.from(TafExercicio.class);
				listaexercicios = sessao.createQuery(cq).getResultList();
				listaexercicios.removeIf(s -> s.getTaf().getId() != tafAtual.getId());
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
	
	//Listando o ponto total de  todos alunos
	public void listarTotalPontos() {
		  List<Grafico>listaTotal = new ArrayList<Grafico>();
		  int total = 0; 
		  
		  Taf taf = null;
		  Usuario aluno = null;

		  boolean novoAluno = true;
		  boolean novoTaf = true;
		  List<TafAluno> alunos = tafalunos;
		  Collections.sort(alunos, (o1, o2) -> o1.getUsuario().getId().compareTo(o2.getUsuario().getId()));
		  
		  for(TafAluno te : alunos) {
			  
			  if(taf != te.getTafexercicio().getTaf() && taf != null)
				  novoTaf = true;
			  
			  if(aluno != te.getUsuario() && aluno != null) {
				  System.out.println("Aluno: "+aluno.getNome()+" TAF: "+taf.getNome()+" Total: "+total);
				  Grafico graf = new Grafico(te.getTafexercicio().getTaf(), aluno, total);
				  listaTotal.add(graf);
				  novoAluno = true;
				  total=0;
			  }
			  
			  if(novoAluno) {
				  aluno = te.getUsuario();
				  System.out.println("Aluno atual: "+aluno.getNome());
			  	  novoAluno = false;
			  }
			  
			  if(novoTaf) {
				  taf = te.getTafexercicio().getTaf();
				  System.out.println("Taf atual: "+taf.getNome()+" - "+taf.getData());
				  novoTaf = false;
			  }
			  total += te.getPontuacao();

			/*  if(listarTafExercicios(taf).size() == saida) {
				  Grafico rank = new Grafico(te.getTafexercicio().getTaf(), aluno, total);
				  listaTotal.add(rank);
				  novoAluno = true;
				  total=0; saida=0;
			  }*/
		  }
		  listaTotal.sort(Comparator.comparing(Grafico::getTotal).reversed());
		  
		  boolean novaEntrada = true;
		  int max=0;
		  Taf tafAux=null;
		  int index=0; 
		  
		  for(Grafico g : listaTotal) {
			  index++;
			  if(g.getTaf() != tafAux)
				  
				  novaEntrada = true;
			  
			  if(novaEntrada) {
				tafAux = g.getTaf();
				max = g.getTotal();
			  	novaEntrada = false;			  	
			  }
			  double percentual = ((g.getTotal()*100)/max);
			  System.out.println("Percentual "+percentual);
			  g.setPorcentagem(percentual);
			  listaTotal.set(index, g);
		
		  }
		  
		  System.out.println("================");
		  for(Grafico x : listaTotal)
			  System.out.println(x.getTaf().getNome()+" - "+x.getAluno().getNome()+" - "+x.getTotal()+" - "+x.getPorcentagem());
		  //return listaTotal;
	  }
	  
	public void manterAluno() {
		System.out.println("Aluno escolhido: "+alunoselecionado.getNome());
		nomeAluno = alunoselecionado.getNome();
		createBarModels();	
	}
		
	private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries aluno = new ChartSeries();
        aluno.setLabel("Pontos em %");
        aluno.set("2004", 60);
        aluno.set("2005", 90);
        aluno.set("2006", 44);
        aluno.set("2007", 80);
        aluno.set("2008", 25);
 
        model.addSeries(aluno);
           
        return model;
    }
     
    private void createBarModels() {
           createBarModel();
    }
     
    private void createBarModel() {
        barModel = initBarModel();

        barModel.setTitle("Gráfico de Desempenho do Aluno "+nomeAluno);
        barModel.setLegendPosition("box zero");
        barModel.setAnimate(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Data do TAF");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Pontuação %");
        yAxis.setMin(0);
        yAxis.setMax(100);         
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
	
	public BarChartModel getBarModel() {
		return barModel;
	}

	public void setBarModel(BarChartModel barModel) {
		this.barModel = barModel;
	}

	public Usuario getAlunoselecionado() {
		return alunoselecionado;
	}

	public void setAlunoselecionado(Usuario alunoselecionado) {
		this.alunoselecionado = alunoselecionado;
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