
package br.com.rtools.arrecadacao.db;

import br.com.rtools.arrecadacao.GrupoCidade;
import br.com.rtools.arrecadacao.GrupoCidades;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.db.PessoaEnderecoDB;
import br.com.rtools.pessoa.db.PessoaEnderecoDBToplink;
import br.com.rtools.principal.DB;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;



public class GrupoCidadesDBToplink extends DB implements GrupoCidadesDB {

    @Override
    public boolean insert(GrupoCidades grupoCidade) {
        try{
          getEntityManager().getTransaction().begin();
          getEntityManager().persist(grupoCidade);
          getEntityManager().flush();
          getEntityManager().getTransaction().commit();
          return true;
        } catch(Exception e){
            getEntityManager().getTransaction().rollback();
            return false;
        }
    }

    @Override
    public boolean update(GrupoCidades grupoCidade) {
        try{
        getEntityManager().merge(grupoCidade);
        getEntityManager().flush();
        return true;
        }
        catch(Exception e){
            return false;
        }
    }

    @Override
    public boolean delete(GrupoCidades grupoCidade) {
        try{
        getEntityManager().remove(grupoCidade);
        getEntityManager().flush();
        return true;
        }
        catch(Exception e){
            return false;
        }
    }

    @Override
    public GrupoCidades pesquisaCodigo(int id) {
        GrupoCidades result = null;
        try{
            Query qry = getEntityManager().createNamedQuery("GrupoCidades.pesquisaID");
            qry.setParameter("pid", id);
            result = (GrupoCidades) qry.getSingleResult();
        }
        catch(Exception e){
        }
        return result;
    }

    @Override
    public List pesquisaTodos() {
        try{
            Query qry = getEntityManager().createQuery("select cont from GrupoCidades cont ");
            return (qry.getResultList());
        }
        catch(Exception e){
            return null;
        }
    }
    
    @Override
    public List pesquisaTodosCidadeAgrupada() {
        try{
            Query qry = getEntityManager().createQuery(" SELECT GC.cidade FROM GrupoCidades GC GROUP BY GC.cidade ORDER BY GC.cidade.uf ASC, GC.cidade.cidade ASC ");
            return (qry.getResultList());
        }
        catch(Exception e){
            return null;
        }
    }

    @Override
    public List pesquisaCidadesBase() {
        try{
            Query qry = getEntityManager().createQuery("select cont.cidade"
                                                     + "  from GrupoCidades cont "
                                                     //+ " order by cont.cidade"
                                                     + " group by cont.cidade");
            return (qry.getResultList());
        }
        catch(Exception e){
            return new ArrayList();
        }
    }

    @Override
    public GrupoCidades idGrupoCidades(GrupoCidades des_grupoCidade){
        GrupoCidades result = null;
        String descricao = des_grupoCidade.getGrupoCidade().getDescricao().toLowerCase().toUpperCase();
        try{
           Query qry = getEntityManager().createQuery("select con from GrupoCidades con where UPPER(con.grupoCidades.descricao) = :d_grupoCidade");
           qry.setParameter("d_grupoCidade", descricao);
           result = (GrupoCidades) qry.getSingleResult();
        }
        catch(Exception e){
        }
        return result;
    }

    @Override
    public GrupoCidade grupoCidadesPorPessoa(int idPessoa, int idConvencao){
        PessoaEnderecoDB pesdb = new PessoaEnderecoDBToplink();
        PessoaEndereco pes = pesdb.pesquisaEndPorPessoaTipo(idPessoa, 5);
        GrupoCidade result = null;
        try{
            Query qry = getEntityManager().createQuery(
                    "select g.grupoCidade" +
                    "  from GrupoCidades g," +
                    "       ConvencaoCidade cc" +
                    " where g.cidade.id = :pid" +                    
                    "   and cc.grupoCidade.id = g.grupoCidade.id" +
                    "   and cc.convencao.id = :idCon");
            qry.setParameter("pid", pes.getEndereco().getCidade().getId());
            qry.setParameter("idCon", idConvencao);
            result = (GrupoCidade) qry.getSingleResult();
        }catch(Exception e){
        }
        return result;
    }

    @Override
    public List pesquisaPorGrupo(int idGrupoCidade) {
        try{
            Query qry = getEntityManager().createQuery(
                    "select cont" +
                    "  from GrupoCidades cont" +
                    " where cont.grupoCidade.id = :id " +
                    "order by cont.cidade.cidade");
            qry.setParameter("id", idGrupoCidade);
            return (qry.getResultList());
        }
        catch(Exception e){
            return null;
        }
    }

    @Override
    public List pesquisaPorCidade(int idCidade) {
        try{
            Query qry = getEntityManager().createQuery("select gc " +
                                                       " from GrupoCidades gc " +
                                                       " where gc.cidade.id = :idCid");
            qry.setParameter("idCid", idCidade);
            return (qry.getResultList());
        }
        catch(Exception e){
            return null;
        }
    }
}
