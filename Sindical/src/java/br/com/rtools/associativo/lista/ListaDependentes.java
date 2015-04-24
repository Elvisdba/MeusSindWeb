package br.com.rtools.associativo.lista;

import br.com.rtools.pessoa.Fisica;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class ListaDependentes implements Serializable {

    private Fisica fisica;
    private Integer idParentesco;
    private Integer viaCarteirinha;
    private String validadeCarteirinha;
    private String validadeDependente;
    private Float nrDesconto;
    private List<SelectItem> listParentesco;
    private Boolean ativo;

    public ListaDependentes() {
        this.fisica = new Fisica();
        this.idParentesco = 0;
        this.viaCarteirinha = 0;
        this.validadeCarteirinha = "";
        this.validadeDependente = "";
        this.nrDesconto = new Float(0);
        this.listParentesco = new ArrayList<>();
        this.ativo = false;
    }

    public ListaDependentes(Fisica fisica, Integer idParentesco, Integer viaCarteirinha, String validadeCarteirinha, String validadeDependente, Float nrDesconto, List listParentesco, Boolean ativo) {
        this.fisica = fisica;
        this.idParentesco = idParentesco;
        this.viaCarteirinha = viaCarteirinha;
        this.validadeCarteirinha = validadeCarteirinha;
        this.validadeDependente = validadeDependente;
        this.nrDesconto = nrDesconto;
        this.listParentesco = listParentesco;
        this.ativo = ativo;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public Integer getIdParentesco() {
        return idParentesco;
    }

    public void setIdParentesco(Integer idParentesco) {
        this.idParentesco = idParentesco;
    }

    public String getParentescoString() {
        return Integer.toString(idParentesco);
    }

    public void setParentescoString(String parentescoString) {
        this.idParentesco = Integer.parseInt(parentescoString);
    }

    public Integer getViaCarteirinha() {
        return viaCarteirinha;
    }

    public void setViaCarteirinha(Integer viaCarteirinha) {
        this.viaCarteirinha = viaCarteirinha;
    }

    public String getValidadeCarteirinha() {
        return validadeCarteirinha;
    }

    public void setValidadeCarteirinha(String validadeCarteirinha) {
        this.validadeCarteirinha = validadeCarteirinha;
    }

    public String getValidadeDependente() {
        return validadeDependente;
    }

    public void setValidadeDependente(String validadeDependente) {
        this.validadeDependente = validadeDependente;
    }

    public Float getNrDesconto() {
        return nrDesconto;
    }

    public void setNrDesconto(Float nrDesconto) {
        this.nrDesconto = nrDesconto;
    }

    public String getDescontoString() {
        return Float.toString(nrDesconto);
    }

    public void setDescontoString(String desconto) {
        this.nrDesconto = Float.parseFloat(desconto);
    }

    public List getListParentesco() {
        return listParentesco;
    }

    public void setListParentesco(List listParentesco) {
        this.listParentesco = listParentesco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
