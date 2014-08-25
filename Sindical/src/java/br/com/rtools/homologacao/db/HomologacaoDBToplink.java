package br.com.rtools.homologacao.db;

import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.Cancelamento;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.homologacao.Senha;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.principal.DB;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.SalvarAcumuladoDB;
import br.com.rtools.utilitarios.SalvarAcumuladoDBToplink;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;

public class HomologacaoDBToplink extends DB implements HomologacaoDB {

    @Override
    public Agendamento pesquisaProtocolo(int id) {
        Agendamento result = null;
        try {
            Query qry = getEntityManager().createQuery("select a from Agendamento a where a.id = :pid "
                    + "   and a.agendador is null "
                    + "   and a.horarios is null ");
            qry.setParameter("pid", id);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                result = (Agendamento) qry.getSingleResult();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    public List pesquisaAgendado(int idFilial, Date data) {
        String dataCampo = "";
        if (data != null) {
            dataCampo = "   and a.dtData = :data ";
        }
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + dataCampo
                    + "   and a.status.id = 2"
                    + "   and a.horarios.ativo = true"
                    + "   and a.filial.id = :idFilial"
                    + "   order by a.horarios.hora");
            if (data != null) {
                qry.setParameter("data", data);
            }
            qry.setParameter("idFilial", idFilial);
            if (!qry.getResultList().isEmpty()) {
                List xxx = (qry.getResultList());
                return xxx;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List<Agendamento> pesquisaNaoAtendido(int idFilial, Date dataInicial, Date dataFinal) {
        List<Agendamento> agendamentos = new ArrayList<Agendamento>();
        String dataCampo = "";
        if (dataInicial != null) {
            dataCampo = " AND age.dt_data = '" + DataHoje.converteData(dataInicial) + "'  ";
        }
        if (dataFinal != null && dataInicial != null) {
            dataCampo = " AND age.dt_data BETWEEN '" + DataHoje.converteData(dataInicial) + "' AND '" + DataHoje.converteData(dataFinal) + "'  ";
        }
        try {
            String textoQry = "     SELECT age.id                                      "
                    + "       FROM hom_agendamento age                         "
                    + " INNER JOIN hom_horarios hor ON hor.id = age.id_horario "
                    + "      WHERE age.id_horario IS NOT NULL                  "
                    + dataCampo
                    + "                             "
                    + "        AND age.id_status = 7                           "
                    + "        AND hor.ativo = true                            "
                    + "        AND age.id_filial = " + idFilial
                    + "   ORDER BY hor.ds_hora                                 ";
            Query qry = getEntityManager().createNativeQuery(textoQry);
            if (!qry.getResultList().isEmpty()) {
                SalvarAcumuladoDB dB = new SalvarAcumuladoDBToplink();
                List list = qry.getResultList();
                for (int i = 0; i < list.size(); i++) {
                    agendamentos.add((Agendamento) dB.pesquisaCodigo((Integer) ((List) list.get(i)).get(0), "Agendamento"));
                }
                return agendamentos;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public List pesquisaAgendadoDataMaior(Date data) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + "   and a.dtData >= :data"
                    + "   and a.status.id = 2"
                    + "   and a.horarios.ativo = true"
                    + "   order by a.dtData, a.horarios.hora");
            qry.setParameter("data", data);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public List pesquisaAgendadoPorEmpresa(Date data, int idEmpresa) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + "   and a.dtData = :data"
                    + "   and a.status.id = 2"
                    + "   and a.horarios.ativo = true"
                    + "   and a.pessoaEmpresa.juridica.pessoa.id = :idEmpresa"
                    + "   order by a.horarios.hora");
            qry.setParameter("data", data);
            qry.setParameter("idEmpresa", idEmpresa);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public List pesquisaAgendadoPorEmpresaSemHorario(int id_filial, Date data, int idEmpresa) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where (a.dtData = :data or a.dtData is null)"
                    + "   and a.status.id = 2"
                    + "   and a.filial.id = :idFilial"
                    + "   and a.pessoaEmpresa.juridica.pessoa.id = :idEmpresa"
                    + "   order by a.id");

            qry.setParameter("data", data);
            qry.setParameter("idEmpresa", idEmpresa);
            qry.setParameter("idFilial", id_filial);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public List pesquisaAgendadoPorEmpresaDataMaior(int idEmpresa) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.status.id = 2"
                    + "   and a.pessoaEmpresa.juridica.pessoa.id = :idEmpresa");
            qry.setParameter("idEmpresa", idEmpresa);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaCancelado(int idFilial, Date data, int idUsuario) {
        String dataCampo = "";
        String homologadorCampo = "";
        if (data != null) {
            dataCampo = "   and a.dtData = :data ";
        }
        if (idUsuario != 0) {
            homologadorCampo = "   and a.homologador.id = :usuario ";
        }
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + dataCampo
                    + "   and a.status.id = 3"
                    + "   and a.horarios.ativo = true"
                    + "   and a.filial.id = :idFilial"
                    + homologadorCampo
                    + "   order by a.horarios.hora");
            if (data != null) {
                qry.setParameter("data", data);
            }
            if (idUsuario != 0) {
                qry.setParameter("usuario", idUsuario);
            }
            qry.setParameter("idFilial", idFilial);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    public List pesquisaHomologado(int idFilial, Date data, int idUsuario) {
        String dataCampo = "";
        String homologadorCampo = "";
        if (data != null) {
            dataCampo = "   and a.dtData = :data ";
        }
        if (idUsuario != 0) {
            homologadorCampo = "   and a.homologador.id = :usuario ";
        }
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.horarios is not null"
                    + dataCampo
                    + "   and a.status.id = 4"
                    + "   and a.horarios.ativo = true"
                    + "   and a.filial.id = :idFilial"
                    + homologadorCampo
                    + "   order by a.horarios.hora");
            if (data != null) {
                qry.setParameter("data", data);
            }
            if (idUsuario != 0) {
                qry.setParameter("usuario", idUsuario);
            }
            qry.setParameter("idFilial", idFilial);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public List<Agendamento> pesquisaAgendamento(int idStatus, int idFilial, Date dataInicial, Date dataFinal, int idUsuario, int idPessoaFisica, int idPessoaJuridica) {

        String dataCampo = "";
        String homologadorCampo = "";
        String statusCampo = "";
        String innerPessoaEmpresa = "";
        String pessoaEmpresaCampo = "";
        if (idPessoaFisica > 0 || idPessoaJuridica > 0) {
            innerPessoaEmpresa = " INNER JOIN pes_pessoa_empresa pesemp ON pesemp.id = age.id_pessoa_empresa ";
            if (idPessoaFisica > 0) {
                pessoaEmpresaCampo = " AND pesemp.id_fisica = " + idPessoaFisica;
            } else {
                pessoaEmpresaCampo = " AND pesemp.id_juridica = " + idPessoaJuridica;
            }
        }
        if (dataInicial != null) {
            dataCampo = " AND age.dt_data = '" + DataHoje.converteData(dataInicial) + "'  ";
        }
        if (dataFinal != null && dataInicial != null) {
            dataCampo = " AND age.dt_data BETWEEN '" + DataHoje.converteData(dataInicial) + "' AND '" + DataHoje.converteData(dataFinal) + "'  ";
        }
        if (idUsuario != 0) {
            homologadorCampo = " and age.id_homologador = " + idUsuario + " ";
        }
        if (idStatus > 0) {
            statusCampo = " AND age.id_status = " + idStatus;
        }
        try {
            String textoQry = "     SELECT age.id                                      "
                    + "       FROM hom_agendamento age                         "
                    + " INNER JOIN hom_horarios hor ON hor.id = age.id_horario "
                    + innerPessoaEmpresa
                    + "      WHERE age.id_horario IS NOT NULL                  "
                    + dataCampo
                    + homologadorCampo
                    + statusCampo
                    + pessoaEmpresaCampo
                    + "        AND hor.ativo = true                            "
                    + "        AND age.id_filial = " + idFilial
                    + "      LIMIT 1000                                        ";
            Query qry = getEntityManager().createNativeQuery(textoQry);
            if (!qry.getResultList().isEmpty()) {
                SalvarAcumuladoDB dB = new SalvarAcumuladoDBToplink();
                List list = qry.getResultList();
                String stringIn = "";
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        stringIn = ((Integer) ((List) list.get(i)).get(0)).toString();
                    } else {
                        stringIn += " , " + ((Integer) ((List) list.get(i)).get(0)).toString();
                    }
                }
                String queryString = " SELECT A FROM Agendamento AS A WHERE A.id IN(" + stringIn + ") ORDER BY A.dtData DESC, A.horarios.hora ASC ";
                Query qryListaAgendamento = getEntityManager().createQuery(queryString);
                List listX = qryListaAgendamento.getResultList();
                if (!listX.isEmpty()) {
                    return listX;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public List<Agendamento> pesquisaAgendamentoPorProtocolo(int numeroProtocolo) {
        try {
            Query qry = getEntityManager().createQuery(" SELECT A FROM Agendamento AS A WHERE A.id = :id ");
            qry.setParameter("id", numeroProtocolo);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            return new ArrayList();
        }
        return new ArrayList();
    }

    public List<Agendamento> pesquisaAtendimento(int idFilial, Date dataInicial, Date dataFinal, int idUsuario) {
        List<Agendamento> agendamentos = new ArrayList<Agendamento>();
        String dataCampo = "";
        String homologadorCampo = "";
        if (dataInicial != null) {
            dataCampo = " AND age.dt_data = '" + DataHoje.converteData(dataInicial) + "'  ";
        }
        if (dataFinal != null && dataInicial != null) {
            dataCampo = " AND age.dt_data BETWEEN '" + DataHoje.converteData(dataInicial) + "' AND '" + DataHoje.converteData(dataFinal) + "'  ";
        }
        if (idUsuario != 0) {
            homologadorCampo = " and a.id_homologador = " + idUsuario + " ";
        }
        try {
            String textoQry = "     SELECT age.id                                      "
                    + "       FROM hom_agendamento age                         "
                    + " INNER JOIN hom_horarios hor ON hor.id = age.id_horario "
                    + "      WHERE age.id_horario IS NOT NULL                  "
                    + dataCampo
                    + homologadorCampo
                    + "        AND age.id_status = 5                           "
                    + "        AND hor.ativo = true                            "
                    + "        AND age.id_filial = " + idFilial
                    + "   ORDER BY hor.ds_hora                                 ";
            Query qry = getEntityManager().createNativeQuery(textoQry);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                SalvarAcumuladoDB dB = new SalvarAcumuladoDBToplink();
                for (int i = 0; i < list.size(); i++) {
                    agendamentos.add((Agendamento) dB.find(new Agendamento(), (Integer) ((List) list.get(i)).get(0)));
                }
                return agendamentos;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public List pesquisaTodos(int idFilial) {
        try {
            Query qry = getEntityManager().createQuery(""
                    + "   SELECT A "
                    + "     FROM Agendamento AS A "
                    + "    WHERE A.horarios IS NOT NULL"
                    + "      AND A.horarios.ativo = TRUE"
                    + "      AND A.filial.id = :idFilial"
                    + " ORDER BY A.horarios.hora, "
                    + "          A.dtData DESC"
                    + "    ");
            qry.setParameter("idFilial", idFilial);
            qry.setMaxResults(300);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public int pesquisaQntdDisponivel(int idFilial, Horarios horarios, Date data) {
        try {
            String text = " "
                    + "      SELECT CASE WHEN  "
                    + "      ( SELECT nr_quantidade  "
                    + "          FROM hom_horarios   "
                    + "         WHERE id = " + horarios.getId() + " ) -                                                                                                                                         "
                    + "      ( SELECT func_nullInteger ( "
                    + "          ( SELECT nr_quantidade "
                    + "              FROM hom_cancelar_horario "
                    + "             WHERE id_horarios = " + horarios.getId()
                    + "               AND dt_data = '" + data + "' "
                    + "          )"
                    + "        ) "
                    + "      ) - "
                    + "    ( SELECT func_nullInteger ("
                    + "          ( SELECT cast(count(*) AS int) "
                    + "              FROM hom_agendamento "
                    + "             WHERE id_horario = " + horarios.getId() + " "
                    + "               AND id_filial = " + idFilial + " "
                    + "               AND dt_data = '" + data + "' "
                    + "               AND id_status = 2 "
                    + "          ) "
                    + "     ) "
                    + "  ) IS NULL THEN 0 ELSE "
                    + "      ( SELECT "
                    + "      ( SELECT nr_quantidade  "
                    + "          FROM hom_horarios   "
                    + "         WHERE id = " + horarios.getId() + " ) -                                                                                                                                         "
                    + "      ( SELECT func_nullInteger ( "
                    + "          ( SELECT nr_quantidade "
                    + "              FROM hom_cancelar_horario "
                    + "             WHERE id_horarios = " + horarios.getId()
                    + "               AND dt_data = '" + data + "' "
                    + "          )"
                    + "        ) "
                    + "      ) - "
                    + "    ( SELECT func_nullInteger ("
                    + "          ( SELECT cast(count(*) AS int) "
                    + "              FROM hom_agendamento "
                    + "             WHERE id_horario = " + horarios.getId() + " "
                    + "               AND id_filial = " + idFilial + " "
                    + "               AND dt_data = '" + data + "' "
                    + "               AND id_status = 2 "
                    + "          ) "
                    + "     ) ) ) END; ";
            Query qry = getEntityManager().createNativeQuery(text);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                try {
                    Integer quantidade = Integer.valueOf(String.valueOf(((List) list.get(0)).get(0)));
                    if (quantidade < 0) {
                        return 0;
                    }
                    return quantidade;
                } catch (Exception e) {
                    return 0;
                }
            }
        } catch (Exception e) {
        }
        return -1;
    }

    @Override
    public int pesquisaQuantidadeAgendado(int idFilial, Horarios horarios, Date data) {
        try {
            String text = " SELECT count(*) FROM hom_agendamento WHERE id_horario = " + horarios.getId() + " AND id_filial = " + idFilial + " AND dt_data = '" + data + "' AND id_status = 2 ";
            Query qry = getEntityManager().createNativeQuery(text);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return (Integer.valueOf(String.valueOf((Long) ((List) list.get(0)).get(0))));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List pesquisaTodosHorariosDisponiveis(int idFilial, int idDiaSemana) {
        try {
            Query qry = getEntityManager().createQuery(
                    "   SELECT h "
                    + "     FROM Horarios h "
                    + "    WHERE h.ativo = true "
                    + "      AND h.filial.id = :idFilial "
                    + "      AND h.semana.id = :idDiaSemana "
                    + " ORDER BY h.hora");
            qry.setParameter("idFilial", idFilial);
            qry.setParameter("idDiaSemana", idDiaSemana);
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
    public PessoaEmpresa pesquisaPessoaEmpresaOutra(String doc) {
        PessoaEmpresa result = new PessoaEmpresa();
        try {
            Query qry = getEntityManager().createQuery("select pesEmp"
                    + "  from PessoaEmpresa pesEmp,"
                    + "       Pessoa pes"
                    + " where pesEmp.fisica.pessoa.id = pes.id"
                    + "   and pesEmp.dtDemissao is null"
                    + "   and pes.documento like :Sdoc");
            qry.setParameter("Sdoc", doc);
            if (!qry.getResultList().isEmpty()) {
                result = (PessoaEmpresa) qry.getSingleResult();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    @Override
    public List pesquisaPessoaEmpresaPertencente(String doc) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select pesEmp"
                    + "  from PessoaEmpresa pesEmp,"
                    + "       Pessoa pes"
                    + " where pesEmp.fisica.pessoa.id = pes.id"
                    + //"   and pesEmp.dtDemissao is null" +
                    "   and pes.documento like :Sdoc order by pesEmp.id desc");
            qry.setParameter("Sdoc", doc);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    @Override
    public List pesquisaPessoaDebito(int id_pessoa, String vencimento) {
        try {
            String queryString = "SELECT id FROM fin_movimento WHERE id_pessoa = " + id_pessoa + " AND dt_vencimento < '" + vencimento + "' AND is_ativo = TRUE AND id_baixa IS NULL ORDER BY dt_vencimento";
            Query qry = getEntityManager().createNativeQuery(queryString);
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
    public List pesquisaAgendamentoPorPessoaEmpresa(int idPessoaEmpresa) {
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.pessoaEmpresa.id = " + idPessoaEmpresa);
            if (!qry.getResultList().isEmpty()) {
                return (qry.getResultList());
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return new ArrayList();
    }

    @Override
    public Oposicao pesquisaFisicaOposicao(String cpf, int id_juridica) {
        Oposicao result = null;
        try {
            Query qry = getEntityManager().createQuery("select o "
                    + "  from Oposicao o where o.oposicaoPessoa.cpf = '" + cpf + "' and o.juridica.id = " + id_juridica);
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                result = (Oposicao) qry.getSingleResult();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Oposicao> pesquisaFisicaOposicaoSemEmpresa(String cpf) {
        List<Oposicao> result = new ArrayList();
        try {
            String referencia = DataHoje.livre(new Date(), "yyyyMM");
            Query qry = getEntityManager().createQuery("select o "
                    + "  from Oposicao o where o.oposicaoPessoa.cpf = '" + cpf + "' "
                    + "   and '" + referencia + "' BETWEEN CONCAT( SUBSTRING(o.convencaoPeriodo.referenciaInicial, 4, 8), SUBSTRING(o.convencaoPeriodo.referenciaInicial, 0, 3) ) "
                    + "   and                   CONCAT( SUBSTRING(o.convencaoPeriodo.referenciaFinal, 4, 8), SUBSTRING(o.convencaoPeriodo.referenciaFinal, 0, 3) ) order by o.id desc");
            List list = qry.getResultList();
            if (!list.isEmpty()) {
                return list;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    @Override
    public Oposicao pesquisaFisicaOposicaoAgendamento(String cpf, int id_juridica, String referencia) {
        Oposicao result = null;
        try {
            Query qry = getEntityManager().createQuery("select o "
                    + "  from Oposicao o where o.oposicaoPessoa.cpf = '" + cpf + "' "
                    + "   and o.juridica.id = " + id_juridica
                    + "   and '" + referencia + "' BETWEEN CONCAT( SUBSTRING(o.convencaoPeriodo.referenciaInicial, 4, 8), SUBSTRING(o.convencaoPeriodo.referenciaInicial, 0, 3) ) "
                    + "   and                   CONCAT( SUBSTRING(o.convencaoPeriodo.referenciaFinal, 4, 8), SUBSTRING(o.convencaoPeriodo.referenciaFinal, 0, 3) )");
            if (!qry.getResultList().isEmpty()) {
                result = (Oposicao) qry.getSingleResult();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result;
    }

    @Override
    public Agendamento pesquisaFisicaAgendada(int id_fisica) {
        Agendamento result = null;
        try {
            Query qry = getEntityManager().createQuery("select a "
                    + "  from Agendamento a where a.pessoaEmpresa.fisica.id = " + id_fisica + " and a.dtData >= :data and (a.status.id = 2 or a.status.id = 5)");
            qry.setParameter("data", DataHoje.dataHoje());
            if (!qry.getResultList().isEmpty()) {
                result = (Agendamento) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    @Override
    public int pesquisaUltimaSenha(int id_filial) {
        int result = 0;
        try {
            Query qry = getEntityManager().createQuery("SELECT max(s.senha) FROM Senha s WHERE s.dtData = :data AND s.filial.id = " + id_filial);
            qry.setParameter("data", DataHoje.dataHoje());
            if (!qry.getResultList().isEmpty()) {
                result = (Integer) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    @Override
    public Senha pesquisaSenhaAgendamento(int id_agendamento) {
        Senha result = new Senha();
        try {
            Query qry = getEntityManager().createQuery("SELECT S FROM Senha AS S WHERE S.agendamento.id = " + id_agendamento);
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    @Override
    public Senha pesquisaSenhaAtendimento(int id_filial) {
        Senha result = new Senha();
        try {
            Query qry = getEntityManager().createQuery("select s "
                    + " from Senha s "
                    + " where s.senha = (select min(s2.senha) from Senha s2 where s2.dtData = :data and s2.mesa = 0 and s2.filial.id = " + id_filial + ") "
                    + " and s.dtData = :data and s.filial.id = " + id_filial);
            qry.setParameter("data", DataHoje.dataHoje());
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }

    @Override
    public Senha pesquisaAtendimentoIniciado(int id_usuario, int nr_mesa, int id_filial) {
        Senha result = new Senha();
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT S "
                    + "  FROM Senha AS S "
                    + " WHERE S.mesa = :nr_mesa "
                    + "   AND S.agendamento.homologador.id = :id_usuario "
                    + "   AND S.dtData = :data"
                    + "   AND S.agendamento.status.id = 5 and S.filial.id = :id_filial");
            qry.setParameter("data", DataHoje.dataHoje());
            qry.setParameter("nr_mesa", nr_mesa);
            qry.setParameter("id_usuario", id_usuario);
            qry.setParameter("id_filial", id_filial);
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return result;
    }
    
    @Override
    public Senha pesquisaAtendimentoIniciadoSimples(int id_filial) {
        Senha result = null;
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT S "
                    + "  FROM Senha AS S "
                    + " WHERE S.dtData = :data "
                    + "   AND S.ateMovimento.status.id = 1 "
                    + "   AND S.filial.id = :id_filial"
                    + " ORDER BY S.dtData");
            
            qry.setParameter("data", DataHoje.dataHoje());
            qry.setParameter("id_filial", id_filial);
            qry.setMaxResults(1);
            if (!qry.getResultList().isEmpty()) {
                result = (Senha) qry.getSingleResult();
            }
        } catch (Exception e) {
             //e.printStackTrace();
             return null;
        }
        return result;
    }

    @Override
    public boolean verificaNaoAtendidosSegRegistroAgendamento() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    " SELECT *                                            "
                    + "   FROM seg_registro                                 "
                    + "  WHERE (CURRENT_DATE - 1) = dt_atualiza_homologacao ");
            if (qry.getResultList().isEmpty()) {
                getEntityManager().getTransaction().begin();
                Query qryUpdateAgendamento = getEntityManager().createNativeQuery(
                        "UPDATE hom_agendamento                                                               "
                        + "   SET id_status = 7                                                                 "
                        + " WHERE dt_data > (                                                                   "
                        + "       SELECT dt_atualiza_homologacao                                                "
                        + "         FROM seg_registro                                                           "
                        + "        WHERE id = 1                                                                 "
                        + " )                                                                                   "
                        + "   AND dt_data < CURRENT_DATE                                                        "
                        + "   AND id NOT IN ( SELECT id_agendamento FROM hom_senha WHERE nr_senha IS NOT NULL ) "
                        + "   AND id_status = 2");
                if (qryUpdateAgendamento.executeUpdate() == 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
                Query qryUpdateRegistro = getEntityManager().createNativeQuery(
                        " UPDATE seg_registro                              "
                        + "    SET dt_atualiza_homologacao = CURRENT_DATE - 1");
                if (qryUpdateRegistro.executeUpdate() == 0) {
                    getEntityManager().getTransaction().rollback();
                    return false;
                }
                getEntityManager().getTransaction().commit();
            }
        } catch (Exception e) {
            getEntityManager().getTransaction().rollback();
            return false;
        }
        return true;
    }

    @Override
    public boolean existeHorarioDisponivel(Date date, Horarios horarios) {
        String dateString = DataHoje.converteData(date);
        try {
            Query query = getEntityManager().createNativeQuery(" "
                    + "     SELECT id, nr_quantidade                                                "
                    + "       FROM hom_horarios                                                     "
                    + "      WHERE TEXT(id_filial) || TEXT(id_semana) || ds_hora = TEXT(" + horarios.getFilial().getId() + ") || ("
                    + "            EXTRACT(                                                         "
                    + "                     DOW FROM to_date('" + dateString + "', 'DD-MM-YYYY')) + 1   "
                    + "      ) || '" + horarios.getHora() + "'");
            List list = query.getResultList();
            if (!list.isEmpty()) {
                int idHorario = (Integer) ((List) (list.get(0))).get(0);
                int quantidade = (Integer) ((List) (list.get(0))).get(1);
                list.clear();
                query = getEntityManager().createNativeQuery(""
                        + "     SELECT " + quantidade + " - (count(*) - (           "
                        + "             SELECT COUNT(*)                         "
                        + "               FROM hom_cancelar_horario             "
                        + "              WHERE id_horarios = " + idHorario + "      "
                        + "                AND dt_data='" + dateString + "')        "
                        + "     )                                               "
                        + "       FROM hom_agendamento                          "
                        + "      WHERE id_horario = " + idHorario + "               "
                        + "        AND dt_data = '" + dateString + "'               "
                        + "        AND id_status <> 3;");
                list = query.getResultList();
                if (!list.isEmpty()) {
                    quantidade = Integer.parseInt(((List) (list.get(0))).get(0).toString());
                    if (quantidade > 0) {
                        return true;
                    }
                }
            }
        } catch (Exception exception) {
            return false;
        }
        return false;
    }

    @Override
    public Cancelamento pesquisaCancelamentoPorAgendanto(int idAgendamento) {
        try {
            Query query = getEntityManager().createQuery("SELECT C FROM Cancelamento AS C WHERE C.agendamento.id = :agendamento");
            query.setParameter("agendamento", idAgendamento);
            List list = query.getResultList();
            if (!list.isEmpty()) {
                return (Cancelamento) query.getSingleResult();
            }
        } catch (Exception e) {

        }
        return null;
    }
    
    @Override
    public List<Senha> listaAtendimentoIniciadoSimples(int id_filial) {
        List<Senha> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT S "
                    + "  FROM Senha AS S "
                    + " WHERE S.dtData = :data "
                    + "   AND S.ateMovimento.status.id = 1 "
                    + "   AND S.filial.id = :id_filial"
                    + " ORDER BY S.dtData");
            
            qry.setParameter("data", DataHoje.dataHoje());
            qry.setParameter("id_filial", id_filial);
            if (!qry.getResultList().isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
             //e.printStackTrace();
        }
        return result;
    }
    
    @Override
    public List<Senha> listaAtendimentoIniciadoSimplesUsuario(int id_filial, int id_usuario) {
        List<Senha> result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery(
                    "  SELECT S "
                    + "  FROM Senha AS S "
                    + " WHERE S.dtData = :data "
                    + "   AND S.ateMovimento.status.id = 4 "
                    + "   AND S.filial.id = :id_filial"
                    + "   AND S.ateMovimento.atendente.id = :id_usuario"
                    + " ORDER BY S.dtData");
            
            qry.setParameter("data", DataHoje.dataHoje());
            qry.setParameter("id_filial", id_filial);
            qry.setParameter("id_usuario", id_usuario);
            if (!qry.getResultList().isEmpty()) {
                result = qry.getResultList();
            }
        } catch (Exception e) {
             //e.printStackTrace();
        }
        return result;
    }

}
