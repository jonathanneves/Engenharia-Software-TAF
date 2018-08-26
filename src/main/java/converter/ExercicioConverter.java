package converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import model.Exercicio;

@FacesConverter("exercicioConverter")
public class ExercicioConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if(value != null && !value.isEmpty()) {
			return (Exercicio) uiComponent.getAttributes().get(value);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		//exercicio d = (exercicio)value;
		//return d.toString();
		if (value instanceof Exercicio) {
			Exercicio d = (Exercicio) value;
			if(d != null && d instanceof Exercicio && d.getId() != null) {
				uiComponent.getAttributes().put( d.getId().toString(), d);
				return d.getId().toString();
			}
		}
		return "";
	}
}
