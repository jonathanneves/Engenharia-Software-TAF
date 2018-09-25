package com.github.adminfaces.starter.infra.security;

import com.github.adminfaces.starter.controller.UsuarioController;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.template.session.AdminSession;
import org.omnifaces.util.Faces;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Specializes;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;

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

    public void login() throws IOException {	
    	currentUser = cpf;
    	addDetailMessage("Usuário logado com sucesso");
    	Faces.getExternalContext().getFlash().setKeepMessages(true);
    	Faces.redirect("index.xhtml");
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
