package com.github.adminfaces.starter.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import com.github.adminfaces.starter.model.Exercicio;
import com.github.adminfaces.starter.model.Taf;

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
		//exercicio d = (exercicio)value;
		//return d.toString();
		if (value instanceof Taf) {
			Taf d = (Taf) value;
			if(d != null && d instanceof Taf && d.getId() != null) {
				uiComponent.getAttributes().put( d.getId().toString(), d);
				return d.getId().toString();
			}
		}
		return "";
	}
}
