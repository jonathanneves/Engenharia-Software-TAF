package converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import model.Aluno;


@FacesConverter("alunoConverter")
public class AlunoConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if(value != null && !value.isEmpty()) {
			return (Aluno) uiComponent.getAttributes().get(value);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		//Aluno a = (Aluno)value;
		//return a.toString();
		if (value instanceof Aluno) {
			Aluno a = (Aluno) value;
			if(a != null && a instanceof Aluno && a.getId() != null) {
				uiComponent.getAttributes().put( a.getId().toString(), a);
				return a.getId().toString();
			}
		}
		return "";
	}
}
