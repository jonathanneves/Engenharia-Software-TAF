package com.github.adminfaces.starter.controller;
 
import java.io.Serializable;
import java.util.ArrayList;
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
import org.primefaces.event.SelectEvent;

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
    private Usuario alunoatual;
   
    private List<TafAluno> tafalunos;
    private List<TafExercicio> tafexercicios;
    private List<Usuario> usuarios;
    
    public List<Usuario> alunosselecionados = new ArrayList<Usuario>();
    public List<String> listapontos = new ArrayList<String>();
    
    private String pontos;
 
    UsuarioController tu = new UsuarioController();
   
    @PostConstruct
    public void inicializa() {
    	usuarios = tu.filtrarAlunos();
        tafaluno = new TafAluno();
    }
   
    public void salvar () {
        Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
        Transaction t = null;
            try {
                tafselecionado.setRealizado("S");
                for(Usuario u : alunosselecionados) {
	                t = sessao.beginTransaction();
	                tafaluno.setPontuacao(Integer.parseInt(listapontos.get(0)));                
	                tafaluno.setUsuario(u);
	                tafaluno.setTafexercicio(exercicioSel);
	                sessao.merge(tafaluno);
	                sessao.merge(tafselecionado);
	                t.commit();
	                setPontos(null);
	                tafaluno = new TafAluno();
	                listapontos.remove(0);
                }
                alunosselecionados.clear();
                listapontos.clear();
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
   
    public void manterTaf() {
        System.out.println("Nome: "+getTafselecionado().getNome() +"        Data: "+ getTafselecionado().getData());
    }
    
    public void manterPontos(Usuario user) {
    	for(int i = 0; i<alunosselecionados.size(); i++) {
    		if(alunosselecionados.get(i).equals(user)) {
    			System.out.println("EDITADO: "+listapontos.get(i)+"--"+alunosselecionados.get(i));
    			alunosselecionados.remove(i);
    			listapontos.remove(i);
    		}
    	}
    	System.out.println("pontos"+getPontos() + " ID: "+user.getNome());
    	listapontos.add(getPontos());
    	alunosselecionados.add(user);
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
   
    public void setPontos(String pontos) {
        this.pontos = pontos;
    }
     
    public Usuario getAlunoatual() {
		return alunoatual;
	}

	public void setAlunoatual(Usuario alunoatual) {
		this.alunoatual = alunoatual;
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