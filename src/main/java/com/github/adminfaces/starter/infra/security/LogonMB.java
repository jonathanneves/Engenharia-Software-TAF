package com.github.adminfaces.starter.infra.security;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Specializes;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.omnifaces.util.Faces;

import com.github.adminfaces.starter.controller.UsuarioController;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.template.session.AdminSession;

/**
 * Created by rmpestano on 12/20/14.
 *
 * This is just a login example.
 *
 * AdminSession uses isLoggedIn to determine if user must be redirect to login page or not.
 * By default AdminSession isLoggedIn always resolves to true so it will not try to redirect user.
 *
 * If you already have your authorization mechanism which controls when user must be redirect to initial page or logon
 * you can skip this class.
 */
@Named
@SessionScoped
@Specializes
public class LogonMB extends AdminSession implements Serializable {

	private static final long serialVersionUID = 1L;

    private String currentUser = "Aluno";
    private String cpf; 
    private String senha;
    
	private UsuarioController userCont = new UsuarioController();
	private Usuario user;
	private boolean permissao = false;
	
	
    public void login() throws IOException {
    	try {
    	user = userCont.validarUsuario(cpf, senha);
    	if(user != null) {
    		currentUser = user.getNome();
    		//addMessage(currentUser, "Usuário logado com sucesso");
    		Faces.getExternalContext().getFlash().setKeepMessages(true);
    		setPermissao(true);
    		Faces.redirect("index.xhtml");
    	}else {
    		addErro("CPF ou Senha incorreta!","ERRO");
    		Faces.getExternalContext().getFlash().setKeepMessages(true);
     		Faces.redirect("login.xhtml");
    	}
    	} catch (Exception e) {
    		e.getMessage();
    	}
    }
	
	public void voltar() {
		try {
			currentUser = "Aluno";
			Faces.redirect("index.xhtml");
    	} catch (Exception e) {
    		e.getMessage();
    	}
	}
	
	public void logar() {
		try {
			currentUser = null;
			Faces.redirect("login.xhtml");
    	} catch (Exception e) {
    		e.getMessage();
    	}
	}
	
   /* public boolean isProfessor() {
    	if(user.getPermissao().equals("Professor")) 
    		return true;
    	else
    		return false;
    }
    
    public boolean isAluno() {
    	if(user.getPermissao().equals("Aluno"))
    		return true;
    	else
    		return false;
    }*/
    
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void addErro(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;		
	}

	public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public boolean isPermissao() {
		return permissao;
	}

	public void setPermissao(boolean permissao) {
		this.permissao = permissao;
	}
}
