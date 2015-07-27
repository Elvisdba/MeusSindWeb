package br.com.rtools.seguranca.db;

import br.com.rtools.pessoa.Filial;
import br.com.rtools.principal.DB;
import br.com.rtools.seguranca.Rotina;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class RotinaDao extends DB {

    public List pesquisaTodosOrdenadoAtivo() {
        try {
            Query qry = getEntityManager().createQuery(" SELECT rot FROM Rotina rot WHERE rot.ativo = true ORDER BY rot.rotina ASC ");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public List pesquisaRotinasDisponiveisModulo(int idModulo) {
        try {
            Query query = getEntityManager().createQuery(" SELECT ROT FROM Rotina AS ROT WHERE ROT.ativo = true AND ROT.id NOT IN ( SELECT PER.rotina.id FROM Permissao AS PER WHERE PER.modulo.id = " + idModulo + " GROUP BY PER.rotina.id ) ORDER BY ROT.rotina ASC ");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public boolean existeRotina(Rotina rotina) {
        try {
            Query query = getEntityManager().createQuery("SELECT ROT FROM Rotina AS ROT WHERE UPPER(ROT.rotina) = :descricaoRotina");
            query.setParameter("descricaoRotina", rotina.getRotina().toUpperCase());
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public Rotina pesquisaPaginaRotina(String pagina) {
        Rotina rotina = new Rotina();
        try {
            Query qry = getEntityManager().createQuery("select rot from Rotina rot where rot.pagina like '%" + pagina + "%'");
            rotina = (Rotina) qry.getSingleResult();
        } catch (Exception e) {
        }
        return rotina;
    }

    public Rotina pesquisaRotinaPorClasse(String classe) {
        Rotina rotina = new Rotina();
        try {
            Query qry = getEntityManager().createQuery("SELECT R FROM Rotina AS R WHERE R.classe = :classe");
            qry.setParameter("classe", classe);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                if (list.size() > 1) {
                    return null;
                }
                return (Rotina) qry.getSingleResult();
            }
        } catch (Exception e) {
        }
        return rotina;
    }

    public Rotina pesquisaAcesso(String pagina) {
        try {
            Query query = getEntityManager().createQuery("SELECT R FROM Rotina AS R WHERE R.pagina = :pagina");
            query.setParameter("pagina", pagina);
            List list = query.getResultList();
            if (!list.isEmpty() && list.size() == 1) {
                return (Rotina) query.getSingleResult();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public List<Rotina> pesquisaRotina(String rotina) {
        List<Rotina> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select rot from Rotina rot where rot.rotina like '%" + rotina + "%'");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public List<Rotina> pesquisaAcessosOrdem() {
        List<Rotina> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select rot from Rotina rot where rot.acessos > 0 order by rot.acessos desc");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    public Rotina pesquisaRotinaPorPagina(String pagina) {
        try {
            Query query = getEntityManager().createQuery("SELECT ROT FROM Rotina AS ROT WHERE (ROT.pagina LIKE 'Sindical/" + pagina + ".jsf' OR ROT.pagina LIKE '\"/Sindical/" + pagina + ".jsf\"')");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Rotina) query.getSingleResult();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public Rotina pesquisaRotinaPorAcao(String acao) {
        try {
            Query query = getEntityManager().createQuery("SELECT ROT FROM Rotina AS ROT WHERE ROT.acao LIKE :acao");
            query.setParameter("acao", acao);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Rotina) query.getSingleResult();
            }
        } catch (Exception e) {
        }
        return null;
    }

    public List<Rotina> pesquisaRotinaPorDescricao(String descricaoRotina) {
        try {
            Query query = getEntityManager().createQuery("SELECT ROT FROM Rotina AS ROT WHERE UPPER(ROT.rotina) LIKE '%" + descricaoRotina.toUpperCase() + "%' OR UPPER(ROT.pagina) LIKE '%" + descricaoRotina.toUpperCase() + "%' ORDER BY ROT.rotina ASC, ROT.pagina ASC ");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
        }
        return new ArrayList();
    }

    public Rotina pesquisaRotinaPermissao(String dsPermissao) {
        dsPermissao = "%" + dsPermissao + "%";
        try {
            Query qry = getEntityManager().createQuery("select ro "
                    + "  from Rotina ro "
                    + " where ro.pagina like '" + dsPermissao + "'");
            return (Rotina) (qry.getSingleResult());
        } catch (EJBQLException e) {
        }
        return null;
    }

    public Rotina pesquisaRotinaPermissaoPorClasse(String dsClasse) {
        try {
            Query qry = getEntityManager().createQuery("select ro "
                    + "  from Rotina ro "
                    + " where ro.classe like '" + dsClasse + "'");
            return (Rotina) (qry.getSingleResult());
        } catch (EJBQLException e) {
        }
        return null;
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findByTabela('matr_escola');
     *
     * @param table
     * @return Todas as filias da tabela específicada
     */
    public List findByTabela(String table) {
        return findByTabela(table, "id_rotina");
    }

    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findByTabela('matr_escola');
     *
     * @param table
     * @return Todas as rotinas da tabela específicada
     * @param column Nome da coluna
     */
    public List findByTabela(String table, String column) {
        if (column == null || column.isEmpty()) {
            column = "id_rotina";
        }
        try {
            String queryString
                    = "     SELECT R.* FROM seg_rotina AS R                     \n"
                    + "      WHERE R.id IN (                                    \n"
                    + "	           SELECT T." + column + "                      \n"
                    + "              FROM " + table + " AS T                    \n"
                    + "          GROUP BY T." + column + "                      \n"
                    + ")                                                        \n"
                    + " ORDER BY R.ds_rotina ";
            Query query = getEntityManager().createNativeQuery(queryString, Filial.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }
    
    /**
     * Nome da tabela onde esta a lista de filiais Ex:
     * findByTabela('matr_escola');
     *
     * @param table
     * @return Todas as rotinas da tabela específicada
     * @param column Nome da coluna
     */
    public List findByTabelaNon(String table, String column) {
        if (column == null || column.isEmpty()) {
            column = "id_rotina";
        }
        try {
            String queryString
                    = "     SELECT R.* FROM seg_rotina AS R                     \n"
                    + "      WHERE R.id IN (                                    \n"
                    + "	           SELECT T." + column + "                      \n"
                    + "              FROM " + table + " AS T                    \n"
                    + "          GROUP BY T." + column + "                      \n"
                    + ")                                                        \n"
                    + " ORDER BY R.ds_rotina ";
            Query query = getEntityManager().createNativeQuery(queryString, Filial.class);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }
}