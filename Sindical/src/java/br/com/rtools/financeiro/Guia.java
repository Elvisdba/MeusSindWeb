package br.com.rtools.financeiro;

import br.com.rtools.associativo.SubGrupoConvenio;
import br.com.rtools.pessoa.Pessoa;
import javax.persistence.*;

@Entity
@Table(name = "FIN_GUIA")
@NamedQuery(name = "Guia.pesquisaID", query = "select g from Guia g where g.id=:pid")
public class Guia implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "ID_LOTE", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private Lote lote;
    @JoinColumn(name = "ID_CONVENIO", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "ID_CONVENIO_SUB_GRUPO", referencedColumnName = "ID")
    @ManyToOne
    private SubGrupoConvenio subGrupoConvenio;
    @Column(name = "IS_ENCAMINHAMENTO", nullable = true)
    private boolean encaminhamento;

    public Guia(int id, Lote lote, Pessoa pessoa, SubGrupoConvenio subGrupoConvenio, boolean encaminhamento) {
        this.id = id;
        this.lote = lote;
        this.pessoa = pessoa;
        this.subGrupoConvenio = subGrupoConvenio;
        this.encaminhamento = encaminhamento;
    }

    public Guia() {
        this.id = -1;
        this.lote = new Lote();
        this.pessoa = new Pessoa();
        this.subGrupoConvenio = new SubGrupoConvenio();
        this.encaminhamento = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public SubGrupoConvenio getSubGrupoConvenio() {
        return subGrupoConvenio;
    }

    public void setSubGrupoConvenio(SubGrupoConvenio subGrupoConvenio) {
        this.subGrupoConvenio = subGrupoConvenio;
    }

    public boolean isEncaminhamento() {
        return encaminhamento;
    }

    public void setEncaminhamento(boolean encaminhamento) {
        this.encaminhamento = encaminhamento;
    }
}
