package br.com.rtools.pessoa;

import br.com.rtools.utilitarios.DataHoje;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name="PES_PESSOA_COMPLEMENTO")
@NamedQuery(name="PessoaComplemento.pesquisaID", query="select pec from PessoaComplemento pec where pec.id = :pid")
public class PessoaComplemento implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name="ID_PESSOA", referencedColumnName="ID",  nullable=true)
    @ManyToOne (fetch=FetchType.EAGER)
    private Pessoa pessoa;
    
    @Column(name="NR_DIA_VENCIMENTO", nullable=true)
    private int nrDiaVencimento;
    
    @Column(name="IS_COBRANCA_BANCARIA")
    private boolean cobrancaBancaria;

     public PessoaComplemento() {
        this.id = -1;
        this.pessoa = new Pessoa();
        this.nrDiaVencimento = 0;
        this.cobrancaBancaria = false;
    }
    
    public PessoaComplemento(int id, Pessoa pessoa, int nrDiaVencimento, boolean cobrancaBancaria) {
        this.id = id;
        this.pessoa = pessoa;
        this.nrDiaVencimento = nrDiaVencimento;
        this.cobrancaBancaria = cobrancaBancaria;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public int getNrDiaVencimento() {
        return nrDiaVencimento;
    }

    public void setNrDiaVencimento(int nrDiaVencimento) {
        this.nrDiaVencimento = nrDiaVencimento;
    }

    public boolean isCobrancaBancaria() {
        return cobrancaBancaria;
    }

    public void setCobrancaBancaria(boolean cobrancaBancaria) {
        this.cobrancaBancaria = cobrancaBancaria;
    }
}