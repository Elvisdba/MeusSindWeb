package br.com.rtools.endereco;

import javax.persistence.*;

@Entity
@Table(name = "END_LOGRADOURO")
@NamedQueries({
    @NamedQuery(name = "Logradouro.pesquisaID",  query = "SELECT LOGR FROM Logradouro AS LOGR WHERE LOGR.id = :pid"),
    @NamedQuery(name = "Logradouro.findAll",     query = "SELECT LOGR FROM Logradouro AS LOGR ORDER BY LOGR.descricao ASC "),
    @NamedQuery(name = "Logradouro.findName",    query = "SELECT LOGR FROM Logradouro AS LOGR WHERE UPPER(LOGR.descricao) LIKE :pdescricao ORDER BY LOGR.descricao ASC ")
})
public class Logradouro implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "DS_DESCRICAO", length = 50, nullable = false, unique = true)
    private String descricao;

    public Logradouro() {
        this.id = -1;
        this.descricao = "";
    }

    public Logradouro(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
