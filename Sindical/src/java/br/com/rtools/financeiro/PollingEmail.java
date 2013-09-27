package br.com.rtools.financeiro;

import br.com.rtools.sistema.Links;
import br.com.rtools.utilitarios.DataHoje;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="FIN_POLLING_EMAIL")
@NamedQuery(name="PollingEmail.pesquisaID", query="select pe from PollingEmail pe where pe.id = :pid")
public class PollingEmail implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Temporal(TemporalType.DATE)
    @Column(name="DT_EMISSAO")
    private Date dtEmissao;    
    @Column(name="DS_HORA", length = 5)
    private String hora;  
    @Column(name = "IS_ATIVO")
    private boolean ativo;
    @JoinColumn(name="ID_LINK", referencedColumnName="ID")
    @ManyToOne
    private Links links;
    @Temporal(TemporalType.DATE)
    @Column(name="DT_ENVIO")
    private Date dtEnvio;  
    @JoinColumn(name="ID_COBRANCA_ENVIO", referencedColumnName="ID")
    @ManyToOne
    private CobrancaEnvio cobrancaEnvio;
    
    public PollingEmail() {
        this.id = -1;
        this.setEmissao(null);
        this.hora = "";
        this.ativo = true;
        this.links = new Links();
        this.setEnvio(null);
        this.cobrancaEnvio = new CobrancaEnvio();
    }
    
    public PollingEmail(int id, String emissao, String hora, boolean ativo, Links links, String envio, CobrancaEnvio cobrancaEnvio) {
        this.id = id;
        this.setEmissao(emissao);
        this.hora = hora;
        this.ativo = ativo;
        this.links = links;
        this.setEnvio(envio);
        this.cobrancaEnvio = cobrancaEnvio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(Date dtEmissao) {
        this.dtEmissao = dtEmissao;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
    
    public String getEmissao() {
        return DataHoje.converteData(dtEmissao);
    }

    public void setEmissao(String emissao) {
        this.dtEmissao = DataHoje.converte(emissao);
    }    

    public Date getDtEnvio() {
        return dtEnvio;
    }

    public void setDtEnvio(Date dtEnvio) {
        this.dtEnvio = dtEnvio;
    }
    
    public String getEnvio() {
        return DataHoje.converteData(dtEnvio);
    }

    public void setEnvio(String envio) {
        this.dtEnvio = DataHoje.converte(envio);
    }   

    public CobrancaEnvio getCobrancaEnvio() {
        return cobrancaEnvio;
    }

    public void setCobrancaEnvio(CobrancaEnvio cobrancaEnvio) {
        this.cobrancaEnvio = cobrancaEnvio;
    }

}
