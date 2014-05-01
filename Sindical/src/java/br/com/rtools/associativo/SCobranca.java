package br.com.rtools.associativo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "SOC_COBRANCA")
@NamedQuery(name = "SCobranca.pesquisaID", query = "select sc from SCobranca sc where sc.id = :pid")
public class SCobranca implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "DS_DESCRICAO", length = 50)
    private String descricao;
    @Column(name = "DS_INFORMATIVO", length = 2000)
    private String informativo;
    @Column(name = "DS_LOCAL_PAGAMENTO", length = 2000)
    private String localPagamento;

    public SCobranca() {
        this.id = -1;
        this.descricao = "";
        this.informativo = "";
        this.localPagamento = "";
    }

    public SCobranca(int id, String descricao, String informativo, String localPagamento) {
        this.id = id;
        this.descricao = descricao;
        this.informativo = informativo;
        this.localPagamento = localPagamento;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInformativo() {
        return informativo;
    }

    public void setInformativo(String informativo) {
        this.informativo = informativo;
    }

    public String getLocalPagamento() {
        return localPagamento;
    }

    public void setLocalPagamento(String localPagamento) {
        this.localPagamento = localPagamento;
    }
    
}
