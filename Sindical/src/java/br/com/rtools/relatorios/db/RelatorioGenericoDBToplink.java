package br.com.rtools.relatorios.db;

import br.com.rtools.principal.DB;
import br.com.rtools.relatorios.Relatorios;
import br.com.rtools.seguranca.Rotina;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

public class RelatorioGenericoDBToplink extends DB implements RelatorioGenericoDB {

    @Override
    public List pesquisaTipoRelatorio(int idRotina) {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select rel "
                    + "  from Relatorios rel"
                    + " where rel.rotina.id = :idRotina"
                    + " order by rel.nome");
            qry.setParameter("idRotina", idRotina);
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public List pesquisaCidadesRelatorio() {
        List result = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select pes.endereco.cidade "
                    + "  from PessoaEndereco pes"
                    + " where pes.tipoEndereco.id = 2"
                    + " group by pes.endereco.cidade"
                    + " order by pes.endereco.cidade.cidade");
            result = qry.getResultList();
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public Relatorios pesquisaRelatorios(int idRelatorio) {
        Relatorios result = null;
        try {
            Query qry = getEntityManager().createQuery("select rel "
                    + "  from Relatorios rel"
                    + " where rel.id = :idRelatorio");
            qry.setParameter("idRelatorio", idRelatorio);
            result = (Relatorios) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public Relatorios pesquisaRelatoriosPorJasper(String dsJasper) {
        Relatorios result = null;
        try {
            Query qry = getEntityManager().createQuery("select rel "
                    + "  from Relatorios rel"
                    + " where rel.jasper = :jasper");
            qry.setParameter("jasper", dsJasper);
            result = (Relatorios) qry.getSingleResult();
        } catch (Exception e) {
        }
        return result;
    }

    @Override
    public List<Rotina> pesquisaRotina() {
        List<Rotina> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select rot from Rotina rot where rot.rotina like 'RELATÓRIO%'");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }

    @Override
    public List<Relatorios> pesquisaTodosRelatorios() {
        List<Relatorios> lista = new ArrayList();
        try {
            Query qry = getEntityManager().createQuery("select r from Relatorios r");
            lista = qry.getResultList();
        } catch (Exception e) {
        }
        return lista;
    }
}