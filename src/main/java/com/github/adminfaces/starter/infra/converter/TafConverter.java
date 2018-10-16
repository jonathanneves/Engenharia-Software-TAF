package com.github.adminfaces.starter.infra.converter;

import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.github.adminfaces.starter.model.Taf;

//vai poder ser mapeado em qualquer local necessário
@FacesConverter("tafConverter")
public class TafConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if(value != null && !value.isEmpty()) {
			return (Taf) uiComponent.getAttributes().get(value);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		//Aluno a = (Aluno)value;
		//return a.toString();
		if (value instanceof Taf) {
			Taf a = (Taf) value;
			if(a != null && a instanceof Taf && a.getId() != null) {
				uiComponent.getAttributes().put( a.getId().toString(), a);
				return a.getId().toString();
			}
		}
		return "";
	}
}
