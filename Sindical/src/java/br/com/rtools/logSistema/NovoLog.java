package br.com.rtools.logSistema;

import br.com.rtools.seguranca.Evento;
import br.com.rtools.seguranca.Log;
import br.com.rtools.seguranca.Rotina;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.db.RotinaDB;
import br.com.rtools.seguranca.db.RotinaDBToplink;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DaoInterface;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaSessao;
import java.util.Date;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class NovoLog extends salvaLogs {

    // LOG ARQUIVOS    
    public String novo(String acao, String obs) {
        Usuario user = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuario");
        String iusuario = ": ";
        if (user != null) {
            iusuario = iusuario + user.getPessoa().getId() + " " + user.getPessoa().getNome();
        }
        String ihorario = "[horario " + DataHoje.horaMinuto() + "]";
        String iacao = " >> " + acao;
        String iobs = "# " + obs;
        salvarLinha(ihorario + " " + iusuario + " " + iacao + " " + iobs);
        return null;
    }

    // LOG NO BANCO DE DADOS
    /**
     * <p>
     * <strong>Live</strong></p>
     * <p>
     * <strong>Example:</strong>live("User" + user.getLogin());</p>
     *
     * @author Bruno
     * @param infoLive Texto de informações livres para o log, não é gerado
     * nenhum Evento (seg_evento -> default null) para esta execução.
     *
     */
    public void live(String infoLive) {
        Log log = new Log(-1, new Date(), DataHoje.livre(new Date(), "HH:mm"), getUsuario(), getRotina(), infoLive, "", null);
        execute(log);
    }

    /**
     * <p>
     * <strong>Save - String</strong></p>
     * <strong>Example:</strong><ul><li>save("User" + user.getLogin());</li><li> Utilizar se for
     * um novo registro. </li></ul>
     *
     * @author Bruno
     * @param infoLive Texto de informações livres para o log.
     *
     */
    public void save(String infoLive) {
        Log log = new Log(-1, new Date(), DataHoje.livre(new Date(), "HH:mm"), getUsuario(), getRotina(), infoLive, "", getEvento(1));
        execute(log);
    }

    /**
     * <p>
     * <strong>Save - Object</strong></p>
     * <p>
     * <strong>Example:</strong>save((User) user, true); Utilizar se for um novo
     * registro. </p>
     *
     * @author Bruno
     * @param object - Texto de informações livres para o log.
     * @param isObject - default = true
     *
     */
    public void save(Object object, boolean isObject) {
        Log log = new Log(-1, new Date(), DataHoje.livre(new Date(), "HH:mm"), getUsuario(), getRotina(), object.toString(), "", getEvento(1));
        execute(log);
    }

    /**
     * <p>
     * <strong>Update - String</strong></p>
     * <p>
     * <strong>Example:</strong>update("Joao", "João"); Utilizar se for alterar
     * um registro existente. </p>
     *
     * @author Bruno
     * @param beforeUpdate - Informações antes da modificação.
     * @param afterUpdate - Informações depois da modificação.
     *
     */
    public void update(String beforeUpdate, String afterUpdate) {
        Log log = new Log(-1, new Date(), DataHoje.livre(new Date(), "HH:mm"), getUsuario(), getRotina(), beforeUpdate, afterUpdate, getEvento(3));
        execute(log);
    }

    /**
     * <p>
     * <strong>Update - Object</strong></p>
     * <p>
     * <strong>Example:</strong>update((User) userBefore, (User) userAfter,
     * true); Utilizar se for alterar um registro existente. </p>
     *
     * @author Bruno
     * @param beforeUpdate - Informações antes da modificação.
     * @param afterUpdate - Informações depois da modificação.
     * @param isObject - default = true
     *
     */
    public void update(Object beforeUpdate, Object afterUpdate, boolean isObject) {
        Log log = new Log(-1, new Date(), DataHoje.livre(new Date(), "HH:mm"), getUsuario(), getRotina(), beforeUpdate.toString(), afterUpdate.toString(), getEvento(3));
        execute(log);
    }

    /**
     * <p>
     * <strong>Delete - String</strong></p>
     * <p>
     * <strong>Example:</strong>save("User" + user.getLogin()); Utilizar se for
     * remover um registro. </p>
     *
     * @author Bruno
     * @param infoLive - Texto de informações livres para o log.
     *
     */
    public void delete(String infoLive) {
        Log log = new Log(-1, new Date(), DataHoje.livre(new Date(), "HH:mm"), getUsuario(), getRotina(), infoLive, null, getEvento(2));
        execute(log);
    }

    /**
     * <p>
     * <strong>Delete - Object</strong></p>
     * <p>
     * <strong>Example:</strong>delete((User) user, true); Utilizar se for
     * remover um registro. </p>
     *
     * @author Bruno
     * @param object - Texto de informações livres para o log.
     * @param isObject - default = true
     *
     */
    public void delete(Object object, boolean isObject) {
        Log log = new Log(-1, new Date(), DataHoje.livre(new Date(), "HH:mm"), getUsuario(), getRotina(), object.toString(), null, getEvento(2));
        execute(log);
    }

    /**
     * <p>
     * <strong>Execute</strong></p>
     * <p>
     * <strong>Example:</strong>Executa o método.</p>
     *
     * @param log
     *
     */
    public void execute(Log log) {
        DaoInterface di = new Dao();
        di.save(log, true);
    }

    public Usuario getUsuario() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            return (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }
        return null;
    }

    public Rotina getRotina() {
        HttpServletRequest paginaRequerida = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String nomePagina = this.converteURL(paginaRequerida.getRequestURI());
        RotinaDB rotinaDB = new RotinaDBToplink();
        Rotina rotina = rotinaDB.pesquisaRotinaPorPagina(nomePagina);
        try {
            if (rotina.getId() != -1) {
                return rotina;
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * <p>
     * <strong>Evento</strong></p>
     * <p>
     * <strong>Info:</strong> (1) Inclusão; (2) Exclusão; (3) Alteração; (4)
     * Consulta;</p>
     *
     * @author Bruno
     * @param idEvento
     *
     * @return Evento
     */
    public Evento getEvento(Integer idEvento) {
        try {
            DaoInterface di = new Dao();
            return (Evento) di.find(new Evento(), idEvento);
        } catch (Exception e) {
        }
        return null;
    }
}
