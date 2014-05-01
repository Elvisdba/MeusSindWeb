package br.com.rtools.financeiro;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "FIN_CONTA_OPERACAO")
@NamedQuery(name = "ContaOperacao.pesquisaID", query = "select co from ContaOperacao co where co.id = :pid")
public class ContaOperacao implements  java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "ID_PLANO5", referencedColumnName = "ID")
    @ManyToOne
    private Plano5 plano5;
    @JoinColumn(name = "ID_CENTRO_CUSTO_CONTABIL_SUB", referencedColumnName = "ID")
    @ManyToOne
    private CentroCustoContabilSub centroCustoContabilSub;
    @JoinColumn(name = "ID_OPERACAO", referencedColumnName = "ID")
    @ManyToOne
    private Operacao operacao;
    @Column(name = "DS_ES")
    private String es;
    @Column(name = "IS_CONTA_FIXA")
    private boolean contaFixa;

    public ContaOperacao() {
        this.id = -1;
        this.plano5 = new Plano5();
        this.centroCustoContabilSub = new CentroCustoContabilSub();
        this.operacao = new Operacao();
        this.es = "";
        this.contaFixa = false;
    }

    public ContaOperacao(int id, Plano5 plano5, CentroCustoContabilSub centroCustoContabilSub, Operacao operacao, String es, boolean contaFixa) {
        this.id = id;
        this.plano5 = plano5;
        this.centroCustoContabilSub = centroCustoContabilSub;
        this.operacao = operacao;
        this.es = es;
        this.contaFixa = contaFixa;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public CentroCustoContabilSub getCentroCustoContabilSub() {
        return centroCustoContabilSub;
    }

    public void setCentroCustoContabilSub(CentroCustoContabilSub centroCustoContabilSub) {
        this.centroCustoContabilSub = centroCustoContabilSub;
    }

    public Operacao getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public boolean isContaFixa() {
        return contaFixa;
    }

    public void setContaFixa(boolean contaFixa) {
        this.contaFixa = contaFixa;
    }
}