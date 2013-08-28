package br.com.rtools.financeiro;

import javax.persistence.*;

@Entity
@Table(name="FIN_REMESSA")
@NamedQuery(name="Remessa.pesquisaID", query="select r from Remessa r where r.id=:pid")
public class Remessa implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name="ID_LOTE_REMESSA", referencedColumnName="ID", nullable=false)
    @OneToOne
    private LoteRemessa loteRemessa;
    @JoinColumn(name="ID_MOVIMENTO", referencedColumnName="ID", nullable=false)
    @OneToOne
    private Movimento movimento;

    public Remessa() {
        this.id = -1;
        this.loteRemessa = new LoteRemessa();
        this.movimento = new Movimento();
    }
    
    public Remessa(int id, LoteRemessa loteRemessa, Movimento movimento) {
        this.id = id;
        this.loteRemessa = loteRemessa;
        this.movimento = movimento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LoteRemessa getLoteRemessa() {
        return loteRemessa;
    }

    public void setLoteRemessa(LoteRemessa loteRemessa) {
        this.loteRemessa = loteRemessa;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }
}