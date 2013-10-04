package br.com.rtools.financeiro;

import javax.persistence.*;

@Entity
@Table(name = "FIN_HISTORICO")
@NamedQuery(name = "Historico.pesquisaID", query = "select h from Historico h where h.id=:pid")
public class Historico implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "ID_MOVIMENTO", referencedColumnName = "ID", nullable = false)
    @OneToOne
    private Movimento movimento;
    @Column(name = "DS_HISTORICO", length = 2000, nullable = false)
    private String historico;
    @Column(name = "DS_COMPLEMENTO", length = 2000, nullable = true)
    private String complemento;

    public Historico() {
        this.id = -1;
        this.movimento = new Movimento();
        this.historico = "";
        this.complemento = "";
    }

    public Historico(int id, Movimento movimento, String historico, String complemento) {
        this.id = id;
        this.movimento = movimento;
        this.historico = historico;
        this.complemento = complemento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Movimento getMovimento() {
        return movimento;
    }

    public void setMovimento(Movimento movimento) {
        this.movimento = movimento;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}