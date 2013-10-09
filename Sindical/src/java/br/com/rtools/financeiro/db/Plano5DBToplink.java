package br.com.rtools.financeiro.db;

import br.com.rtools.financeiro.ContaBanco;
import br.com.rtools.financeiro.Plano4;
import br.com.rtools.principal.DB;
import br.com.rtools.financeiro.Plano5;
import java.util.List;
import javax.persistence.Query;

public class Plano5DBToplink extends DB implements Plano5DB {

    public boolean insert(Plano5 plano5) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(plano5);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public boolean update(Plano5 plano5) {
        try {
            getEntityManager().merge(plano5);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean delete(Plano5 plano5) {
        try {
            getEntityManager().remove(plano5);
            getEntityManager().flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Plano5 pesquisaCodigo(int id) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("Plano5.pesquisaID");
            qry.setParameter("pid", id);
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select p from Plano5 p ");
            return (qry.getResultList());
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> pesquisaPlano5(int id) {
        List<String> result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select p"
                    + "  from Plano5 p"
                    + " where p.plano4.id = :pid");
            qry.setParameter("pid", id);
            result = (List<String>) qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano5 pesquisaPlano5IDContaBanco(int id) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createQuery("select pl5 from Plano5 pl5 where pl5.contaBanco.id = :pid");
            qry.setParameter("pid", id);
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public List pesquisaCaixaBanco() {
        List result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select p"
                    + "  from Plano5 p"
                    + " where p.contaBanco.id is not null");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public List<String> pesquisaPlano5(String des_plano4, String des_plano5) {
        List<String> result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select p.conta"
                    + "  from Plano5 p"
                    + " where p.conta like :conta5"
                    + "   and p.plano4.conta like :conta4");
            qry.setParameter("conta4", des_plano4);
            qry.setParameter("conta5", des_plano5);
            result = (List<String>) qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano5 idPlano5(Plano5 des_plano5) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createQuery("select p from Plano5 p where p.ds_numero = :d_plano5");
            qry.setParameter("d_plano5", des_plano5.getNumero());
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano5 pesquisaPlano5PorDesc(String desc, String desc4) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pl5"
                    + "  from Plano5 pl5"
                    + " where pl5.conta = :desc"
                    + "   and pl5.plano4.conta = :desc4");
            qry.setParameter("desc", desc);
            qry.setParameter("desc4", desc4);
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano5 pesquisaPlano5PorDesc(String desc) {
        Plano5 result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pl5"
                    + "  from Plano5 pl5"
                    + " where pl5.conta like :desc");
            qry.setParameter("desc", desc);
            result = (Plano5) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public Plano4 pesquisaPl4PorString(String desc, String p4) {
        Plano4 result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select pl5.plano4"
                    + "  from Plano5 pl5"
                    + " where pl5.conta = :desc"
                    + "   and pl5.plano4.conta = :p4");
            qry.setParameter("desc", desc);
            qry.setParameter("p4", p4);
            result = (Plano4) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    public ContaBanco pesquisaUltimoCheque(int pid) {
        ContaBanco result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select p.contaBanco"
                    + "  from Plano5 p"
                    + " where p.id = :pid");
            qry.setParameter("pid", pid);
            result = (ContaBanco) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }
}
