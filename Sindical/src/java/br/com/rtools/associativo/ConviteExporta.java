package br.com.rtools.associativo;

import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "CONV_EXPORTA")
@NamedQuery(name = "ConviteExporta.pesquisaID", query = "SELECT CONEX FROM ConviteExporta CONEX WHERE CONEX.id = :pid")
public class ConviteExporta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "ID_CONV_MOVIMENTO ", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private ConviteMovimento conviteMovimento;
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_EXPORTACAO")
    private Date dtExportacao;

    public ConviteExporta() {
        this.id = -1;
        this.conviteMovimento = new ConviteMovimento();
        this.dtExportacao = DataHoje.dataHoje();
    }

    public ConviteExporta(int id, ConviteMovimento conviteMovimento, String exportacao) {
        this.id = id;
        this.conviteMovimento = conviteMovimento;
        this.dtExportacao = DataHoje.converte(exportacao);
    }

    public String getExportacao() {
        return DataHoje.converteData(getDtExportacao());
    }

    public void setExportacao(String exportacao) {
        this.setDtExportacao(DataHoje.converte(exportacao));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ConviteMovimento getConviteMovimento() {
        return conviteMovimento;
    }

    public void setConviteMovimento(ConviteMovimento conviteMovimento) {
        this.conviteMovimento = conviteMovimento;
    }

    public Date getDtExportacao() {
        return dtExportacao;
    }

    public void setDtExportacao(Date dtExportacao) {
        this.dtExportacao = dtExportacao;
    }

}
