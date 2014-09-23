package br.com.rtools.endereco.beans;

import br.com.rtools.endereco.*;
import br.com.rtools.endereco.db.EnderecoDB;
import br.com.rtools.endereco.db.EnderecoDBToplink;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class PesquisaEnderecoBean implements Serializable {

    private String cep;
    private String tcase;
    private List<Endereco> listaEndereco;

    @PostConstruct
    public void init() {
        listaEndereco = new ArrayList();
        cep = "";
        tcase = "";
    }

    @PreDestroy
    public void destroy() {
        clear();
    }

    public void clear() {
        GenericaSessao.remove("pesquisaEnderecoBean");
    }

    public void find() {
        listaEndereco.clear();
    }

    public void put(String t) {
        tcase = t;
    }

    public String put(Endereco e) {
        switch (tcase) {
            case "pessoaFisica":
                GenericaSessao.put("enderecoPesquisa", e);
                PF.closeDialog("dlg_pesquisa_endereco");
                PF.update("form_pessoa_fisica:");
            case "pessoaJuridica":
                GenericaSessao.put("enderecoPesquisa", e);
                PF.closeDialog("dlg_pesquisa_endereco");
                PF.update("formPessoaJuridica:");
        }
        return tcase;
    }

    public List<Endereco> getListaEndereco() {
        if(!cep.isEmpty()) {
            if (listaEndereco.isEmpty()) {
                EnderecoDB db = new EnderecoDBToplink();
                listaEndereco = db.pesquisaEnderecoCep(cep);
            }
        }
        return listaEndereco;
    }

    public void setListaEndereco(List<Endereco> listaEndereco) {
        this.listaEndereco = listaEndereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTcase() {
        return tcase;
    }

    public void setTcase(String tcase) {
        this.tcase = tcase;
    }
}
