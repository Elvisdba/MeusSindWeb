package br.com.rtools.financeiro;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "conf_financeiro")
public class ConfiguracaoFinanceiro implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;   
    @Column(name = "is_transferencia_automatica_caixa", columnDefinition = "boolean default true")
    private boolean transferenciaAutomaticaCaixa;    
    @Column(name = "is_caixa_operador", columnDefinition = "boolean default false")
    private boolean caixaOperador;    

    public ConfiguracaoFinanceiro() {
        this.id = -1;
        this.transferenciaAutomaticaCaixa = true;
        this.caixaOperador = false;
    }
    
    public ConfiguracaoFinanceiro(int id, boolean transferenciaAutomaticaCaixa, boolean caixaOperador) {
        this.id = id;
        this.transferenciaAutomaticaCaixa = transferenciaAutomaticaCaixa;
        this.caixaOperador = caixaOperador;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isTransferenciaAutomaticaCaixa() {
        return transferenciaAutomaticaCaixa;
    }

    public void setTransferenciaAutomaticaCaixa(boolean transferenciaAutomaticaCaixa) {
        this.transferenciaAutomaticaCaixa = transferenciaAutomaticaCaixa;
    }

    public boolean isCaixaOperador() {
        return caixaOperador;
    }

    public void setCaixaOperador(boolean caixaOperador) {
        this.caixaOperador = caixaOperador;
    }
    
}