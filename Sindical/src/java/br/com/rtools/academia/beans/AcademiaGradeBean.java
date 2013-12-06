package br.com.rtools.academia.beans;

import br.com.rtools.academia.AcademiaGrade;
import br.com.rtools.academia.AcademiaSemana;
import br.com.rtools.academia.db.AcademiaDB;
import br.com.rtools.academia.db.AcademiaDBToplink;
import br.com.rtools.sistema.Semana;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.SalvarAcumuladoDB;
import br.com.rtools.utilitarios.SalvarAcumuladoDBToplink;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.event.SelectEvent;

@ManagedBean
@SessionScoped
public class AcademiaGradeBean implements Serializable {

    private AcademiaGrade academiaGrade = new AcademiaGrade();
    private AcademiaSemana academiaSemana = new AcademiaSemana();
    private List<AcademiaSemana> academiaSemanas = new ArrayList<AcademiaSemana>();
    private List<AcademiaGrade> listaAcademiaGrades = new ArrayList<AcademiaGrade>();
    private List<Semana> listaSemana = new ArrayList<Semana>();
    private String mensagem = "";
    private Date horaInicio = new Date();
    private Date horaFim = new Date();
    // SEMANA
    private boolean dom = false;
    private boolean seg = false;
    private boolean ter = false;
    private boolean qua = false;
    private boolean qui = false;
    private boolean sex = false;
    private boolean sab = false;

    public void init() {
        System.out.println("Iniciando grade academia");
        this.academiaGrade = new AcademiaGrade();
        this.mensagem = "";
        this.listaAcademiaGrades = new ArrayList<AcademiaGrade>();
    }

    public String salvar() {
        int horaInicioI = Integer.parseInt(academiaGrade.getHoraInicio().replace(":", ""));
        int horaFimI = Integer.parseInt(academiaGrade.getHoraFim().replace(":", ""));
        if (horaFimI <= horaInicioI) {
            GenericaMensagem.info("Validação", "Hora inicio deve ser maior que hora fim");
            return null;
        }
        SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
        if (academiaGrade.getId() == -1) {
//            AcademiaDB academiaDB = new AcademiaDBToplink();
//            if (((AcademiaGrade) academiaDB.existeAcademiaGrade(academiaGrade.getHoraInicio(), academiaGrade.getHoraFim())) != null) {
//                GenericaMensagem.warn("Sistema", "Horário já cadastrado!");
//                return null;
//            }
            salvarAcumuladoDB.abrirTransacao();
            if (salvarAcumuladoDB.inserirObjeto(academiaGrade)) {
                salvarAcumuladoDB.comitarTransacao();
                GenericaMensagem.info("Sucesso", "Registro inserido");
                salvarAcumuladoDB.abrirTransacao();                
                if (dom) {
                    addListaSemana(1);
                }
                if (seg) {
                    addListaSemana(2);
                }
                if (ter) {
                    addListaSemana(3);
                } 
                if (qua) {
                    addListaSemana(4);
                } 
                if (qui) {
                    addListaSemana(5);
                } 
                if (sex) {
                    addListaSemana(6);
                } 
                if (sab) {
                    addListaSemana(7);
                }
                for (int i = 0; i < academiaSemanas.size(); i++) {
                    if (!salvarAcumuladoDB.inserirObjeto(academiaSemanas.get(i))) {
                        salvarAcumuladoDB.desfazerTransacao();
                        academiaSemanas.clear();
                        break;
                    }
                }
                salvarAcumuladoDB.comitarTransacao();
                academiaSemanas.clear();
                listaAcademiaGrades.clear();
            } else {
                salvarAcumuladoDB.desfazerTransacao();
                GenericaMensagem.warn("Erro", "Ao adicionar registro!");
            }
        } else {
            salvarAcumuladoDB.abrirTransacao();
            if (salvarAcumuladoDB.alterarObjeto(academiaGrade)) {
                salvarAcumuladoDB.comitarTransacao();
                GenericaMensagem.info("Sucesso", "Registro atualizado");
                listaAcademiaGrades.clear();
            } else {
                salvarAcumuladoDB.desfazerTransacao();
                GenericaMensagem.warn("Erro", "Ao atualizar registro!");
            }
        }
        novo();
        return "academiaGrade";
    }

    public void addListaSemana(int idSemana) {
        for (int i = 0; i < getListaSemana().size(); i++) {
            if (listaSemana.get(i).getId() == idSemana) {
                academiaSemanas.add(new AcademiaSemana(-1, academiaGrade, listaSemana.get(i)));
                break;
            }
        }
    }
    
    public boolean showSemanaGrade(AcademiaGrade academiaGrade, int idSemana) {
        AcademiaDB academiaDB = new AcademiaDBToplink();
        boolean isSemana = academiaDB.existeAcademiaSemana(academiaGrade.getId(), idSemana);
        if (isSemana) {
            return true;
        }
        return false;
    }
    
    public void updateSemanaGrade(AcademiaGrade academiaGrade, int idSemana) {
        AcademiaDB academiaDB = new AcademiaDBToplink();
        AcademiaSemana as = academiaDB.pesquisaAcademiaSemana(academiaGrade.getId(), idSemana);
        SalvarAcumuladoDB dB = new SalvarAcumuladoDBToplink();
        if(as != null) {
            dB.abrirTransacao();
            if (dB.deletarObjeto((AcademiaSemana) dB.pesquisaCodigo(as.getId(), "AcademiaSemana"))) {
                dB.comitarTransacao();                
            } else {
                dB.desfazerTransacao();
            }
        } else {
            dB.abrirTransacao();
            AcademiaSemana academiaSemanax = new AcademiaSemana();
            academiaSemanax.setAcademiaGrade(academiaGrade);
            academiaSemanax.setSemana((Semana) dB.pesquisaCodigo(idSemana, "Semana"));
            if (dB.inserirObjeto(academiaSemanax)) {
                dB.comitarTransacao();                
            } else {
                dB.desfazerTransacao();
            }            
        }
    }

    public void novo() {
        horaInicio = new Date();
        horaFim = new Date();
        academiaGrade = new AcademiaGrade();
        academiaGrade.setHoraInicio(DataHoje.livre(new Date(), "HH:mm"));
        academiaGrade.setHoraFim(DataHoje.livre(new Date(), "HH:mm"));
        listaSemana.clear();
        academiaSemanas.clear();
        dom = false;
        seg = false;
        ter = false;
        qua = false;
        qui = false;
        sex = false;
        sab = false;
    }

    public void editar(AcademiaGrade ag) {
        academiaGrade = ag;
    }

    public void excluir(AcademiaGrade ag) {
        if (ag.getId() != -1) {
            SalvarAcumuladoDB salvarAcumuladoDB = new SalvarAcumuladoDBToplink();
            AcademiaDB academiaDB = new AcademiaDBToplink();
            salvarAcumuladoDB.abrirTransacao();
            List<AcademiaSemana> list = academiaDB.listaAcademiaSemana(ag.getId());
            for (int i = 0; i < list.size(); i++) {
                if (!salvarAcumuladoDB.deletarObjeto((AcademiaSemana) salvarAcumuladoDB.pesquisaCodigo(list.get(i).getId(), "AcademiaSemana"))) {
                    salvarAcumuladoDB.desfazerTransacao();
                    break;
                }
            }
            if (salvarAcumuladoDB.deletarObjeto((AcademiaGrade) salvarAcumuladoDB.pesquisaCodigo(ag.getId(), "AcademiaGrade"))) {
                salvarAcumuladoDB.comitarTransacao();
                GenericaMensagem.info("Sucesso", "Excluído com sucesso");
                listaAcademiaGrades.clear();
            } else {
                salvarAcumuladoDB.desfazerTransacao();
                GenericaMensagem.warn("Erro", "Ao excluir registro!");
            }
        }        
    }

    public AcademiaGrade getAcademiaGrade() {
        return academiaGrade;
    }

    public void setAcademiaGrade(AcademiaGrade academiaGrade) {
        this.academiaGrade = academiaGrade;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<AcademiaGrade> getListaAcademiaGrades() {
        if (listaAcademiaGrades.isEmpty()) {
            SalvarAcumuladoDB dB = new SalvarAcumuladoDBToplink();
            listaAcademiaGrades = (List<AcademiaGrade>) dB.listaObjeto("AcademiaGrade", true);
        }
        return listaAcademiaGrades;
    }

    public void setListaAcademiaGrades(List<AcademiaGrade> listaAcademiaGrades) {
        this.listaAcademiaGrades = listaAcademiaGrades;
    }

    public void atualizarGradeSemana() {
        AcademiaDB academiaDB = new AcademiaDBToplink();
        List<AcademiaSemana> list = academiaDB.listaAcademiaSemana(academiaGrade.getId());
//        for (int i = 0; i < listaSemana.size(); i++) {
//            for (int j = 0; j < list.size(); j++) {
//                if (dom) {                    
//                } else if (seg) {
//                } else if (ter) {
//                } else if (qua) {
//                } else if (qui) {
//                } else if (sex) {                
//                } else if (sab) {                
//                }
//            }
//            
//        }

    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(Date horaFim) {
        this.horaFim = horaFim;
    }

    public void selecionaHoraInicio(SelectEvent event) {
        this.academiaGrade.setHoraInicio(DataHoje.livre((Date) event.getObject(), "HH:mm"));
    }

    public void selecionaHoraFim(SelectEvent event) {
        this.academiaGrade.setHoraFim(DataHoje.livre((Date) event.getObject(), "HH:mm"));
    }

    public boolean isDom() {
        return dom;
    }

    public void setDom(boolean dom) {
        this.dom = dom;
    }

    public boolean isSeg() {
        return seg;
    }

    public void setSeg(boolean seg) {
        this.seg = seg;
    }

    public boolean isTer() {
        return ter;
    }

    public void setTer(boolean ter) {
        this.ter = ter;
    }

    public boolean isQua() {
        return qua;
    }

    public void setQua(boolean qua) {
        this.qua = qua;
    }

    public boolean isQui() {
        return qui;
    }

    public void setQui(boolean qui) {
        this.qui = qui;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public boolean isSab() {
        return sab;
    }

    public void setSab(boolean sab) {
        this.sab = sab;
    }

    public List<Semana> getListaSemana() {
        if (listaSemana.isEmpty()) {
            SalvarAcumuladoDB dB = new SalvarAcumuladoDBToplink();
            listaSemana = (List<Semana>) dB.listaObjeto("Semana");
        }
        return listaSemana;
    }

    public void setListaSemana(List<Semana> listaSemana) {
        this.listaSemana = listaSemana;
    }

    public AcademiaSemana getAcademiaSemana() {
        return academiaSemana;
    }

    public void setAcademiaSemana(AcademiaSemana academiaSemana) {
        this.academiaSemana = academiaSemana;
    }

    public List<AcademiaSemana> getAcademiaSemanas() {
        return academiaSemanas;
    }

    public void setAcademiaSemanas(List<AcademiaSemana> academiaSemanas) {
        this.academiaSemanas = academiaSemanas;
    }

}
