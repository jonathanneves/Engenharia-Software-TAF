package com.github.adminfaces.starter.infra.security;

import com.github.adminfaces.starter.controller.UsuarioController;
import com.github.adminfaces.starter.controller.GraficoController;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.util.HibernateUtil;
import com.github.adminfaces.template.session.AdminSession;

import org.hibernate.Session;
import org.omnifaces.util.Faces;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Specializes;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaQuery;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

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

    private String currentUser;
    private String cpf;
    private boolean remember;
	private UsuarioController userCont = new UsuarioController();
	private GraficoController grafCont = new GraficoController();
	private Usuario user;

    public void login() throws IOException {
    	try {
    	user = userCont.validarUsuario(cpf);
    	grafCont.setandoUsuario(user);
    	if(user != null) {
    		currentUser = user.getNome();
    		//addMessage(currentUser, "Usuário logado com sucesso");
    		Faces.getExternalContext().getFlash().setKeepMessages(true);
    		Faces.redirect("index.xhtml");
    	}else {
    		addErro("CPF não encontrado!!!","ERRO");
    		Faces.getExternalContext().getFlash().setKeepMessages(true);
     		Faces.redirect("login.xhtml");
    	}
    	} catch (Exception e) {
    		e.getMessage();
    	}
    }
	
    public boolean isProfessor() {
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
    }
    
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

	public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
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
    
    
    
    
}
