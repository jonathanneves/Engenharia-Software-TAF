package controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

import model.Aluno;
import util.HibernateUtil;

@ManagedBean //Classe controladora
@ViewScoped
public class AlunoController implements Serializable {

private static final long serialVersionUID = 1L;
	
	private BarChartModel barModel;
	
	private Aluno aluno;
	private List<Aluno> alunos;
    
	@PostConstruct
	public void init() {
		aluno = new Aluno(); 
		graficoPontos();
		listarTodas();
	}
	
	public void salvar () {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.merge(aluno);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
			t.commit();
			aluno = new Aluno();
			addMessage("Cadastro", "Aluno cadastrado com sucesso");
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
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.delete(aluno);			
			t.commit();
			aluno = new Aluno();
			addMessage("Exclusão", "Aluno excluído com sucesso");
			listarTodas();
		} catch (Exception e) {
			if(t!=null)
				t.rollback();
			throw(e);
		}finally{
			sessao.close();
		}
	}
	
	@SuppressWarnings("unchecked")	//ADD WARNING para tirar o erro de consulta.list()
	public void listarTodas() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Aluno.class);	//Criteria = select (consulta)
			alunos = consulta.list();
		} catch (Exception e) {
			addMessage("ERRO", "ERRO");
		} finally {
			sessao.close();
		}
	}
	
	 private BarChartModel initBarModel() {
	        BarChartModel model = new BarChartModel();
	 
	        ChartSeries peso = new ChartSeries();
	        peso.setLabel("Peso");
	        peso.set("20/05/2017", 80.60);
	        peso.set("05/08/2017", 75.10);
	        peso.set("26/02/2018", 72.05);
	        peso.set("03/05/2018", 68.60);
	        peso.set("25/08/2018", 64.40);	 
	 
	        model.addSeries(peso);

	        return model;
	 }
	 
	 private void graficoPontos() {
	        createBarModel();
	 }
	     
	 private void createBarModel() {
        barModel = initBarModel();
         
        barModel.setTitle("Acompanhamento de peso");
        barModel.setLegendPosition("ne");
         
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setLabel("Data");
         
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setLabel("Peso");
        yAxis.setMin(0);
        yAxis.setMax(200);
    }	
	 
	public void editar(ActionEvent evt) {
		aluno = (Aluno)evt.getComponent().getAttributes().get("alunoEdita");
	}
	
	public void excluir(ActionEvent evt)  {
		aluno = (Aluno)evt.getComponent().getAttributes().get("alunoExclui");
		exclui();
	}
	
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	public Aluno getAluno() {
		return aluno;
	}
	public void setAluno(Aluno Aluno) {
		this.aluno = Aluno;
	}
	public List<Aluno> getAlunos() {
		return alunos;
	}
	public void setAlunos(List<Aluno> Alunos) {
		this.alunos = Alunos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public BarChartModel getBarModel() {
	    return barModel;
	}     
}

