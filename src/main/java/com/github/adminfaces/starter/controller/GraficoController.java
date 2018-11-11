package com.github.adminfaces.starter.controller;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.github.adminfaces.starter.model.Grafico;
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
	private List<TafExercicio> tafexercicios;
	private List<Usuario> usuarios;
	
	private BarChartModel barModel;
	public String nomeAluno;
	
	@PostConstruct
	public void inicializa() {
		listarTodas();
		nomeAluno = " ";
		createBarModels();
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
	public List<Grafico> filtrarPorAluno(){
		List<Grafico> tafsdoaluno = new ArrayList<Grafico>();
		tafsdoaluno = grupandoDados();
		try {
			if(alunoselecionado != null) {
				tafsdoaluno.removeIf(s -> s.getAluno().getId() != alunoselecionado.getId());
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
	
	public List<Grafico> grupandoDados() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		
		String sql = "SELECT t.taf, a.usuario, SUM(a.pontuacao) as total FROM TafAluno a, TafExercicio t " + 
				"WHERE a.tafexercicio = t.id " + 
				"GROUP BY t.taf, a.usuario " +
				"ORDER BY t.taf, total DESC";
		
		Query query = sessao.createQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		List<Grafico> dados = new ArrayList<Grafico>();
		for(Object[]c:resultados) {
			Grafico graf = new Grafico();
			graf.setTaf((Taf) c[0]);
			graf.setAluno((Usuario) c[1]);
			graf.setTotal(Math.round((Long) c[2]));
			dados.add(graf);
		}
		int index = 0;
		boolean novaTaf = true;
		Taf tafAux = null;
		int max = 0;
		
		for(Grafico d : dados) {		
			if(d.getTaf() != tafAux)	{	  
				novaTaf = true;
				max = 0;
			}			  
			if(novaTaf) {
				tafAux = d.getTaf();
				max = d.getTotal();
				novaTaf = false;			  	
			}
			DecimalFormat df = new DecimalFormat("#.#");
			df.setRoundingMode(RoundingMode.CEILING);
			double percentual = Double.parseDouble(df.format(((d.getTotal()*100)/max)));
			d.setPorcentagem(percentual);
			dados.set(index, d);
			index++;
		}	

		for(Grafico d : dados) 
			System.out.println(d.getTaf().getNome()+" - "+d.getAluno().getNome()+" - "+d.getTotal()+" - "+d.getPorcentagem());

		return dados;
	}
	  
	public void atualizarGrafico() {
		System.out.println("Aluno escolhido: "+alunoselecionado.getNome());
		nomeAluno = alunoselecionado.getNome();
		createBarModels();	
	}
		
	private BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
        ChartSeries aluno = new ChartSeries();
        aluno.setLabel("Pontos em %"); 
	    
		if(alunoselecionado != null) {
			List<Grafico> dados = filtrarPorAluno();
	        for(Grafico d : dados) {
	        	aluno.set(d.getTaf().toString(), d.getPorcentagem());       
	        	System.out.println(d.getTaf().toString()+" - "+d.getPorcentagem());	       
	        }
		}else {
			aluno.set("VAZIO", 0);
		}
 
       model.addSeries(aluno);
       barModel = model;
       
       barModel.setTitle("Gráfico de Desempenho de "+nomeAluno);
       barModel.setLegendPosition("box zero");
       barModel.setAnimate(true);
        
       Axis xAxis = barModel.getAxis(AxisType.X);
       xAxis.setLabel("Data do TAF");
        
       Axis yAxis = barModel.getAxis(AxisType.Y);
       yAxis.setLabel("Pontuação %");
       yAxis.setMin(0);
       yAxis.setMax(100);   
       
       return model;
    }
     
    private void createBarModels() {
          initBarModel();
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