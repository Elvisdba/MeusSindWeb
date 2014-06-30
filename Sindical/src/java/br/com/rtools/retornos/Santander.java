package br.com.rtools.retornos;

import br.com.rtools.financeiro.ContaCobranca;
import br.com.rtools.seguranca.Usuario;
import br.com.rtools.seguranca.controleUsuario.ControleUsuarioBean;
import br.com.rtools.utilitarios.ArquivoRetorno;
import br.com.rtools.utilitarios.GenericaRetorno;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class Santander extends ArquivoRetorno {

    public Santander(ContaCobranca contaCobranca, boolean pendentes) {
        super(contaCobranca, pendentes);
    }

    @Override
    public List<GenericaRetorno> sicob(boolean baixar, String host) {
        GenericaRetorno genericaRetorno = new GenericaRetorno();
        FacesContext context = FacesContext.getCurrentInstance();
        String pasta = "";
        String linha = null;
        String cnpj = "";
        String codigoCedente = "";
        String nossoNumero = "";
        String valorTaxa = "";
        String valorPago = "";
        String dataPagamento = "";
        String dataVencimento = "";
        String caminho = "";
        String valorCredito = "";
        String dataCredito = "";
        if (super.isPendentes()) {
            caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/retorno");
            if (baixar) {
                caminho += "/" + super.getContaCobranca().getApelido() + "_" + super.getContaCobranca().getCodCedente();
                caminho += "/pendentes";
                //pasta = "/"+super.getServicoContaCobranca().getServicos().getDescricao()+"_"+super.getServicoContaCobranca().getContaCobranca().getCodCedente()+"/pendentes";
                //            pasta = caminho;
            } else {
                //caminho += "/" +super.getServicoContaCobranca().getServicos().getDescricao()+"_"+super.getServicoContaCobranca().getContaCobranca().getCodCedente();
                //caminho += "/"+host;
                //pasta = "/"+super.getServicoContaCobranca().getServicos().getDescricao()+"_"+super.getServicoContaCobranca().getContaCobranca().getCodCedente()+"/"+host;
                //            pasta = caminho;
            }
        } else {
            caminho = ((ServletContext) context.getExternalContext().getContext()).getRealPath("/Cliente/" + ControleUsuarioBean.getCliente() + "/Arquivos/retorno/pendentes");
        }

        File fl = new File(caminho);
        File listFile[] = fl.listFiles();
        List<GenericaRetorno> listaRetorno = new ArrayList();
        if (listFile != null) {
            int qntRetornos = listFile.length;
            for (int u = 0; u < qntRetornos; u++) {
                try {
                    FileReader reader = new FileReader(caminho + "/" + listFile[u].getName());
                    BufferedReader buffReader = new BufferedReader(reader);
                    List lista = new Vector();
                    while ((linha = buffReader.readLine()) != null) {
                        lista.add(linha);
                    }
                    reader.close();
                    buffReader.close();
                    int i = 0;
                    while (i < lista.size()) {
                        if (i < 1) {
                            cnpj = ((String) lista.get(i)).substring(18, 32);
                            int codc = Integer.valueOf( lista.get(0).toString().substring(53, 61) );
                            codigoCedente = String.valueOf(codc);
                        }
                        if (((String) lista.get(i)).substring(13, 14).equals("T")) {
                            nossoNumero = ((String) lista.get(i)).substring(40, 52).trim();
                            valorTaxa = ((String) lista.get(i)).substring(193, 208);
                            dataVencimento = ((String) lista.get(i)).substring(69, 77);
                        }
                        try {
                            int con = Integer.parseInt(dataVencimento);
                            if (con == 0) {
                                dataVencimento = "11111111";
                            }
                        } catch (Exception e) {
                        }
                        i++;
                        if (i < lista.size() && ((String) lista.get(i)).substring(13, 14).equals("U")) {
                            valorPago = ((String) lista.get(i)).substring(77, 92);
                            dataPagamento = ((String) lista.get(i)).substring(137, 145);
                            valorCredito = ((String) lista.get(i)).substring(97, 107);
                            dataCredito = ((String) lista.get(i)).substring(145, 153);

                            genericaRetorno = new GenericaRetorno(
                                    cnpj, //1 ENTIDADE
                                    codigoCedente, //2 NESTE CASO SICAS
                                    nossoNumero, //3
                                    valorPago, //4
                                    valorTaxa, //5
                                    valorCredito,//valorCredito,   //6
                                    dataPagamento, //7
                                    dataVencimento,//dataVencimento, //8
                                    "", //9 ACRESCIMO
                                    "", //10 VALOR DESCONTO
                                    "", //11 VALOR ABATIMENTO
                                    "", //12 VALOR REPASSE ...(valorPago - valorCredito)
                                    pasta, // 13 NOME DA PASTA
                                    listFile[u].getName(), //14 NOME DO ARQUIVO
                                    dataCredito, //15 DATA CREDITO
                                    ""); // 16 SEQUENCIAL DO ARQUIVO
                            listaRetorno.add(genericaRetorno);
                            i++;
                        }
                    }
                } catch (IOException e) {
                } catch (NumberFormatException e) {
                }
            }
        }
        return listaRetorno;
    }

    @Override
    public List<GenericaRetorno> sindical(boolean baixar, String host) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<GenericaRetorno> sigCB(boolean baixar, String host) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String darBaixaSicob(String caminho, Usuario usuario) {
        String mensagem = super.baixarArquivo(this.sicob(true, ""), caminho, usuario);
        return mensagem;
    }

    @Override
    public String darBaixaSigCB(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaSindical(String caminho, Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }

    @Override
    public String darBaixaPadrao(Usuario usuario) {
        String mensagem = "NÃO EXISTE IMPLEMENTAÇÃO PARA ESTE TIPO!";
        return mensagem;
    }
}