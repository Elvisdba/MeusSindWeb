package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.GrupoCidades;
import br.com.rtools.associativo.*;
import br.com.rtools.associativo.dao.SocioCarteirinhaDao;
import br.com.rtools.associativo.dao.SociosDao;
import br.com.rtools.associativo.db.*;
import br.com.rtools.endereco.Cidade;
import br.com.rtools.financeiro.FTipoDocumento;
import br.com.rtools.financeiro.ServicoPessoa;
import br.com.rtools.financeiro.db.*;
import br.com.rtools.logSistema.NovoLog;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaComplemento;
import br.com.rtools.pessoa.PessoaEmpresa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.TipoDocumento;
import br.com.rtools.pessoa.beans.FisicaBean;
import br.com.rtools.pessoa.db.*;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import static br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean.getCliente;
import br.com.rtools.sistema.ConfiguracaoUpload;
import br.com.rtools.utilitarios.*;
import br.com.rtools.utilitarios.db.FunctionsDBTopLink;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class SociosBean implements Serializable {

    private ServicoPessoa servicoPessoa;
    private ServicoCategoria servicoCategoria;
    private Socios socios;
    private SocioCarteirinha socCarteirinha;
    private MatriculaSocios matriculaSocios;
    private PessoaEmpresa pessoaEmpresa;
    private Fisica dependente;
    private boolean chkContaCobranca;
    private List<SelectItem> listaTipoDocumento;
    private List<SelectItem> listaServicos;
    //private List<SelectItem> listaParentesco;
    private List<DataObject> listaDependentes;
    private List<DataObject> listaDependentesInativos;
    private final List<SelectItem> listaMotivoInativacao;
    private int idTipoDocumento;
    private int idServico;
    private int idGrupoCategoria;
    private int idCategoria;
    private int idIndexCombo;
    private Integer idInativacao;
    private boolean renderServicos;
    private boolean fotoTemp;
    private boolean temFoto;
    private boolean desabilitaImpressao;
    private boolean imprimirVerso;
    private String msgConfirma;
    private String lblSocio;
    private String lblSocioPergunta;
    private String tipoDestinario;
    private String dataInativacao;
    private String dataReativacao;
    private String statusSocio;
// -- 
    private List<SelectItem> listaGrupoCategoria;
    private List<SelectItem> listaCategoria;
    private List<Fisica> listaFisica;
    private Fisica fisicaPesquisa;
    private Pessoa pessoaPesquisa;
    private List<SelectItem> listaSelectFisica;

    private Fisica novoDependente;
    private int index_dependente;
    private final String[] imagensTipo;
    private List<Socios> listaSocioInativo;

    private Part filePart;
    private String fotoTempPerfil;
    private Usuario usuario;

    private boolean modelVisible;

    private final Registro registro;

    private String novaValidadeCartao;
    private DescontoSocial descontoSocial;
    private List<SelectItem> listaDescontoSocial;
    private DescontoSocial novoDescontoSocial;
    private String valorServico;
    private String novoValorServico;
    private String alteraValorServico;
    private Integer index_desconto;

    public SociosBean() {
        servicoPessoa = new ServicoPessoa();
        servicoCategoria = new ServicoCategoria();
        socios = new Socios();
        socCarteirinha = new SocioCarteirinha();
        matriculaSocios = new MatriculaSocios();
        pessoaEmpresa = new PessoaEmpresa();
        dependente = new Fisica();
        chkContaCobranca = false;
        listaTipoDocumento = new ArrayList();
        listaServicos = new ArrayList();
        listaDependentes = new ArrayList();
        listaDependentesInativos = new ArrayList();
        idTipoDocumento = 0;
        idServico = 0;
        idGrupoCategoria = 0;
        idCategoria = 0;
        idIndexCombo = -1;
        idInativacao = 0;
        renderServicos = true;
        fotoTemp = false;
        temFoto = false;
        desabilitaImpressao = true;
        imprimirVerso = false;
        dataInativacao = DataHoje.data();
        dataReativacao = DataHoje.data();
        msgConfirma = "";
        lblSocio = "";
        lblSocioPergunta = "";
        tipoDestinario = "socio";
        statusSocio = "NOVO";
        listaMotivoInativacao = new ArrayList();

        registro = (Registro) new Dao().find(new Registro(), 1);
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
        listaDescontoSocial = new ArrayList();
        novoDescontoSocial = new DescontoSocial();

        listaGrupoCategoria = new ArrayList();
        listaCategoria = new ArrayList();
        listaFisica = new ArrayList();
        fisicaPesquisa = new Fisica();
        pessoaPesquisa = new Pessoa();
        listaSelectFisica = new ArrayList();
        novoDependente = new Fisica();
        index_dependente = 0;
        imagensTipo = new String[]{"jpg", "jpeg", "png", "gif"};
        listaSocioInativo = new ArrayList();
        fotoTempPerfil = "";
        usuario = new Usuario();
        modelVisible = false;
        novaValidadeCartao = "";

        loadGrupoCategoria();
        loadCategoria();
        loadTipoDocumento();
        // CARREGAR OS SERVICOS APENAS QUANDO TER UMA PESSOA NA SESSÃO
        //loadServicos();

        valorServico = "0,00";
        novoValorServico = "0,00";
        alteraValorServico = "0,00";
        index_desconto = 0;
        matriculaSocios.setEmissao(DataHoje.data());
    }

    public void loadPessoaComplemento(Integer id_pessoa) {
        PessoaDB db = new PessoaDBToplink();
        PessoaComplemento pc = db.pesquisaPessoaComplementoPorPessoa(id_pessoa);

        if (pc.getId() == -1) {
            servicoPessoa.setNrDiaVencimento(getRegistro().getFinDiaVencimentoCobranca());
        } else {
            servicoPessoa.setNrDiaVencimento(pc.getNrDiaVencimento());
        }
    }

    public void loadSocio(Pessoa p, boolean reativar) {
        loadSocio(p, reativar, null);
    }

    public void loadSocio(Pessoa p, boolean reativar, Integer tcase) {
        SociosDB db = new SociosDBToplink();
        SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();
        Socios socio_pessoa = db.pesquisaSocioPorPessoa(p.getId());
        Dao dao = new Dao();
        // SE REATIVAR == FALSE NÃO CARREGAR SOCIO

        pessoaEmpresa = (PessoaEmpresa) GenericaSessao.getObject("pessoaEmpresaPesquisa");

        if (reativar == false) {
            descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
            servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
            servicoPessoa.setPessoa(p);
            // CARREGAR OS SERVICOS APENAS QUANDO TER UMA PESSOA NA SESSÃO
            loadServicos();
            loadPessoaComplemento(servicoPessoa.getPessoa().getId());
            return;
        } else {
            socios = socio_pessoa;

        }

        if (socios.getMatriculaSocios().getTitular().getId() != servicoPessoa.getPessoa().getId()) {
            if (socios.getServicoPessoa().isAtivo() || reativar) {
                socios = db.pesquisaSocioPorPessoa(socios.getMatriculaSocios().getTitular().getId());
            } else {
                socios.setMatriculaSocios(new MatriculaSocios());
                Pessoa ps = socios.getServicoPessoa().getPessoa();
                ServicoPessoa sp = new ServicoPessoa();
                sp.setPessoa(ps);
                socios.setId(-1);
                socios.setNrViaCarteirinha(1);
                socios.setParentesco((Parentesco) dao.find(new Parentesco(), 1));
                sp.setDescontoSocial((DescontoSocial) dao.find(new DescontoSocial(), 1));
                socios.setServicoPessoa(sp);
                servicoPessoa.setEmissao(DataHoje.data());
                socios.getMatriculaSocios().setEmissao(DataHoje.data());
            }
        }

        servicoPessoa = socios.getServicoPessoa();
        descontoSocial = servicoPessoa.getDescontoSocial();

        // CARREGAR OS SERVICOS APENAS QUANDO TER UMA PESSOA NA SESSÃO
        loadServicos();
        loadPessoaComplemento(servicoPessoa.getPessoa().getId());

        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);
        List<SocioCarteirinha> listsc;

        if (modeloc != null) {
            listsc = db.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());
            if (!listsc.isEmpty()) {
                socCarteirinha = listsc.get(0);
            } else {
                GenericaMensagem.warn("Ateção", "Sócio sem modelo de Carteirinha!");
            }
        }

        matriculaSocios = socios.getMatriculaSocios();

        //GrupoCategoria gpCat = dbca.pesquisaGrupoPorCategoria(socios.getMatriculaSocios().getCategoria().getId());
        loadGrupoCategoria();
        for (int i = 0; i < listaGrupoCategoria.size(); i++) {
            if (Integer.parseInt(listaGrupoCategoria.get(i).getDescription()) == socios.getMatriculaSocios().getCategoria().getGrupoCategoria().getId()) {
                //  if (Integer.parseInt((String) getListaGrupoCategoria().get(i).getDescription()) == gpCat.getId()) {
                idGrupoCategoria = i;
                break;
            }
        }

        loadCategoria();
        //int qntCategoria = getListaCategoria().size();
        for (int i = 0; i < listaCategoria.size(); i++) {
            if (Integer.parseInt(listaCategoria.get(i).getDescription()) == socios.getMatriculaSocios().getCategoria().getId()) {
                idCategoria = i;
                break;
            }
        }

        for (int i = 0; i < getListaTipoDocumento().size(); i++) {
            if (Integer.parseInt((String) listaTipoDocumento.get(i).getDescription()) == servicoPessoa.getTipoDocumento().getId()) {
                idTipoDocumento = i;
                break;
            }
        }

        listaDescontoSocial.clear();
        getListaDescontoSocial();
        if (servicoPessoa.getDescontoSocial() != null) {
            for (int i = 0; i < listaDescontoSocial.size(); i++) {
                if (Integer.parseInt(listaDescontoSocial.get(i).getDescription()) == servicoPessoa.getDescontoSocial().getId()) {
                    index_desconto = i;
                    break;
                }
            }
        }

        loadServicos();

        atualizarListaDependenteAtivo();
        atualizarListaDependenteInativo();
    }

    public void loadGrupoCategoria() {
        listaGrupoCategoria.clear();
        idGrupoCategoria = 0;

        //SalvarAcumuladoDB sadb = new SalvarAcumuladoDBToplink();
        //List<GrupoCategoria> grupoCategorias = (List<GrupoCategoria>) sadb.listaObjeto("GrupoCategoria");
        CategoriaDB db = new CategoriaDBToplink();
        List<GrupoCategoria> grupoCategorias = db.pesquisaGrupoCategoriaOrdenada();

        if (!grupoCategorias.isEmpty()) {
            for (int i = 0; i < grupoCategorias.size(); i++) {
                listaGrupoCategoria.add(new SelectItem(i, grupoCategorias.get(i).getGrupoCategoria(), "" + grupoCategorias.get(i).getId()));
            }
        } else {
            listaGrupoCategoria.add(new SelectItem(0, "Nenhum Grupo Categoria Encontrado", "0"));
        }
    }

    public void loadCategoria() {
        listaCategoria.clear();
        idCategoria = 0;
        if (!listaGrupoCategoria.isEmpty()) {
            CategoriaDB db = new CategoriaDBToplink();
            List<Categoria> select = db.pesquisaCategoriaPorGrupo(Integer.parseInt(listaGrupoCategoria.get(idGrupoCategoria).getDescription()));
            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaCategoria.add(new SelectItem(i, select.get(i).getCategoria(), Integer.toString(select.get(i).getId())));
                }
            } else {
                listaCategoria.add(new SelectItem(0, "Nenhuma Categoria Encontrada", "0"));
            }
        } else {
            listaCategoria.add(new SelectItem(0, "Nenhuma Categoria Encontrada", "0"));
        }
    }

    public void loadTipoDocumento() {
        listaTipoDocumento.clear();
        idTipoDocumento = 0;
        FTipoDocumentoDB db = new FTipoDocumentoDBToplink();
        List<FTipoDocumento> select = new ArrayList();

        if (isChkContaCobranca()) {
            select.add(db.pesquisaCodigo(2));
        } else {
            select = db.pesquisaListaTipoExtrato();
        }

        if (!select.isEmpty()) {
            for (int i = 0; i < select.size(); i++) {
                listaTipoDocumento.add(
                        new SelectItem(i,
                                select.get(i).getDescricao(),
                                Integer.toString(select.get(i).getId())
                        )
                );
            }
        }
    }

    public void loadServicos() {
        listaServicos.clear();
        idServico = 0;
        if (!listaGrupoCategoria.isEmpty() && !listaCategoria.isEmpty()) {
            ServicoCategoriaDB db = new ServicoCategoriaDBToplink();
            int id_categoria = Integer.parseInt(listaCategoria.get(idCategoria).getDescription());

            servicoCategoria = db.pesquisaPorParECat(1, id_categoria);

            if (servicoCategoria != null) {
                listaServicos.add(new SelectItem(0, servicoCategoria.getServicos().getDescricao(),
                        Integer.toString(servicoCategoria.getServicos().getId()))
                );
                valorServico = Moeda.converteR$Float(new FunctionsDBTopLink().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
                calculoValor();
                calculoDesconto();
            } else {
                listaServicos.add(new SelectItem(0, "Nenhum Serviço Encontrado", "0"));
            }
        }
    }

    public void calculoDesconto() {
        String valorx = Moeda.converteR$Float(new FunctionsDBTopLink().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        servicoPessoa.setNrDescontoString(Moeda.percentualDoValor(valorx, valorServico));
    }

    public void calculoValor() {
        String valorx = Moeda.converteR$Float(new FunctionsDBTopLink().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        valorServico = Moeda.valorDoPercentual(valorx, servicoPessoa.getNrDescontoString());
    }

    public void calculoNovoDesconto() {
        String valorx = Moeda.converteR$Float(new FunctionsDBTopLink().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        novoDescontoSocial.setNrDescontoString(Moeda.percentualDoValor(valorx, novoValorServico));
    }

    public void calculoNovoValor() {
        String valorx = Moeda.converteR$Float(new FunctionsDBTopLink().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        novoValorServico = Moeda.valorDoPercentual(valorx, novoDescontoSocial.getNrDescontoString());
    }

    public void calculoAlterarDesconto() {
        String valorx = Moeda.converteR$Float(new FunctionsDBTopLink().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        descontoSocial.setNrDescontoString(Moeda.percentualDoValor(valorx, alteraValorServico));
    }

    public void calculoAlterarValor() {
        String valorx = Moeda.converteR$Float(new FunctionsDBTopLink().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0, servicoCategoria.getCategoria().getId()));
        alteraValorServico = Moeda.valorDoPercentual(valorx, descontoSocial.getNrDescontoString());
    }

    public void alterarDesconto() {
        Dao di = new Dao();

        di.openTransaction();
        if (descontoSocial.getId() == -1) {

        } else {
            if (!di.update(descontoSocial)) {
                di.rollback();
                GenericaMensagem.error("Erro", "Nao foi possivel alterar esse desconto!");
                return;
            }

            servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
            // aqui
            //String valorx = Moeda.converteR$Float( new FunctionsDBTopLink().valorServico(servicoPessoa.getPessoa().getId(), servicoCategoria.getServicos().getId(), DataHoje.dataHoje(), 0) );
            //valorServico = Moeda.valorDoPercentual(valorx, servicoPessoa.getNrDescontoString());
            calculoValor();
            SociosDB db = new SociosDBToplink();

            List<ServicoPessoa> lsp = db.listaServicoPessoaPorDescontoSocial(descontoSocial.getId(), null);

            for (ServicoPessoa sp : lsp) {
                sp.setNrDesconto(descontoSocial.getNrDesconto());
                if (!di.update(sp)) {
                    di.rollback();
                    GenericaMensagem.error("Erro", "Nao foi possivel alterar esse Serviço Pessoa!");
                    return;
                }
            }

            di.commit();

            atualizarListaDependenteAtivo();
            atualizarListaDependenteInativo();
            PF.closeDialog("dlg_alterar_desconto");
        }
        listaDescontoSocial.clear();
        PF.update("formSocios");
    }

    public void salvarDesconto() {
        if (novoDescontoSocial.getDescricao().isEmpty() || novoDescontoSocial.getDescricao().length() < 3) {
            GenericaMensagem.warn("Atenção", "Digite um Nome do Desconto válido!");
            PF.update("form_desconto:i_panel_desconto");
            return;
        }

        Dao di = new Dao();

        di.openTransaction();
        if (novoDescontoSocial.getId() == -1) {
            if (!di.save(novoDescontoSocial)) {
                di.rollback();
                GenericaMensagem.error("Erro", "Não foi possível salvar esse desconto!");
                PF.update("form_desconto:i_panel_desconto");
                return;
            }
            di.commit();
            servicoPessoa.setNrDesconto(novoDescontoSocial.getNrDesconto());
            descontoSocial = novoDescontoSocial;
            PF.closeDialog("dlg_desconto");
        }
        listaDescontoSocial.clear();

        getListaDescontoSocial();

        for (int i = 0; i < listaDescontoSocial.size(); i++) {
            if (Integer.valueOf(listaDescontoSocial.get(i).getDescription()) == descontoSocial.getId()) {
                index_desconto = i;
            }
        }

        PF.update("formSocios:i_panel_servicos");
    }

    public void selecionarDesconto(DescontoSocial ds) {
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), Integer.valueOf(listaDescontoSocial.get(index_desconto).getDescription()));
        servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());

        calculoValor();
        PF.update("formSocios:i_panel_servicos");
    }

    public void clickSalvarDesconto() {
        if (!listaCategoria.isEmpty() && !listaCategoria.get(0).getDescription().equals("0")) {

            novoDescontoSocial = new DescontoSocial();
            novoDescontoSocial.setCategoria((Categoria) new Dao().find(new Categoria(), Integer.valueOf(listaCategoria.get(idCategoria).getDescription())));
            //ds.setNrDesconto(servicoPessoa.getNrDesconto());
            calculoNovoValor();

            PF.update("form_desconto:i_panel_desconto");
            PF.openDialog("dlg_desconto");
        } else {
            GenericaMensagem.error("Atenção", "Lista de Categoria VAZIA!");
        }
    }

    public void clickAlterarDesconto() {
        if (!listaCategoria.isEmpty() && !listaCategoria.get(0).getDescription().equals("0")) {
            descontoSocial = (DescontoSocial) new Dao().find(descontoSocial);
            //        descontoSocial.setCategoria((Categoria) new Dao().find(new Categoria(), Integer.valueOf(listaCategoria.get(idCategoria).getDescription())));
            //        descontoSocial.setNrDesconto(servicoPessoa.getNrDesconto());

            calculoAlterarValor();
            calculoAlterarDesconto();

            PF.update("form_desconto:i_panel_alterar_desconto");
            PF.openDialog("dlg_alterar_desconto");
        } else {
            GenericaMensagem.error("Atenção", "Lista de Categoria VAZIA!");
        }
    }

    public void upload(FileUploadEvent event) {
        String fotoTempCaminho = "foto/" + getUsuario().getId();
        File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/perfil.png"));
        if (f.exists()) {
            boolean delete = f.delete();
        } else {
            fotoTempPerfil = "";
        }
        ConfiguracaoUpload cu = new ConfiguracaoUpload();
        cu.setArquivo(event.getFile().getFileName());
        cu.setDiretorio("temp/foto/" + getUsuario().getId());
        cu.setArquivo("perfil.png");
        cu.setSubstituir(true);
        cu.setRenomear("perfil.png");
        cu.setEvent(event);
        if (Upload.enviar(cu, true)) {
            fotoTempPerfil = "/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/perfil.png";
            //fotoPerfil = "";
        } else {
            fotoTempPerfil = "";
            //fotoPerfil = "";
        }
        RequestContext.getCurrentInstance().update(":formSocios:tab_view:i_panel_dados");

    }

    public String apagarImagem() {
        boolean sucesso = false;
        if (!fotoTempPerfil.equals("")) {
            File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/foto/" + getUsuario().getId() + "/perfil.png"));
            sucesso = f.delete();
        } else {
            if (novoDependente.getId() != -1) {
                File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/" + novoDependente.getPessoa().getId() + ".png"));
                sucesso = f.delete();
            }
        }
        if (sucesso) {
            fotoTempPerfil = "";
            RequestContext.getCurrentInstance().update(":formSocios:tab_view:i_panel_dados");
        }

        return null;
    }

    public void salvarImagem() {
        if (!Diretorio.criar("Imagens/Fotos/")) {
            return;
        }
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/Imagens/Fotos/");
        boolean error = false;
        if (!fotoTempPerfil.equals("")) {
            File des = new File(arquivo + "/" + novoDependente.getPessoa().getId() + ".png");
            if (des.exists()) {
                des.delete();
            }
            File src = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(fotoTempPerfil));
            boolean rename = src.renameTo(des);
            //fotoPerfil = "/Cliente/" + getCliente() + "/Imagens/Fotos/" + novoDependente.getPessoa().getId() + ".png";
            fotoTempPerfil = "";

            if (!rename) {
                error = true;
            }
        }
        if (!error) {
            File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/foto/" + getUsuario().getId()));
            boolean delete = f.delete();
        }
    }

    public String getFotoPerfilDependente() {
        if (!fotoTempPerfil.isEmpty()) {
            return fotoTempPerfil;
        }

        String caminhoTemp = "/Cliente/" + getCliente() + "/Imagens/Fotos/";
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(caminhoTemp);
        for (String imagensTipo1 : imagensTipo) {
            File f = new File(arquivo + "/" + novoDependente.getPessoa().getId() + "." + imagensTipo1);
            if (f.exists()) {
                return caminhoTemp + "/" + novoDependente.getPessoa().getId() + "." + imagensTipo1;
            }
        }

        if (novoDependente.getSexo().equals("M")) {
            return "/Imagens/user_male.png";
        } else {
            return "/Imagens/user_female.png";
        }
    }

    public void capturar(CaptureEvent captureEvent) {
        String fotoTempCaminho = "foto/" + getUsuario().getId();
        if (PhotoCam.oncapture(captureEvent, "perfil", fotoTempCaminho, true)) {
            File f = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/perfil.png"));
            if (f.exists()) {
                fotoTempPerfil = "/Cliente/" + getCliente() + "/temp/" + fotoTempCaminho + "/perfil.png";
            } else {
                fotoTempPerfil = "";
            }
        }
        RequestContext.getCurrentInstance().update(":formSocios:tab_view:i_panel_dados");
        RequestContext.getCurrentInstance().execute("dgl_captura.hide();");
    }

    public String getFotoTipTitular() {
        String caminhoTemp = "/Cliente/" + getCliente() + "/Imagens/Fotos/";
        String arquivo = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(caminhoTemp);
        for (String imagensTipo1 : imagensTipo) {
            File f = new File(arquivo + "/" + servicoPessoa.getPessoa().getId() + "." + imagensTipo1);
            if (f.exists()) {
                return caminhoTemp + "/" + servicoPessoa.getPessoa().getId() + "." + imagensTipo1;
            }
        }

        FisicaDB db = new FisicaDBToplink();
        Fisica fis = db.pesquisaFisicaPorPessoa(servicoPessoa.getPessoa().getId());
        if (fis.getSexo().equals("M")) {
            return "/Imagens/user_male.png";
        } else {
            return "/Imagens/user_female.png";
        }
    }

    public void salvarData() {
        if (servicoPessoa.getId() != -1) {
            Dao dao = new Dao();
            dao.openTransaction();
            if (!dao.update(servicoPessoa)) {
                // ERRO
                dao.rollback();
            } else {
                dao.commit();
            }
        }
    }

    public List<Fisica> listaPesquisaDependente(String query) {
        List<Fisica> result = new ArrayList<Fisica>();
        FisicaDB db = new FisicaDBToplink();
        listaFisica = db.pesquisaFisicaPorNome(query.toUpperCase());
//        for(int i = 0; i < listaFisica.size(); i++) {
        //result.add(new SelectItem(i, listaFisica.get(i).getPessoa().getNome(), String.valueOf(listaFisica.get(i).getId())));
//        }
        return listaFisica;
    }

    public void selectDependente() {
        if (fisicaPesquisa == null) {

        } else {
            novoDependente = fisicaPesquisa;
        }
    }

    public void inativarSocio() {
        if (socios.getId() != -1) {
            SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
            SociosDB db = new SociosDBToplink();
            if (dataInativacao.length() < 10) {
                GenericaMensagem.warn("Erro", "Data de inativação inválida!");
                return;
            }

            if (DataHoje.converteDataParaInteger(dataInativacao) > DataHoje.converteDataParaInteger(DataHoje.data())) {
                GenericaMensagem.warn("Erro", "Data de inativação não pode ser maior que dia de hoje!");
                return;
            }

            SMotivoInativacao smi = (SMotivoInativacao) sv.pesquisaCodigo(Integer.parseInt(listaMotivoInativacao.get(idInativacao).getDescription()), "SMotivoInativacao");
            if (smi.getId() == 6 && (matriculaSocios.getMotivo().isEmpty() || matriculaSocios.getMotivo().length() < 3)) {
                if (matriculaSocios.getMotivo().isEmpty() || matriculaSocios.getMotivo().length() < 3) {
                    GenericaMensagem.error("Atenção", "Digite um Motivo de Inativação válido!");
                    PF.update("formSocios:i_msg_motivo");
                    return;
                }
            }

            sv.abrirTransacao();

            ServicoPessoa sp = (ServicoPessoa) sv.pesquisaCodigo(servicoPessoa.getId(), "ServicoPessoa");
            sp.setAtivo(false);
            List<Socios> listaDps = db.listaDependentes(matriculaSocios.getId());
            if (!sv.alterarObjeto(sp)) {
                GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa!");
                sv.desfazerTransacao();
                return;
            }
            servicoPessoa = sp;
            for (int i = 0; i < listaDps.size(); i++) {
                ServicoPessoa sp2 = (ServicoPessoa) sv.pesquisaCodigo(listaDps.get(i).getServicoPessoa().getId(), "ServicoPessoa");
                sp2.setAtivo(false);
                if (!sv.alterarObjeto(sp2)) {
                    GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa!");
                    sv.desfazerTransacao();
                    return;
                }
                sp2 = new ServicoPessoa();
            }

            matriculaSocios.setMotivoInativacao(smi);
            matriculaSocios.setInativo(dataInativacao);
            if (!sv.alterarObjeto(matriculaSocios)) {
                GenericaMensagem.error("Erro", "Erro ao alterar matrícula");
                sv.desfazerTransacao();
                return;
            }

            GenericaMensagem.info("Concluído", "Sócio inativado com Sucesso!");
            sv.comitarTransacao();
            FisicaDB dbf = new FisicaDBToplink();
            GenericaSessao.put("fisicaBean", new FisicaBean());
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setSocios(socios);
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).editarFisicaParametro(dbf.pesquisaFisicaPorPessoa(socios.getServicoPessoa().getPessoa().getId()));
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).showImagemFisica();
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getListaSocioInativo().clear();
            PF.update("formSocios");
            listaDependentes.clear();
            listaDependentesInativos.clear();
            atualizarListaDependenteAtivo();
            atualizarListaDependenteInativo();

        } else {
            GenericaMensagem.warn("Erro", "Não existe sócio para ser inativado!");
        }
    }

    public void validaMotivoInativacao() {
        SMotivoInativacao smi = (SMotivoInativacao) new Dao().find(new SMotivoInativacao(), Integer.parseInt(listaMotivoInativacao.get(idInativacao).getDescription()));

        if (smi.getId() == 6 && (matriculaSocios.getMotivo().isEmpty() || matriculaSocios.getMotivo().length() < 3)) {
            PF.openDialog("i_dlg_smi");
        } else {
            inativarSocio();
        }
    }

    public String reativarSocio() {
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        SociosDB db = new SociosDBToplink();

        if (db.pesquisaSocioPorPessoaAtivo(socios.getServicoPessoa().getPessoa().getId()).getId() != -1) {
            GenericaMensagem.warn("Erro", "Este sócio já esta cadastrado!");
            return null;
        }

        sv.abrirTransacao();

        ServicoPessoa sp = (ServicoPessoa) sv.find("ServicoPessoa", servicoPessoa.getId());
        if (sp == null) {
            GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa");
            sv.desfazerTransacao();
            return null;
        }
        sp.setAtivo(true);
        if (!sv.alterarObjeto(sp)) {
            GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa");
            sv.desfazerTransacao();
            return null;
        }

        servicoPessoa = sp;
//        List<Socios> listaDps = db.listaDependentesInativos(matriculaSocios.getId());
//        for (int i = 0; i < listaDps.size(); i++) {
//            ServicoPessoa sp2 = (ServicoPessoa) sv.pesquisaCodigo(listaDps.get(i).getServicoPessoa().getId(), "ServicoPessoa");
//            sp2.setAtivo(true);
//            if (!sv.alterarObjeto(sp2)) {
//                GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa do Dependente");
//                sv.desfazerTransacao();
//                return null;
//            }
//            sp2 = new ServicoPessoa();
//        }
        matriculaSocios.setMotivoInativacao(null);
        matriculaSocios.setDtInativo(null);
        if (!sv.alterarObjeto(matriculaSocios)) {
            GenericaMensagem.error("Erro", "Erro ao ativar matrícula");
            sv.desfazerTransacao();
            return null;
        }
        GenericaMensagem.info("Concluído", "Sócio ativado com Sucesso!");
        dataInativacao = DataHoje.data();

        sv.comitarTransacao();
        FisicaDB dbf = new FisicaDBToplink();
        GenericaSessao.put("fisicaBean", new FisicaBean());
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).editarFisicaParametro(dbf.pesquisaFisicaPorPessoa(socios.getServicoPessoa().getPessoa().getId()));
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setSocios(socios);
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).showImagemFisica();
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getListaSocioInativo().clear();
        PF.update("formSocios");
        listaDependentes.clear();
        listaDependentesInativos.clear();
        atualizarListaDependenteAtivo();
        atualizarListaDependenteInativo();
        return null;
    }

    public String salvar() {
        if (!validaSalvar()) {
            return null;
        }
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        MatriculaSocios msMemoria = new MatriculaSocios();
        sv.abrirTransacao();
        try {
            servicoPessoa.setTipoDocumento((FTipoDocumento) sv.pesquisaCodigo(Integer.parseInt(getListaTipoDocumento().get(idTipoDocumento).getDescription()), "FTipoDocumento"));
        } catch (NumberFormatException e) {
            servicoPessoa.setTipoDocumento((FTipoDocumento) sv.pesquisaCodigo(Integer.parseInt(getListaTipoDocumento().get(0).getDescription()), "FTipoDocumento"));
        }
        // NOVO REGISTRO -----------------------
        if (servicoPessoa.getId() == -1) {
            servicoPessoa.setAtivo(true);
            servicoPessoa.setCobranca(servicoPessoa.getPessoa());

            // set desconto 
            if (descontoSocial.getId() != 1) {
                servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
            }

            servicoPessoa.setDescontoSocial(descontoSocial);

            if (!sv.inserirObjeto(servicoPessoa)) {
                GenericaMensagem.error("Erro", "Erro ao salvar Serviço Pessoa!");
                return null;
            }
            matriculaSocios.setMotivoInativacao(null);
        } else {
            // ATUALIZA REGISTRO -------------------

            // set desconto
            if (descontoSocial.getId() != 1) {
                servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());
            }

            servicoPessoa.setDescontoSocial(descontoSocial);

            if (!sv.alterarObjeto(servicoPessoa)) {
                GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa!");
                return null;
            }
        }
        GrupoCategoria grupoCategoria = (GrupoCategoria) sv.pesquisaCodigo(Integer.parseInt(getListaGrupoCategoria().get(idGrupoCategoria).getDescription()), "GrupoCategoria");
//        if(matriculaSocios.getNrMatricula() <= grupoCategoria.getNrProximaMatricula()) {
//            msgConfirma = "Número de matrícula deve ser menor ou igual!";
//            return null;
//        }
        MatriculaSociosDB dbMat = new MatriculaSociosDBToplink();
        matriculaSocios.setCategoria(servicoCategoria.getCategoria());
        matriculaSocios.setTitular(servicoPessoa.getPessoa());
        if (matriculaSocios.getNrMatricula() <= 0) {
            // MATRICULA MENOR QUE ZERO 
            matriculaSocios.setNrMatricula(grupoCategoria.getNrProximaMatricula());
            grupoCategoria.setNrProximaMatricula(matriculaSocios.getNrMatricula() + 1);

        } else if (matriculaSocios.getNrMatricula() > grupoCategoria.getNrProximaMatricula()) {
            // MATRICULA MAIOR QUE A PROXIMA DA CATEGORIA 
            matriculaSocios.setNrMatricula(grupoCategoria.getNrProximaMatricula());
            grupoCategoria.setNrProximaMatricula(matriculaSocios.getNrMatricula() + 1);

        } else if (matriculaSocios.getNrMatricula() < grupoCategoria.getNrProximaMatricula()
                && // MATRICULA MENOR QUE A PROXIMA DA CATEGORIA E NÃO EXISTIR UMA IGUAL 
                dbMat.pesquisaPorNrMatricula(matriculaSocios.getNrMatricula(), servicoCategoria.getCategoria().getId()) != null) {
            matriculaSocios.setNrMatricula(grupoCategoria.getNrProximaMatricula());
            grupoCategoria.setNrProximaMatricula(matriculaSocios.getNrMatricula() + 1);

        } else if (matriculaSocios.getNrMatricula() < grupoCategoria.getNrProximaMatricula()
                && dbMat.pesquisaPorNrMatricula(matriculaSocios.getNrMatricula(), servicoCategoria.getCategoria().getId()) == null) {
            // MATRICULA MENOR QUE A PROXIMA DA CATEGORIA E NÃO EXISTIR
            //////////////////////////////////// NAO FAZ NADA
        }
        if (matriculaSocios.getId() != -1) {
            msMemoria = (MatriculaSocios) sv.pesquisaObjeto(matriculaSocios.getId(), "MatriculaSocios");
        }
        if (msMemoria.getNrMatricula() != matriculaSocios.getNrMatricula() || matriculaSocios.getNrMatricula() == 0) {
            List list = dbMat.listaMatriculaPorGrupoNrMatricula(matriculaSocios.getCategoria().getGrupoCategoria().getId(), matriculaSocios.getNrMatricula());
            if (!list.isEmpty()) {
                GenericaMensagem.error("Erro", "Matrícula já existe!");
                sv.desfazerTransacao();
                return null;
            }
        }
        if (matriculaSocios.getId() == -1) {
            if (!sv.inserirObjeto(matriculaSocios)) {
                GenericaMensagem.error("Erro", "Erro ao Salvar Matrícula!");
                sv.desfazerTransacao();
                return null;
            }
        } else {
            if (!sv.alterarObjeto(matriculaSocios)) {
                GenericaMensagem.error("Erro", "Erro ao Atualizar Matrícula!");
                sv.desfazerTransacao();
                return null;
            }
        }

        if (!sv.alterarObjeto(grupoCategoria)) {
            GenericaMensagem.error("Erro", "Ao atualizar Grupo Categoria!");
            sv.desfazerTransacao();
            return null;
        }

        socios.setMatriculaSocios(matriculaSocios);
        socios.setParentesco((Parentesco) sv.pesquisaCodigo(1, "Parentesco"));
        socios.setServicoPessoa(servicoPessoa);
        socios.setNrViaCarteirinha(1);

        DataHoje dh = new DataHoje();
        SociosDB db = new SociosDBToplink();
        SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();
        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()), 170);

        if (modeloc == null) {
            GenericaMensagem.error("Erro", "Não existe modelo de carteirinha para esta categoria " + listaCategoria.get(idCategoria).getLabel() + " do sócio!");
            sv.desfazerTransacao();
            return null;
        }

        List<SocioCarteirinha> list_carteirinha_socio = db.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());
        // VERIFICA SE SÓCIO TEM CARTEIRINHA -- SE TIVER NÃO ADICIONAR --
        if (list_carteirinha_socio.isEmpty()) {
            //Date validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(grupoCategoria.getNrValidadeMesCartao(), DataHoje.data()));
            String validadeCarteirinha = dh.incrementarMeses(grupoCategoria.getNrValidadeMesCartao(), DataHoje.data());

            SocioCarteirinha sc = new SocioCarteirinha(-1, "", servicoPessoa.getPessoa(), modeloc, servicoPessoa.getPessoa().getId(), 1, validadeCarteirinha);
            if (!sv.inserirObjeto(sc)) {
                GenericaMensagem.error("Erro", "Não foi possivel salvar Socio Carteirinha!");
                sv.desfazerTransacao();
                return null;
            }

            socCarteirinha = sc;
        }

        if (socios.getId() == -1) {
            if (!sv.inserirObjeto(socios)) {
                GenericaMensagem.error("Erro", "Erro ao Salvar Sócio!");
                sv.desfazerTransacao();
                return null;
            }
            GenericaMensagem.info("Sucesso", "Pessoa Associada!");
        } else {
            if (!sv.alterarObjeto(socios)) {
                GenericaMensagem.error("Erro", "Erro ao Atualizar Sócio!");
                sv.desfazerTransacao();
                return null;
            }
            GenericaMensagem.info("Sucesso", "Cadastro Atualizado!");
            ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setSocios(socios);
        }

        /* 
         // SE TIVER UMA LISTA COM DEPENDENTES
         *
         *
         */
        if (!listaDependentes.isEmpty()) {

            SocioCarteirinhaDao socioCarteirinhaDao = new SocioCarteirinhaDao();

            ServicoCategoriaDB dbSCat = new ServicoCategoriaDBToplink();
            SocioCarteirinha sc = new SocioCarteirinha();
            ParentescoDB dbPar = new ParentescoDBToplink();
            boolean message = true;
            for (int i = 0; i < listaDependentes.size(); i++) {
                if (((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getId() != -1) {
                    Socios socioDependente = db.pesquisaSocioPorPessoaAtivo(((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getPessoa().getId());
                    Fisica fisicaDependente = ((Fisica) listaDependentes.get(i).getArgumento0());

                    List<SelectItem> lista_si = (ArrayList<SelectItem>) listaDependentes.get(i).getArgumento6();
                    Parentesco parentesco = dbPar.pesquisaCodigo(Integer.valueOf(lista_si.get(Integer.valueOf(listaDependentes.get(i).getArgumento1().toString())).getDescription()));

                    ServicoCategoria servicoCategoriaDep = dbSCat.pesquisaPorParECat(parentesco.getId(), servicoCategoria.getCategoria().getId());

                    String ref_dependente = (listaDependentes.get(i).getArgumento4() == null) ? "" : listaDependentes.get(i).getArgumento4().toString();

                    if (servicoCategoriaDep == null) {
                        GenericaMensagem.warn("Erro", "Erro para Serviço Categoria: " + ((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getPessoa().getNome());
                        sv.desfazerTransacao();
                        return null;
                    }

                    modeloc = dbc.pesquisaModeloCarteirinha(matriculaSocios.getCategoria().getId(), 170);

                    List<SocioCarteirinha> list_carteirinha_dep = new ArrayList();
                    if (modeloc == null) {
                        list_carteirinha_dep = db.pesquisaCarteirinhasPorPessoa(socioDependente.getServicoPessoa().getPessoa().getId(), modeloc.getId());
                        //GenericaMensagem.warn("Atenção", "Sócio sem modelo de Carteirinha!");
                        //sv.desfazerTransacao();
                        //return null;
                    }
                    // VERIFICA SE SÓCIO DEPENDENTE TEM CARTEIRINHA -- SE TIVER NÃO ADICIONAR --

                    if (list_carteirinha_dep.isEmpty() && registro.isCarteirinhaDependente()) {

                        if (modeloc == null) {
                            GenericaMensagem.error("Erro", "Não existe modelo de carteirinha para categoria " + servicoCategoriaDep.getCategoria().getCategoria() + " do dependente " + socioDependente.getServicoPessoa().getPessoa().getNome());
                            sv.desfazerTransacao();
                            return null;
                        }
                        String validadeCarteirinha = dh.incrementarMeses(grupoCategoria.getNrValidadeMesCartao(), DataHoje.data());
                        if (GenericaSessao.getString("sessaoCliente").equals("ServidoresRP")) {
                            Integer verificaHoje = DataHoje.converteDataParaInteger(DataHoje.data());
                            Integer verificaFuturo = DataHoje.converteDataParaInteger("30/06/2016");
                            if (verificaFuturo < verificaHoje) {
                                GenericaMensagem.warn("Atenção", "Entrar em contato com nosso suporte técnico!");
                                return null;
                            }
                            if (!(parentesco.getParentesco().toUpperCase()).equals("TITULAR")
                                    && !(parentesco.getParentesco().toUpperCase()).equals("ESPOSA")
                                    && !(parentesco.getParentesco().toUpperCase()).equals("ESPOSO")
                                    && !(parentesco.getParentesco().toUpperCase()).equals("SOGRA")
                                    && !(parentesco.getParentesco().toUpperCase()).equals("SOGRO")
                                    && !(parentesco.getParentesco().toUpperCase()).equals("PAI")
                                    && !(parentesco.getParentesco().toUpperCase()).equals("MÃE")) {
                                validadeCarteirinha = ("30/06/2016");
                                if (message) {
                                    GenericaMensagem.warn("Atenção", "Esta data de validade é provisória e este critério será mantido até o dia 30/06/2016, conforme solicitação do cliente, exceto para os graus de parentesco titular, esposa, sogra e paes.");
                                    message = false;

                                }
                            }
                        }

                        sc = socioCarteirinhaDao.pesquisaPorPessoaModelo(fisicaDependente.getPessoa().getId(), modeloc.getId());
                        if (sc == null) {
                            sc = new SocioCarteirinha(-1, "", fisicaDependente.getPessoa(), modeloc, fisicaDependente.getPessoa().getId(), 1, validadeCarteirinha);
                        }
                        if (sc.getId() == -1) {
                            if (!sv.inserirObjeto(sc)) {
                                GenericaMensagem.error("Erro", "Não foi possivel salvar Sócio Carteirinha Dependente!");
                                sv.desfazerTransacao();
                                return null;
                            }
                        } else {
                            sc.setValidadeCarteirinha(validadeCarteirinha);
                            if (!sv.alterarObjeto(sc)) {
                                GenericaMensagem.error("Erro", "Não foi possivel atualizar Sócio Carteirinha Dependente!");
                                sv.desfazerTransacao();
                                return null;
                            }
                        }

                        listaDependentes.get(i).setArgumento3(validadeCarteirinha);
                    }

                    if (socioDependente.getId() == -1) {
                        ServicoPessoa servicoPessoaDependente = new ServicoPessoa(-1,
                                servicoPessoa.getEmissao(),
                                fisicaDependente.getPessoa(),
                                servicoPessoa.isDescontoFolha(),
                                servicoCategoriaDep.getServicos(),
                                Moeda.substituiVirgulaFloat(listaDependentes.get(i).getArgumento5().toString()),
                                servicoPessoa.getReferenciaVigoracao(),
                                ref_dependente,
                                servicoPessoa.getNrDiaVencimento(),
                                servicoPessoa.getTipoDocumento(),
                                servicoPessoa.getCobranca(),
                                servicoPessoa.isAtivo(),
                                servicoPessoa.isBanco(),
                                0,
                                servicoPessoa.getDescontoSocial(),
                                null
                        );
                        if (!sv.inserirObjeto(servicoPessoaDependente)) {
                            GenericaMensagem.warn("Erro", "Erro ao salvar Serviço Pessoa: " + ((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getPessoa().getNome());
                            sv.desfazerTransacao();
                            return null;
                        }
                        socioDependente = new Socios(-1,
                                matriculaSocios,
                                servicoPessoaDependente,
                                parentesco,
                                Integer.parseInt((String) ((DataObject) listaDependentes.get(i)).getArgumento2())
                        );

                        if (!sv.inserirObjeto(socioDependente)) {
                            GenericaMensagem.warn("Erro", "Erro ao salvar Sócio: " + ((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getPessoa().getNome());
                            sv.desfazerTransacao();
                            return null;
                        }
                    } else {
                        ServicoPessoaDB dbSerP = new ServicoPessoaDBToplink();
                        //ServicoPessoa servicoPessoaDependente = dbSerP.pesquisaServicoPessoaPorPessoa(((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getPessoa().getId());
                        ServicoPessoa servicoPessoaDependente = (ServicoPessoa) sv.pesquisaCodigo(socioDependente.getServicoPessoa().getId(), "ServicoPessoa");

                        servicoPessoaDependente.setEmissao(servicoPessoa.getEmissao());
                        servicoPessoaDependente.setPessoa(fisicaDependente.getPessoa());
                        servicoPessoaDependente.setDescontoFolha(servicoPessoa.isDescontoFolha());
                        servicoPessoaDependente.setServicos(servicoCategoriaDep.getServicos());
                        servicoPessoaDependente.setNrDesconto(Moeda.substituiVirgulaFloat(listaDependentes.get(i).getArgumento5().toString()));
                        servicoPessoaDependente.setReferenciaVigoracao(servicoPessoa.getReferenciaVigoracao());
                        servicoPessoaDependente.setReferenciaValidade(ref_dependente);
                        servicoPessoaDependente.setNrDiaVencimento(servicoPessoa.getNrDiaVencimento());
                        servicoPessoaDependente.setTipoDocumento(servicoPessoa.getTipoDocumento());
                        servicoPessoaDependente.setCobranca(servicoPessoa.getCobranca());
                        servicoPessoaDependente.setAtivo(servicoPessoa.isAtivo());
                        servicoPessoaDependente.setBanco(servicoPessoa.isBanco());
                        if (!sv.alterarObjeto(servicoPessoaDependente)) {
                            GenericaMensagem.error("Erro", "Erro ao Alterar Serviço Pessoa: " + ((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getPessoa().getNome());
                            sv.desfazerTransacao();
                            return null;
                        }
                        //socioDependente.setValidadeCarteirinha((String) ((DataObject) listaDependentes.get(i)).getArgumento3());
                        socioDependente.setServicoPessoa(servicoPessoaDependente);
                        socioDependente.setMatriculaSocios(matriculaSocios);
                        socioDependente.setNrViaCarteirinha(Integer.parseInt((String) ((DataObject) listaDependentes.get(i)).getArgumento2()));
                        socioDependente.setParentesco(parentesco);
                        if (!sv.alterarObjeto(socioDependente)) {
                            GenericaMensagem.error("Erro", "Erro ao Salvar Sócio: " + ((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getPessoa().getNome());
                            sv.desfazerTransacao();
                            return null;
                        }
                    }
                }
            }
        }

        ((FisicaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("fisicaBean")).setSocios(socios);
        sv.comitarTransacao();

        atualizarListaDependenteAtivo();
        atualizarListaDependenteInativo();
        return null;
    }

    public String editarTitular() {
        if (socios.getId() == -1) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
            return "pessoaFisica";
        }
        FisicaDB db = new FisicaDBToplink();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("fisicaBean", new FisicaBean());
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).setSocios(socios);
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).editarFisicaParametro(db.pesquisaFisicaPorPessoa(socios.getServicoPessoa().getPessoa().getId()));
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).showImagemFisica();
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getListaSocioInativo().clear();
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getListaPessoaEndereco().clear();
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getListaPessoaEndereco();
        ((FisicaBean) GenericaSessao.getObject("fisicaBean")).getStrEndereco();
        GenericaSessao.put("linkClicado", true);
        return "pessoaFisica";
    }

    public boolean validaSalvar() {

        if (servicoPessoa.getId() == -1) {
            if (servicoPessoa.getPessoa().getDocumento().isEmpty() || servicoPessoa.getPessoa().getDocumento().equals("0")) {
                GenericaMensagem.warn("Erro", "Para ser titular é necessário ter número de documento (CPF) no cadastro!");
                return false;
            }
        }

        if (matriculaSocios.getEmissao().isEmpty()) {
            GenericaMensagem.warn("Erro", "Data de Emissão Inválida!");
            return false;
        }

        if (getListaGrupoCategoria().isEmpty()) {
            GenericaMensagem.warn("Erro", "Lista de Grupos Categoria Vazia!");
            return false;
        }

        if (getListaCategoria().isEmpty()) {
            GenericaMensagem.warn("Erro", "Lista de Categoria Vazia!");
            return false;
        }

        if (getListaTipoDocumento().isEmpty()) {
            GenericaMensagem.warn("Erro", "Lista de Tipo Documentos Vazia!");
            return false;
        }

        for (DataObject linha : listaDependentes) {
            if (((Fisica) linha.getArgumento0()).getId() == -1) {
                GenericaMensagem.warn("Erro", "Pesquise um Dependente para Salvar!");
                return false;
            }

//            List<SelectItem> lista_si = new ArrayList<SelectItem>();
//            
//            lista_si.addAll( (Collection<? extends SelectItem>) linha.getArgumento6());
//            
//            if ( Integer.valueOf( linha.getArgumento1().toString()  )  == 0){
//                GenericaMensagem.warn("Erro", "Dependente não pode ser salvo sem Parentesco!");
//                return false;
//            }
        }

        ServicoCategoriaDB dbSCat = new ServicoCategoriaDBToplink();
        int idCat = Integer.parseInt(getListaCategoria().get(idCategoria).getDescription());
        servicoCategoria = dbSCat.pesquisaPorParECat(1, idCat);
        if (servicoCategoria == null) {
            GenericaMensagem.warn("Erro", "Não existe Serviço Categoria cadastrado!");
            return false;
        } else {
            servicoPessoa.setServicos(servicoCategoria.getServicos());
        }

        // SE DESCONTO FOLHA = true NAO SALVAR EM cobranca ID EMPRESA -- alterado na data 12/01/2014 (ID da tarefa 388)
        servicoPessoa.setCobranca(servicoPessoa.getPessoa());
//        
//        if (servicoPessoa.isDescontoFolha()) {
//            if (pessoaEmpresa.getId() == -1) {
//                GenericaMensagem.error("Erro", "Este sócio não possui Empresa para desconto em folha!");
//                servicoPessoa.setDescontoFolha(false);
//                return false;
//            }
//            servicoPessoa.setCobranca(pessoaEmpresa.getJuridica().getPessoa());
//        } else {
//            servicoPessoa.setCobranca(servicoPessoa.getPessoa());
//        }
//        

        SociosDB db = new SociosDBToplink();
        if ((servicoPessoa.getId() == -1) && (db.pesquisaSocioPorPessoaAtivo(servicoPessoa.getPessoa().getId()).getId() != -1)) {
            GenericaMensagem.error("Erro", "Esta pessoa já um Sócio Cadastrado!");
            return false;
        }

        ServicoContaCobrancaDB dbSCB = new ServicoContaCobrancaDBToplink();
        if (chkContaCobranca) {
            List l = dbSCB.pesquisaServPorIdServIdTipoServ(servicoCategoria.getServicos().getId(), 1);
            if (!l.isEmpty()) {
                servicoPessoa.setBanco(true);
            } else {
                servicoPessoa.setBanco(false);
                GenericaMensagem.error("Erro", "Não existe Serviço Conta Cobrança!");
                return false;
            }
        } else {
            servicoPessoa.setBanco(false);
        }
        return true;
    }

    public void salvarFisicaDependente() {
        if (!validaSalvarDependente()) {
            return;
        }
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        if (temFoto) {
            novoDependente.setDataFoto(DataHoje.data());
        } else {
            novoDependente.setDtFoto(null);
        }

        if (novoDependente.getId() == -1) {
            novoDependente.getPessoa().setTipoDocumento((TipoDocumento) sv.pesquisaCodigo(1, "TipoDocumento"));
            sv.abrirTransacao();
            if (!sv.inserirObjeto(novoDependente.getPessoa())) {
                GenericaMensagem.error("Erro", "Erro ao salvar Pessoa!");
                sv.desfazerTransacao();
                return;
            }

            if (!sv.inserirObjeto(novoDependente)) {
                GenericaMensagem.error("Erro", "Erro ao salvar Cadastro!");
                sv.desfazerTransacao();
                return;
            }
            sv.comitarTransacao();
            GenericaMensagem.info("Sucesso", "Dependente Salvo!");
        } else {
            sv.abrirTransacao();
            if (!sv.alterarObjeto(novoDependente.getPessoa())) {
                GenericaMensagem.error("Erro", "Erro ao atualizar Pessoa!");
                sv.desfazerTransacao();
                return;
            }

            if (!sv.alterarObjeto(novoDependente)) {
                GenericaMensagem.error("Erro", "Erro ao atualizar Cadastro!");
                sv.desfazerTransacao();
                return;
            }
            sv.comitarTransacao();
            GenericaMensagem.info("Sucesso", "Dependente Atualizado!");
        }

        ((DataObject) listaDependentes.get(index_dependente)).setArgumento0(novoDependente);
        atualizaValidadeTela(index_dependente);
        salvarImagem();
        modelVisible = false;
        index_dependente = 0;
        novoDependente = new Fisica();
        RequestContext.getCurrentInstance().execute("PF('dlg_dependente').hide()");
    }

    public boolean validaSalvarDependente() {
        FisicaDB db = new FisicaDBToplink();
        if (novoDependente.getId() == -1) {
            if (!db.pesquisaFisicaPorNomeNascRG(novoDependente.getPessoa().getNome(),
                    novoDependente.getDtNascimento(),
                    novoDependente.getRg()).isEmpty()) {
                GenericaMensagem.error("Erro", "Esta pessoa já esta Cadastrada!");
                return false;
            }

            if (novoDependente.getPessoa().getDocumento().equals("") || novoDependente.getPessoa().getDocumento().equals("0")) {
                novoDependente.getPessoa().setDocumento("0");
            } else {
                List listDocumento = db.pesquisaFisicaPorDoc(novoDependente.getPessoa().getDocumento());
                if (!listDocumento.isEmpty()) {
                    GenericaMensagem.error("Erro", "Documento já existente!");
                    return false;
                }
            }
        } else {
            if (novoDependente.getPessoa().getDocumento().isEmpty() || novoDependente.getPessoa().getDocumento().equals("0")) {
                novoDependente.getPessoa().setDocumento("0");
            } else {

                List listDocumento = db.pesquisaFisicaPorDoc(novoDependente.getPessoa().getDocumento());
                for (Object listDocumento1 : listDocumento) {
                    if (!listDocumento.isEmpty() && ((Fisica) listDocumento1).getId() != novoDependente.getId()) {
                        GenericaMensagem.error("Erro", "Documento já existente!");
                        return false;
                    }
                }
            }
        }

        if (novoDependente.getPessoa().getCriacao().isEmpty()) {
            GenericaMensagem.warn("Atenção", "Data de Cadastro inválida!");
            return false;
        }

        if (novoDependente.getNascimento().isEmpty() || novoDependente.getNascimento().length() < 10) {
            GenericaMensagem.fatal("Validação", "Data de Nascimento inválida!");
            return false;
        }

        if (novoDependente.getPessoa().getNome().equals("")) {
            GenericaMensagem.error("Validação", "O campo nome não pode ser nulo!");
            return false;
        }

        if (!novoDependente.getPessoa().getDocumento().isEmpty() && !novoDependente.getPessoa().getDocumento().equals("0")) {
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(novoDependente.getPessoa().getDocumento()))) {
                GenericaMensagem.error("Validação", "Documento Inválido!");
                return false;
            }
        }

        for (DataObject linha : listaDependentesInativos) {
            if (((Fisica) linha.getArgumento0()).getId() == novoDependente.getId()) {
                GenericaMensagem.warn("Validação", "Este dependente esta inativado nesta matrícula!");
                return false;
            }
        }

        SociosDB dbs = new SociosDBToplink();
        SociosDao sociosDao = new SociosDao();
        Socios s = dbs.pesquisaSocioPorPessoaAtivo(novoDependente.getPessoa().getId());
        if (s.getId() != -1) {
            if (s.getServicoPessoa().isAtivo()) {
                if (s.getMatriculaSocios().getTitular().getId() == socios.getMatriculaSocios().getTitular().getId()) {
                    GenericaMensagem.error("Validação", "Pessoa já é dependente nesta matrícula!");
                } else {
                    GenericaMensagem.error("Validação", "Esta pessoa já é sócia em outra matrícula para o(a) titular " + s.getMatriculaSocios().getTitular().getNome());
                }
                return false;
            }
        }
        List<Socios> list = sociosDao.listaPorPessoa(novoDependente.getPessoa().getId());
        Socios soc_dep = dbs.pesquisaSocioPorPessoaAtivo(novoDependente.getPessoa().getId());
        if (soc_dep.getId() != -1 && (soc_dep.getMatriculaSocios().getId() != socios.getMatriculaSocios().getId())) {
            for (int i = 0; i < list.size(); i++) {
                if (soc_dep.getMatriculaSocios().getNrMatricula() == list.get(i).getMatriculaSocios().getNrMatricula()) {
                    if (list.get(i).getServicoPessoa().isAtivo()) {
                        GenericaMensagem.error("Validação", "Esta pessoa já é sócia em outra matrícula para o(a) titular " + list.get(i).getMatriculaSocios().getTitular().getNome());
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void pesquisaCPF() {
        if (novoDependente.getId() == -1) {
            if (!novoDependente.getPessoa().getDocumento().isEmpty() && !novoDependente.getPessoa().getDocumento().equals("___.___.___-__")) {
                FisicaDB db = new FisicaDBToplink();
                List<Fisica> listDocumento = db.pesquisaFisicaPorDoc(novoDependente.getPessoa().getDocumento());
                if (!listDocumento.isEmpty()) {
                    novoDependente = listDocumento.get(0);
                } else if (novoDependente.getId() != -1) {
                    String doc = novoDependente.getPessoa().getDocumento();

                    novoDependente = new Fisica();

                    novoDependente.getPessoa().setDocumento(doc);
                }
            }
        }
    }

    public void adicionarDependente() {

//        if (getListaParentesco().size() == 1 && Integer.valueOf(listaParentesco.get(0).getDescription()) == 0){
//            GenericaMensagem.warn("Erro", "Nenhum Serviço adicionado para Dependentes!");
//            RequestContext.getCurrentInstance().execute("i_dlg_c.show()");
//            return;
//        }
        if (Integer.valueOf(listaCategoria.get(idCategoria).getDescription()) == 0) {
            GenericaMensagem.warn("Erro", "Nenhuma Categoria Encontrada!");
            RequestContext.getCurrentInstance().execute("i_dlg_c.show()");
            return;
        }

        Fisica fisica = new Fisica();
        DataHoje dh = new DataHoje();

        CategoriaDB dbCat = new CategoriaDBToplink();
        GrupoCategoria gpCat = dbCat.pesquisaGrupoPorCategoria(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));

        Date validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(gpCat.getNrValidadeMesCartao(), DataHoje.data()));
        if (GenericaSessao.getString("sessaoCliente").equals("ServidoresRP")) {
            Integer verificaHoje = DataHoje.converteDataParaInteger(DataHoje.data());
            Integer verificaFuturo = DataHoje.converteDataParaInteger("30/06/2016");
            validadeCarteirinha = null;
            if (verificaFuturo < verificaHoje) {
                GenericaMensagem.warn("Atenção", "Entrar em contato com nosso suporte técnico!");
            }
        }

        if (listaDependentes.isEmpty()) {
            fisica.getPessoa().setNome("");
            DataObject dtObj = new DataObject(
                    fisica, // NOME
                    0, // IDPARENTESCO
                    1, // VIA CARTEIRINHA
                    DataHoje.converteData(validadeCarteirinha), // DATA VALIDADE CARTEIRINHA
                    null, // DATA VAL DEP
                    servicoPessoa.getNrDesconto(), // DESCONTO
                    new SelectItem(0, "Selecione um Dependente", "0"), // LISTA DE PARENTESCO
                    null,
                    null,
                    null
            );
            listaDependentes.add(dtObj);
        } else {
            for (int i = 0; i < listaDependentes.size(); i++) {
                if (((Fisica) ((DataObject) listaDependentes.get(i)).getArgumento0()).getId() != -1
                        && (i - (listaDependentes.size() - 1) == 0)) {
                    fisica.getPessoa().setNome("");
                    DataObject dtObj = new DataObject(
                            fisica, // NOME
                            0, // IDPARENTESCO
                            1, // VIA CARTEIRINHA
                            DataHoje.converteData(validadeCarteirinha), // DATA VALIDADE CARTEIRINHA
                            null, // DATA VAL DEP
                            servicoPessoa.getNrDesconto(), // DESCONTO
                            new SelectItem(0, "Selecione um Dependente", "0"), // LISTA DE PARENTESCO
                            null,
                            null,
                            null
                    );
                    listaDependentes.add(dtObj);
                    break;
                }
            }
        }
        dependente = new Fisica();
    }

    public String novoCadastroDependente() {
        novoDependente = new Fisica();
        fisicaPesquisa = new Fisica();
        temFoto = false;
        return null;
    }

    public void novoVoid() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("sociosBean", new SociosBean());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("fisicaBean", new FisicaBean());
    }

    public String excluir() {
        if (!validaExcluir()) {
            RequestContext.getCurrentInstance().execute("i_dlg_c.show()");
            return null;
        }

        //SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        SociosDB db = new SociosDBToplink();

        Dao di = new Dao();
        // DELETAR DEPENDENTES -----
        di.openTransaction();

        for (DataObject listaDependente : listaDependentes) {
            Socios soc = db.pesquisaSocioPorPessoa(((Fisica) ((DataObject) listaDependente).getArgumento0()).getPessoa().getId());
            if (soc.getId() == -1) {
                //listaDependentes.remove(idIndexDep);
            } else if (!excluirDependentes(di, (Socios) di.find(soc))) {
                di.rollback();
                GenericaMensagem.error("Erro", "Erro ao Excluir Dependentes!");
                PF.openDialog("i_dlg_c");
                return null;
            }
        }

        for (DataObject listaDependentesInativo : listaDependentesInativos) {
            Socios soc = db.pesquisaSocioPorPessoa(((Fisica) ((DataObject) listaDependentesInativo).getArgumento0()).getPessoa().getId());
            if (soc.getId() == -1) {
                //listaDependentes.remove(idIndexDep);
            } else if (!excluirDependentes(di, (Socios) di.find(soc))) {
                di.rollback();
                GenericaMensagem.error("Erro", "Erro ao Excluir Dependentes!");
                PF.openDialog("i_dlg_c");
                return null;
            }
        }

        SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();
        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);

        List<SocioCarteirinha> list = new ArrayList();
        if (modeloc != null) {
            list = db.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());
        }
        SocioCarteirinhaDB socioCarteirinhaDB = new SocioCarteirinhaDBToplink();
        List<HistoricoCarteirinha> hcs;
        if (!list.isEmpty()) {
            for (SocioCarteirinha socioCarteirinha : list) {
                hcs = socioCarteirinhaDB.listaHistoricoCarteirinha(socios.getServicoPessoa().getPessoa().getId());
                for (HistoricoCarteirinha hc : hcs) {
                    if (!di.delete(hc)) {
                        GenericaMensagem.error("Erro", "Erro ao Excluir carteirinha do Dependente!");
                        PF.openDialog("i_dlg_c");
                        di.rollback();
                        return null;
                    }
                }
                hcs.clear();
                if (!di.delete(socioCarteirinha)) {
                    GenericaMensagem.error("Erro", "Erro ao Excluir carteirinha do Dependente!");
                    PF.openDialog("i_dlg_c");
                    di.rollback();
                    return null;
                }
            }
        }

        // DELETAR SOCIOS ------
        if (!di.delete(di.find(socios))) {
            GenericaMensagem.error("Erro", "Erro ao Deletar Sócio!");
            PF.openDialog("i_dlg_c");
            di.rollback();
            return null;
        }

        if (!di.delete(di.find(matriculaSocios))) {
            GenericaMensagem.error("Erro", "Erro ao Deletar Matricula, existem movimentos para essa Pessoa!");
            PF.openDialog("i_dlg_c");
            di.rollback();
            return null;
        }

        if (!di.delete(di.find(servicoPessoa))) {
            GenericaMensagem.error("Erro", "Erro ao Deletar Servico Pessoa!");
            PF.openDialog("i_dlg_c");
            di.rollback();
            return null;
        }
        NovoLog novoLog = new NovoLog();
        novoLog.delete(
                " ID: " + socios.getId()
                + " - Pessoa: (" + socios.getServicoPessoa().getPessoa().getId() + ") - " + socios.getServicoPessoa().getPessoa().getNome()
                + " - Titular: (" + socios.getMatriculaSocios().getTitular().getId() + ") - " + socios.getMatriculaSocios().getTitular().getNome()
                + " - Matrícula: " + socios.getMatriculaSocios().getNrMatricula()
                + " - Categoria: " + socios.getMatriculaSocios().getCategoria().getCategoria()
                + " - Parentesco: " + socios.getParentesco().getParentesco()
        );
        GenericaMensagem.info("Sucesso", "Cadastro Deletado!");
        di.commit();
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);

        // FUNCIONANDO --
        //FisicaJSFBean fizx = (FisicaJSFBean)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("fisicaBean", new FisicaJSFBean());
        //fizx.setSocios(new Socios());
        ((FisicaBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("fisicaBean")).setSocios(new Socios());
        //FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("socioPesquisa",socios);
        return "pessoaFisica";
    }

    public boolean validaExcluir() {
        if (servicoPessoa.getId() == -1) {
            GenericaMensagem.error("Erro", "Não existe Sócio para ser Excluído!");
            return false;
        }
        return true;
    }

    public String excluirDependente(DataObject linha, int index) {
        Fisica fi = (Fisica) linha.getArgumento0();
        if (fi.getId() != -1) {
            SociosDB db = new SociosDBToplink();
            // Socios soc = db.pesquisaSocioPorPessoa(fi.getPessoa().getId());
            Socios soc = db.pesquisaSocioPorPessoaEMatriculaSocio(fi.getPessoa().getId(), socios.getMatriculaSocios().getId());
            Dao dao = new Dao();
            dao.openTransaction();
            soc.getServicoPessoa().setReferenciaValidade((String) linha.getArgumento4());
            if (soc.getId() == -1) {
                listaDependentes.remove(index);
                GenericaMensagem.warn("Erro", "Dependente Excluído!");
                dao.rollback();
            } else if (!inativaDependentes(dao, soc)) {
                dao.rollback();
                GenericaMensagem.warn("Erro", "Erro ao inativar dependente!");
            } else {
                listaDependentes.remove(index);
                GenericaMensagem.warn("Erro", "Dependente inativado!");
                dao.commit();
                atualizarListaDependenteInativo();
            }
        } else {
            listaDependentes.remove(index);
            GenericaMensagem.warn("Erro", "Dependente Excluído!");
        }
        return null;
    }

    public boolean inativaDependentes(Dao dao, Socios soc) {
        soc.getServicoPessoa().setAtivo(false);
        return dao.update(soc.getServicoPessoa());
    }

    public boolean excluirDependentes(Dao dao, Socios soc) {
        SociosDB db = new SociosDBToplink();

        SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();

        ServicoCategoriaDB dbSCat = new ServicoCategoriaDBToplink();

        ServicoCategoria servicoCategoriaDep = dbSCat.pesquisaPorParECat(soc.getParentesco().getId(), servicoCategoria.getCategoria().getId());

        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(servicoCategoriaDep.getCategoria().getId(), 170);
        List<SocioCarteirinha> list = new ArrayList();

        if (modeloc != null) {
            list = db.pesquisaCarteirinhasPorPessoa(soc.getServicoPessoa().getPessoa().getId(), modeloc.getId());
        }

        if (!list.isEmpty()) {
            for (SocioCarteirinha socioCarteirinha : list) {
                if (!dao.delete(socioCarteirinha)) {
                    msgConfirma = "Erro ao excluir carteirinha do Dependente!";
                    return false;
                }
            }
        }

        if (!dao.delete(soc)) {
            msgConfirma = "Erro ao excluir Dependente!";
            return false;
        }

        //ServicoPessoa serPessoa = dbS.pesquisaServicoPessoaPorPessoa(soc.getServicoPessoa().getPessoa().getId());
        ServicoPessoa serPessoa = (ServicoPessoa) dao.find(soc.getServicoPessoa());
        if (!dao.delete(serPessoa)) {
            msgConfirma = "Erro ao excluir serviço pessoa dependente!";
            return false;
        }
        msgConfirma = "Dependente excluído!";
        return true;
    }

    public void editarOUsalvar(int index) {
        Fisica fisica = (Fisica) listaDependentes.get(index).getArgumento0();
        if (fisica.getId() == -1) {
            novoDependente = new Fisica();
        } else {
            novoDependente = fisica;
        }

        fisicaPesquisa = new Fisica();
        index_dependente = index;

        modelVisible = true;
    }

    public void fechaModal() {
        modelVisible = false;
    }

    public void editarDependente(Fisica f) {
        dependente = (Fisica) f;

    }

    public void reativarDependente(int index) {
        String dataRef = DataHoje.dataReferencia(DataHoje.data());
        int dataHoje = DataHoje.converteDataParaRefInteger(dataRef);
        ServicoPessoaDB spdb = new ServicoPessoaDBToplink();
        List<ServicoPessoa> list = spdb.listByPessoa(((Fisica) listaDependentesInativos.get(index).getArgumento0()).getPessoa().getId());
        SociosDao sociosDao = new SociosDao();
        Socios s = null;
        Dao dao = new Dao();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isAtivo()) {
                GenericaMensagem.warn("Validação", "Pessoa já esta ativa em outra matrícula! Ativo no cadastro de " + list.get(i).getPessoa().getNome());
                return;
            }
        }
        int dataValidade = 0;
        if (!listaDependentesInativos.get(index).getArgumento4().toString().isEmpty()) {
            dataValidade = DataHoje.converteDataParaRefInteger(listaDependentesInativos.get(index).getArgumento4().toString());
        }
        if (dataValidade != 0 && dataValidade < dataHoje) {
            GenericaMensagem.warn("Validação", "Não reativa com validade vencida!");
            return;
        }
        list.clear();
        list = spdb.listByPessoaInativo(((Fisica) listaDependentesInativos.get(index).getArgumento0()).getPessoa().getId());
        if (servicoPessoa.isAtivo()) {
            NovoLog novoLog = new NovoLog();
            for (int i = 0; i < list.size(); i++) {
                s = sociosDao.pesquisaSocioPorServicoPessoa(list.get(i).getId());
                if (s.getMatriculaSocios().getId() == socios.getMatriculaSocios().getId()) {
                    list.get(i).setAtivo(true);
                    list.get(i).setReferenciaValidade(servicoPessoa.getReferenciaValidade());
                    dao.update(list.get(i), true);
                    novoLog.update("Dependente reativado",
                            " ID:" + list.get(i).getId()
                            + " - Pessoa: (" + s.getServicoPessoa().getPessoa().getId() + ") - " + s.getServicoPessoa().getPessoa().getNome()
                            + " - Titular: (" + s.getMatriculaSocios().getTitular().getId() + ") - " + s.getMatriculaSocios().getTitular().getNome()
                            + " - Matrícula: " + s.getMatriculaSocios().getNrMatricula()
                            + " - Categoria: " + s.getMatriculaSocios().getCategoria().getCategoria()
                            + " - Parentesco: " + s.getParentesco().getParentesco()
                    );
                }
            }
            listaDependentes.add(listaDependentesInativos.get(index));
            listaDependentesInativos.remove(index);
        } else {
            GenericaMensagem.warn("Validação", "Não é possível reativar dependente com titular (Serviço Pessoa) inativo! Contate o administrador do sistema!");
        }
    }

    public void reativarDependente() {
        for (int i = 0; i < listaDependentesInativos.size(); i++) {
            String vencimento_dep = DataHoje.data().substring(0, 2) + "/" + listaDependentesInativos.get(i).getArgumento4().toString();
            String data_hoje = DataHoje.data();

            if (DataHoje.igualdadeData(vencimento_dep, data_hoje) || DataHoje.maiorData(vencimento_dep, data_hoje) || listaDependentesInativos.get(i).getArgumento4().toString().isEmpty()) {
                listaDependentes.add(listaDependentesInativos.get(i));
                listaDependentesInativos.remove(i);
            }
        }
    }

    public void atualizarListaDependenteAtivo() {
        int index = 0;
        SociosDB db = new SociosDBToplink();
        FisicaDB dbf = new FisicaDBToplink();
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        // LISTA DE DEPENDENTES ATIVOS
        List<Socios> listaDepsAtivo = db.listaDependentes(matriculaSocios.getId());
        if (!listaDepsAtivo.isEmpty()) {
            listaDependentes.clear();
            for (int i = 0; i < listaDepsAtivo.size(); i++) {
                // FISICA, PARENTESCO, VIA_CARTEIRINHA, DATA VALIDADE CARTEIRINHA, DATA VAL DEP
                Fisica fisica = dbf.pesquisaFisicaPorPessoa(listaDepsAtivo.get(i).getServicoPessoa().getPessoa().getId());
                List<Parentesco> listap = getListaParentesco(fisica.getSexo());
                ParentescoDB dbp = new ParentescoDBToplink();

                List<SelectItem> lista_si = new ArrayList<SelectItem>();
                for (int w = 0; w < listap.size(); w++) {
                    if (listaDepsAtivo.get(i).getParentesco().getId() == listap.get(w).getId()) {
                        index = w;
                    }

                    lista_si.add(new SelectItem(w, listap.get(w).getParentesco(), Integer.toString(listap.get(w).getId())));
                }

                String vencimento_dep = "";

                if (listaDepsAtivo.get(i).getServicoPessoa().getReferenciaValidade() != null && !listaDepsAtivo.get(i).getServicoPessoa().getReferenciaValidade().isEmpty()) {
                    vencimento_dep = DataHoje.data().substring(0, 2) + "/" + listaDepsAtivo.get(i).getServicoPessoa().getReferenciaValidade();
                }

                String data_hoje = DataHoje.data();

                if (vencimento_dep.isEmpty()
                        || (!vencimento_dep.isEmpty() && (DataHoje.igualdadeData(vencimento_dep, data_hoje) || DataHoje.maiorData(vencimento_dep, data_hoje)))) {
                    SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();
                    ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);

                    List<SocioCarteirinha> listsc = new ArrayList<>();
                    if (modeloc != null) {
                        listsc = db.pesquisaCarteirinhasPorPessoa(listaDepsAtivo.get(i).getServicoPessoa().getPessoa().getId(), modeloc.getId());
                    }

                    listaDependentes.add(
                            new DataObject(
                                    fisica,
                                    index,// PARENTESCO
                                    listaDepsAtivo.get(i).getNrViaCarteirinha(), // VIA CARTEIRINHA
                                    (listsc.isEmpty()) ? "" : listsc.get(0).getValidadeCarteirinha(), // DATA VALIDADE CARTEIRINHA
                                    listaDepsAtivo.get(i).getServicoPessoa().getReferenciaValidade(), // DATA VAL DEP
                                    listaDepsAtivo.get(i).getServicoPessoa().getNrDesconto(), // DESCONTO
                                    lista_si, // LISTA DE PARENTESCO
                                    null,
                                    null,
                                    null
                            )
                    );
                } else {
                    // AQUI INATIVA AUTOMATICAMENTE SE O DEPENDENTE ESTIVER COM A REF VALIDADE < QUE A DATA ATUAL
                    sv.abrirTransacao();
                    ServicoPessoa sp2 = (ServicoPessoa) sv.pesquisaCodigo(listaDepsAtivo.get(i).getServicoPessoa().getId(), "ServicoPessoa");
                    sp2.setAtivo(false);
                    if (!sv.alterarObjeto(sp2)) {
                        GenericaMensagem.error("Erro", "Erro ao alterar Serviço Pessoa!");
                        sv.desfazerTransacao();
                        return;
                    }

                    Socios soc2 = db.pesquisaSocioPorPessoa(sp2.getPessoa().getId());

                    soc2.getMatriculaSocios().setMotivoInativacao((SMotivoInativacao) sv.pesquisaCodigo(5, "SMotivoInativacao"));
                    if (!sv.alterarObjeto(soc2)) {
                        GenericaMensagem.error("Erro", "Erro ao alterar Matrícula do Dependente!");
                        sv.desfazerTransacao();
                        return;
                    }

                    sp2 = new ServicoPessoa();
                    sv.comitarTransacao();
                }
            }
        }

    }

    public void atualizarListaDependenteInativo() {
        int index = 0;
        SociosDB db = new SociosDBToplink();
        FisicaDB dbf = new FisicaDBToplink();
        // LISTA DE DEPENDENTES INATIVOS
        List<Socios> listaDepsInativo = db.listaDependentesInativos(matriculaSocios.getId());
        if (!listaDepsInativo.isEmpty()) {
            listaDependentesInativos.clear();
            for (int i = 0; i < listaDepsInativo.size(); i++) {
                // FISICA, PARENTESCO, VIA_CARTEIRINHA, DATA VALIDADE CARTEIRINHA, DATA VAL DEP
                Fisica fisica = dbf.pesquisaFisicaPorPessoa(listaDepsInativo.get(i).getServicoPessoa().getPessoa().getId());

                List<Parentesco> listap = getListaParentesco(fisica.getSexo());
                ParentescoDB dbp = new ParentescoDBToplink();
                List<SelectItem> lista_si = new ArrayList<SelectItem>();
                for (int w = 0; w < listap.size(); w++) {
                    if (listaDepsInativo.get(i).getParentesco().getId() == listap.get(w).getId()) {
                        index = w;
                    }

                    lista_si.add(new SelectItem(w, listap.get(w).getParentesco(), Integer.toString(listap.get(w).getId())));
                }

                SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();
                ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);
                List<SocioCarteirinha> listsc = new ArrayList();

                if (modeloc != null) {
                    listsc = db.pesquisaCarteirinhasPorPessoa(listaDepsInativo.get(i).getServicoPessoa().getPessoa().getId(), modeloc.getId());
                }

                listaDependentesInativos.add(
                        new DataObject(
                                fisica,
                                index,// PARENTESCO
                                listaDepsInativo.get(i).getNrViaCarteirinha(), // VIA CARTEIRINHA
                                (listsc.isEmpty()) ? "" : listsc.get(0).getValidadeCarteirinha(), // DATA VALIDADE CARTEIRINHA
                                listaDepsInativo.get(i).getServicoPessoa().getReferenciaValidade(), // DATA VAL DEP
                                listaDepsInativo.get(i).getServicoPessoa().getNrDesconto(), // DESCONTO
                                lista_si, // LISTA DE PARENTESCO
                                null,
                                null,
                                null
                        )
                );
            }
        }
    }

    public String atualizaValidade(Parentesco par, Fisica fisica) {
        if (par.getNrValidade() == 0) {
            return null;
        } else if (par.getNrValidade() > 0 && par.isValidade()) {
            if (!fisica.getNascimento().equals("") && fisica.getNascimento() != null) {
                return (new DataHoje()).incrementarAnos(par.getNrValidade(), fisica.getNascimento()).substring(3, 10);
            } else {
                return null;
            }
        } else if (par.getNrValidade() > 0 && !par.isValidade()) {
            return ((new DataHoje()).incrementarAnos(par.getNrValidade(), DataHoje.data())).substring(3, 10);
        }
        return null;
    }

    public String atualizaValidadeCarteirinha(Parentesco par, Fisica fisica) {
        GrupoCategoria grupoCategoria = (GrupoCategoria) new Dao().find(new GrupoCategoria(), Integer.parseInt(getListaGrupoCategoria().get(idGrupoCategoria).getDescription()));
        String validadeCarteirinha = new DataHoje().incrementarMeses(grupoCategoria.getNrValidadeMesCartao(), DataHoje.data());
        if (GenericaSessao.getString("sessaoCliente").equals("ServidoresRP")) {
            Integer verificaHoje = DataHoje.converteDataParaInteger(DataHoje.data());
            Integer verificaFuturo = DataHoje.converteDataParaInteger("30/06/2016");
            if (verificaFuturo < verificaHoje) {
                GenericaMensagem.warn("Atenção", "Entrar em contato com nosso suporte técnico!");
                return "";
            }
            if (!(par.getParentesco().toUpperCase()).equals("TITULAR")
                    && !(par.getParentesco().toUpperCase()).equals("ESPOSA")
                    && !(par.getParentesco().toUpperCase()).equals("ESPOSO")
                    && !(par.getParentesco().toUpperCase()).equals("SOGRA")
                    && !(par.getParentesco().toUpperCase()).equals("SOGRO")
                    && !(par.getParentesco().toUpperCase()).equals("PAI")
                    && !(par.getParentesco().toUpperCase()).equals("MÃE")) {
                validadeCarteirinha = ("30/06/2016");
                GenericaMensagem.warn("Atenção", "Esta data de validade é provisória e este critério será mantido até o dia 30/06/2016, conforme solicitação do cliente, exceto para os graus de parentesco titular, esposa, sogra e paes.");
            }
        }
        return validadeCarteirinha;
    }

    public void atualizaValidadeTela(int index) {
        ParentescoDB db = new ParentescoDBToplink();

        Parentesco par = null;
        Fisica fisica = (Fisica) ((DataObject) listaDependentes.get(index)).getArgumento0();

        List<Parentesco> listap = getListaParentesco(fisica.getSexo());
        List<SelectItem> lista_si = new ArrayList<SelectItem>();
        for (int w = 0; w < listap.size(); w++) {
            lista_si.add(new SelectItem(w, listap.get(w).getParentesco(), Integer.toString(listap.get(w).getId())));
        }

        ((DataObject) listaDependentes.get(index)).setArgumento6(lista_si);
        int index_pa = Integer.valueOf(((DataObject) listaDependentes.get(index)).getArgumento1().toString());

        par = db.pesquisaCodigo(Integer.valueOf(lista_si.get(index_pa).getDescription()));

        ((DataObject) listaDependentes.get(index)).setArgumento3(atualizaValidadeCarteirinha(par, fisica));
        ((DataObject) listaDependentes.get(index)).setArgumento4(atualizaValidade(par, fisica));
    }

    public void confirmaImprimirCartao() {
        if (socios.getId() == -1) {
            return;
        }

        SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();
        SociosDB dbs = new SociosDBToplink();
        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);

        if (modeloc == null) {
            GenericaMensagem.warn("Atenção", "Sócio sem modelo de Carteirinha!");
            return;
        }

        List<SocioCarteirinha> listsc = dbs.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());

        if (DataHoje.menorData(listsc.get(0).getValidadeCarteirinha(), DataHoje.data())) {
            DataHoje dh = new DataHoje();
            CategoriaDB db = new CategoriaDBToplink();
            GrupoCategoria gpCat = db.pesquisaGrupoPorCategoria(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));
            Date validadeCarteirinha = DataHoje.converte(dh.incrementarMeses(gpCat.getNrValidadeMesCartao(), DataHoje.data()));

            novaValidadeCartao = DataHoje.converteData(validadeCarteirinha);
            PF.openDialog("dlg_validade_carteirinha");
        } else {
            PF.openDialog("dlg_imprimir_carteirinha");
        }
    }

    public String visualizarCarteirinha(boolean alterarValidade) {
        /*
         COMENTEI TODO ESSE CÓDIGO PORQUE A PRINCIPIO NA MUDANÇA QUANDO SALVAR O SÓCIO ELE SEMPRE TERÁ CARTEIRINHA
         EM FASE DE TESTES 22/05/2014 QUINTA-FEIRA -- COMÉRCIO RP -- DEPOIS EXCLUIR COMENTÁRIO
         */

        SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();
        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);

        if (modeloc == null) {
            GenericaMensagem.warn("Atenção", "Sócio sem modelo de Carteirinha!");
            return null;
        }

        SocioCarteirinha sctitular = dbc.pesquisaCarteirinhaPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());

        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        if (sctitular != null && sctitular.getDtEmissao() == null) {
            sv.abrirTransacao();
            sctitular.setDtEmissao(DataHoje.dataHoje());
            if (!sv.alterarObjeto(sctitular)) {
                GenericaMensagem.error("Erro", "Não foi possivel alterar data de emissão!");
                sv.desfazerTransacao();
                return null;
            }
            sv.comitarTransacao();
        }

        if (alterarValidade) {
            sv.abrirTransacao();
            sctitular.setDtValidadeCarteirinha(DataHoje.converte(novaValidadeCartao));
            socios.setNrViaCarteirinha(socios.getNrViaCarteirinha() + 1);

            HistoricoCarteirinha hc = new HistoricoCarteirinha();
            hc.setCarteirinha(sctitular);
            hc.setDescricao("Reimpressão de Carteirinha 2º Via");

            if (!sv.inserirObjeto(hc) || !sv.alterarObjeto(sctitular)) {
                GenericaMensagem.error("Erro", "Não foi possivel salvar histórico!");
                sv.desfazerTransacao();
                return null;
            }

            sv.comitarTransacao();
        }

        List listaAux = dbc.filtroCartao(socios.getServicoPessoa().getPessoa().getId());

        SociosDB db = new SociosDBToplink();
        if (registro.isCarteirinhaDependente() && !listaDependentes.isEmpty()) {
            sv.abrirTransacao();
            for (DataObject listaDependente : listaDependentes) {
                Socios socioDependente = db.pesquisaSocioPorPessoaAtivo(((Fisica) listaDependente.getArgumento0()).getPessoa().getId());
                SocioCarteirinha sc = dbc.pesquisaCarteirinhaPessoa(socioDependente.getServicoPessoa().getPessoa().getId(), modeloc.getId());

                if (sc != null && sc.getDtEmissao() == null) {
                    sc.setDtEmissao(DataHoje.dataHoje());
                    if (!sv.alterarObjeto(sc)) {
                        GenericaMensagem.warn("Erro", "Não foi possivel alterar data de emissão do dependente!");
                        sv.desfazerTransacao();
                        return null;
                    }
                }
                listaAux.addAll(dbc.filtroCartao(socioDependente.getServicoPessoa().getPessoa().getId()));
            }
            sv.comitarTransacao();
        }

        if (!listaAux.isEmpty()) {
            ((List) listaAux.get(0)).set(6, sctitular.getValidadeCarteirinha());
            ((List) listaAux.get(0)).set(11, socios.getNrViaCarteirinha());
            ImpressaoParaSocios.imprimirCarteirinha(listaAux);
        } else {
            msgConfirma = "Socio não tem carteirinha";
            GenericaMensagem.warn("Sistema", "Socio não tem carteirinha!");
        }

//        if (!comita) {
//            sv.desfazerTransacao();
//        }      
        return null;
    }

    public String imprimirFichaSocial() {
        String foto = getFotoSocio();
        String path = "/Relatorios/FICHACADASTRO.jasper";
        String pathVerso = "/Relatorios/FICHACADASTROVERSO.jasper";
        File fp = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper"));
        if (fp.exists()) {
            path = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper";
        }
        File f = new File("/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper");
        if (f.exists()) {
            pathVerso = "/Cliente/" + ControleUsuarioBean.getCliente() + "/Relatorios/FICHACADASTRO.jasper";
        }
        Diretorio.criar("Arquivos/downloads/fichas");
        String caminhoDiretorio = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/fichas");
        ImpressaoParaSocios.comDependente(
                caminhoDiretorio,
                "ficha_" + socios.getId() + "_" + socios.getServicoPessoa().getPessoa().getId() + ".pdf",
                path,
                pathVerso,
                socios,
                pessoaEmpresa,
                matriculaSocios,
                imprimirVerso,
                foto);
        return null;
    }

    public String imprimirFichaSocialVazia() {
        ImpressaoParaSocios.branco();
        return "menuSocial";
    }

    public void imprimirFichaSocialBranco() {
        ImpressaoParaSocios.branco();
    }

    public String getFotoSocio() {
        FacesContext context = FacesContext.getCurrentInstance();
        File files;
        String extensao = "jpg";
        String fotoCaminho = (String) ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + socios.getServicoPessoa().getPessoa().getId());
        if (new File(fotoCaminho + ".jpg").exists()) {
            extensao = "jpg";
        } else if (new File(fotoCaminho + ".JPG").exists()) {
            extensao = "JPG";
        } else if (new File(fotoCaminho + ".png").exists()) {
            extensao = "png";
        } else if (new File(fotoCaminho + ".PNG").exists()) {
            extensao = "PNG";
        } else if (new File(fotoCaminho + ".gif").exists()) {
            extensao = "gif";
        }            
        if (socios.getId() != -1) {
            files = new File(((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/" + socios.getServicoPessoa().getPessoa().getId() + "." + extensao));
            if (files.exists()) {
                return files.getPath();
            } else {
                return ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/semFoto.jpg");
            }
        } else {
            return ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Imagens/Fotos/semFoto.jpg");
        }
    }

    public void atualizarCategoria() {
        listaDescontoSocial.clear();
        index_desconto = 0;
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
        servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());

        loadCategoria();
        loadServicos();
    }

    public void atualizarListaCategoria() {
        listaDescontoSocial.clear();
        index_desconto = 0;
        descontoSocial = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
        servicoPessoa.setNrDesconto(descontoSocial.getNrDesconto());

        loadServicos();
    }

    public List<SelectItem> getListaGrupoCategoria() {
        return listaGrupoCategoria;
    }

    public List<SelectItem> getListaCategoria() {
        return listaCategoria;
    }

    public ServicoPessoa getServicoPessoa() {
//        if (GenericaSessao.getObject("fisicaPesquisa") != null && GenericaSessao.getObject("reativarSocio") != null) {
//            servicoPessoa.setPessoa(((Fisica) GenericaSessao.getObject("fisicaPesquisa")).getPessoa());
//            editarGenerico(((Fisica) GenericaSessao.getObject("fisicaPesquisa")).getPessoa(),
//                    GenericaSessao.getBoolean("reativarSocio"));
//            pessoaEmpresa = (PessoaEmpresa) GenericaSessao.getObject("pessoaEmpresaPesquisa");
//            GenericaSessao.remove("fisicaPesquisa");
//            GenericaSessao.remove("pessoaEmpresaPesquisa");
//            GenericaSessao.remove("reativarSocio");
//        }
        return servicoPessoa;
    }

    public void setServicoPessoa(ServicoPessoa servicoPessoa) {
        this.servicoPessoa = servicoPessoa;
    }

    public boolean isChkContaCobranca() {
        return chkContaCobranca;
    }

    public void setChkContaCobranca(boolean chkContaCobranca) {
        if (this.chkContaCobranca != chkContaCobranca) {
            listaTipoDocumento.clear();
        }
        this.chkContaCobranca = chkContaCobranca;
    }

    public List<SelectItem> getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    public void setListaTipoDocumento(List<SelectItem> listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    public int getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(int idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public boolean isRenderServicos() {
        return renderServicos;
    }

    public void setRenderServicos(boolean renderServicos) {
        this.renderServicos = renderServicos;
    }

    public int getIdServico() {
        return idServico;
    }

    public void setIdServico(int idServico) {
        this.idServico = idServico;
    }

    public List<SelectItem> getListaServicos() {
        return listaServicos;
    }

    public void setListaServicos(List<SelectItem> listaServicos) {
        this.listaServicos = listaServicos;
    }

    public Socios getSocios() {
        return socios;
    }

    public void setSocios(Socios socios) {
        this.socios = socios;
    }

    public int getIdGrupoCategoria() {
        return idGrupoCategoria;
    }

    public void setIdGrupoCategoria(int idGrupoCategoria) {
        this.idGrupoCategoria = idGrupoCategoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public MatriculaSocios getMatriculaSocios() {
        PessoaEnderecoDB db = new PessoaEnderecoDBToplink();
        SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
        List<GrupoCidades> cids = (List<GrupoCidades>) salvarAcumuladoDB.listaObjeto("GrupoCidades", true);
        if (socios.getId() == -1 && matriculaSocios.getId() == -1) {
            PessoaEndereco ende = db.pesquisaEndPorPessoaTipo(servicoPessoa.getPessoa().getId(), 3);
            if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cidadePesquisa") != null) {
                matriculaSocios.setCidade((Cidade) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cidadePesquisa"));
            } else if (ende != null && ende.getId() != -1) {
                for (int i = 0; i < cids.size(); i++) {
                    if (cids.get(i).getCidade().getId() == ende.getEndereco().getCidade().getId()) {
                        matriculaSocios.setCidade(ende.getEndereco().getCidade());
                        return matriculaSocios;
                    }
                }
                matriculaSocios.setCidade(((PessoaEndereco) db.pesquisaEndPorPessoaTipo(1, 3)).getEndereco().getCidade());
            } else {
                matriculaSocios.setCidade(((PessoaEndereco) db.pesquisaEndPorPessoaTipo(1, 3)).getEndereco().getCidade());
            }
        } else if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cidadePesquisa") != null) {
            matriculaSocios.setCidade((Cidade) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("cidadePesquisa"));
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("cidadePesquisa");
        }
        return matriculaSocios;
    }

    public void setMatriculaSocios(MatriculaSocios matriculaSocios) {
        this.matriculaSocios = matriculaSocios;
    }

    public List<DataObject> getListaDependentes() {
        return listaDependentes;
    }

    public void setListaDependentes(List<DataObject> listaDependentes) {
        this.listaDependentes = listaDependentes;
    }
// AQUI LISTA DE PARENTESCO
//    public List<SelectItem> getListaParentesco() {
//        if (listaParentesco.isEmpty() && !listaCategoria.isEmpty()) {
//            ParentescoDB db = new ParentescoDBToplink();
//            if (Integer.valueOf(listaCategoria.get(idCategoria).getDescription()) == 0){
//                listaParentesco.add(new SelectItem(0, "Sem Categoria", "0"));
//                return listaParentesco;
//            }
//            
//            List<Parentesco> select = db.pesquisaTodosSemTitularCategoria(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));
//            //List<Parentesco> select = db.pesquisaTodosSemTitular();
//            
//            if (!select.isEmpty()){
//                for (int i = 0; i < select.size(); i++) {
//                    listaParentesco.add(new SelectItem(i,
//                            select.get(i).getParentesco(),
//                            Integer.toString(select.get(i).getId()))
//                    );
//                }
//            }else
//                listaParentesco.add(new SelectItem(0, "Sem Categoria", "0"));
//        }
//        return listaParentesco;
//    }

    public List<Parentesco> getListaParentesco(String sexo) {
        if (!listaCategoria.isEmpty()) {
            ParentescoDB db = new ParentescoDBToplink();
            if (Integer.valueOf(listaCategoria.get(idCategoria).getDescription()) == 0) {
                return new ArrayList<Parentesco>();
            }

            //List<Parentesco> select = db.pesquisaTodosSemTitularCategoria(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));
            List<Parentesco> select = db.pesquisaTodosSemTitularCategoriaSexo(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()), sexo);
            //List<Parentesco> select = db.pesquisaTodosSemTitular();

            if (!select.isEmpty()) {
                return select;
            } else {
                return new ArrayList<Parentesco>();
            }
        }

        return new ArrayList<Parentesco>();
    }
//
//    public void setListaParentesco(List<SelectItem> listaParentesco) {
//        this.listaParentesco = listaParentesco;
//    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public String getPessoaImagem() {
        FacesContext context = FacesContext.getCurrentInstance();
        File files = new File(((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/"));
        File fExiste = new File(((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/fotoTemp.jpg"));
        File listFile[] = files.listFiles();
        String nome;
        //temFoto = false;
        if (fExiste.exists() && dependente.getDataFoto().isEmpty()) {
            fotoTemp = true;
        }
        if (fotoTemp) {
            nome = "fotoTemp";
        } else {
            nome = "semFoto";
        }
        int numArq = listFile.length;
        for (int i = 0; i < numArq; i++) {
            String n = listFile[i].getName();
            for (int o = 0; o < n.length(); o++) {
                if (n.substring(o, o + 1).equals(".")) {
                    n = listFile[i].getName().substring(0, o);
                }
            }
            try {
                if (!fotoTemp) {
                    if (Integer.parseInt(n) == dependente.getPessoa().getId()) {
                        nome = n;
                        fotoTemp = false;
                        String caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/Fotos/fotoTemp.jpg");
                        File fl = new File(caminho);
                        fl.delete();
                        break;
                    }
                } else {
                    fotoTemp = false;
                    break;
                }
            } catch (NumberFormatException e) {

            }
        }
        return nome + ".jpg";
    }

    public Fisica getDependente() {
        return dependente;
    }

    public void setDependente(Fisica dependente) {
        this.dependente = dependente;
    }

    public int getIdIndexCombo() {
        return idIndexCombo;
    }

    public void setIdIndexCombo(int idIndexCombo) {
        this.idIndexCombo = idIndexCombo;
    }

    public String getLblSocio() {
        if (socios.getMatriculaSocios().getId() == -1) {
            lblSocio = "SALVAR ";
        } else {
            lblSocio = "ALTERAR";
        }
        return lblSocio;
    }

    public void setLblSocio(String lblSocio) {
        this.lblSocio = lblSocio;
    }

    public String getLblSocioPergunta() {
        if (socios.getId() == -1) {
            lblSocioPergunta = "Deseja associar esse cadastro? ";
        } else {
            lblSocioPergunta = "Deseja alterar esse cadastro?";
        }
        return lblSocioPergunta;
    }

    public void setLblSocioPergunta(String lblSocioPergunta) {
        this.lblSocioPergunta = lblSocioPergunta;
    }

    public boolean isDesabilitaImpressao() {
        if (socios.getId() != -1 && matriculaSocios.getId() != -1) {
            desabilitaImpressao = false;
        }
        return desabilitaImpressao;
    }

    public void setDesabilitaImpressao(boolean desabilitaImpressao) {
        this.desabilitaImpressao = desabilitaImpressao;
    }

    public String getTipoDestinario() {
        return tipoDestinario;
    }

    public void setTipoDestinario(String tipoDestinario) {
        this.tipoDestinario = tipoDestinario;
    }

    public boolean isImprimirVerso() {
        return imprimirVerso;
    }

    public void setImprimirVerso(boolean imprimirVerso) {
        this.imprimirVerso = imprimirVerso;
    }

    public String getDataInativacao() {
        return dataInativacao;
    }

    public void setDataInativacao(String dataInativacao) {
        this.dataInativacao = dataInativacao;
    }

    public Integer getIdInativacao() {
        return idInativacao;
    }

    public void setIdInativacao(Integer idInativacao) {
        this.idInativacao = idInativacao;
    }

    public List<SelectItem> getListaMotivoInativacao() {
        if (listaMotivoInativacao.isEmpty()) {
            SociosDB db = new SociosDBToplink();
            List<SMotivoInativacao> select = db.pesquisaMotivoInativacao();
            for (int i = 0; i < select.size(); i++) {
                listaMotivoInativacao.add(new SelectItem(i, select.get(i).getDescricao(), Integer.toString(select.get(i).getId())));
            }
        }
        return listaMotivoInativacao;
    }

    public String getDataReativacao() {
        return dataReativacao;
    }

    public void setDataReativacao(String dataReativacao) {
        this.dataReativacao = dataReativacao;
    }

    public String getStatusSocio() {
        if (socios.getId() == -1) {
            statusSocio = "STATUS";
        } else {
            if (matriculaSocios.getMotivoInativacao() != null) {
                statusSocio = "INATIVO / " + matriculaSocios.getMotivoInativacao().getDescricao() + " - " + matriculaSocios.getInativo();
            } else {
                statusSocio = "ATIVO";
            }
        }
        return statusSocio;
    }

    public void setStatusSocio(String statusSocio) {
        this.statusSocio = statusSocio;
    }

    public List<Fisica> getListaFisica() {
        return listaFisica;
    }

    public void setListaFisica(List<Fisica> listaFisica) {
        this.listaFisica = listaFisica;
    }

    public List<SelectItem> getListaSelectFisica() {
        return listaSelectFisica;
    }

    public void setListaSelectFisica(List<SelectItem> listaSelectFisica) {
        this.listaSelectFisica = listaSelectFisica;
    }

    public Fisica getFisicaPesquisa() {
        return fisicaPesquisa;
    }

    public void setFisicaPesquisa(Fisica fisicaPesquisa) {
        this.fisicaPesquisa = fisicaPesquisa;
    }

    public Pessoa getPessoaPesquisa() {
        return pessoaPesquisa;
    }

    public void setPessoaPesquisa(Pessoa pessoaPesquisa) {
        this.pessoaPesquisa = pessoaPesquisa;
    }

    public Fisica getNovoDependente() {
        return novoDependente;
    }

    public void setNovoDependente(Fisica novoDependente) {
        this.novoDependente = novoDependente;
    }

    public int getIndex_dependente() {
        return index_dependente;
    }

    public void setIndex_dependente(int index_dependente) {
        this.index_dependente = index_dependente;
    }

    public List<Socios> getListaSocioInativo() {
        return listaSocioInativo;
    }

    public void setListaSocioInativo(List<Socios> listaSocioInativo) {
        this.listaSocioInativo = listaSocioInativo;
    }

    public List<DataObject> getListaDependentesInativos() {
        return listaDependentesInativos;
    }

    public void setListaDependentesInativos(List<DataObject> listaDependentesInativos) {
        this.listaDependentesInativos = listaDependentesInativos;
    }

    public Part getFilePart() {
        return filePart;
    }

    public void setFilePart(Part filePart) {
        this.filePart = filePart;
    }

    public Usuario getUsuario() {
        if (GenericaSessao.exists("sessaoUsuario")) {
            usuario = (Usuario) GenericaSessao.getObject("sessaoUsuario");
        }
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getFotoTempPerfil() {
        return fotoTempPerfil;
    }

    public void setFotoTempPerfil(String fotoTempPerfil) {
        this.fotoTempPerfil = fotoTempPerfil;
    }

    public boolean isModelVisible() {
        return modelVisible;
    }

    public void setModelVisible(boolean modelVisible) {
        this.modelVisible = modelVisible;
    }

    public String getNovaValidadeCartao() {
        return novaValidadeCartao;
    }

    public void setNovaValidadeCartao(String novaValidadeCartao) {
        this.novaValidadeCartao = novaValidadeCartao;
    }

    public SocioCarteirinha getSocCarteirinha() {
        return socCarteirinha;
    }

    public void setSocCarteirinha(SocioCarteirinha socCarteirinha) {
        this.socCarteirinha = socCarteirinha;
    }

    public Registro getRegistro() {
        return registro;
    }

    public DescontoSocial getDescontoSocial() {
        return descontoSocial;
    }

    public void setDescontoSocial(DescontoSocial descontoSocial) {
        this.descontoSocial = descontoSocial;
    }

    public List<SelectItem> getListaDescontoSocial() {
        if (listaDescontoSocial.isEmpty()) {
            SociosDB db = new SociosDBToplink();
            //listaDescontoSocial.add((DescontoSocial) new Dao().find(new DescontoSocial(), 1));
            DescontoSocial ds = (DescontoSocial) new Dao().find(new DescontoSocial(), 1);
            listaDescontoSocial.add(new SelectItem(0, ds.getDescricao(), "" + ds.getId()));
            if (Integer.valueOf(listaCategoria.get(idCategoria).getDescription()) != 0) {
                //listaDescontoSocial.addAll(db.listaDescontoSocial(Integer.valueOf(listaCategoria.get(idCategoria).getDescription())));
                List<DescontoSocial> lds = db.listaDescontoSocial(Integer.valueOf(listaCategoria.get(idCategoria).getDescription()));
                for (int i = 0; i < lds.size(); i++) {
                    listaDescontoSocial.add(
                            new SelectItem(i + 1, lds.get(i).getDescricao(), "" + lds.get(i).getId())
                    );
                }
            }
        }
        return listaDescontoSocial;
    }

    public void setListaDescontoSocial(List<SelectItem> listaDescontoSocial) {
        this.listaDescontoSocial = listaDescontoSocial;
    }

    public DescontoSocial getNovoDescontoSocial() {
        return novoDescontoSocial;
    }

    public void setNovoDescontoSocial(DescontoSocial novoDescontoSocial) {
        this.novoDescontoSocial = novoDescontoSocial;
    }

    public String getValorServico() {
        return valorServico;
    }

    public void setValorServico(String valorServico) {
        this.valorServico = Moeda.converteR$(valorServico);
    }

    public Integer getIndex_desconto() {
        return index_desconto;
    }

    public void setIndex_desconto(Integer index_desconto) {
        this.index_desconto = index_desconto;
    }

    public String getNovoValorServico() {
        return novoValorServico;
    }

    public void setNovoValorServico(String novoValorServico) {
        this.novoValorServico = Moeda.converteR$(novoValorServico);
    }

    public String getAlteraValorServico() {
        return alteraValorServico;
    }

    public void setAlteraValorServico(String alteraValorServico) {
        this.alteraValorServico = Moeda.converteR$(alteraValorServico);;
    }
}

//    public void editarGenerico(Pessoa sessao, boolean reativar) {
//        CategoriaDB dbCat = new CategoriaDBToplink();
//        SociosDB db = new SociosDBToplink();
//        SocioCarteirinhaDB dbc = new SocioCarteirinhaDBToplink();
//        //FisicaDB dbf = new FisicaDBToplink();
//
//        //socSessao = db.pesquisaSocioPorPessoaAtivo(sessao.getId());
//        Socios socSessao = db.pesquisaSocioPorPessoa(sessao.getId());
//        if (socSessao.getId() != -1 && reativar) {
//            socios = socSessao;
//        } else {
//            return;
//        }
//        
//        if (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("urlRetorno").equals("emissaoCarteirinha")) {
//            return;
//        }
//
//        //Socios soc = db.pesquisaSocioDoDependente(socios.getId());
//        // SOCIO DIFERENTE PARA TRAZER NA TELA O TITULAR
//        if (socios.getMatriculaSocios().getTitular().getId() != servicoPessoa.getPessoa().getId()) {
//            socios = db.pesquisaSocioPorPessoa(socios.getMatriculaSocios().getTitular().getId());
//        }
//
//        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("linkClicado", true);
//
//        ModeloCarteirinha modeloc = dbc.pesquisaModeloCarteirinha(socios.getMatriculaSocios().getCategoria().getId(), 170);
//        List<SocioCarteirinha> listsc = new ArrayList();
//
//        if (modeloc != null) {
//            listsc = db.pesquisaCarteirinhasPorPessoa(socios.getServicoPessoa().getPessoa().getId(), modeloc.getId());
//            if (!listsc.isEmpty()) {
//                socCarteirinha = listsc.get(0);
//            } else {
//                GenericaMensagem.warn("Ateção", "Sócio sem modelo de Carteirinha!");
//            }
//        }
//
//        servicoPessoa = socios.getServicoPessoa();
//        descontoSocial = servicoPessoa.getDescontoSocial();
//        
//        matriculaSocios = socios.getMatriculaSocios();
//
//        GrupoCategoria gpCat = dbCat.pesquisaGrupoPorCategoria(socios.getMatriculaSocios().getCategoria().getId());
//        for (int i = 0; i < getListaGrupoCategoria().size(); i++) {
//            if (Integer.parseInt((String) getListaGrupoCategoria().get(i).getDescription()) == gpCat.getId()) {
//                idGrupoCategoria = i;
//                break;
//            }
//        }
//        int qntCategoria = getListaCategoria().size();
//        for (int i = 0; i < qntCategoria; i++) {
//            if (Integer.parseInt((String) getListaCategoria().get(i).getDescription()) == socios.getMatriculaSocios().getCategoria().getId()) {
//                idCategoria = i;
//                break;
//            }
//        }
//
//        for (int i = 0; i < getListaTipoDocumento().size(); i++) {
//            if (Integer.parseInt((String) listaTipoDocumento.get(i).getDescription()) == servicoPessoa.getTipoDocumento().getId()) {
//                idTipoDocumento = i;
//                break;
//            }
//        }
//
//        atualizarListaDependenteAtivo();
//        atualizarListaDependenteInativo();
//
//    }
