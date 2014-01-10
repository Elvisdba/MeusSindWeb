package br.com.rtools.endereco;

import javax.persistence.*;

/**
 * <p>
 * <b>Cidade</b></p>
 * <p>
 * Cadastro de cidade e estado</p>
 *
 * @author rtools
 */

@Entity
@Table(name = "END_CIDADE")
@NamedQueries({
    @NamedQuery(name = "Cidade.pesquisaID",  query = "SELECT CID FROM Cidade AS CID WHERE CID.id = :pid"),
    @NamedQuery(name = "Cidade.findAll",     query = "SELECT CID FROM Cidade AS CID  ORDER BY CID.cidade ASC, CID.uf ASC "),
    @NamedQuery(name = "Cidade.findName",    query = "SELECT CID FROM Cidade AS CID WHERE UPPER(CID.cidade) LIKE :pdescricao ORDER BY CID.cidade ASC, CID.uf ASC ")
})
public class Cidade implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "DS_CIDADE", length = 50, nullable = false)
    private String cidade;
    @Column(name = "DS_UF", length = 2, nullable = false)
    private String uf;

    public Cidade() {
        this.id = -1;
        this.cidade = "";
        this.uf = "";
    }

    public Cidade(int id, String cidade, String uf) {
        this.id = id;
        this.cidade = cidade;
        this.uf = uf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCidadeToString() {
        if (cidade.equals("")) {
            return "";
        } else {
            return cidade + " - " + uf;
        }
    }
}