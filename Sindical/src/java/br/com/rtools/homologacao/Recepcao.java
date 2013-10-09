package br.com.rtools.homologacao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "HOM_RECEPCAO")
@NamedQuery(name = "Recepcao.pesquisaID", query = "select r from Recepcao r where r.id = :pid")
public class Recepcao implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "DS_PREPOSTO", length = 200)
    private String preposto;
    @Column(name = "DS_HORA_INICIAL_PREPOSTO", length = 5)
    private String horaInicialPreposto;
    @Column(name = "DS_HORA_FINAL_PREPOSTO", length = 5)
    private String horaFinalPreposto;
    @Column(name = "DS_HORA_INICIAL_FUNCIONARIO", length = 5)
    private String horaInicialFuncionario;
    @Column(name = "DS_HORA_FINAL_FUNCIONARIO", length = 5)
    private String horaFinalFuncionario;

    public Recepcao() {
        this.id = -1;
        this.preposto = "";
        this.horaInicialPreposto = "";
        this.horaFinalPreposto = "";
        this.horaInicialFuncionario = "";
        this.horaFinalFuncionario = "";
    }

    public Recepcao(int id, String preposto, String horaInicialPreposto, String horaFinalPreposto, String horaInicialFuncionario, String horaFinalFuncionario) {
        this.id = id;
        this.preposto = preposto;
        this.horaInicialPreposto = horaInicialPreposto;
        this.horaFinalPreposto = horaFinalPreposto;
        this.horaInicialFuncionario = horaInicialFuncionario;
        this.horaFinalFuncionario = horaFinalFuncionario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPreposto() {
        return preposto;
    }

    public void setPreposto(String preposto) {
        this.preposto = preposto;
    }

    public String getHoraInicialPreposto() {
        return horaInicialPreposto;
    }

    public void setHoraInicialPreposto(String horaInicialPreposto) {
        this.horaInicialPreposto = horaInicialPreposto;
    }

    public String getHoraFinalPreposto() {
        return horaFinalPreposto;
    }

    public void setHoraFinalPreposto(String horaFinalPreposto) {
        this.horaFinalPreposto = horaFinalPreposto;
    }

    public String getHoraInicialFuncionario() {
        return horaInicialFuncionario;
    }

    public void setHoraInicialFuncionario(String horaInicialFuncionario) {
        this.horaInicialFuncionario = horaInicialFuncionario;
    }

    public String getHoraFinalFuncionario() {
        return horaFinalFuncionario;
    }

    public void setHoraFinalFuncionario(String horaFinalFuncionario) {
        this.horaFinalFuncionario = horaFinalFuncionario;
    }
}
