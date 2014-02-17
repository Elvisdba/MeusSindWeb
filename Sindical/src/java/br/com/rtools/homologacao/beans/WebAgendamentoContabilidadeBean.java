package br.com.rtools.homologacao.beans;

import br.com.rtools.pessoa.beans.PesquisarProfissaoBean;
import br.com.rtools.arrecadacao.Oposicao;
import br.com.rtools.arrecadacao.db.WebContabilidadeDB;
import br.com.rtools.arrecadacao.db.WebContabilidadeDBToplink;
import br.com.rtools.atendimento.db.AtendimentoDB;
import br.com.rtools.atendimento.db.AtendimentoDBTopLink;
import br.com.rtools.endereco.Endereco;
import br.com.rtools.endereco.db.EnderecoDB;
import br.com.rtools.endereco.db.EnderecoDBToplink;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.homologacao.Agendamento;
import br.com.rtools.homologacao.Demissao;
import br.com.rtools.homologacao.Horarios;
import br.com.rtools.homologacao.Status;
import br.com.rtools.homologacao.db.*;
import br.com.rtools.movimento.ImprimirBoleto;
import br.com.rtools.pessoa.*;
import br.com.rtools.pessoa.db.*;
import br.com.rtools.seguranca.Registro;
import br.com.rtools.utilitarios.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class WebAgendamentoContabilidadeBean extends PesquisarProfissaoBean implements Serializable{

    private List listaGrid = new ArrayList();
    private List listaEmDebito = new ArrayList();
    private List listaEmpresas = new ArrayList();
    private List<SelectItem> resultEmp = new ArrayList<SelectItem>();
    private int idStatus = 0;
    private int idIndex = -1;
    private int idIndexEndereco = -1;
    private int idMotivoDemissao = 0;
    private int idSelectRadio = 0;
    private String strSalvar = "Agendar";
    private String msgAgendamento = "";
    private String tipoAviso = "true";
    private String statusEmpresa = "REGULAR";
    private String strEndereco = "";
    private String msgConfirma = "";
    private String filtraPor = "todos";
    private boolean renderAgendamento = true;
    private boolean renderConcluir = false;
    private boolean chkFiltrar = true;
    private boolean renderBtnAgendar = true;
    private Date data = DataHoje.converte(new DataHoje().incrementarDias(1, DataHoje.data()));
    private Agendamento agendamento = new Agendamento();
    private Agendamento agendamentoProtocolo = new Agendamento();
    private Fisica fisica = new Fisica();
    private PessoaEmpresa pessoaEmpresa = new PessoaEmpresa();
    private Juridica juridica = new Juridica();
    private FilialCidade sindicatoFilial;
    private PessoaEndereco enderecoFilial = new PessoaEndereco();
    private Juridica empresa = new Juridica();
    private PessoaEndereco enderecoEmpresa = new PessoaEndereco();
    private PessoaEndereco enderecoFisica = new PessoaEndereco();
    private String cepEndereco = "";
    private List listaEnderecos = new ArrayList();
    private boolean imprimirPro = false;
    private String strContribuinte = "";
    private Registro registro = new Registro();
    public List<SelectItem> listaStatus = new ArrayList<SelectItem>();
    public List<SelectItem> listaMotivoDemissao = new ArrayList<SelectItem>();
    
    public String imprimirPlanilha() {
        if (listaEmDebito.isEmpty()) {
            return null;
        }
        ImprimirBoleto imp = new ImprimirBoleto();
        List<Movimento> lista = new ArrayList();
        List<Float> listaValores = new ArrayList<Float>();
        for (int i = 0; i < listaEmDebito.size(); i++) {
            Movimento m = (Movimento) new SalvarAcumuladoDBToplink().pesquisaCodigo((Integer) ((List) listaEmDebito.get(i)).get(0), "Movimento");
            lista.add(m);
            listaValores.add(m.getValor());
        }
        imp.imprimirPlanilha(lista, listaValores, false, false);
        imp.visualizar(null);
        return null;
    }

    public List<SelectItem> getListaStatus() {
        if (listaStatus.isEmpty()) {
            SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
            List select = new ArrayList();
            select.add((Status) salvarAcumuladoDB.pesquisaCodigo(1, "Status"));
            select.add((Status) salvarAcumuladoDB.pesquisaCodigo(2, "Status"));
            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaStatus.add(new SelectItem(new Integer(i),
                            (String) ((Status) select.get(i)).getDescricao(),
                            Integer.toString(((Status) select.get(i)).getId())));
                }
            }
        }
        return listaStatus;
    }

    public List<SelectItem> getListaMotivoDemissao() {
        if (listaMotivoDemissao.isEmpty()) {
            SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
            List select = salvarAcumuladoDB.listaObjeto("Demissao");
            if (!select.isEmpty()) {
                for (int i = 0; i < select.size(); i++) {
                    listaMotivoDemissao.add(new SelectItem(new Integer(i),
                            (String) ((Demissao) select.get(i)).getDescricao(),
                            Integer.toString(((Demissao) select.get(i)).getId())));
                }
            }
        }
        return listaMotivoDemissao;
    }

    public List<SelectItem> getListaEmpresaPertencentes() {
        if (resultEmp.isEmpty()) {
            WebContabilidadeDB db = new WebContabilidadeDBToplink();
            JuridicaDB dbJur = new JuridicaDBToplink();
            int i = 0;
            if (juridica.getId() == -1) {
                juridica = dbJur.pesquisaJuridicaPorPessoa(((Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb")).getId());
            }
            listaEmpresas = db.listaEmpresasPertContabilidade(juridica.getId());
            if (!listaEmpresas.isEmpty()) {
                while (i < listaEmpresas.size()) {
                    resultEmp.add(new SelectItem(new Integer(i),
                            ((Juridica) listaEmpresas.get(i)).getPessoa().getDocumento() + " - " + ((Juridica) listaEmpresas.get(i)).getPessoa().getNome(),
                            Integer.toString(((Juridica) listaEmpresas.get(i)).getId())));
                    i++;
                }
            }
        }
        return resultEmp;
    }

    public synchronized List getListaHorarios() {
        if (getSindicatoFilial() != null) {
            listaGrid = new ArrayList();
            List<Agendamento> ag = new ArrayList<Agendamento>();
            List<Horarios> horario;
            SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
            HomologacaoDB db = new HomologacaoDBToplink();
            String agendador;
            String homologador;
            DataObject dtObj = null;
            switch (Integer.parseInt(((SelectItem) getListaStatus().get(idStatus)).getDescription())) {
                //STATUS DISPONIVEL ----------------------------------------------------------------------------------------------
                case 1: {
                    int idDiaSemana = DataHoje.diaDaSemana(data);
                    horario = (List<Horarios>) db.pesquisaTodosHorariosDisponiveis(getSindicatoFilial().getFilial().getId(), idDiaSemana);
                    strSalvar = "Agendar";
                    int qnt;
                    for (int i = 0; i < horario.size(); i++) {
                        qnt = db.pesquisaQntdDisponivel(getSindicatoFilial().getFilial().getId(), horario.get(i), data);
                        if (qnt == -1) {
                            msgAgendamento = "Erro ao pesquisar horários disponíveis!";
                            listaGrid.clear();
                            break;
                        }
                        if (qnt > 0) {
                            dtObj = new DataObject(horario.get(i), // ARG 0 HORA
                                    null, // ARG 1 CNPJ
                                    null, //ARG 2 NOME
                                    null, //ARG 3 HOMOLOGADOR
                                    null, // ARG 4 CONTATO
                                    null, // ARG 5 TELEFONE
                                    null, // ARG 6 USUARIO
                                    null,
                                    qnt, // ARG 8 QUANTIDADE DISPONÍVEL
                                    null);
                            listaGrid.add(dtObj);
                        }
                    }
                    break;
                }

                // STATUS AGENDADO -----------------------------------------------------------------------------------------------
                case 2: {
                    strSalvar = "Atualizar";
                    if (filtraPor.equals("selecionado")) {
                        ag = db.pesquisaAgendadoPorEmpresaSemHorario(getSindicatoFilial().getFilial().getId(), data, ((Juridica) salvarAcumuladoDB.pesquisaCodigo(Integer.parseInt(((SelectItem) getListaEmpresaPertencentes().get(idSelectRadio)).getDescription()), "Juridica")).getPessoa().getId());
                    } else {
                        for (int w = 0; w < listaEmpresas.size(); w++) {
                            ag.addAll(db.pesquisaAgendadoPorEmpresaSemHorario(getSindicatoFilial().getFilial().getId(), data, ((Juridica) listaEmpresas.get(w)).getPessoa().getId()));
                        }
                    }
                    for (int i = 0; i < ag.size(); i++) {
                        if (ag.get(i).getAgendador() != null) {
                            agendador = ag.get(i).getAgendador().getPessoa().getNome();
                        } else {
                            agendador = "** Web User **";
                        }
                        if (ag.get(i).getHomologador() != null) {
                            homologador = ag.get(i).getHomologador().getPessoa().getNome();
                        } else {
                            homologador = "";
                        }

                        dtObj = new DataObject(ag.get(i).getHorarios(), // ARG 0 HORA
                                ag.get(i).getPessoaEmpresa().getJuridica().getPessoa().getDocumento(), // ARG 1 CNPJ
                                ag.get(i).getPessoaEmpresa().getJuridica().getPessoa().getNome(), //ARG 2 NOME
                                homologador, //ARG 3 HOMOLOGADOR
                                ag.get(i).getContato(), // ARG 4 CONTATO
                                ag.get(i).getTelefone(), // ARG 5 TELEFONE
                                agendador, // ARG 6 USUARIO
                                ag.get(i).getPessoaEmpresa(), // ARG 7 PESSOA EMPRESA
                                ag.get(i).getData(), // ARG 8
                                ag.get(i));// ARG 9 AGENDAMENTO
                        listaGrid.add(dtObj);
                    }
                    break;
                }
                // ---------------------------------------------------------------------------------------------------------------
                // ---------------------------------------------------------------------------------------------------------------
            }
        }
        return listaGrid;
    }

    public String novoProtocolo() {
        SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
        imprimirPro = false;
        agendamentoProtocolo = new Agendamento();
        renderBtnAgendar = true;
        renderAgendamento = false;
        renderConcluir = true;
        empresa = (Juridica) salvarAcumuladoDB.pesquisaCodigo(Integer.parseInt(((SelectItem) getListaEmpresaPertencentes().get(idSelectRadio)).getDescription()), "Juridica");
        agendamento.setDtData(null);
        agendamento.setHorarios(null);
        //agendamento.setFilial((Filial) salvarAcumuladoDB.pesquisaCodigo(1, "Filial"));
        agendamento.setFilial( getSindicatoFilial().getFilial() );
        agendamentoProtocolo = agendamento;
        if (empresa.getContabilidade() != null) {
            agendamento.setTelefone(empresa.getContabilidade().getPessoa().getTelefone1());
        }
        if (profissao.getId() == -1) {
            profissao = (Profissao) salvarAcumuladoDB.pesquisaCodigo(0, "Profissao");
        }
        msgAgendamento = "";
        return "webAgendamentoContabilidade";
    }

    public String agendar() {
        if (data.getDay() == 6 || data.getDay() == 0) {
            msgAgendamento = "Fins de semana não permitido!";
            return "webAgendamentoContabilidade";
        }

        if (DataHoje.converteDataParaInteger(DataHoje.converteData(data))
                == DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
            msgAgendamento = "Data deve ser apartir de hoje, caso deseje marcar para esta data contate seu Sindicato!";
            return "webAgendamentoContabilidade";
        }

        if (DataHoje.converteDataParaInteger(DataHoje.converteData(data))
                < DataHoje.converteDataParaInteger(DataHoje.converteData(DataHoje.dataHoje()))) {
            msgAgendamento = "Data anterior ao dia de hoje!";
            return "webAgendamentoContabilidade";
        }

        if (DataHoje.converteDataParaInteger(((new DataHoje()).incrementarMeses(3, DataHoje.data())))
                < DataHoje.converteDataParaInteger(DataHoje.converteData(data))) {
            msgAgendamento = "Data maior que 3 meses!";
            return "webAgendamentoContabilidade";
        }

        if (pesquisarFeriado()) {
            msgAgendamento = "Esta data esta cadastrada como Feriado!";
            return "webAgendamentoContabilidade";
        }

        renderAgendamento = false;
        renderConcluir = true;
        switch (Integer.parseInt(((SelectItem) getListaStatus().get(idStatus)).getDescription())) {
            case 1: {
                if (data == null) {
                    msgAgendamento = "Selecione uma data para Agendamento!";
                    renderAgendamento = true;
                    renderConcluir = false;
                } else {
                    renderBtnAgendar = true;
                    SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
                    if (empresa.getContabilidade() != null) {
                        agendamento.setTelefone(empresa.getContabilidade().getPessoa().getTelefone1());
                    }
                    if (profissao.getId() == -1) {
                        profissao = (Profissao) salvarAcumuladoDB.pesquisaCodigo(0, "Profissao");
                    }

                    agendamento.setData(DataHoje.converteData(data));
                    agendamento.setHorarios((Horarios) ((DataObject) listaGrid.get(idIndex)).getArgumento0());
                    msgAgendamento = "";
                    //agendamento.setFilial((Filial) salvarAcumuladoDB.pesquisaCodigo(1, "Filial"));
                    agendamento.setFilial( getSindicatoFilial().getFilial() );
                    agendamentoProtocolo = agendamento;
                }
                break;
            }
            case 2: {
                PessoaEnderecoDB db = new PessoaEnderecoDBToplink();
                renderBtnAgendar = false;
                agendamento = (Agendamento) ((DataObject) listaGrid.get(idIndex)).getArgumento9();
                agendamentoProtocolo = agendamento;
                fisica = ((PessoaEmpresa) ((DataObject) listaGrid.get(idIndex)).getArgumento7()).getFisica();
                enderecoFisica = db.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1);
                empresa = ((PessoaEmpresa) ((DataObject) listaGrid.get(idIndex)).getArgumento7()).getJuridica();
                pessoaEmpresa = agendamento.getPessoaEmpresa();
                profissao = ((PessoaEmpresa) ((DataObject) listaGrid.get(idIndex)).getArgumento7()).getFuncao();
                for (int i = 0; i < getListaMotivoDemissao().size(); i++) {
                    if (Integer.parseInt(getListaMotivoDemissao().get(i).getDescription()) == agendamento.getDemissao().getId()) {
                        idMotivoDemissao = (Integer) getListaMotivoDemissao().get(i).getValue();
                        break;
                    }
                }
                tipoAviso = String.valueOf(pessoaEmpresa.isAvisoTrabalhado());
                break;
            }
        }
        return "webAgendamentoContabilidade";
    }

    public boolean pesquisarFeriado() {
        FeriadosDB db = new FeriadosDBToplink();
        List listFeriados = db.pesquisarPorData(DataHoje.converteData(getData()));
        if (!listFeriados.isEmpty()) {
            return true;
        }
        return false;
    }

    public String salvar() {
        SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
        Registro reg = (Registro) salvarAcumuladoDB.pesquisaCodigo(1, "Registro");
        if (!listaEmDebito.isEmpty() && !reg.isBloquearHomologacao()) {
            msgConfirma = "Para efetuar esse agendamento CONTATE o Sindicato!";
            return null;
        }

        if (reg.getMesesInadimplentesAgenda() != 0) {
            msgConfirma = "Para efetuar esse agendamento CONTATCE o Sindicato!";
            return null;
        }

        if (fisica.getPessoa().getNome().equals("") || fisica.getPessoa().getNome() == null) {
            msgConfirma = "Digite o nome do Funcionário!";
            return null;
        }

        if (!strContribuinte.isEmpty()) {
            msgConfirma = "Não é permitido agendar para uma empresa não contribuinte!";
            return null;
        }
        int ids[] = {1, 3, 4};
        DataHoje dataH = new DataHoje();
        Demissao demissao = (Demissao) salvarAcumuladoDB.pesquisaCodigo(Integer.parseInt(((SelectItem) getListaMotivoDemissao().get(idMotivoDemissao)).getDescription()), "Demissao");
        if (!pessoaEmpresa.getDemissao().isEmpty() && pessoaEmpresa.getDemissao() != null) {
            if (demissao.getId() == 1) {
                if (DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao())
                        > DataHoje.converteDataParaInteger(dataH.incrementarMeses(1, DataHoje.data()))) {
                    msgConfirma = "Por " + demissao.getDescricao() + " data de Demissão não pode ser maior que 30 dias!";
                    return null;
                }
            } else if (demissao.getId() == 2) {
                if (DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao())
                        > DataHoje.converteDataParaInteger(dataH.incrementarMeses(3, DataHoje.data()))) {
                    msgConfirma = "Por " + demissao.getDescricao() + " data de Demissão não pode ser maior que 90 dias!";
                    return null;
                }
            } else if (demissao.getId() == 3) {
                if (DataHoje.converteDataParaInteger(pessoaEmpresa.getDemissao())
                        > DataHoje.converteDataParaInteger(dataH.incrementarDias(10, DataHoje.data()))) {
                    msgConfirma = "Por " + demissao.getDescricao() + " data de Demissão não pode ser maior que 10 dias!";
                    return null;
                }
            }
        } else {
            msgConfirma = "Data de Demissão é obrigatória!";
            return null;
        }

        salvarAcumuladoDB.abrirTransacao();
        if (fisica.getId() == -1) {
            fisica.getPessoa().setTipoDocumento((TipoDocumento) salvarAcumuladoDB.pesquisaCodigo(1, "TipoDocumento"));
            if (!ValidaDocumentos.isValidoCPF(AnaliseString.extrairNumeros(fisica.getPessoa().getDocumento()))) {
                msgConfirma = "Documento Inválido!";
                return null;
            }
            if (salvarAcumuladoDB.inserirObjeto(fisica.getPessoa())) {
                salvarAcumuladoDB.inserirObjeto(fisica);
            } else {
                msgConfirma = "Erro ao Inserir pessoa!";
                salvarAcumuladoDB.desfazerTransacao();
                return null;
            }
        }

        HomologacaoDB dba = new HomologacaoDBToplink();
        Agendamento age = dba.pesquisaFisicaAgendada(fisica.getId());
        if (age != null) {
            msgConfirma = "Pessoa já foi agendada para empresa " + age.getPessoaEmpresa().getJuridica().getPessoa().getNome();
            salvarAcumuladoDB.desfazerTransacao();
            return null;
        }

        if (enderecoFisica.getId() == -1) {
            if (enderecoFisica.getEndereco().getId() != -1) {
                enderecoFisica.setPessoa(fisica.getPessoa());
                PessoaEndereco pesEnd = enderecoFisica;
                for (int i = 0; i < ids.length; i++) {
                    pesEnd.setTipoEndereco((TipoEndereco) salvarAcumuladoDB.pesquisaCodigo(ids[i], "TipoEndereco"));
                    if (!salvarAcumuladoDB.inserirObjeto(pesEnd)) {
                        msgConfirma = "Erro ao Inserir endereço da pessoa!";
                        salvarAcumuladoDB.desfazerTransacao();
                        return null;
                    }
                    pesEnd = new PessoaEndereco();
                    pesEnd.setComplemento(enderecoFisica.getComplemento());
                    pesEnd.setEndereco(enderecoFisica.getEndereco());
                    pesEnd.setNumero(enderecoFisica.getNumero());
                    pesEnd.setPessoa(enderecoFisica.getPessoa());
                }
            }
        } else {
            PessoaEnderecoDB pessoaEnderecoDB = new PessoaEnderecoDBToplink();
            List<PessoaEndereco> ends = pessoaEnderecoDB.pesquisaEndPorPessoa(fisica.getPessoa().getId());
            for (int i = 0; i < ends.size(); i++) {
                ends.get(i).setComplemento(msgAgendamento);
                ends.get(i).setEndereco(enderecoFisica.getEndereco());
                ends.get(i).setNumero(enderecoFisica.getNumero());
                ends.get(i).setPessoa(enderecoFisica.getPessoa());
                if (!salvarAcumuladoDB.alterarObjeto(ends.get(i))) {
                    msgConfirma = "Erro ao atualizar endereço da pessoa!";
                    salvarAcumuladoDB.desfazerTransacao();
                    return null;
                }
            }
        }

        if (pessoaEmpresa.getId() == -1) {
            pessoaEmpresa.setFisica(fisica);
            pessoaEmpresa.setJuridica(empresa);
            pessoaEmpresa.setFuncao(profissao);
            pessoaEmpresa.setAvisoTrabalhado(Boolean.valueOf(tipoAviso));
            if (!salvarAcumuladoDB.inserirObjeto(pessoaEmpresa)) {
                msgConfirma = "Erro ao Pessoa Empresa!";
                salvarAcumuladoDB.desfazerTransacao();
                return null;
            }
        } else {
            pessoaEmpresa.setAvisoTrabalhado(Boolean.valueOf(tipoAviso));
            if (!salvarAcumuladoDB.alterarObjeto(pessoaEmpresa)) {
                msgConfirma = "Erro ao atualizar Pessoa Empresa!";
                salvarAcumuladoDB.desfazerTransacao();
                return null;
            }
        }

        AtendimentoDB dbat = new AtendimentoDBTopLink();
        if (dbat.pessoaOposicao(fisica.getPessoa().getDocumento())) {
            msgConfirma = "Pessoa cadastrada em oposição, não poderá ser agendada, contate seu Sindicato";
            salvarAcumuladoDB.comitarTransacao();
            return null;
        } else {
            if (agendamento.getId() == -1) {
                agendamento.setAgendador(null);
                agendamento.setRecepcao(null);
                agendamento.setDemissao((Demissao) salvarAcumuladoDB.pesquisaCodigo(Integer.parseInt(((SelectItem) getListaMotivoDemissao().get(idMotivoDemissao)).getDescription()), "Demissao"));
                agendamento.setHomologador(null);
                agendamento.setDtEmissao(DataHoje.dataHoje());
                agendamento.setPessoaEmpresa(pessoaEmpresa);
                agendamento.setStatus((Status) salvarAcumuladoDB.pesquisaCodigo(2, "Status"));
                if (salvarAcumuladoDB.inserirObjeto(agendamento)) {
                    msgConfirma = "Agendamento realizado com sucesso";
                    agendamentoProtocolo = agendamento;
                    limpar();
                } else {
                    msgConfirma = "Erro ao salvar protocolo!";
                    salvarAcumuladoDB.desfazerTransacao();
                    return null;
                }
            } else {
                agendamento.setDemissao((Demissao) salvarAcumuladoDB.pesquisaCodigo(Integer.parseInt(((SelectItem) getListaMotivoDemissao().get(idMotivoDemissao)).getDescription()), "Demissao"));
                if (salvarAcumuladoDB.alterarObjeto(agendamento)) {
                    msgConfirma = "Agendamento atualizado com sucesso";
                    agendamentoProtocolo = agendamento;
                    limpar();
                } else {
                    msgConfirma = "Erro ao atualizar protocolo!";
                    salvarAcumuladoDB.desfazerTransacao();
                    return null;
                }
            }
            salvarAcumuladoDB.comitarTransacao();
            imprimirPro = true;
        }
        return null;
    }

    public String cancelar() {
        strEndereco = "";
        renderAgendamento = true;
        renderConcluir = false;
        tipoAviso = "true";
        //msgConfirma = "";
        msgAgendamento = "";
        fisica = new Fisica();
        agendamento = new Agendamento();
        agendamentoProtocolo = agendamento;
        pessoaEmpresa = new PessoaEmpresa();
        profissao = new Profissao();
        empresa = new Juridica();
        enderecoFisica = new PessoaEndereco();
        return "webAgendamentoContabilidade";
    }

    public void limpar() {
        strEndereco = "";
        renderAgendamento = true;
        renderConcluir = false;
        tipoAviso = "true";
        //msgConfirma = "";
        msgAgendamento = "";
        fisica = new Fisica();
        pessoaEmpresa = new PessoaEmpresa();
        agendamento = new Agendamento();
        profissao = new Profissao();
        empresa = new Juridica();
        enderecoFisica = new PessoaEndereco();
    }

    public void pesquisarFuncionarioCPF() throws IOException {
        if (!fisica.getPessoa().getDocumento().isEmpty() && !fisica.getPessoa().getDocumento().equals("___.___.___-__")) {
            String documento = fisica.getPessoa().getDocumento();
            SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
            FisicaDB dbFis = new FisicaDBToplink();
            PessoaEnderecoDB dbp = new PessoaEnderecoDBToplink();
            HomologacaoDB db = new HomologacaoDBToplink();
            fisica.getPessoa().setTipoDocumento((TipoDocumento) salvarAcumuladoDB.pesquisaCodigo(1, "TipoDocumento"));
            PessoaEmpresa pe = db.pesquisaPessoaEmpresaOutra(documento);

            if (pe.getId() != -1 && pe.getJuridica().getId() != empresa.getId()) {
                msgConfirma = "Esta pessoa pertence a Empresa " + pe.getJuridica().getPessoa().getNome();
                fisica = new Fisica();
                enderecoFisica = new PessoaEndereco();
                return;
            }

            List<Fisica> listFisica = dbFis.pesquisaFisicaPorDocSemLike(fisica.getPessoa().getDocumento());
            if (!listFisica.isEmpty()) {
                for (int i = 0; i < listFisica.size(); i++) {
                    if (listFisica.get(i).getId() != fisica.getId()) {
                        fisica = listFisica.get(i);
                        break;
                    }
                }
                if ((enderecoFisica = dbp.pesquisaEndPorPessoaTipo(fisica.getPessoa().getId(), 1)) == null) {
                    enderecoFisica = new PessoaEndereco();
                }
            }

            Oposicao op = db.pesquisaFisicaOposicaoAgendamento(documento, empresa.getId(), DataHoje.ArrayDataHoje()[2] + DataHoje.ArrayDataHoje()[1]);
            if (op == null) {
                msgConfirma = "Erro na pesquisa Oposição!";
                op = new Oposicao();
            }

            if (fisica.getId() == -1 && op.getId() != -1) {
                fisica.getPessoa().setNome(op.getOposicaoPessoa().getNome());
                fisica.setRg(op.getOposicaoPessoa().getRg());
                fisica.setSexo(op.getOposicaoPessoa().getSexo());
                fisica.getPessoa().setDocumento(documento);
            }

            if (op.getId() != -1) {
                //msgConfirma = "Este CPF possui carta de oposição em "+op.getEmissao();
                //return;
            }
        } else {
            if (fisica.getId() != -1) {
                fisica = new Fisica();
                enderecoFisica = new PessoaEndereco();
            }
        }
        msgConfirma = "";
        FacesContext.getCurrentInstance().getExternalContext().redirect("/Sindical/webAgendamentoContabilidade.jsf");
    }

    public String pesquisaEndereco() {
        EnderecoDB db = new EnderecoDBToplink();
        listaEnderecos.clear();
        if (!cepEndereco.isEmpty()) {
            listaEnderecos = db.pesquisaEnderecoCep(cepEndereco);
        }
        return null;
    }

    public String editarEndereco() {
        enderecoFisica.setEndereco((Endereco) listaEnderecos.get(idIndexEndereco));
        return null;
    }

// VERIFICAR UMA FORMA PRA BLOQUEAR ESSE FERIADO ANTES DE AGENDAR --------------------------------------
//    public boolean pesquisarFeriado(){
//        FeriadosDB db = new FeriadosDBToplink();
//        List listFeriados  = new ArrayList();
//        listFeriados = db.pesquisarPorDataFilial( DataHoje.converteData(data), macFilial.getFilial());
//        if (!listFeriados.isEmpty())
//            return true;
//        else
//            return false;
//    }
    public int getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(int idStatus) {
        this.idStatus = idStatus;
    }

    public int getIdMotivoDemissao() {
        return idMotivoDemissao;
    }

    public void setIdMotivoDemissao(int idMotivoDemissao) {
        this.idMotivoDemissao = idMotivoDemissao;
    }

    public String getStrSalvar() {
        return strSalvar;
    }

    public void setStrSalvar(String strSalvar) {
        this.strSalvar = strSalvar;
    }

    public boolean isRenderAgendamento() {
        return renderAgendamento;
    }

    public void setRenderAgendamento(boolean renderAgendamento) {
        this.renderAgendamento = renderAgendamento;
    }

    public String getMsgAgendamento() {
        return msgAgendamento;
    }

    public void setMsgAgendamento(String msgAgendamento) {
        this.msgAgendamento = msgAgendamento;
    }

    public boolean isRenderConcluir() {
        return renderConcluir;
    }

    public void setRenderConcluir(boolean renderConcluir) {
        this.renderConcluir = renderConcluir;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public String getTipoAviso() {
        return tipoAviso;
    }

    public void setTipoAviso(String tipoAviso) {
        this.tipoAviso = tipoAviso;
    }

    public Fisica getFisica() {
        return fisica;
    }

    public void setFisica(Fisica fisica) {
        this.fisica = fisica;
    }

    public PessoaEmpresa getPessoaEmpresa() {
        return pessoaEmpresa;
    }

    public void setPessoaEmpresa(PessoaEmpresa pessoaEmpresa) {
        this.pessoaEmpresa = pessoaEmpresa;
    }

    public String getStatusEmpresa() {
        HomologacaoDB db = new HomologacaoDBToplink();
        if (empresa.getId() != -1) {
            listaEmDebito = db.pesquisaPessoaDebito(empresa.getPessoa().getId(), DataHoje.data());
        }
        if (!listaEmDebito.isEmpty()) {
            statusEmpresa = "EM DÉBITO";
        } else {
            statusEmpresa = "REGULAR";
        }
        return statusEmpresa;
    }

    public void setStatusEmpresa(String statusEmpresa) {
        this.statusEmpresa = statusEmpresa;
    }

    public String getStrEndereco() {
        if (!getListaEmpresaPertencentes().isEmpty()) {
            SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
            PessoaEnderecoDB pessoaEnderecoDB = new PessoaEnderecoDBToplink();
            empresa = (Juridica) salvarAcumuladoDB.pesquisaCodigo(Integer.parseInt(((SelectItem) getListaEmpresaPertencentes().get(idSelectRadio)).getDescription()), "Juridica");
            enderecoEmpresa = pessoaEnderecoDB.pesquisaEndPorPessoaTipo(empresa.getPessoa().getId(), 5);
            if (enderecoEmpresa.getId() != -1) {
                String strCompl;
                if (enderecoEmpresa.getComplemento().equals("")) {
                    strCompl = " ";
                } else {
                    strCompl = " ( " + enderecoEmpresa.getComplemento() + " ) ";
                }

                strEndereco = enderecoEmpresa.getEndereco().getLogradouro().getDescricao() + " "
                        + enderecoEmpresa.getEndereco().getDescricaoEndereco().getDescricao() + ", " + enderecoEmpresa.getNumero() + " " + enderecoEmpresa.getEndereco().getBairro().getDescricao() + ","
                        + strCompl + enderecoEmpresa.getEndereco().getCidade().getCidade() + " - " + enderecoEmpresa.getEndereco().getCidade().getUf() + " - " + AnaliseString.mascaraCep(enderecoEmpresa.getEndereco().getCep());
            } else {
                strEndereco = "";
            }
            //}else{
            //    enderecoEmpresa = new PessoaEndereco();
            //    strEndereco = "";
            // }
        } else {
            strEndereco = "";
        }
        return strEndereco;
    }

    public void setStrEndereco(String strEndereco) {
        this.strEndereco = strEndereco;
    }

    public PessoaEndereco getEnderecoEmpresa() {
        return enderecoEmpresa;
    }

    public void setEnderecoEmpresa(PessoaEndereco enderecoEmpresa) {
        this.enderecoEmpresa = enderecoEmpresa;
    }

    public Juridica getJuridica() {
        JuridicaDB db = new JuridicaDBToplink();
        if (juridica.getId() == -1) {
            juridica = db.pesquisaJuridicaPorPessoa(((Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("sessaoUsuarioAcessoWeb")).getId());
        }
        return juridica;
    }

    public void setJuridica(Juridica juridica) {
        this.juridica = juridica;
    }

    public Juridica getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Juridica empresa) {
        this.empresa = empresa;
    }

    public int getIdSelectRadio() {
        return idSelectRadio;
    }

    public void setIdSelectRadio(int idSelectRadio) {
        this.idSelectRadio = idSelectRadio;
    }

    public String getMsgConfirma() {
        return msgConfirma;
    }

    public void setMsgConfirma(String msgConfirma) {
        this.msgConfirma = msgConfirma;
    }

    public boolean isChkFiltrar() {
        return chkFiltrar;
    }

    public void setChkFiltrar(boolean chkFiltrar) {
        this.chkFiltrar = chkFiltrar;
    }

    public boolean isRenderBtnAgendar() {
        return renderBtnAgendar;
    }

    public void setRenderBtnAgendar(boolean renderBtnAgendar) {
        this.renderBtnAgendar = renderBtnAgendar;
    }

    public String getCepEndereco() {
        return cepEndereco;
    }

    public void setCepEndereco(String cepEndereco) {
        this.cepEndereco = cepEndereco;
    }

    public List getListaEnderecos() {
        return listaEnderecos;
    }

    public void setListaEnderecos(List listaEnderecos) {
        this.listaEnderecos = listaEnderecos;
    }

    public PessoaEndereco getEnderecoFisica() {
        if (enderecoFisica == null) {
            enderecoFisica = new PessoaEndereco();
        }
        return enderecoFisica;
    }

    public void setEnderecoFisica(PessoaEndereco enderecoFisica) {
        this.enderecoFisica = enderecoFisica;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public void setIdIndex(int idIndex) {
        this.idIndex = idIndex;
    }

    public int getIdIndexEndereco() {
        return idIndexEndereco;
    }

    public void setIdIndexEndereco(int idIndexEndereco) {
        this.idIndexEndereco = idIndexEndereco;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getFiltraPor() {
        return filtraPor;
    }

    public void setFiltraPor(String filtraPor) {
        this.filtraPor = filtraPor;
    }

//    public CalendarDataModel getCalendarModel() {
//        return calendarModel;
//    }
//
//    public void setCalendarModel(CalendarDataModel calendarModel) {
//        this.calendarModel = calendarModel;
//    }
    public boolean isImprimirPro() {
        return imprimirPro;
    }

    public void setImprimirPro(boolean imprimirPro) {
        this.imprimirPro = imprimirPro;
    }

    public String getStrContribuinte() {
        if (empresa.getId() != -1) {
            JuridicaDB db = new JuridicaDBToplink();
            List listax = db.listaJuridicaContribuinte(empresa.getId());
            for (int i = 0; i < listax.size(); i++) {
                if (((List) listax.get(0)).get(11) != null) {
                    return strContribuinte = "Empresa Inativa";
                } else {
                    return strContribuinte = "";
                }
            }
        }
        return strContribuinte = "Empresa não contribuinte, não poderá efetuar um agendamento!";
    }

    public FilialCidade getSindicatoFilial() {
        getStrEndereco();
        if (empresa.getId() != -1 && enderecoEmpresa.getId() != -1) {
            FilialCidadeDB db = new FilialCidadeDBToplink();
            sindicatoFilial = db.pesquisaFilialPorCidade(enderecoEmpresa.getEndereco().getCidade().getId());
        }
        return sindicatoFilial;
    }

    public void setSindicatoFilial(FilialCidade sindicatoFilial) {
        this.sindicatoFilial = sindicatoFilial;
    }

    public PessoaEndereco getEnderecoFilial() {
        PessoaEnderecoDB pessoaEnderecoDB = new PessoaEnderecoDBToplink();
        if (enderecoFilial.getId() == -1) {
            enderecoFilial = pessoaEnderecoDB.pesquisaEndPorPessoaTipo(sindicatoFilial.getFilial().getFilial().getPessoa().getId(), 2);
        }
        return enderecoFilial;
    }

    public void setEnderecoFilial(PessoaEndereco enderecoFilial) {
        this.enderecoFilial = enderecoFilial;
    }

    public Registro getRegistro() {
        if (registro.getId() == -1) {
            registro = (Registro) new SalvarAcumuladoDBToplink().pesquisaCodigo(1, "Registro");
        }
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }

    public Agendamento getAgendamentoProtocolo() {
        return agendamentoProtocolo;
    }

    public void setAgendamentoProtocolo(Agendamento agendamentoProtocolo) {
        this.agendamentoProtocolo = agendamentoProtocolo;
    }
}
