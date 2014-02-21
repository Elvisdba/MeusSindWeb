package br.com.rtools.converter;

import br.com.rtools.utilitarios.Moeda;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("toCurrency")
public class ToCurrency implements Converter {

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (String) value; // Or (value != null) ? value.toString().toUpperCase() : null;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value != null ) {
            if (value.isEmpty()) {
                value = "0";
            }
            String somenteLetras = value.replaceAll("[^a-zA-Z]", "");
            if(somenteLetras.length() > 0) {
                return null;
            }
            return Moeda.converteR$(value);
        }
        return null;
    }

}