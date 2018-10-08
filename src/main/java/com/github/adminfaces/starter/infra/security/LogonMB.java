package com.github.adminfaces.starter.infra.security;

import com.github.adminfaces.starter.controller.UsuarioController;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.util.HibernateUtil;
import com.github.adminfaces.template.session.AdminSession;

import org.hibernate.Session;
import org.omnifaces.util.Faces;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Specializes;
import javax.faces.application.FacesMessage;
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

    private String currentUser;
    private String cpf;
    private boolean remember;
	private UsuarioController userCont = new UsuarioController();
	private Usuario user = new Usuario();

    public void login() throws IOException {
    	try {
    	user = userCont.validarUsuario(cpf);
    	if(user != null) {
    		currentUser = user.getNome();
    		addDetailMessage("Usuário logado com sucesso");
    		Faces.getExternalContext().getFlash().setKeepMessages(true);
    		Faces.redirect("index.xhtml");
    	}else {
    		addMessage("CPF não encontrado!!!","ERRO");
    		Faces.getExternalContext().getFlash().setKeepMessages(true);
     		Faces.redirect("login.xhtml");
    	}
    	} catch (Exception e) {
    		e.getMessage();
    	}
    }
	
    public boolean isProfessor() {
    	user = userCont.validarUsuario(cpf);
    	if(user.getPermissao().equals("Professor")) 
    		return true;
    	else
    		return false;
    }
    
    public boolean isAluno() {
    	user = userCont.validarUsuario(cpf);
    	if(user.getPermissao().equals("Aluno"))
    		return true;
    	else
    		return false;
    }
    
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
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
}
