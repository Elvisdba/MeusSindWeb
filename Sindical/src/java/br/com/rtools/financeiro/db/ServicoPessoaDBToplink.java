package br.com.rtools.financeiro.db;

//import br.com.rtools.associativo.Responsavel;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;
import oracle.toplink.essentials.exceptions.EJBQLException;

public class ServicoPessoaDBToplink extends DB implements ServicoPessoaDB {

    public boolean insert(ServicoPessoa servicoPessoa) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().persist(servicoPessoa);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public boolean update(ServicoPessoa servicoPessoa) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().merge(servicoPessoa);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public boolean delete(ServicoPessoa servicoPessoa) {
        try {
            getEntityManager().getTransaction().begin();
            getEntityManager().remove(servicoPessoa);
            getEntityManager().flush();
            getEntityManager().getTransaction().commit();
            return true;
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    public ServicoPessoa pesquisaCodigo(int id) {
        ServicoPessoa result = null;
        try {
            Query qry = getEntityManager().createNamedQuery("ServicoPessoa.pesquisaID");
            qry.setParameter("pid", id);
            result = (ServicoPessoa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List pesquisaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select sp from ServicoPessoa sp");
            return (qry.getResultList());
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public ServicoPessoa pesquisaServicoPessoaPorPessoa(int idPessoa) {
        ServicoPessoa result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "select sp "
                    + "  from ServicoPessoa sp"
                    + " where sp.pessoa.id = :pid"
                    + "   and sp.ativo = true");
            qry.setParameter("pid", idPessoa);
            result = (ServicoPessoa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }

    public List pesquisaTodosParaGeracao(String referencia) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select sp"
                    + "  from ServicoPessoa sp"
                    + " where sp.id in (select s.servicoPessoa.id from Socios s where s.matriculaSocios.dtInativo is null)"
                    + "  and CONCAT( SUBSTRING(sp.referenciaVigoracao,4,8) , SUBSTRING(sp.referenciaVigoracao,0,3) ) <= :ref"
                    + "  and ((CONCAT( SUBSTRING(sp.referenciaVigoracao,4,8) , SUBSTRING(sp.referenciaVigoracao,0,3) ) > :ref) or "
                    + "       (sp.referenciaValidade is null))");
            qry.setParameter("ref", referencia.substring(3, 7) + referencia.substring(0, 2));
            return (qry.getResultList());
        } catch (EJBQLException e) {
            e.getMessage();
            return null;
        }
    }

    public List pesquisaTodosParaGeracao(String referencia, int idPessoa) {
        try {
            Query qry = getEntityManager().createQuery(
                    "select sp"
                    + "  from Socios s join s.servicoPessoa sp  "
                    + " where s.matriculaSocios.dtInativo is null"
                    + "  and CONCAT( SUBSTRING(sp.referenciaVigoracao,4,8) , SUBSTRING(sp.referenciaVigoracao,0,3) ) <= :ref"
                    + "  and ((CONCAT( SUBSTRING(sp.referenciaVigoracao,4,8) , SUBSTRING(sp.referenciaVigoracao,0,3) ) > :ref) or "
                    + "       (sp.referenciaValidade is null))"
                    + "  and (   (sp.pessoa.id = :idPessoa)"
                    + "       or (s.matriculaSocios.pessoa.id = :idPessoa))");
            qry.setParameter("ref", referencia.substring(3, 7) + referencia.substring(0, 2));
            qry.setParameter("idPessoa", idPessoa);
            return (qry.getResultList());
        } catch (EJBQLException e) {
            e.getMessage();
            return null;
        }
    }
//    public Responsavel buscaResponsavel(int idServicoPessoa) {
//        try{
//            Query qry = getEntityManager().createQuery(
//                    "  select r"
//                    +"   from MatriculaAcademia ma,"
//                    + "       MatriculaConvenioMedico mcm,"
//                    + "       MatriculaSocios ms,"
//                    + "       ServicoPessoa sp,"
//                    + "       Responsavel r"
//                    + " where ((sp.id = ma.servicoPessoa.id and r.id  = ma.responsavel.id)"
//                    + "    or (sp.id = mcm.servicoPessoa.id and r.id  = mcm.responsavel.id)"
//                    + "    or (sp.id = ms.servicoPessoa.id and r.id  = ms.responsavel.id))"
//                    + "   and sp.id = :pid"
//                    );
//                    qry.setParameter("pid", idServicoPessoa);
//            return (Responsavel)  qry.getSingleResult();
//        }catch(EJBQLException e){
//            e.getMessage();
//            return null;
//        }
//    }
}
