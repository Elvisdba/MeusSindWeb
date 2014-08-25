package br.com.rtools.endereco;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "end_descricao_endereco")
@NamedQueries({
    @NamedQuery(name = "DescricaoEndereco.pesquisaID", query = "SELECT DECE FROM DescricaoEndereco AS DECE WHERE DECE.id = :pid"),
    @NamedQuery(name = "DescricaoEndereco.findAll", query = "SELECT DECE FROM DescricaoEndereco AS DECE ORDER BY DECE.descricao ASC "),
    @NamedQuery(name = "DescricaoEndereco.findName", query = "SELECT DECE FROM DescricaoEndereco AS DECE WHERE UPPER(DECE.descricao) LIKE :pdescricao ORDER BY DECE.descricao ASC ")
})
public class DescricaoEndereco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "ds_descricao", length = 100, nullable = false, unique = true)
    private String descricao;

    public DescricaoEndereco() {
        this.id = -1;
        this.descricao = "";
    }

    public DescricaoEndereco(int id, String descricao) {
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
