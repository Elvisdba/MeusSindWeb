package br.com.rtools.associativo.beans;

import br.com.rtools.arrecadacao.beans.GerarBoletoBean;
import br.com.rtools.cobranca.BancoDoBrasil;
import br.com.rtools.cobranca.Bradesco;
import br.com.rtools.cobranca.CaixaFederalSicob;
import br.com.rtools.cobranca.CaixaFederalSigCB;
import br.com.rtools.cobranca.CaixaFederalSindical;
import br.com.rtools.cobranca.Cobranca;
import br.com.rtools.cobranca.Itau;
import br.com.rtools.cobranca.Real;
import br.com.rtools.cobranca.Santander;
import br.com.rtools.cobranca.Sicoob;
import br.com.rtools.financeiro.Boleto;
import br.com.rtools.financeiro.Movimento;
import br.com.rtools.financeiro.db.FinanceiroDB;
import br.com.rtools.financeiro.db.FinanceiroDBToplink;
import br.com.rtools.financeiro.db.MovimentoDB;
import br.com.rtools.financeiro.db.MovimentoDBToplink;
import br.com.rtools.impressao.Etiquetas;
import br.com.rtools.impressao.ParametroBoletoSocial;
import br.com.rtools.pessoa.Filial;
import br.com.rtools.pessoa.Fisica;
import br.com.rtools.pessoa.Juridica;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.pessoa.PessoaEndereco;
import br.com.rtools.pessoa.beans.FisicaBean;
import br.com.rtools.pessoa.beans.JuridicaBean;
import br.com.rtools.pessoa.db.FisicaDB;
import br.com.rtools.pessoa.db.FisicaDBToplink;
import br.com.rtools.pessoa.db.JuridicaDB;
import br.com.rtools.pessoa.db.JuridicaDBToplink;
import br.com.rtools.pessoa.db.PessoaEnderecoDB;
import br.com.rtools.pessoa.db.PessoaEnderecoDBToplink;
import br.com.rtools.seguranca.controleUsuario.ChamadaPaginaBean;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.sistema.beans.UploadFilesBean;
import br.com.rtools.utilitarios.Dao;
import br.com.rtools.utilitarios.DataHoje;
import br.com.rtools.utilitarios.DataObject;
import br.com.rtools.utilitarios.Download;
import br.com.rtools.utilitarios.GenericaMensagem;
import br.com.rtools.utilitarios.GenericaSessao;
import br.com.rtools.utilitarios.Moeda;
import br.com.rtools.utilitarios.PF;
import br.com.rtools.utilitarios.SalvarAcumuladoDB;
import br.com.rtools.utilitarios.SalvarAcumuladoDBToplink;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

@ManagedBean
@SessionScoped
public class ImpressaoBoletoSocialBean {
    private List<DataObject> listaGrid = new ArrayList();
    private int de = 0;
    private int ate = 0;
    private boolean imprimeVerso = true;
    
    private String strResponsavel = "";
    private String strLote = "";
    private String strData = "";
    
    private String tipo = "fisica";
    private Integer qntFolhas = 0;
    
    private List<Pessoa> listaPessoaSemEndereco = new ArrayList();
    private boolean atualizaListaPessoaSemEndereco = true;
    private Integer qntPessoasSelecionadas = 0;
    private String valorTotal = "0,00";
    
    @PostConstruct
    public void init(){
        UploadFilesBean uploadFilesBean = new UploadFilesBean("Imagens/");
        GenericaSessao.put("uploadFilesBean", uploadFilesBean);
    }
    
    public void atualizaValores(){
        float soma_valor = 0;
        qntPessoasSelecionadas = 0;
        for(DataObject ldo : listaGrid){
            if((Boolean)ldo.getArgumento1()){
                soma_valor = Moeda.somaValores(soma_valor, Moeda.converteUS$(ldo.getArgumento3().toString()));
                qntPessoasSelecionadas++;
            }
        }
        valorTotal = Moeda.converteR$Float(soma_valor);
    }    
    
    public String editarPessoaSemEndereco(Pessoa pessoa){
        ChamadaPaginaBean cp = (ChamadaPaginaBean) GenericaSessao.getObject("chamadaPaginaBean");
        String pagina = "";
        
        FisicaDB dbf = new FisicaDBToplink();
        Fisica f = dbf.pesquisaFisicaPorPessoa(pessoa.getId());
        if (f != null){
            pagina = cp.pessoaFisica();
            FisicaBean fb = new FisicaBean();
            fb.editarFisica(f);
            GenericaSessao.put("fisicaBean", fb);
            setAtualizaListaPessoaSemEndereco(true);
        }else{
            JuridicaDB jdb = new JuridicaDBToplink();
            Juridica j = jdb.pesquisaJuridicaPorPessoa(pessoa.getId());
            
            pagina = cp.pessoaJuridica();
            
            JuridicaBean jb = new JuridicaBean();
            jb.editar(j);
            GenericaSessao.put("juridicaBean", jb);
            setAtualizaListaPessoaSemEndereco(true);
        }
        return pagina;
    }
    
    public String qntDeFolhas(String nrCtrBoleto){
        FinanceiroDB db = new FinanceiroDBToplink();
        List<Vector> lista_socio;
        if (tipo.equals("fisica"))
            lista_socio = db.listaBoletoSocioFisica(nrCtrBoleto); // NR_CTR_BOLETO
        else
            lista_socio = db.listaBoletoSocioJuridica(nrCtrBoleto); // NR_CTR_BOLETO
        
        return String.valueOf(lista_socio.size());
    }
    
    public void loadLista(){
        listaGrid.clear();
        listaPessoaSemEndereco.clear();
        
        if (strResponsavel.length() == 1 && strLote.isEmpty() && strData.isEmpty()){
            GenericaMensagem.warn("Atenção", "Muitos resultatos na pesquisa pode gerar lentidão!");
            return;
        }
        
        if (!strResponsavel.isEmpty() || !strLote.isEmpty() || !strData.isEmpty()){
            FinanceiroDB db = new FinanceiroDBToplink();
            List<Vector> lista_agrupado = db.listaBoletoSocioAgrupado(strResponsavel, strLote, strData, tipo);
            
            int contador = 1;
            for (int i = 0; i < lista_agrupado.size(); i++){
                List<Vector> lista_socio;
                if (tipo.equals("fisica"))
                    lista_socio = db.listaQntPorFisica(lista_agrupado.get(i).get(0).toString()); // NR_CTR_BOLETO
                else
                    lista_socio = db.listaQntPorJuridica(lista_agrupado.get(i).get(0).toString()); // NR_CTR_BOLETO
            
                if (qntFolhas == 0){
                    // TODAS
                    listaGrid.add(new DataObject(contador, true, lista_agrupado.get(i), Moeda.converteR$(lista_agrupado.get(i).get(6).toString()), calculoDePaginas(lista_socio.size()), null));
                    contador++;
                }else if (qntFolhas == 1 && lista_socio.size() <= 25){ // 25 quantidade de linhas que cabe em um boleto sem que estore
                    // APENAS COM 1 PÁGINA    
                    listaGrid.add(new DataObject(contador, true, lista_agrupado.get(i), Moeda.converteR$(lista_agrupado.get(i).get(6).toString()), calculoDePaginas(lista_socio.size()), null));
                    contador++;
                }else if (qntFolhas == 2 && (lista_socio.size() >= 26 && lista_socio.size() <= 125)){
                    // DE 2 A 5 PAGINAS    
                    listaGrid.add(new DataObject(contador, true, lista_agrupado.get(i), Moeda.converteR$(lista_agrupado.get(i).get(6).toString()), calculoDePaginas(lista_socio.size()), null));
                    contador++;
                }else if (qntFolhas == 3 && lista_socio.size() > 126){
                    // ACIMA DE 5 PAGINAS    
                    listaGrid.add(new DataObject(contador, true, lista_agrupado.get(i), Moeda.converteR$(lista_agrupado.get(i).get(6).toString()), calculoDePaginas(lista_socio.size()), null));
                    contador++;
                }
                
                // FILTRA PESSOAS SEM ENDERECO ---
                if (lista_agrupado.get(i).get(7) == null || lista_agrupado.get(i).get(7).toString().isEmpty()){
                    listaPessoaSemEndereco.add((Pessoa)new Dao().find(new Pessoa(), Integer.valueOf(lista_agrupado.get(i).get(8).toString())));
                }
            }
            setAtualizaListaPessoaSemEndereco(false);
            atualizaValores();
        }
    }
    
    public int calculoDePaginas(int quantidade){
        float soma = Moeda.divisaoValores(quantidade, 25);
        // return ((int) Math.ceil(soma) == 0) ? 1 : (int) Math.ceil(soma); // CALCULO
        return (int) Math.ceil(soma);
    }
    
    public void alterarPathImagem(String path){
        UploadFilesBean uploadFilesBean = new UploadFilesBean("Imagens/");
        GenericaSessao.put("uploadFilesBean", uploadFilesBean);
    }
    
//    public String imagemBannerBoletoSocial(){
//        File file_promo = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/BannerPromoBoleto.png"));
//
//        if (!file_promo.exists())
//            return null;
//        else
//            return "Imagens/BannerPromoBoleto.png";
//    } 
//    
//    public String imagemVersoBannerBoletoSocial(){
//        File file_verso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoBoletoVersoSocial.png"));
//
//        if (!file_verso.exists())
//            return null;
//        else
//            return file_verso.getPath();
//    } 
//    
    public void marcar(){
        for (int i = 0; i < listaGrid.size(); i++){
            if ((i+1) >= de && ate == 0)
                listaGrid.get(i).setArgumento1(true);
            else if ((i+1) >= de && (i+1) <= ate)
                listaGrid.get(i).setArgumento1(true);
            else if (de == 0 && (i+1) <= ate)
                listaGrid.get(i).setArgumento1(true);
            else
                listaGrid.get(i).setArgumento1(false);
        }
        
        atualizaValores();
    }
    
    public void desmarcarTudo(){
        for (int i = 0; i < listaGrid.size(); i++){
            listaGrid.get(i).setArgumento1(false);
        }
        
        atualizaValores();
    }
    
    public void imprimir(){
        if (!listaPessoaSemEndereco.isEmpty()){
            GenericaMensagem.fatal("Atenção", "Existem pessoas sem endereço, favor cadastra-las!");
            return;
        }
        
        List lista = new ArrayList();
        //List<Vector> result = new ArrayList<Vector>();//db.listaChequesRecebidos(ids_filial, ids_caixa, tipo, d_i, d_f, id_status);
        
        SalvarAcumuladoDB sv = new SalvarAcumuladoDBToplink();
        Filial filial = (Filial) sv.pesquisaCodigo(1, "Filial");
        FinanceiroDB db = new FinanceiroDBToplink();
        
        Map<String, Object> map = new LinkedHashMap();  
                
        float valor = 0, valor_total = 0;
        
        try {
            File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/BOLETO_SOCIAL.jasper"));
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);
            
            File file_jasper_verso = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/BOLETO_SOCIAL_VERSO.jasper"));
            JasperReport jasperReportVerso = (JasperReport) JRLoader.loadObject(file_jasper_verso);
            
            List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
            File file_promo = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/BannerPromoBoleto.png"));
            
            if (!file_promo.exists())
                file_promo = null;
            
            MovimentoDB movDB = new MovimentoDBToplink();
            Cobranca cobranca = null;
            
            
            for (int i = 0; i < listaGrid.size(); i++){
                if ((Boolean)listaGrid.get(i).getArgumento1()){
                    Pessoa pessoa = (Pessoa)(new Dao()).find(new Pessoa(), (Integer)((Vector)((DataObject)listaGrid.get(i)).getArgumento2()).get(8));
                    List<Vector> lista_socio = null;
                    if (tipo.equals("fisica"))
                        lista_socio = db.listaBoletoSocioFisica((String) ((Vector)listaGrid.get(i).getArgumento2()).get(0)); // NR_CTR_BOLETO
                    else 
                        lista_socio = db.listaBoletoSocioJuridica((String) ((Vector)listaGrid.get(i).getArgumento2()).get(0)); // NR_CTR_BOLETO

                    for (int w = 0; w < lista_socio.size(); w++){
                        Boleto boletox = movDB.pesquisaBoletos("'"+(String) ((Vector)listaGrid.get(i).getArgumento2()).get(0)+"'"); // NR_CTR_BOLETO
                        //Movimento mov = (Movimento)sv.pesquisaCodigo((Integer)lista_socio.get(w).get(1), "Movimento");
                        List<Movimento> list_mov = movDB.listaMovimentoPorNrCtrBoleto(boletox.getNrCtrBoleto());
                        
                        valor = Moeda.converteUS$(lista_socio.get(w).get(14).toString());
                        valor_total = Moeda.somaValores(valor_total, Moeda.converteUS$(lista_socio.get(w).get(14).toString()));
                        
                        // ALTERO O VALOR DO MOVIMENTO PARA QUE NA SOMA FINAL DE O VALOR TOTAL DAS GUIAS
                        // O MÉTODO PADRÃO PEGA O VALOR DE UM MOVIMENTO APENAS
                        list_mov.get(0).setValor(valor_total);
        
                        if (boletox.getContaCobranca().getLayout().getId() == Cobranca.SINDICAL) {
                            cobranca = new CaixaFederalSindical(list_mov.get(0), boletox);
                            //swap[43] = "EXERC " + lista.get(i).getReferencia().substring(3);
                            //swap[42] = "BLOQUETO DE CONTRIBUIÇÃO SINDICAL URBANA.";
                        } else if ((boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.caixaFederal))
                                && (boletox.getContaCobranca().getLayout().getId() == Cobranca.SICOB)) {
                            cobranca = new CaixaFederalSicob(list_mov.get(0), boletox);
                        } else if ((boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.caixaFederal))
                                && (boletox.getContaCobranca().getLayout().getId() == Cobranca.SIGCB)) {
                            cobranca = new CaixaFederalSigCB(list_mov.get(0), boletox);
                        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.itau)) {
                            cobranca = new Itau(list_mov.get(0), boletox);
                        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.bancoDoBrasil)) {
                            cobranca = new BancoDoBrasil(list_mov.get(0), boletox);
                        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.real)) {
                            cobranca = new Real(list_mov.get(0), boletox);
                        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.bradesco)) {
                            cobranca = new Bradesco(list_mov.get(0), boletox);
                        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.santander)) {
                            cobranca = new Santander(list_mov.get(0), boletox);
                        } else if (boletox.getContaCobranca().getContaBanco().getBanco().getNumero().equals(Cobranca.sicoob)) {
                            cobranca = new Sicoob(list_mov.get(0), boletox);
                        }
                        
                        lista.add(new ParametroBoletoSocial(
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoCliente.png"), // LOGO SINDICATO
                                filial.getFilial().getPessoa().getNome(), 
                                lista_socio.get(w).get(5).toString(), // CODIGO
                                lista_socio.get(w).get(6).toString(), // RESPONSAVEL
                                DataHoje.converteData((Date)lista_socio.get(w).get(7)), // VENCIMENTO
                                (lista_socio.get(w).get(8) == null) ? "" : lista_socio.get(w).get(8).toString(), // MATRICULA
                                (lista_socio.get(w).get(10) == null) ? "" : lista_socio.get(w).get(10).toString(), // CATEGORIA
                                (lista_socio.get(w).get(9) == null) ? "" : lista_socio.get(w).get(9).toString(), // GRUPO
                                lista_socio.get(w).get(12).toString(), // CODIGO BENEFICIARIO
                                lista_socio.get(w).get(13).toString(), // BENEFICIARIO
                                lista_socio.get(w).get(11).toString(), // SERVICO
                                Moeda.converteR$Float(valor), // VALOR
                                Moeda.converteR$Float(valor_total), // VALOR TOTAL
                                Moeda.converteR$(lista_socio.get(w).get(15).toString()), // VALOR ATRASADAS
                                Moeda.converteR$Float(valor_total), // VALOR ATÉ  VALORVENCIMENTO
                                file_promo == null ? null : file_promo.getAbsolutePath(), // LOGO PROMOÇÃO
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath(boletox.getContaCobranca().getContaBanco().getBanco().getLogo().trim()), // LOGO BANCO
                                lista_socio.get(w).get(16).toString(), // MENSAGEM
                                lista_socio.get(w).get(18).toString(), // AGENCIA
                                cobranca.representacao(), // REPRESENTACAO
                                lista_socio.get(w).get(19).toString(), // CODIGO CEDENTE
                                lista_socio.get(w).get(20).toString(), // NOSSO NUMENTO
                                DataHoje.converteData((Date)lista_socio.get(w).get(4)), // PROCESSAMENTO
                                cobranca.codigoBarras(), // CODIGO DE BARRAS
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Imagens/serrilha.GIF"), // SERRILHA
                                lista_socio.get(w).get(31).toString() +" "+ lista_socio.get(w).get(32).toString(), // ENDERECO RESPONSAVEL
                                //lista_socio.get(w).get(30).toString() +" "+ lista_socio.get(w).get(31).toString(), // ENDERECO FILIAL
                                lista_socio.get(w).get(26).toString() +" "+ lista_socio.get(w).get(27).toString(), // ENDERECO FILIAL
                                lista_socio.get(w).get(35).toString() +" "+ lista_socio.get(w).get(34).toString() +" " + lista_socio.get(w).get(33).toString(), // COMPLEMENTO RESPONSAVEL
                                lista_socio.get(w).get(28).toString() +" - "+ lista_socio.get(w).get(29).toString() +" "+ lista_socio.get(w).get(30).toString(), // COMPLEMENTO FILIAL
                                lista_socio.get(w).get(24).toString(), // CNPJ FILIAL
                                lista_socio.get(w).get(25).toString(), // TELEFONE FILIAL
                                lista_socio.get(w).get(21).toString(), // EMAIL FILIAL
                                lista_socio.get(w).get(23).toString(), // SITE FILIAL
                                ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Imagens/LogoBoletoVersoSocial.png"), // LOGO BOLETO VERSO SOCIAL
                                lista_socio.get(w).get(37).toString(), // LOCAL DE PAGAMENTO
                                lista_socio.get(w).get(36).toString(), // INFORMATIVO
                                pessoa.getTipoDocumento().getDescricao()+": "+pessoa.getDocumento(), 
                                String.valueOf(lista_socio.size()),
                                boletox.getContaCobranca().getContaBanco().getBanco().getNumero()
                        ));
                    }
                
                    JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
                    jasperPrintList.add(JasperFillManager.fillReport(jasperReport, null, dtSource));
                    if (imprimeVerso){
                        dtSource = new JRBeanCollectionDataSource(lista);
                        jasperPrintList.add(JasperFillManager.fillReport(jasperReportVerso, null, dtSource));
                    }

                    lista.clear();
                    valor = 0;
                    valor_total = 0;
                }
            }
            
            JRPdfExporter exporter = new JRPdfExporter();
            ByteArrayOutputStream retorno = new ByteArrayOutputStream();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, retorno);
            exporter.setParameter(JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS, Boolean.TRUE);
            exporter.exportReport();

            byte[] arquivo = retorno.toByteArray();
            
            HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            res.setContentType("application/pdf");
            res.setHeader("Content-disposition", "inline; filename=\"Boleto Social.pdf\"");
            res.getOutputStream().write(arquivo);
            res.getCharacterEncoding();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (JRException e) {
            e.getMessage();
        } catch (IOException ex) {
            Logger.getLogger(GerarBoletoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    public void etiqueta(){
        if (!listaPessoaSemEndereco.isEmpty()){
            GenericaMensagem.fatal("Atenção", "Existem pessoas sem endereço, favor cadastra-las!");
            return;
        }
        
        List lista = new ArrayList();
        
        FinanceiroDB db = new FinanceiroDBToplink();
        
        try {
            for (int i = 0; i < listaGrid.size(); i++){
                if ((Boolean)listaGrid.get(i).getArgumento1()){
                    List<Vector> lista_socio;
                    PessoaEnderecoDB dbpe = new PessoaEnderecoDBToplink();
                    PessoaEndereco pe;
                        
                    if (tipo.equals("fisica")){
                        lista_socio = db.listaBoletoSocioFisica((String) ((Vector)listaGrid.get(i).getArgumento2()).get(0)); // NR_CTR_BOLETO
                    }else {
                        lista_socio = db.listaBoletoSocioJuridica((String) ((Vector)listaGrid.get(i).getArgumento2()).get(0)); // NR_CTR_BOLETO
                    }
                    
                    for (int w = 0; w < lista_socio.size(); w++){
                        if (tipo.equals("fisica")){
                            pe = dbpe.pesquisaEndPorPessoaTipo(Integer.valueOf(lista_socio.get(w).get(5).toString()), 1);
                        } else {
                            pe = dbpe.pesquisaEndPorPessoaTipo(Integer.valueOf(lista_socio.get(w).get(5).toString()), 5);
                        }
                        
                        if (pe != null){
                            lista.add(
                                    new Etiquetas(
                                            pe.getPessoa().getNome(), // NOME
                                            pe.getEndereco().getLogradouro().getDescricao(), // LOGRADOURO
                                            pe.getEndereco().getDescricaoEndereco().getDescricao(), // ENDERECO
                                            pe.getNumero(), // NÚMERO
                                            pe.getEndereco().getBairro().getDescricao(), // BAIRRO
                                            pe.getEndereco().getCidade().getCidade(), // CIDADE
                                            pe.getEndereco().getCidade().getUf(), // UF
                                            pe.getEndereco().getCep(), // CEP
                                            pe.getComplemento() // COMPLEMENTO
                                    ) 
                            );
                        }
                    }
                }
            }
            File file_jasper = new File(((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Relatorios/ETIQUETA_SOCIO.jasper"));
            
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file_jasper);
            
            JRBeanCollectionDataSource dtSource = new JRBeanCollectionDataSource(lista);
            
            List<JasperPrint> lista_jasper = new ArrayList();
            lista_jasper.add(JasperFillManager.fillReport(jasperReport, null, dtSource));
            
            JRPdfExporter exporter = new JRPdfExporter();
            
            String nomeDownload = "etiqueta_" + DataHoje.livre(DataHoje.dataHoje(), "yyyyMMdd-HHmmss") + ".pdf";
            String pathPasta = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/downloads/etiquetas");
            
            exporter.setExporterInput(SimpleExporterInput.getInstance(lista_jasper));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pathPasta+"/"+nomeDownload));
            
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            
            configuration.setCreatingBatchModeBookmarks(true);
            
            exporter.setConfiguration(configuration);
            
            exporter.exportReport();

            File fl = new File(pathPasta);
            
            if (fl.exists()){
                Download download = new Download(
                        nomeDownload,
                        pathPasta,
                        "application/pdf",
                        FacesContext.getCurrentInstance()
                );
                download.baixar();
                download.remover();
            }
        } catch (JRException e) {
            e.getMessage();
        } 
    }
    
    public List<DataObject> getListaGrid() {
        return listaGrid;
    }

    public void setListaGrid(List<DataObject> listaGrid) {
        this.listaGrid = listaGrid;
    }

    public int getDe() {
        return de;
    }

    public void setDe(int de) {
        this.de = de;
    }

    public int getAte() {
        return ate;
    }

    public void setAte(int ate) {
        this.ate = ate;
    }

    public boolean isImprimeVerso() {
        return imprimeVerso;
    }

    public void setImprimeVerso(boolean imprimeVerso) {
        this.imprimeVerso = imprimeVerso;
    }

    public String getStrResponsavel() {
        return strResponsavel;
    }

    public void setStrResponsavel(String strResponsavel) {
        this.strResponsavel = strResponsavel;
    }

    public String getStrLote() {
        return strLote;
    }

    public void setStrLote(String strLote) {
        this.strLote = strLote;
    }

    public String getStrData() {
        return strData;
    }

    public void setStrData(String strData) {
        this.strData = strData;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getQntFolhas() {
        return qntFolhas;
    }

    public void setQntFolhas(Integer qntFolhas) {
        this.qntFolhas = qntFolhas;
    }

    public List<Pessoa> getListaPessoaSemEndereco() {
        if (atualizaListaPessoaSemEndereco){
            loadLista();
        }
        return listaPessoaSemEndereco;
    }

    public void setListaPessoaSemEndereco(List<Pessoa> listaPessoaSemEndereco) {
        this.listaPessoaSemEndereco = listaPessoaSemEndereco;
    }

    public boolean isAtualizaListaPessoaSemEndereco() {
        return atualizaListaPessoaSemEndereco;
    }

    public void setAtualizaListaPessoaSemEndereco(boolean atualizaListaPessoaSemEndereco) {
        this.atualizaListaPessoaSemEndereco = atualizaListaPessoaSemEndereco;
    }

    public Integer getQntPessoasSelecionadas() {
        return qntPessoasSelecionadas;
    }

    public void setQntPessoasSelecionadas(Integer qntPessoasSelecionadas) {
        this.qntPessoasSelecionadas = qntPessoasSelecionadas;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }
}