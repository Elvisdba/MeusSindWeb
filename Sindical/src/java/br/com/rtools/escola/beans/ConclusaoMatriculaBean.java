package br.com.rtools.escola.beans;

import br.com.rtools.escola.EscStatus;
import br.com.rtools.escola.MatriculaEscola;
import br.com.rtools.escola.MatriculaIndividual;
import br.com.rtools.escola.MatriculaTurma;
import br.com.rtools.escola.Turma;
import br.com.rtools.escola.db.MatriculaEscolaDB;
import br.com.rtools.escola.db.MatriculaEscolaDBToplink;
import br.com.rtools.escola.lista.ListaMatriculaEscola;
import br.com.rtools.homologacao.db.HomologacaoDB;
import br.com.rtools.homologacao.db.HomologacaoDBToplink;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.SalvarAcumuladoDB;
import br.com.rtools.utilitarios.SalvarAcumuladoDBToplink;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.PostLoad;

@ManagedBean
@SessionScoped
public class ConclusaoMatriculaBean implements Serializable {

    private MatriculaEscola matriculaEscola;
    private MatriculaIndividual matriculaIndividual;
    private MatriculaTurma matriculaTurma;
    private Turma turma;
    private List<ListaMatriculaEscola> listaMatriculaEscola;
    private ListaMatriculaEscola[] matriculaEscolaSelecionado;
    private String mensagem;
    private boolean turmaPesquisa;

    @PostConstruct
    public void init() {
        matriculaEscola = new MatriculaEscola();
        matriculaIndividual = new MatriculaIndividual();
        matriculaTurma = new MatriculaTurma();
        listaMatriculaEscola = new ArrayList<ListaMatriculaEscola>();
        matriculaEscolaSelecionado = null;
        turma = new Turma();
        mensagem = "";
        turmaPesquisa = false;
    }

    @PreDestroy
    public void destroy() {
        /* chamado quando outra view for chamada através do UIViewRoot.setViewId(String viewId) */
    }

    public void novo() {
        GenericaSessao.remove("conclusaoMatriculaBean");
    }

    public void editar(ListaMatriculaEscola lme) {
        matriculaEscola = lme.getMatriculaEscola();
        matriculaTurma = lme.getMatriculaTurma();
        matriculaIndividual = lme.getMatriculaIndividual();
    }

    public void salvar() {
        HomologacaoDB hdb = new HomologacaoDBToplink();
        List list = hdb.pesquisaPessoaDebito(matriculaEscola.getResponsavel().getId(), DataHoje.data());
        if(!list.isEmpty()) {
            mensagem = "Responsável possui débitos!";
            return;            
        }
        if (matriculaEscola.getEscStatus().getId() == 3 || matriculaEscola.getEscStatus().getId() == 4) {
            mensagem = "Não é possível realizar a conclusão se o status estiver como trancado ou desistente!";
            return;
        }
        if (matriculaEscola.getEscStatus().getId() == 2) {
            mensagem = "Matrícula já concluída!";
            return;
        }
        int dataHoje = DataHoje.converteDataParaInteger(DataHoje.data());
        int dataConclusao;
        String dtConclusao;
        boolean isTurma = false;
        if (matriculaTurma != null && matriculaTurma.getId() != -1) {
            dataConclusao = DataHoje.converteDataParaInteger(matriculaTurma.getTurma().getDataTermino());
            dtConclusao = matriculaTurma.getTurma().getDataTermino();
            isTurma = true;
        } else if (matriculaIndividual != null && matriculaIndividual.getId() != -1) {
            dataConclusao = DataHoje.converteDataParaInteger(matriculaIndividual.getTermino());
            dtConclusao = matriculaIndividual.getTermino();
        } else {
            mensagem = "Matrícula não encontrada!";
            return;
        }
        if (dataHoje <= dataConclusao) {
            mensagem = "O curso ainda não foi concluído! Data da finalização do curso é " + dtConclusao;
            return;
        }
        SalvarAcumuladoDB sadb = new SalvarAcumuladoDBToplink();
        matriculaEscola.setEscStatus((EscStatus) sadb.find(new EscStatus(), 2));
        matriculaEscola.setStatus(DataHoje.dataHoje());
        sadb.abrirTransacao();
        if(sadb.alterarObjeto(matriculaEscola)) {
            sadb.comitarTransacao();
            listaMatriculaEscola.clear();
            mensagem = "Matrícula atualizada com sucesso!";
            if(isTurma) {
                GenericaSessao.put("matriculaTurmaPesquisa", matriculaTurma);
            } else {
                GenericaSessao.put("matriculaIndiviualPesquisa", matriculaIndividual);                
            }
        } else {
            sadb.desfazerTransacao();
        }
    }

    public void salvarTodos() {
        if (matriculaEscolaSelecionado.length == 0) {
            mensagem = "Nenhuma matrícula selecionada!";
            return;
        }
        HomologacaoDB hdb = new HomologacaoDBToplink();        
        int dataHoje = DataHoje.converteDataParaInteger(DataHoje.data());
        int dataConclusao;
        SalvarAcumuladoDB sadb = new SalvarAcumuladoDBToplink();
        EscStatus es = (EscStatus) sadb.find(new EscStatus(), 2);
        Date dataStatus = DataHoje.dataHoje();
        ListaMatriculaEscola lme = null;
        Turma t = new Turma();
        sadb.abrirTransacao();
        for (int i = 0; i < matriculaEscolaSelecionado.length; i++) {
            lme = matriculaEscolaSelecionado[i];
            if (i == 0) {
                t = lme.getMatriculaTurma().getTurma();
            }
            lme.getMatriculaEscola().setEscStatus(es);
            lme.getMatriculaEscola().setStatus(dataStatus);
            dataConclusao = DataHoje.converteDataParaInteger(lme.getMatriculaTurma().getTurma().getDataTermino());
            if (dataHoje > dataConclusao) {
                if (lme.getMatriculaEscola().getEscStatus().getId() != 3 || lme.getMatriculaEscola().getEscStatus().getId() != 4 && lme.getMatriculaEscola().getEscStatus().getId() == 2) {
                    List list = hdb.pesquisaPessoaDebito(matriculaEscola.getResponsavel().getId(), DataHoje.data());
                    if(!list.isEmpty()) {
                        sadb.desfazerTransacao();
                        return;            
                    }                    
                    if (!sadb.alterarObjeto(lme.getMatriculaEscola())) {
                        sadb.desfazerTransacao();
                        return;
                    }
                }
            }
        }
        sadb.comitarTransacao();
        GenericaSessao.put("turmaPesquisa", t);
        listaMatriculaEscola.clear();
        mensagem = "Matrículas atualizadas com sucesso!";
    }

//    public String load() {
//        getMatriculaTurma();
//        getMatriculaIndividual();
//        getTurma();
//        return null;
//    }
    public MatriculaEscola getMatriculaEscola() {
        return matriculaEscola;
    }

    public void setMatriculaEscola(MatriculaEscola matriculaEscola) {
        this.matriculaEscola = matriculaEscola;
    }

    public MatriculaIndividual getMatriculaIndividual() {
        return matriculaIndividual;
    }

    public void setMatriculaIndividual(MatriculaIndividual matriculaIndividual) {
        this.matriculaIndividual = matriculaIndividual;
    }

    public MatriculaTurma getMatriculaTurma() {
        return matriculaTurma;
    }

    public void setMatriculaTurma(MatriculaTurma matriculaTurma) {
        this.matriculaTurma = matriculaTurma;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @PostLoad
    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public List<ListaMatriculaEscola> getListaMatriculaEscola() {
        ListaMatriculaEscola lme = new ListaMatriculaEscola();
        int dataHoje = DataHoje.converteDataParaInteger(DataHoje.data());
        int dataConclusao;
        if (GenericaSessao.exists("turmaPesquisa")) {
            listaMatriculaEscola.clear();
            turma = (Turma) GenericaSessao.getObject("turmaPesquisa", true);
            MatriculaEscolaDB medb = new MatriculaEscolaDBToplink();
            List<MatriculaTurma> matriculaTurmas = (List<MatriculaTurma>) medb.pesquisaMatriculaEscolaPorTurma(turma.getId());
            if (!matriculaTurmas.isEmpty()) {
                dataConclusao = DataHoje.converteDataParaInteger(matriculaTurmas.get(0).getTurma().getDataTermino());
                if (dataHoje <= dataConclusao) {
                    GenericaMensagem.warn("Sistema", "Este curso ainda não foi finalizado!");
                    PF.update("i_growl");
                    return listaMatriculaEscola;
                }
                for (MatriculaTurma mt : matriculaTurmas) {
                    lme.setMatriculaEscola(mt.getMatriculaEscola());
                    lme.setMatriculaTurma(mt);
                    lme.setCurso(mt.getTurma().getCursos().getDescricao() + " - " + mt.getTurma().getDescricao());
                    lme.setPeriodo(mt.getTurma().getDataInicio() + " - " + mt.getTurma().getDataTermino());
                    listaMatriculaEscola.add(lme);
                    lme = new ListaMatriculaEscola();
                }
            }
            turmaPesquisa = true;
            turma = new Turma();
        }
        if (GenericaSessao.exists("matriculaTurmaPesquisa")) {
            listaMatriculaEscola.clear();
            matriculaTurma = (MatriculaTurma) GenericaSessao.getObject("matriculaTurmaPesquisa", true);
            dataConclusao = DataHoje.converteDataParaInteger(matriculaTurma.getTurma().getDataInicio());
            if (dataHoje <= dataConclusao) {
                GenericaMensagem.warn("Sistema", "Este curso ainda não foi finalizado!");
                PF.update("i_growl");
                return listaMatriculaEscola;
            }
            matriculaEscola = matriculaTurma.getMatriculaEscola();
            GenericaSessao.remove("matriculaEscolaPesquisa");
            lme.setMatriculaEscola(matriculaEscola);
            lme.setMatriculaTurma(matriculaTurma);
            lme.setCurso(matriculaTurma.getTurma().getCursos().getDescricao() + " - " + matriculaTurma.getTurma().getDescricao());
            lme.setPeriodo(matriculaTurma.getTurma().getDataInicio() + " - " + matriculaTurma.getTurma().getDataTermino());
            listaMatriculaEscola.add(lme);
            matriculaTurma = new MatriculaTurma();
            matriculaIndividual = new MatriculaIndividual();
            matriculaEscola = new MatriculaEscola();
            turmaPesquisa = false;
        }
        if (GenericaSessao.exists("matriculaIndividualPesquisa")) {
            listaMatriculaEscola.clear();
            matriculaIndividual = (MatriculaIndividual) GenericaSessao.getObject("matriculaIndividualPesquisa", true);
            dataConclusao = DataHoje.converteDataParaInteger(matriculaIndividual.getDataInicioString());
            if (dataHoje <= dataConclusao) {
                GenericaMensagem.warn("Sistema", "Este curso ainda não foi finalizado!");
                PF.update("form_conclusao:i_growl");
                return listaMatriculaEscola;
            }
            matriculaEscola = matriculaIndividual.getMatriculaEscola();
            GenericaSessao.remove("matriculaEscolaPesquisa");
            lme.setMatriculaEscola(matriculaEscola);
            lme.setMatriculaIndividual(matriculaIndividual);
            lme.setCurso(matriculaIndividual.getCurso().getDescricao());
            lme.setPeriodo(matriculaIndividual.getDataInicioString() + " - " + matriculaIndividual.getDataTerminoString());
            listaMatriculaEscola.add(lme);
            matriculaTurma = new MatriculaTurma();
            matriculaIndividual = new MatriculaIndividual();
            matriculaEscola = new MatriculaEscola();
            turmaPesquisa = false;
        }
        return listaMatriculaEscola;
    }

    public void setListaMatriculaEscola(List<ListaMatriculaEscola> listaMatriculaEscola) {
        this.listaMatriculaEscola = listaMatriculaEscola;
    }

    public boolean isTurmaPesquisa() {
        return turmaPesquisa;
    }

    public void setTurmaPesquisa(boolean turmaPesquisa) {
        this.turmaPesquisa = turmaPesquisa;
    }

    public ListaMatriculaEscola[] getMatriculaEscolaSelecionado() {
        return matriculaEscolaSelecionado;
    }

    public void setMatriculaEscolaSelecionado(ListaMatriculaEscola[] matriculaEscolaSelecionado) {
        this.matriculaEscolaSelecionado = matriculaEscolaSelecionado;
    }

}
