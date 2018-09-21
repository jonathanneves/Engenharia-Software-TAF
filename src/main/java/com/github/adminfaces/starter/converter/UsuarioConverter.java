package com.github.adminfaces.starter.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import com.github.adminfaces.starter.model.Usuario;

@FacesConverter("usuarioConverter")
public class UsuarioConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if(value != null && !value.isEmpty()) {
			return (Usuario) uiComponent.getAttributes().get(value);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		//Aluno a = (Aluno)value;
		//return a.toString();
		if (value instanceof Usuario) {
			Usuario a = (Usuario) value;
			if(a != null && a instanceof Usuario && a.getId() != null) {
				uiComponent.getAttributes().put( a.getId().toString(), a);
				return a.getId().toString();
			}
		}
		return "";
	}
}
