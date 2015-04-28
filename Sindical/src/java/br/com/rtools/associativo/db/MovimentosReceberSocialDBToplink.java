package br.com.rtools.associativo.db;

import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.Moeda;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class MovimentosReceberSocialDBToplink extends DB implements MovimentosReceberSocialDB {

    @Override
    public List pesquisaListaMovimentos(String id_pessoa, String id_responsavel, String por_status, String referencia) {
        try {
            if (id_pessoa.isEmpty()) {
                return new ArrayList();
            }
            String textqry = " select  "
                    + "     se.ds_descricao as servico,  \n"
                    + "     tp.ds_descricao as tipo, \n"
                    + "     m.ds_referencia, \n"
                    + "     m.dt_vencimento as vencimento, \n"
                    + "     func_valor(m.id) as valor, \n"
                    //+ "     m.nr_valor as valor, "
                    + "     func_multa_ass(m.id)+func_juros_ass(m.id)+func_correcao_ass(m.id) as acrescimo, \n"
                    + "     m.nr_desconto as desconto, \n"
                    + "     m.nr_valor+func_multa_ass(m.id)+func_juros_ass(m.id)+func_correcao_ass(m.id) as vl_calculado, \n"
                    + "     bx.dt_baixa, \n"
                    + "     nr_valor_baixa as valor_pago,  \n"
                    + "     m.ds_es as es, \n"
                    + "     p.ds_nome as responsavel, \n"
                    + "     b.ds_nome as beneficiario, \n" //12
                    + "     p.id as id_responsavel, \n" //13
                    + "     m.id as id_movimento, \n"
                    + "     m.id_lote as lote, \n"
                    + "     l.dt_lancamento as criacao,  \n" //16
                    + "     m.ds_documento as boleto, \n" //17
                    + "     func_intervalo_dias(m.dt_vencimento,CURRENT_DATE) as dias_atraso, \n"
                    + "     func_multa_ass(m.id) as multa,  \n"
                    + "     func_juros_ass(m.id) as juros, \n"
                    + "     func_correcao_ass(m.id) as correcao, \n"
                    + "     us.ds_nome as caixa,  \n"
                    + "     m.id_baixa as lote_baixa, \n"
                    + "     l.ds_documento as documento, \n"
                    + "     t.ds_nome as titular " // 25
                    + " from fin_movimento as m  \n"
                    + "inner join fin_lote as l on l.id=m.id_lote \n"
                    + "inner join pes_pessoa as p on p.id=m.id_pessoa \n"
                    + "inner join pes_pessoa as b on b.id=m.id_beneficiario \n"
                    + "left join pes_pessoa as t on t.id=m.id_titular \n"
                    + "inner join fin_servicos as se on se.id=m.id_servicos \n"
                    + "inner join fin_tipo_servico as tp on tp.id=m.id_tipo_servico  \n"
                    + " left join fin_baixa as bx on bx.id=m.id_baixa \n"
                    + " left join seg_usuario as u on u.id=bx.id_usuario \n"
                    + " left join pes_pessoa as us on us.id=u.id_pessoa \n"
                    + " left join pes_juridica as j on j.id_pessoa=m.id_pessoa \n";

            //String order_by = " order by m.dt_vencimento, se.ds_descricao, p.ds_nome, b.ds_nome ";
            String order_by = "";
            String where = "";
            String ands = "";

            switch (por_status) {
                case "todos":
                    ands = where + " where (m.id_pessoa in (" + id_responsavel + ") or (m.id_beneficiario in (" + id_pessoa + ") and j.id is null)) and m.is_ativo = true and m.id_servicos not in (select sr.id_servicos from fin_servico_rotina sr where id_rotina = 4) \n";
                    order_by = " order by m.dt_vencimento asc, p.ds_nome, t.ds_nome, b.ds_nome, se.ds_descricao \n";
                    //order_by = " order by m.dt_vencimento asc, p.ds_nome, b.ds_nome, se.ds_descricao \n";
                    break;
                case "abertos":
                    ands = where + " where (m.id_pessoa in (" + id_responsavel + ") or (m.id_beneficiario in (" + id_pessoa + ") and j.id is null)) and m.id_baixa is null and m.is_ativo = true and m.id_servicos not in (select sr.id_servicos from fin_servico_rotina sr where id_rotina = 4) \n";
                    order_by = " order by m.dt_vencimento asc, p.ds_nome, t.ds_nome, b.ds_nome, se.ds_descricao \n";
                    //order_by = " order by m.dt_vencimento asc, p.ds_nome, b.ds_nome, se.ds_descricao \n";
                    break;
                case "quitados":
                    ands = where + " where (m.id_pessoa in (" + id_responsavel + ") or (m.id_beneficiario in (" + id_pessoa + ") and j.id is null)) and m.id_baixa is not null and m.is_ativo = true and m.id_servicos not in (select sr.id_servicos from fin_servico_rotina sr where id_rotina = 4) \n";
                    order_by = " order by bx.dt_baixa asc, m.dt_vencimento, p.ds_nome, se.ds_descricao \n";
                    break;
                case "atrasados":
                    ands = where + " where (m.id_pessoa in (" + id_responsavel + ") or (m.id_beneficiario in (" + id_pessoa + ") and j.id is null)) and m.id_baixa is null and m.is_ativo = true and m.dt_vencimento < current_date and m.id_servicos not in (select sr.id_servicos from fin_servico_rotina sr where id_rotina = 4) \n";
                    order_by = " order by m.dt_vencimento, p.ds_nome, t.ds_nome, se.ds_descricao \n";
                    //order_by = " order by bx.dt_baixa asc, m.dt_vencimento, p.ds_nome, se.ds_descricao \n";
                    break;
                case "vencer":
                    ands = where + " where (m.id_pessoa in (" + id_responsavel + ") or (m.id_beneficiario in (" + id_pessoa + ") and j.id is null)) and m.id_baixa is null and m.is_ativo = true and m.dt_vencimento > current_date and m.id_servicos not in (select sr.id_servicos from fin_servico_rotina sr where id_rotina = 4) \n";
                    order_by = " order by m.dt_vencimento, p.ds_nome, t.ds_nome, se.ds_descricao \n";
                    //order_by = " order by bx.dt_baixa asc, m.dt_vencimento, p.ds_nome, se.ds_descricao \n";
                    break;
            }

            if (!referencia.isEmpty()) {
                ands += " and m.ds_referencia = '" + referencia + "' \n";
            }

            textqry += ands + order_by;
            Query qry = getEntityManager().createNativeQuery(textqry);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    @Override
    public List dadosSocio(int id_lote) {
        try {

            String textqry = " select "
                    + "     p.ds_nome as titular,"
                    + "     m.nr_matricula as matricula,"
                    + "     c.ds_categoria as categoria,"
                    + "     g.ds_grupo_categoria as grupo,"
                    + "  case "
                    + "  when m.dt_inativo is null then 'Matricula ATIVA' "
                    + "	 when m.dt_inativo is not null then 'Matricula INATIVA' "
                    + "   end "
                    + "  from fin_lote as l "
                    + " inner join matr_socios as m on m.id=l.id_matricula_socios "
                    + " inner join pes_pessoa as p on p.id=m.id_titular "
                    + " inner join soc_categoria as c on c.id=m.id_categoria "
                    + " inner join soc_grupo_categoria as g on g.id=c.id_grupo_categoria "
                    + " where l.id = " + id_lote;

            Query qry = getEntityManager().createNativeQuery(textqry);

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    @Override
    public Pessoa pesquisaPessoaPorBoleto(String boleto, int id_conta_cobranca) {
        Pessoa pessoa = null;
        String textqry
                = " SELECT pes.* "
                + "   FROM pes_pessoa pes "
                + "  INNER JOIN fin_movimento mov ON pes.id = mov.id_pessoa "
                + "  INNER JOIN fin_boleto bol ON mov.nr_ctr_boleto = bol.nr_ctr_boleto "
                + "    AND mov.is_ativo is true "
                + "    AND bol.id_conta_cobranca = " + id_conta_cobranca
                + "    AND mov.ds_documento = '" + boleto + "'";
        try {
            Query qry = getEntityManager().createNativeQuery(textqry, Pessoa.class);
            qry.setMaxResults(1);
            pessoa = (Pessoa) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return pessoa;
    }

    @Override
    public float[] pesquisaValorAcrescimo(int id_movimento) {
        float[] valor = new float[2];
        String textqry
                = " SELECT func_multa_ass(m.id)+func_juros_ass(m.id)+func_correcao_ass(m.id) as acrescimo, "
                + "        m.nr_valor+func_multa_ass(m.id)+func_juros_ass(m.id)+func_correcao_ass(m.id) as valor "
                + "   FROM fin_movimento m "
                + "  WHERE m.id = " + id_movimento;
        try {
            Query qry = getEntityManager().createNativeQuery(textqry);
            valor[0] = Moeda.converteUS$( Double.toString((Double) ((Vector)qry.getSingleResult()).get(0) ) );
            valor[1] = Moeda.converteUS$( Double.toString((Double) ((Vector)qry.getSingleResult()).get(1) ) );
        } catch (Exception e) {
            e.getMessage();
        }
        return valor;
    }

}
