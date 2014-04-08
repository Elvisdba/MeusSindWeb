package br.com.rtools.arrecadacao.db;

import br.com.rtools.arrecadacao.ConvencaoPeriodo;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.OposicaoPessoa;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.SalvarAcumuladoDB;
import br.com.rtools.utilitarios.SalvarAcumuladoDBToplink;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.persistence.Query;

public class OposicaoDBToplink extends DB implements OposicaoDB {

    @Override
    public ConvencaoPeriodo pesquisaConvencaoPeriodo(int id_convencao, int id_grupo) {
        ConvencaoPeriodo result = new ConvencaoPeriodo();
        try {
            Query qry = getEntityManager().createQuery("select cp from ConvencaoPeriodo cp where cp.convencao.id = " + id_convencao + " and cp.grupoCidade.id = " + id_grupo);
            result = (ConvencaoPeriodo) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public List<ConvencaoPeriodo> listaConvencaoPeriodo() {
        try {
            Query qry = getEntityManager().createQuery("select cp from ConvencaoPeriodo cp order by cp.convencao.descricao");
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public List<Oposicao> listaTodos() {
        try {
            Query qry = getEntityManager().createQuery("select o from Oposicao o order by o.fisica.pessoa.nome");
            return qry.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

    @Override
    public Oposicao pesquisaOposicao(int id_fisica, int id_juridica) {
        Oposicao result = new Oposicao();
        try {
            Query qry = getEntityManager().createQuery("select o from Oposicao o where o.fisica.id = " + id_fisica + " and o.juridica.id = " + id_juridica);
            result = (Oposicao) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public PessoaEmpresa pesquisaPessoaFisicaEmpresa(String cpf, String rg) {
        SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
        PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
        List list = new Vector();
        String queryString = "  SELECT emp.id                                                                        "
                + "              FROM pes_pessoa pes                                                                 "
                + "         INNER JOIN pes_fisica fis ON(fis.id_pessoa = pes.id)                                     "
                + "         INNER JOIN pes_pessoa_empresa emp ON(emp.id_fisica = fis.id)                             "
                + "         INNER JOIN pes_juridica jur ON(jur.id = emp.id_juridica)                                 "
                + "              WHERE (                                                                             "
                + "                         pes.ds_documento = '" + cpf + "'                                             "
                + "                 OR (                                                                             "
                + "                      TRANSLATE(UPPER(fis.ds_rg),'./-', '') = TRANSLATE('" + rg + "','./-', '')       "
                + "                      AND fis.ds_rg is not null                                                   "
                + "                      AND trim(fis.ds_rg)<>''                                                     "
                + "                      AND trim(fis.ds_rg)<>'0'                                                    "
                + "                     )                                                                            "
                + "               )                                                                                  "
                + "                AND emp.dt_demissao   is null                                                     "
                + "                AND jur.dt_fechamento is null LIMIT 1                                             ";

        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            list = (Vector) qry.getSingleResult();
            if (list.size() > 0) {
                pessoaEmpresa = (PessoaEmpresa) salvarAcumuladoDB.pesquisaCodigo((Integer) list.get(0), "PessoaEmpresa");
                return pessoaEmpresa;
            }
        } catch (Exception e) {
            return pessoaEmpresa;
        }
        return pessoaEmpresa;
    }

    @Override
    public OposicaoPessoa pesquisaOposicaoPessoa(String cpf, String rg) {
        SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
        OposicaoPessoa oposicaoPessoa = new OposicaoPessoa();
        List vector = new Vector();
        String queryString = "                                                                          "
                + "      SELECT id                                                                        "
                + "        FROM arr_oposicao_pessoa                                                       "
                + "       WHERE ds_cpf = '" + cpf + "'                                                        "
                + "          OR (                                                                         "
                + "                 TRANSLATE(UPPER(ds_rg),'./-', '') = TRANSLATE('" + rg + "','./-', '')     "
                + "                 AND ds_rg is not null                                                 "
                + "                 AND trim(ds_rg)<>''                                                   "
                + "                 AND trim(ds_rg)<>'0'                                                  "
                + "          )                                                                            "
                + "                                                                                       ";
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            vector = (Vector) qry.getSingleResult();
            if (!vector.isEmpty()) {
                oposicaoPessoa = (OposicaoPessoa) salvarAcumuladoDB.pesquisaCodigo((Integer) vector.get(0), "OposicaoPessoa");
                return oposicaoPessoa;
            }
        } catch (Exception e) {
            return oposicaoPessoa;
        }
        return oposicaoPessoa;
    }

    @Override
    public List<Vector> pesquisaPessoaConvencaoGrupoCidade(int id) {
        List<Vector> vetor = new ArrayList();
        String queryString = " SELECT id_convencao, id_grupo_cidade from arr_contribuintes_vw where dt_inativacao is null and id_juridica = " + id;
        try {
            Query qry = getEntityManager().createNativeQuery(queryString);
            vetor = (Vector) qry.getSingleResult();
        } catch (Exception e) {
            return new ArrayList();
        }
        return vetor;
    }

    @Override
    public List<Oposicao> pesquisaOposicao(String descricaoPesquisa, String tipoPesquisa, String comoPesquisa) {
        String filtroString = "";
        if (tipoPesquisa.equals("nome")) {
            filtroString = " WHERE UPPER(opo.oposicaoPessoa.nome) LIKE :descricaoPesquisa ";
        } else if (tipoPesquisa.equals("cpf")) {
            filtroString = " WHERE opo.oposicaoPessoa.cpf LIKE :descricaoPesquisa ";
        } else if (tipoPesquisa.equals("rgs")) {
            filtroString = " WHERE UPPER(opo.oposicaoPessoa.rg) LIKE :descricaoPesquisa ";
        } else if (tipoPesquisa.equals("empresa")) {
            filtroString = " WHERE UPPER(opo.juridica.pessoa.nome) LIKE :descricaoPesquisa ";
        } else if (tipoPesquisa.equals("cnpj")) {
            filtroString = " WHERE opo.juridica.pessoa.documento LIKE :descricaoPesquisa ";
        } else if (tipoPesquisa.equals("observacao")) {
            filtroString = " WHERE UPPER(opo.observacao) LIKE :descricaoPesquisa ";
        } else if (tipoPesquisa.equals("data")) {
            filtroString = " WHERE opo.dtEmissao = '" + DataHoje.livre(DataHoje.converte(descricaoPesquisa), "yyyy-MM-dd") + "'";
        } else if (tipoPesquisa.equals("todos")) {
            DataHoje dh = new DataHoje();
            String dataAntiga = dh.decrementarMeses(1, DataHoje.data());
            filtroString = " WHERE opo.dtEmissao >= '" + dataAntiga + "' ";
        }
        String queryString = " SELECT OPO FROM Oposicao AS OPO " + (filtroString) + " ORDER BY OPO.dtEmissao DESC ";
        try {
            Query qry = getEntityManager().createQuery(queryString);
            if (!descricaoPesquisa.equals("") && !tipoPesquisa.equals("todos") && !tipoPesquisa.equals("data")) {
                if (comoPesquisa.equals("Inicial")) {
                    qry.setParameter("descricaoPesquisa", "" + descricaoPesquisa.toUpperCase() + "%");
                } else if (comoPesquisa.equals("Parcial")) {
                    qry.setParameter("descricaoPesquisa", "%" + descricaoPesquisa.toUpperCase() + "%");
                }
            }
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    @Override
    public boolean validaOposicao(Oposicao oposicao) {
        List result = null;
        try {
            Query query = getEntityManager().createQuery(""
                    + "SELECT opo "
                    + "  FROM Oposicao opo "
                    + " WHERE opo.juridica.id = :juridica "
                    + "   AND opo.oposicaoPessoa.cpf = :cpf "
                    + "   AND opo.convencaoPeriodo.convencao.id = :convencao "
                    + "   AND opo.convencaoPeriodo.grupoCidade.id = :grupoCidade "
                    + "   AND opo.convencaoPeriodo.referenciaInicial = :referenciaInicial "
                    + "   AND opo.convencaoPeriodo.referenciaFinal= :referenciaFinal ");
            query.setParameter("juridica", oposicao.getJuridica().getId());
            query.setParameter("referenciaInicial", oposicao.getConvencaoPeriodo().getReferenciaInicial());
            query.setParameter("referenciaFinal", oposicao.getConvencaoPeriodo().getReferenciaFinal());
            query.setParameter("cpf", oposicao.getOposicaoPessoa().getCpf());
            query.setParameter("convencao", oposicao.getConvencaoPeriodo().getConvencao().getId());
            query.setParameter("grupoCidade", oposicao.getConvencaoPeriodo().getGrupoCidade().getId());
            List list = query.getResultList();
            if (list.size() > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public List filtroRelatorio(int idEmpresa, int idFuncionario, String emissaoInicial, String emissaoFinal, String refInicial, String refFinal, Relatorios r) {
        try {
            List listQuery = new ArrayList();
            String queryEmissao = "";
            String queryReferencia = "";
            String queryString = ""
                    + "    SELECT TO_CHAR(O.dt_emissao, 'DD/MM/YYYY') AS emissao,"
                    + "           T.ds_descricao              AS tipo,           "
                    + "           PES.ds_documento            AS documento,      "
                    + "           UPPER(PES.ds_nome)          AS empresa,        "
                    + "           UPPER(P.ds_nome)            AS funcionario,    "
                    + "           P.ds_sexo                   AS sexo,           "
                    + "           P.ds_cpf                    AS cpf,            "
                    + "           P.ds_rg                     AS rg,             "
                    + "           PC.ds_referencia_inicial    AS ref_i,          "
                    + "           PC.ds_referencia_final      AS ref_f           "
                    + "      FROM arr_oposicao                AS O               "
                    + "INNER JOIN arr_oposicao_pessoa         AS P      ON O.id_oposicao_pessoa = P.id          "
                    + "INNER JOIN arr_convencao_periodo       AS PC     ON PC.id    =   O.id_convencao_periodo  "
                    + "INNER JOIN pes_juridica                AS J      ON J.id     =   O.id_juridica           "
                    + "INNER JOIN pes_pessoa                  AS PES    ON PES.id   =   J.id_pessoa             "
                    + "INNER JOIN pes_tipo_documento          AS T      ON T.id     =   PES.id_tipo_documento   ";

            if (idEmpresa > 0) {
                listQuery.add(" O.id_juridica =  "+idEmpresa);
            }
            if (idFuncionario > 0) {
                listQuery.add(" O.id_oposicao_pessoa = " + idFuncionario);
            }
            if (!emissaoInicial.equals("")) {
                queryEmissao = " O.dt_emissao = '" + emissaoInicial + "'";
            }
            if (!emissaoInicial.equals("") && !emissaoFinal.equals("")) {
                queryEmissao = " O.dt_emissao BETWEEN '" + emissaoInicial + "' AND '" + emissaoFinal + "' ";
            }
            String refInicialS = refInicial.substring(3, 7)+""+refInicial.substring(0, 2);
            String refFinalS = refFinal.substring(3, 7)+""+refFinal.substring(0, 2);
            if (!refInicial.equals("")) {
                queryReferencia = "'"+refInicialS+"'  >= (SUBSTRING(pc.ds_referencia_inicial,4,4)||SUBSTRING(pc.ds_referencia_inicial,0,3)) ";                
            }
            if (!refFinal.equals("") && !refFinal.equals(refInicial)) {
                if(!queryReferencia.isEmpty()) {
                    queryReferencia += " AND "; 
                }
                queryReferencia = "'"+refFinalS+"'  <= (SUBSTRING(pc.ds_referencia_final,4,4)||SUBSTRING(pc.ds_referencia_final,0,3)) ";
            }
            if (!queryEmissao.isEmpty()) {
                listQuery.add(queryEmissao);
            }
            if (!queryReferencia.isEmpty()) {
                listQuery.add(queryReferencia);
            }
            for (int i = 0; i < listQuery.size(); i++) {
                if (i == 0) {
                    queryString += " WHERE " + listQuery.get(i).toString() + " ";
                } else {
                    queryString += " AND " + listQuery.get(i).toString() + " ";
                }
            }
            String orderQuery = " O.dt_emissao ASC, PES.ds_nome ASC, P.ds_nome ASC ";
            if (r != null) {
                if (r.getId() != -1) {
                    if (!r.getQryOrdem().isEmpty()) {
                        orderQuery = r.getQryOrdem();
                    }
                }
            }
            Query query = getEntityManager().createNativeQuery(queryString + " ORDER BY " + orderQuery);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {

        }
        return new ArrayList();
    }
}
