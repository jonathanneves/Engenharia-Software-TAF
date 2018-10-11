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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import com.github.adminfaces.starter.model.Exercicio;
import com.github.adminfaces.starter.model.Taf;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean //Classe controladora
@ViewScoped
public class TafController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BarChartModel barModel;
	
	private Taf taf;
	private List<Taf> tafs;
 
	@PostConstruct
	public void init() {
		taf = new Taf(); 
		graficoPontos();
		listarTodas();
	}
	
	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.merge(taf);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
			t.commit();
			taf = new Taf();
			addMessage("Cadastro", "TAF cadastrado com sucesso");
			listarTodas();
		} catch (Exception e) {
			if(t!=null)
				t.rollback();
			throw(e);
		}finally{
			sessao.close();
		}
	}
	
	public void exclui ()  {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		System.out.println("-------------");
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.delete(taf);			
			t.commit();
			taf = new Taf();
			System.out.println("--------------");
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
			addMessage("ERRO", "ERRO ao listar tafs");
		} finally {
			sessao.close();
		}
	}
		
	
	 private BarChartModel initBarModel() {
	        BarChartModel model = new BarChartModel();
	 
	        ChartSeries aluno = new ChartSeries();
	        aluno.setLabel("pontos");
	        aluno.set("20/05/2017", 120);
	        aluno.set("05/08/2017", 100);
	        aluno.set("26/02/2018", 44);
	        aluno.set("03/05/2018", 150);
	        aluno.set("25/08/2018", 25);	 
	 
	        model.addSeries(aluno);

	        return model;
	 }
	 
	 private void graficoPontos() {
	        createBarModel();
	 }
	  
	private void createBarModel() {
        barModel = initBarModel();
         
        barModel.setTitle("Progresso de Pontuação");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Data da TAF");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Média");
        yAxis.setMin(0);
        yAxis.setMax(200);
    }	
	 
	public void editar(ActionEvent evt) {
		taf = (Taf)evt.getComponent().getAttributes().get("tafEdita");
	}
	
	public void excluir(ActionEvent evt)  {
		System.out.println("VAI TOMA NO CU");
		taf = (Taf)evt.getComponent().getAttributes().get("tafExclui");
		exclui();
	}
	
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	public Taf getTaf() {
		return taf;
	}
	public void setTaf(Taf Taf) {
		this.taf = Taf;
	}
	public List<Taf> getTafs() {
		return tafs;
	}
	public void setTafs(List<Taf> Tafs) {
		this.tafs = Tafs;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public BarChartModel getBarModel() {
	    return barModel;
	}
	     
}

