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
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.primefaces.event.ItemSelectEvent;
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

	public Session sessao;
	
	private TafAluno tafaluno;
	private Usuario alunoselecionado;
	
	private List<TafAluno> tafalunos;
	private List<TafExercicio> tafexercicios;
	private List<Usuario> usuarios;	
	private List<TafAluno> resultados; //Filtrar cada exercicio na taf;
	
	private BarChartModel barModel;
	public String nomeAluno;
	Taf tafselecionado;
	
	@PostConstruct
	public void inicializa() {
		sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		listarTodas();
		nomeAluno = " ";
		createBarModels();
	}

	//LISTAR TODAS AS TAFALUNOS
	public void listarTodas() {
		String sql = "SELECT ta FROM TafAluno ta";
		TypedQuery<TafAluno> query = sessao.createQuery(sql, TafAluno.class);
		tafalunos = query.getResultList();
	}
	
	//filtrar determinado aluno
	public List<TafAluno> filtrarPorTaf(){
		if(tafselecionado != null && alunoselecionado != null) {
			String sql = "SELECT ta FROM TafAluno ta WHERE ta.tafexercicio.taf = " + tafselecionado.getId()+ 
					" AND ta.usuario = " + alunoselecionado.getId();
			TypedQuery<TafAluno> query = sessao.createQuery(sql, TafAluno.class);
			return query.getResultList();
		} else {
			System.out.println("Nenhum aluno selecionado");
			return null;
		}
	}

	
	//SOMANDO PONTOS DE CADA TAF OBTIDOS POR UM ALUNO 
	public List<Grafico> grupandoDados() {
		String sql = "SELECT t.taf, a.usuario, SUM(a.pontuacao) as total FROM TafAluno a, TafExercicio t " + 
				"WHERE a.tafexercicio = t.id " + 
				"AND "+alunoselecionado.getId()+ "= a.usuario.id " +
				"GROUP BY t.taf, a.usuario " +
				"ORDER BY t.taf, total DESC";		
		Query query = sessao.createQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> resultados = query.getResultList();
		
		List<Grafico> dados = new ArrayList<Grafico>();
	
		if(!resultados.isEmpty()) {
			for(Object[]c:resultados) {
				Grafico graf = new Grafico();
				graf.setTaf((Taf) c[0]);
				graf.setAluno((Usuario) c[1]);
				graf.setTotal(Math.round((Long) c[2]));
				dados.add(graf);
			}
			int index = 0;
			int max = dados.get(0).getTotal();
			
			for(Grafico d : dados) {		 
				DecimalFormat df = new DecimalFormat("#.#");
				df.setRoundingMode(RoundingMode.CEILING);
				double percentual = Double.parseDouble(df.format(((d.getTotal()*100)/max)));
				d.setPorcentagem(percentual);
				dados.set(index, d);
				index++;
			}	
			return dados;
		}
		return null;
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
			List<Grafico> dados = grupandoDados();
			if(dados != null) {
			    for(Grafico d : dados) {
		        	aluno.set(d.getTaf().toString(), d.getPorcentagem());      
		        	System.out.println(d.getTaf().getNome() + " - " + d.getPorcentagem() + " - " + d.getTotal());
			    }
			}else
				aluno.set("VAZIO", 0);
		}else 
			aluno.set("VAZIO", 0);
 
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
	
	public void manterTaf(ItemSelectEvent event) {
		List<Grafico> dados = grupandoDados();
		tafselecionado = dados.get(event.getItemIndex()).getTaf();
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
	
	public List<TafAluno> getResultados() {
		return resultados;
	}

	public void setResultados(List<TafAluno> resultados) {
		this.resultados = resultados;
	}

	public Taf getTafselecionado() {
		return tafselecionado;
	}

	public void setTafselecionado(Taf tafselecionado) {
		this.tafselecionado = tafselecionado;
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