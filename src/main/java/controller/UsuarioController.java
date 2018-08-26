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

import model.Usuario;
import util.HibernateUtil;

@ManagedBean //Classe controladora
@ViewScoped
public class UsuarioController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BarChartModel barModel;
	
	private Usuario usuario;
	private List<Usuario> usuarios;

	@PostConstruct
	public void inicializa() {
		usuario = new Usuario(); 		
		graficoPontos();
		listarUsuarios();
	}
	
	public void salvar() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.merge(usuario);		//merge = Salvar (Insert) Ele identifica o ID direto e ja edita
			t.commit();
			usuario = new Usuario();
			addMessage("Cadastro", "Aluno(a) cadastrado(a) com sucesso");
			listarUsuarios();
		} catch (Exception e) {
			if(t!=null)
				t.rollback();
			throw(e);
		}finally{
			sessao.close();
		}
	}
	
	public void exclui() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		Transaction t = null;
		try {
			t = sessao.beginTransaction();
			sessao.delete(usuario);			
			t.commit();
			usuario = new Usuario();
			addMessage("Excluído", "Usuário excluído com sucesso");
			listarUsuarios();
		} catch (Exception e) {
			if(t!=null)
				t.rollback();
			throw(e);
		}finally{
			sessao.close();
		}
	}
	
	@SuppressWarnings("unchecked")	//ADD WARNING para tirar o erro de consulta.list()
	public void listarUsuarios() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			Criteria consulta = sessao.createCriteria(Usuario.class);	//Criteria = select (consulta)
			usuarios = consulta.list();
		} catch (Exception e) {
			addMessage("ERRO", "Erro ao listar alunos");
		} finally {
			sessao.close();
		}
	}
	
	 private BarChartModel initBarModel() {
	        BarChartModel model = new BarChartModel();
	 
	        ChartSeries peso = new ChartSeries();
	        peso.setLabel("Peso");
	        peso.set("20/05/2017", 80.50);
	        peso.set("05/08/2017", 72.40);
	        peso.set("26/02/2018", 68.20);
	        peso.set("03/05/2018", 63.80);
	        peso.set("25/08/2018", 58.10);	 
	        peso.set("02/10/2018", 57.30);	 
	        model.addSeries(peso);

	        return model;
	 }
	 
	 private void graficoPontos() {
	        createBarModel();
	 }
	     
	 private void createBarModel() {
	     barModel = initBarModel();
	      
	     barModel.setTitle("Acompanhamento de Peso");
	     barModel.setLegendPosition("ne");
	      
	     Axis xAxis = barModel.getAxis(AxisType.X);
	     xAxis.setLabel("Data");
	      
	     Axis yAxis = barModel.getAxis(AxisType.Y);
	     yAxis.setLabel("Peso");
	     yAxis.setMin(0);
	     yAxis.setMax(200);
	 }	
	 
	 
	public void editar(ActionEvent evt) {
		usuario = (Usuario)evt.getComponent().getAttributes().get("usuarioEdita");
	}
	
	public void excluir(ActionEvent evt) {
		usuario = (Usuario)evt.getComponent().getAttributes().get("usuarioExclui");
		exclui();
	}
	
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public BarChartModel getBarModel() {
		return barModel;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
