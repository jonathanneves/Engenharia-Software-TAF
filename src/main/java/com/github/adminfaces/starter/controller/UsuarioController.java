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
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.util.HibernateUtil;


@ManagedBean //Classe controladora
@ViewScoped
public class UsuarioController implements Serializable {

	private static final long serialVersionUID = 1L;
		
	private Usuario usuario;
	private List<Usuario> usuarios;
	private Usuario usuarioSelecionado;
	
	@PostConstruct
	public void inicializa() {
		usuario = new Usuario(); 
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
	
	public void listarUsuarios() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<Usuario> cq = sessao.getCriteriaBuilder().createQuery(Usuario.class);
			cq.from(Usuario.class);
			usuarios = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			addMessage("ERRO", "Erro ao listar usuários");
		} finally {
			sessao.close();
		}
	}

	 public Usuario validarUsuario(String cpf) {  
			Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
			try {
				CriteriaQuery<Usuario> cq = sessao.getCriteriaBuilder().createQuery(Usuario.class);
				cq.from(Usuario.class);
				usuarios = sessao.createQuery(cq).getResultList();
				for (Usuario user : usuarios) {
					if(user.getCpf().equals(cpf)) {
						return user;
					} 
				}   
				return null;
           } catch (Exception e) {
       		   return null;
           } finally {
        	   sessao.close();
           }		
     }
	
	 public List<Usuario> filtrarAlunos() {
		List<Usuario> alunosfiltrados = new ArrayList<Usuario>();
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<Usuario> cq = sessao.getCriteriaBuilder().createQuery(Usuario.class);
			cq.from(Usuario.class);
			alunosfiltrados = sessao.createQuery(cq).getResultList();
			alunosfiltrados.removeIf(s -> s.getPermissao().equals("Professor"));
		} catch (Exception e) {
			e.getMessage();
			addMessage("ERRO", "Erro ao filtrar alunos");
		} finally {
			sessao.close();
		}	
		return alunosfiltrados;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}
}
