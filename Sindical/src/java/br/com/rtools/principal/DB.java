package br.com.rtools.principal;

import br.com.rtools.sistema.Configuracao;
import br.com.rtools.utilitarios.GenericaRequisicao;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.GenericaString;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import oracle.toplink.essentials.config.CacheType;
import oracle.toplink.essentials.config.TopLinkProperties;
import oracle.toplink.essentials.ejb.cmp3.EntityManagerFactoryProvider;
// import oracle.toplink.essentials.ejb.cmp3.EntityManagerFactoryProvider;

public class DB {

    private EntityManager entidade;

    public EntityManager getEntityManager() {
        if (entidade == null) {
            if (!GenericaSessao.exists("conexao")) {
                String cliente = (String) GenericaSessao.getString("sessaoCliente");
                Configuracao configuracao = servidor(cliente.replace("/", ""));
                try {
                    Map properties = new HashMap();
                    properties.put(TopLinkProperties.CACHE_TYPE_DEFAULT, CacheType.SoftWeak);
                    //properties.put(TopLinkProperties.CACHE_TYPE_DEFAULT, CacheType.FULL);
                    properties.put(TopLinkProperties.JDBC_USER, "postgres");
                    properties.put(TopLinkProperties.TRANSACTION_TYPE, "RESOURCE_LOCAL");
                    properties.put(TopLinkProperties.JDBC_DRIVER, "org.postgresql.Driver");
                    properties.put(TopLinkProperties.JDBC_PASSWORD, configuracao.getSenha());
                    properties.put(TopLinkProperties.JDBC_URL, "jdbc:postgresql://" + configuracao.getHost() + ":5432/" + configuracao.getPersistence());
                    EntityManagerFactory emf = Persistence.createEntityManagerFactory(configuracao.getPersistence(), properties);
                    String createTable = GenericaString.converterNullToString(GenericaRequisicao.getParametro("createTable"));
                    if (createTable.equals("criar")) {
                        properties.put(EntityManagerFactoryProvider.DDL_GENERATION, EntityManagerFactoryProvider.CREATE_ONLY);
                    }
                    entidade = emf.createEntityManager();
                    GenericaSessao.put("conexao", emf);
                } catch (Exception e) {
                    return null;
                }
            } else {
                try {
                    EntityManagerFactory emf = (EntityManagerFactory) GenericaSessao.getObject("conexao");
                    entidade = emf.createEntityManager();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return entidade;
    }

    public Configuracao servidor(String cliente) {
        Configuracao configuracao = new Configuracao();
        switch (cliente) {
            case "ComercioAraras":
            case "ComercioSertaozinho":
            case "FederacaoBH":
            case "SinpaaeRP":
            case "VestuarioLimeira":
            case "ComercioItapetininga":
            case "SeaacRP":
            case "MetalRP":
            case "ExtrativaRP":
            case "AlimentacaoArceburgo":
            case "FederacaoExtrativaSP":
            case "ExtrativaSP":
            case "HoteleiroRP":
            case "GaragistaRP":
            case "MetalBatatais":
            case "ServidoresRP":
            case "SeaacFranca":
            case "SincovagaSP":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                configuracao.setHost("192.168.1.102");
                configuracao.setSenha("r#@tools");
                break;
            case "Rtools":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                configuracao.setHost("192.168.1.102");
                configuracao.setSenha("r#@tools");
                break;
            case "ComercioLimeira":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
//                configuracao.setHost("200.204.32.23");
//                configuracao.setSenha("r#@tools");
                configuracao.setHost("localhost");
                configuracao.setSenha("989899");
                break;
            case "NovaBase":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                configuracao.setHost("192.168.1.69");
                configuracao.setSenha("989899");
                break;
            case "ComercioRP":
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence("Sindical");
                configuracao.setHost("200.152.187.241");
                configuracao.setSenha("989899");
                break;
            default:
                if (cliente.equals("Sindical")) {
//                cliente = "c_itapetininga";
//                configuracao.setHost("localhost");
//                configuracao.setSenha("989899");
                    // -- ATUAL
                    cliente = "ComercioRP";
                    configuracao.setHost("192.168.1.102");
                    configuracao.setSenha("r#@tools");
                }   //            } else {
//                if (cliente.equals("ServidoresRP")) {
//                    configuracao.setHost("localhost");
//                    configuracao.setSenha("989899");
//                }
//            }
                configuracao.setCaminhoSistema(cliente);
                configuracao.setPersistence(cliente);
                break;
        }
        return configuracao;
    }
}
