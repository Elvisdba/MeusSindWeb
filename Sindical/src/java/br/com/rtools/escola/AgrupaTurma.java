package br.com.rtools.escola;

import java.io.Serializable;
import javax.persistence.*;

/**
 * <p><b>Agrupa Turma</b></p>
 * <p>Agrupa turmas para realizar o cálculo da quantidade de vagas disponíveis no momento de uma nova matrícula.</p>
 * <br /><br />
 * <table border="1">
 *      <tHead>
 *          <tr>
 *              <td colspan="3">Exemplo prático</td>
 *          </tr>
 *      </tHead>
 *      <tbody>
 *          <tr><td>Id</td><td>Turma</td><td>TurmaIntegral</td></tr>
 *          <tr><td>01</td><td>01</td><td>01</td></tr>
 *          <tr><td>02</td><td>02</td><td>01</td></tr>
 *          <tr><td>03</td><td>05</td><td>01</td></tr>
 *      </tbody>
 *      <tFoot>
 *          <tr>
 *              <td colspan="3">No exemplo acima definiu-se que a turma principal desse agrupamento é a turma 01, dela será realizada a base de cálculo.</td>
 *          </tr>
 *      </tFoot>
 * </table>
 * <br /><br />
 * <p style="color: red;"><b>IMPORTANTE:</b>Para agrupar é necessários que as turmas sejam da mesma sala.</p>
 * @author rtools
 */
@Entity
@Table(name = "ESC_AGRUPA_TURMA",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ID_TURMA, ID_TURMA_INTEGRAL"})
)
@NamedQueries({
    @NamedQuery(name = "AgrupaTurma.pesquisaID", query = "SELECT AT FROM AgrupaTurma AS AT WHERE AT.id = :pid"),
    @NamedQuery(name = "AgrupaTurma.findAll", query = "SELECT AT FROM AgrupaTurma AS AT ORDER BY AT.turma.cursos.descricao ASC ")
})
public class AgrupaTurma implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "ID_TURMA", referencedColumnName = "ID")
    @ManyToOne
    private Turma turma;
    @JoinColumn(name = "ID_TURMA_INTEGRAL", referencedColumnName = "ID")
    @ManyToOne
    private Turma turmaIntegral;

    public AgrupaTurma() {
        this.id = -1;
        this.turma = new Turma();
        this.turmaIntegral = new Turma();
    }

    public AgrupaTurma(int id, Turma turma, Turma turmaIntegral) {
        this.id = id;
        this.turma = turma;
        this.turmaIntegral = turmaIntegral;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Turma getTurmaIntegral() {
        return turmaIntegral;
    }

    public void setTurmaIntegral(Turma turmaIntegral) {
        this.turmaIntegral = turmaIntegral;
    }

    @Override
    public String toString() {
        return "AgrupaTurma{" + "id=" + id + ", turma=" + turma + ", turmaIntegral=" + turmaIntegral + '}';
    }

}
