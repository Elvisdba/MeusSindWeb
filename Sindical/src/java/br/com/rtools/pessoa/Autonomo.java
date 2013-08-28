package br.com.rtools.pessoa;

import javax.persistence.*;

@Entity
@Table(name="PES_AUTONOMO")
@NamedQuery(name="Autonomo.pesquisaID", query="select aut from Autonomo aut where aut.id=:pid")
public class Autonomo implements java.io.Serializable {    
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;    
    @JoinColumn(name="ID_PESSOA", referencedColumnName="ID", nullable=false)
    @OneToOne(fetch=FetchType.EAGER)//(optional=false)   (cascade=CascadeType.ALL)
    private Pessoa pessoa;

    public Autonomo() {
        this.id = -1;
        this.pessoa = new Pessoa();
    }
    
    public Autonomo(Pessoa pessoa) {
        this.pessoa = pessoa;
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
}