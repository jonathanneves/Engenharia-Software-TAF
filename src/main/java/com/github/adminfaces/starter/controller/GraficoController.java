package com.github.adminfaces.starter.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.github.adminfaces.starter.infra.security.LogonMB;
import com.github.adminfaces.starter.model.TafAluno;
import com.github.adminfaces.starter.model.TafExercicio;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean
@ViewScoped
public class GraficoController implements Serializable {

private static final long serialVersionUID = 1L;

	private LogonMB logonmb;
	
	private TafAluno tafaluno;
	
	private List<TafAluno> tafalunos;
	private List<TafExercicio> tafexercicios;
	private List<Usuario> usuarios;
	
	private Usuario user;
	
	private BarChartModel barModel;
	
	@PostConstruct
	public void inicializa() {
		user = logonmb.getUser();
		listarTodas();
		createBarModels();
		
	}
	
	public void setandoUsuario(Usuario u) {
		System.out.println(u.getNome());
		user = u;
		System.out.println(user.getNome());
		//setUser(u);	
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
         
        barModel.setTitle("Gráfico de Desempenho do aluno "+getUser().getNome());
        barModel.setLegendPosition("box zero");
        barModel.setAnimate(true);
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Data do TAF");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Pontuação %");
        yAxis.setMin(0);
        yAxis.setMax(100);         
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
			
/*	public List<TafAluno> filtrarAlunosTaf(){
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
			cq.from(TafAluno.class);
			alunosparticipantes = sessao.createQuery(cq).getResultList();
			alunosparticipantes.removeIf(s -> s.getTafexercicio().getTaf().getId() != tafselecionado.getId());
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		return alunosparticipantes;
	}*/
	 

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

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
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