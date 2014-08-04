package br.com.rtools.pessoa.db;

import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.pessoa.Fisica;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

public interface FisicaDB {

    public boolean insert(Fisica fisica);

    public boolean update(Fisica fisica);

    public boolean delete(Fisica fisica);

    public EntityManager getEntityManager();

    public Fisica pesquisaCodigo(int id);

    public List pesquisaTodos();

    public List pesquisaPessoa(String desc, String por, String como);

    public List pesquisaPessoaSocio(String desc, String por, String como);

    public List pesquisaPessoaSocioInativo(String desc, String por, String como);

    public Fisica idFisica(Fisica des_fisica);

    public List pesquisaFisicaPorDoc(String doc);
    
    public List pesquisaFisicaPorDoc(String doc, boolean like);

    public List pesquisaFisicaPorDocSemLike(String doc);

    public Fisica pesquisaFisicaPorPessoa(int idPessoa);

    public List pesquisaFisicaPorNomeNascRG(String nome, Date nascimento, String RG);

    public List pesquisaFisicaPorNome(String nome);

    public List pesquisaPessoaSocioID(int id_pessoa);
    
    public List<ServicoPessoa> listaServicoPessoa(int id_pessoa, boolean dependente);
    
    public Fisica pesquisaFisicaPorNomeNascimento(String nome, Date nascimento);
}