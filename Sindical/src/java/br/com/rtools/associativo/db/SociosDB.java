package br.com.rtools.associativo.db;

import br.com.rtools.associativo.SocioCarteirinha;
import br.com.rtools.associativo.Socios;
import br.com.rtools.pessoa.Pessoa;
import java.util.List;
import javax.persistence.EntityManager;

public interface SociosDB {

    public EntityManager getEntityManager();

    public List pesquisaTodos();

    public Socios pesquisaSocioPorId(int idServicoPessoa);

    public List pesquisaSocios(String desc, String por, String como, String status);

    public List<Socios> listaDependentes(int id_matricula);
    
    public List<Socios> listaDependentesInativos(int id_matricula);

    public Socios pesquisaSocioPorPessoa(int idPessoa);

    public Socios pesquisaSocioDoDependente(int idDependente);

    public Socios pesquisaSocioDoDependente(Pessoa pessoa);

    public List pesquisaSociosAtivos();

    public List pesquisaSociosInativos();

    public Socios pesquisaSocioPorPessoaAtivo(int idPessoa);

    public List pesquisaSocioPorPessoaInativo(int idPessoa);

    public List pesquisaDependentesOrdenado(int idPessoaSocio);

    public float descontoSocioEve(int idPessoa, int idServico);

    public List<SocioCarteirinha> pesquisaCarteirinhasPorPessoa(int id_pessoa, int id_modelo);

    public List pesquisaMotivoInativacao();
    
    public boolean socioDebito(int idPessoa);
}