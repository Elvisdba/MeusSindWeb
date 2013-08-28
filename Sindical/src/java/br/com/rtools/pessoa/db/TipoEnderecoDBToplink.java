
package br.com.rtools.pessoa.db;

import br.com.rtools.pessoa.TipoEndereco;
import br.com.rtools.principal.DB;
import java.util.List;
import javax.persistence.Query;



public class TipoEnderecoDBToplink extends DB implements TipoEnderecoDB {

    @Override
    public boolean insert(TipoEndereco tipoEndereco) {
        try{
          getEntityManager().getTransaction().begin();
          getEntityManager().persist(tipoEndereco);
          getEntityManager().flush();
          getEntityManager().getTransaction().commit();
          return true;
        } catch(Exception e){
            getEntityManager().getTransaction().rollback();
            return false;
        }   
    }

    @Override
    public boolean update(TipoEndereco tipoEndereco) {
        try{
        getEntityManager().merge(tipoEndereco);
        getEntityManager().flush();
        return true;
        }
        catch(Exception e){
            return false;
        }
    }

    @Override
    public boolean delete(TipoEndereco tipoEndereco) {
        try{
        getEntityManager().remove(tipoEndereco);
        getEntityManager().flush();
        return true;
        }
        catch(Exception e){
            return false;
        }        
    }

    @Override
    public TipoEndereco pesquisaCodigo(int id) {
        TipoEndereco result = null;
        try{
            Query qry = getEntityManager().createNamedQuery("TipoEndereco.pesquisaID");
            qry.setParameter("pid", id);
            result = (TipoEndereco) qry.getSingleResult(); 
        }
        catch(Exception e){
        }
        return result;
    }

    @Override
    public List pesquisaTodos() {
        try{
            Query qry = getEntityManager().createQuery("select tipo from TipoEndereco tipo ");
            return (qry.getResultList());
        }
        catch(Exception e){
            return null;
        }                       
    }

    @Override
    public List<String> pesquisaTipoEndereco(String des_tipo){
        List<String> result = null;
        try{
           Query qry = getEntityManager().createQuery("select tipo.descricao from TipoEndereco tipo where tipo.descricao like :texto");
           qry.setParameter("texto", des_tipo);
           result = (List<String>) qry.getResultList();
        }
        catch(Exception e){
        }
        return result;
    }

    @Override
    public List pesquisaTipoEndereco(){
        List result = null;
        try{
           Query qry = getEntityManager().createQuery("select tipo from TipoEndereco tipo");
           result = (List) qry.getResultList();
        }
        catch(Exception e){
        }
        return result;
    }
        
    @Override
    public TipoEndereco idTipoEndereco(TipoEndereco des_tipo){
        TipoEndereco result = null;
        try{
           Query qry = getEntityManager().createQuery("select tipo from TipoEndereco tipo where tipo.descricao = :d_tipo");
           qry.setParameter("d_tipo", des_tipo.getDescricao());
           result = (TipoEndereco) qry.getSingleResult(); 
        }
        catch(Exception e){
        }
        return result;
    }

    @Override
    public List<String> pesquisaTipoEnderecoParaJuridica(String des_tipo){
        List<String> result = null;
        try{
           Query qry = getEntityManager().createQuery(
                   "select tipo.descricao from TipoEndereco tipo " +
                   "where  tipo.descricao like :texto " +
                   "and (tipo.id = 2 " +
                   "or tipo.id = 3 " +
                   "or tipo.id = 4 " +
                   "or tipo.id = 5)");
           qry.setParameter("texto", des_tipo);
           result = (List<String>) qry.getResultList();
        }
        catch(Exception e){
        }
        return result;
    }

    @Override
    public List<String> pesquisaTipoEnderecoParaFisica(String des_tipo){
        List<String> result = null;
        try{
           Query qry = getEntityManager().createQuery(
                   "select tipo.descricao from TipoEndereco tipo " +
                   "where tipo.descricao like :texto " +
                   "and (tipo.id = 1 " +
                   "  or tipo.id = 3 " +
                   "  or tipo.id = 4)");
           qry.setParameter("texto", des_tipo);
           result = (List<String>) qry.getResultList();
        }
        catch(Exception e){
        }
        return result;
    }

    @Override
    public List<String> listaTipoEnderecoParaJuridica(){
        List<String> result = null;
        try{
           Query qry = getEntityManager().createQuery(
                   "select tipo from TipoEndereco tipo " +
                   "where tipo.id = 2 " +
                   "   or tipo.id = 3 " +
                   "   or tipo.id = 4 " +
                   "   or tipo.id = 5 ");
           result = (List<String>) qry.getResultList();
        }
        catch(Exception e){
        }
        return result;
    }

    @Override
    public List<String> listaTipoEnderecoParaFisica(){
        List<String> result = null;
        try{
           Query qry = getEntityManager().createQuery(
                   "select tipo from TipoEndereco tipo " +
                   " where tipo.id = 1 " +
                   "    or tipo.id = 3 " +
                   "    or tipo.id = 4 ");
           result = (List<String>) qry.getResultList();
        }
        catch(Exception e){
        }
        return result;
    }
}