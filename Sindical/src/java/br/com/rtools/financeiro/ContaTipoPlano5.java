package br.com.rtools.financeiro;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "FIN_CONTA_TIPO_PLANO5")
@NamedQuery(name = "ContaTipoPlano5.pesquisaID", query = "select ct from ContaTipoPlano5 ct where ct.id = :pid")
public class ContaTipoPlano5 implements  java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "ID_PLANO5", referencedColumnName = "ID")
    @ManyToOne
    private Plano5 plano5;
    @JoinColumn(name = "ID_CONTA_TIPO", referencedColumnName = "ID")
    @ManyToOne
    private ContaTipo contaTipo;

    public ContaTipoPlano5() {
        this.id = -1;
        this.plano5 = new Plano5();
        this.contaTipo = new ContaTipo();
    }
    
    public ContaTipoPlano5(int id, Plano5 plano5, ContaTipo contaTipo) {
        this.id = id;
        this.plano5 = plano5;
        this.contaTipo = contaTipo;
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

    public ContaTipo getContaTipo() {
        return contaTipo;
    }

    public void setContaTipo(ContaTipo contaTipo) {
        this.contaTipo = contaTipo;
    }
    
    
}
