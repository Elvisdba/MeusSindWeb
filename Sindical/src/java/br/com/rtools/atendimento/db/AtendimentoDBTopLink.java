/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.rtools.atendimento.db;

import br.com.rtools.atendimento.AteMovimento;
import br.com.rtools.atendimento.AtePessoa;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.AnaliseString;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.SalvarAcumuladoDB;
import br.com.rtools.utilitarios.SalvarAcumuladoDBToplink;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

/**
 *
 * @author rtools
 */
public class AtendimentoDBTopLink extends DB implements AtendimentoDB {
    
    public AteMovimento pesquisaCodigoAteMovimento(int id){
       AteMovimento ate = new AteMovimento();
       try{
            Query qry = getEntityManager().createQuery(
                "select mov from AteMovimento mov where mov.id = "+id
            );
            ate = (AteMovimento)qry.getSingleResult();
        }
        catch(Exception e){
        }
        return ate;
    }

    
    @Override
    public boolean pessoaOposicao(String cpf){
        try{        
//            Query qry = getEntityManager().createQuery(
//            " select opo " +        
//            "   from Oposicao opo " +        
//            "  where opo.oposicaoPessoa.cpf = :cpf "
//            );
            String data = DataHoje.livre(new Date(), "yyyyMM");
            Query qry = getEntityManager().createNativeQuery(""
                +" select * " 
                +" from arr_oposicao opo " 
                +" inner join arr_oposicao_pessoa pes on pes.id = opo.id_oposicao_pessoa "
                +" inner join arr_convencao_periodo per on per.id = opo.id_convencao_periodo "
                +" where pes.ds_cpf = '"+cpf+"' "
                +" and '"+data+"' >= (substring(per.ds_referencia_inicial,4,4)||substring(per.ds_referencia_inicial,1,2)) "
                +" and '"+data+"' <= (substring(per.ds_referencia_final,4,4)||substring(per.ds_referencia_final,1,2)) "
            );
            if(qry.getResultList().size() > 0){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }
    
    @Override
    public AtePessoa pessoaDocumento(String valor){
       List vetor;
       AtePessoa atePessoa = new AtePessoa();
       SalvarAcumuladoDB dB = new SalvarAcumuladoDBToplink();
       String queryString = ""+
                   "        select ate.id                                   "+
                   "          from ate_pessoa ate                           "+
                   "         where ate.ds_cpf = '"+valor+"' or              "+
                   "                translate(upper(ate.ds_rg),'./-', '') = translate(upper('"+valor+"'),'./-','')";
               
        try{        
            Query qry = getEntityManager().createNativeQuery(queryString);
            qry.setFirstResult(0);
            vetor = qry.getResultList();
            if (!vetor.isEmpty()){
                atePessoa = (AtePessoa) dB.pesquisaObjeto((Integer) ((Vector) vetor.get(0)).get(0), "AtePessoa");
                return atePessoa;
            }else{
                return null;
            }
        }catch(Exception e){
           return null;
        }
    }
    
    public boolean validaAtendimento(AteMovimento ateMovimento){
        try{
            Query query = getEntityManager().createQuery(" SELECT mov FROM AteMovimento mov WHERE mov.pessoa.id = :pessoa AND mov.dataEmissao = :dataEmissao AND mov.operacao.id = :operacao AND mov.filial.id = :filial ");
            query.setParameter("pessoa", ateMovimento.getPessoa().getId());
            query.setParameter("dataEmissao", ateMovimento.getDataEmissao());
            query.setParameter("operacao", ateMovimento.getOperacao().getId());
            query.setParameter("filial", ateMovimento.getFilial().getId());
            if(query.getResultList().size() > 0){
                return true;
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }
    
    @Override
    public List<AteMovimento> listaAteMovimento(String cpf, String por){
        String strQuery;
        List result;
        if(por.equals("hoje")){
            por = " and ate.dataEmissao = now() ";
        }else if(por.equals("ontem")){
            por = " and ate.dataEmissao = now() ";
        }else if(por.equals("60")){
            por = " and between now() ";
        }
        if(!cpf.equals("")){
            strQuery = " select ate from AteMovimento ate where ate.cpf = "+cpf + " " + por;
        }else{
            strQuery = " select ate from AteMovimento ate ";
        }
        try{
            //+ "order ate.dataEmissao desc, ate.ds_hora DESC "
            Query qry = getEntityManager().createQuery(strQuery + por);
            return qry.getResultList();
        }catch(Exception e){
            return new ArrayList<AteMovimento>();
        }
    }
    
    @Override
    public List listaAteMovimentos(String cpf, String por){
        String porStr = "";
        String innerPes = "";
        List<AteMovimento> result = new ArrayList();
        boolean isWhere = false;
        boolean isTodos = false;
        if(por.equals("todos")){
            isTodos = true;
        }
        if(!cpf.equals("") || ( !por.equals("todos") && !por.equals("") )){
            porStr = " where ";
            isWhere = true;
        }else{
            porStr = "";
        }
        if(por.equals("hoje")){
            porStr += " mov.dt_emissao = current_date ";
        }else if(por.equals("ontem")){
            porStr += " mov.dt_emissao = current_date - interval '1 days' ";
        }else if(por.equals("60")){
            porStr += "  mov.dt_emissao between current_date - interval '2000 days' and current_date ";
        }
        if(!cpf.equals("")){
            if(isWhere == true){
                if(isTodos == false){
                    porStr += " and ";
                }
            }
            innerPes = " inner join ate_pessoa pes on(pes.id = mov.id_ate_pessoa)" ;
            porStr += " pes.ds_cpf = '"+ cpf +"'" ;
            
        }
        String text = " select mov.id from ate_movimento mov " + innerPes + porStr;
        List vetor;
        SalvarAcumuladoDB db = new SalvarAcumuladoDBToplink();
        try{
            Query qry = getEntityManager().createNativeQuery(text + " order by mov.dt_emissao desc, mov.ds_hora desc ");
            
            vetor = qry.getResultList();
            if (!vetor.isEmpty()){
                for (int i = 0; i < vetor.size(); i++){
                    result.add( pesquisaCodigoAteMovimento((Integer) ((Vector) vetor.get(i)).get(0)));
                }
            }            
            
            return result;
        }catch(Exception e){
            return result;
        }
    }   
    
}
