package br.com.rtools.pessoa.db;

import br.com.rtools.arrecadacao.CnaeConvencao;
import br.com.rtools.arrecadacao.MotivoInativacao;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.PessoaEndereco;
import java.util.List;
import javax.persistence.EntityManager;

public interface JuridicaDB {

//    public boolean insert(Juridica juridica);

//    public boolean update(Juridica Juridica);

//    public boolean delete(Juridica Juridica);

    public EntityManager getEntityManager();

    public Juridica pesquisaCodigo(int id);

//    public List pesquisaTodos();

    public List pesquisaPessoa(String desc, String por, String como);

    public PessoaEndereco pesquisarPessoaEnderecoJuridica(int id);

    public CnaeConvencao pesquisaCnaeParaContribuicao(int id);

    public List listaMotivoInativacao();

    public MotivoInativacao pesquisaCodigoMotivoInativacao(int id);

    public Juridica pesquisaJuridicaPorPessoa(int id);

    public List pesquisaJuridicaPorDoc(String doc);

    public List pesquisaPesEndEmpresaComContabil(int idJurCon);

    public List pesquisaJuridicaComEmail();

    public List<Juridica> pesquisaJuridicaParaRetorno(String documento);

    public int quantidadeEmpresas(int idContabilidade);

    public List listaJuridicaContribuinte(int id_juridica);

    public List listaContabilidadePertencente(int id_juridica);

    public List listaJuridicaContribuinteID();

    public List pesquisaContabilidade();

    public int[] listaInadimplencia(int id_juridica);
}