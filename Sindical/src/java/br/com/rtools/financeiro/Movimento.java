package br.com.rtools.financeiro;

import br.com.rtools.arrecadacao.Acordo;
import br.com.rtools.associativo.MatriculaSocios;
import br.com.rtools.associativo.Socios;
import br.com.rtools.associativo.db.SociosDB;
import br.com.rtools.associativo.db.SociosDBToplink;
import br.com.rtools.pessoa.Pessoa;
import br.com.rtools.utilitarios.DataHoje;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "FIN_MOVIMENTO")
@NamedQuery(name = "Movimento.pesquisaID", query = "select m from Movimento m where m.id=:pid")
public class Movimento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "ID_LOTE", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private Lote lote;
    @JoinColumn(name = "ID_PLANO5", referencedColumnName = "ID")
    @ManyToOne
    private Plano5 plano5;
    @JoinColumn(name = "ID_PESSOA", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private Pessoa pessoa;
    @JoinColumn(name = "ID_SERVICOS", referencedColumnName = "ID")
    @ManyToOne
    private Servicos servicos;
    @JoinColumn(name = "ID_BAIXA", referencedColumnName = "ID")
    @ManyToOne
    private Baixa baixa;
    @JoinColumn(name = "ID_TIPO_SERVICO", referencedColumnName = "ID")
    @ManyToOne
    private TipoServico tipoServico;
    @JoinColumn(name = "ID_ACORDO", referencedColumnName = "ID")
    @ManyToOne
    private Acordo acordo;
    @Column(name = "NR_VALOR", length = 10, nullable = false)
    private float valor;
    @Column(name = "DS_REFERENCIA", length = 7)
    private String referencia;
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VENCIMENTO")
    private Date dtVencimento;
    @Column(name = "NR_QUANTIDADE")
    private int quantidade;
    @Column(name = "IS_ATIVO", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo;
    @Column(name = "DS_ES", length = 1)
    private String es;
    @Column(name = "IS_OBRIGACAO", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean obrigacao;
    @JoinColumn(name = "ID_TITULAR", referencedColumnName = "ID")
    @OneToOne
    private Pessoa titular;
    @JoinColumn(name = "ID_BENEFICIARIO", referencedColumnName = "ID")
    @OneToOne
    private Pessoa beneficiario;
    @Column(name = "DS_DOCUMENTO", length = 100)
    private String documento;
    @Column(name = "NR_CTR_BOLETO", length = 30)
    private String nrCtrBoleto;
    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VENCIMENTO_ORIGINAL")
    private Date dtVencimentoOriginal;
    @Column(name = "NR_DESCONTO_ATE_VENCIMENTO", length = 10)
    private float descontoAteVencimento;
    @Column(name = "NR_CORRECAO", length = 10)
    private float correcao;
    @Column(name = "NR_JUROS", length = 10)
    private float juros;
    @Column(name = "NR_MULTA", length = 10)
    private float multa;
    @Column(name = "NR_DESCONTO", length = 10)
    private float desconto;
    @Column(name = "NR_TAXA", length = 10)
    private float taxa;
    @Column(name = "NR_VALOR_BAIXA", length = 10)
    private float valorBaixa;
    @Column(name = "NR_REPASSE_AUTOMATICO", length = 10)
    private float repasseAutomatico;
    @JoinColumn(name = "ID_TIPO_DOCUMENTO", referencedColumnName = "ID")
    @ManyToOne
    private FTipoDocumento tipoDocumento;
    @JoinColumn(name = "ID_MATRICULA_SOCIOS", referencedColumnName = "ID", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private MatriculaSocios matriculaSocios;
    
    /**
     * <p>
     * <strong>Inserir Matricula Sócio</strong>
     * Para Inserir este Objeto basta adicinar " new MatriculaSócio() " na variável ( Somente Associativo ) 
     * Para demais Rotinas ( ex. Arrecadação ) adicionar " null " na variável
     * </p>
     * @return MatriculaSocio
     */
    public Movimento() {
        this.id = -1;
        this.lote = new Lote();
        this.plano5 = new Plano5();
        this.pessoa = new Pessoa();
        this.servicos = new Servicos();
        this.baixa = new Baixa();
        this.tipoServico = new TipoServico();
        this.acordo = new Acordo();
        this.valor = 0;
        this.referencia = "";
        this.setVencimento("");
        this.quantidade = 1;
        this.ativo = true;
        this.es = "";
        this.obrigacao = false;
        this.titular = new Pessoa();
        this.beneficiario = new Pessoa();
        this.documento = "";
        this.nrCtrBoleto = "";
        this.setVencimentoOriginal("");
        this.descontoAteVencimento = 0;
        this.correcao = 0;
        this.juros = 0;
        this.multa = 0;
        this.desconto = 0;
        this.taxa = 0;
        this.valorBaixa = 0;
        this.repasseAutomatico = 0;
        this.tipoDocumento = new FTipoDocumento();
        this.matriculaSocios = null;
    }

    /**
     * <p>
     * <strong>Inserir Matricula Sócio</strong>
     * Para Inserir este Objeto basta adicinar " new MatriculaSócio() " na variável ( Somente Associativo ) 
     * Para demais Rotinas ( ex. Arrecadação ) adicionar " null " na variável
     * </p>
     * @param id
     * @param lote
     * @param plano5
     * @param pessoa
     * @param servicos
     * @param baixa
     * @param tipoServico
     * @param acordo
     * @param valor
     * @param referencia
     * @param vencimento
     * @param quantidade
     * @param ativo
     * @param es
     * @param obrigacao
     * @param titular
     * @param beneficiario
     * @param documento
     * @param nrCtrBoleto
     * @param vencimentoOriginal
     * @param descontoAteVencimento
     * @param correcao
     * @param juros
     * @param multa
     * @param desconto
     * @param taxa
     * @param valorBaixa
     * @param tipoDocumento
     * @param repasseAutomatico
     * @param matriculaSocios 
     */
    public Movimento(int id,
            Lote lote,
            Plano5 plano5,
            Pessoa pessoa,
            Servicos servicos,
            Baixa baixa,
            TipoServico tipoServico,
            Acordo acordo,
            float valor,
            String referencia,
            String vencimento,
            int quantidade,
            boolean ativo,
            String es,
            boolean obrigacao,
            Pessoa titular,
            Pessoa beneficiario,
            String documento,
            String nrCtrBoleto,
            String vencimentoOriginal,
            float descontoAteVencimento,
            float correcao,
            float juros,
            float multa,
            float desconto,
            float taxa,
            float valorBaixa,
            FTipoDocumento tipoDocumento,
            float repasseAutomatico,
            MatriculaSocios matriculaSocios) {
        this.id = id;
        this.lote = lote;
        this.plano5 = plano5;
        this.pessoa = pessoa;
        this.servicos = servicos;
        this.baixa = baixa;
        this.tipoServico = tipoServico;
        this.acordo = acordo;
        this.valor = valor;
        this.referencia = referencia;
        this.setVencimento(vencimento);
        this.quantidade = quantidade;
        this.ativo = ativo;
        this.es = es;
        this.obrigacao = obrigacao;
        this.titular = titular;
        this.beneficiario = beneficiario;
        this.documento = documento;
        this.nrCtrBoleto = nrCtrBoleto;
        this.setVencimentoOriginal(vencimentoOriginal);
        this.descontoAteVencimento = descontoAteVencimento;
        this.correcao = correcao;
        this.juros = juros;
        this.multa = multa;
        this.desconto = desconto;
        this.taxa = taxa;
        this.valorBaixa = valorBaixa;
        this.repasseAutomatico = repasseAutomatico;
        this.tipoDocumento = tipoDocumento;
        this.setMatriculaSocios(matriculaSocios);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Plano5 getPlano5() {
        return plano5;
    }

    public void setPlano5(Plano5 plano5) {
        this.plano5 = plano5;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Servicos getServicos() {
        return servicos;
    }

    public void setServicos(Servicos servicos) {
        this.servicos = servicos;
    }

    public Baixa getBaixa() {
        return baixa;
    }

    public void setBaixa(Baixa baixa) {
        this.baixa = baixa;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Date getDtVencimento() {
        return dtVencimento;
    }

    public void setDtVencimento(Date dtVencimento) {
        this.dtVencimento = dtVencimento;
    }

    public String getVencimento() {
        return DataHoje.converteData(dtVencimento);
    }

    public void setVencimento(String vencimento) {
        this.dtVencimento = DataHoje.converte(vencimento);
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getEs() {
        return es;
    }

    public void setEs(String es) {
        this.es = es;
    }

    public boolean isObrigacao() {
        return obrigacao;
    }

    public void setObrigacao(boolean obrigacao) {
        this.obrigacao = obrigacao;
    }

    public Pessoa getTitular() {
        return titular;
    }

    public void setTitular(Pessoa titular) {
        this.titular = titular;
    }

    public Pessoa getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(Pessoa beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public Date getDtVencimentoOriginal() {
        return dtVencimentoOriginal;
    }

    public void setDtVencimentoOriginal(Date dtVencimentoOriginal) {
        this.dtVencimentoOriginal = dtVencimentoOriginal;
    }

    public String getVencimentoOriginal() {
        return DataHoje.converteData(dtVencimentoOriginal);
    }

    public void setVencimentoOriginal(String vencimentoOriginal) {
        this.dtVencimentoOriginal = DataHoje.converte(vencimentoOriginal);
    }

    public Acordo getAcordo() {
        return acordo;
    }

    public void setAcordo(Acordo acordo) {
        this.acordo = acordo;
    }

    public float getDescontoAteVencimento() {
        return descontoAteVencimento;
    }

    public void setDescontoAteVencimento(float descontoAteVencimento) {
        this.descontoAteVencimento = descontoAteVencimento;
    }

    public float getCorrecao() {
        return correcao;
    }

    public void setCorrecao(float correcao) {
        this.correcao = correcao;
    }

    public float getJuros() {
        return juros;
    }

    public void setJuros(float juros) {
        this.juros = juros;
    }

    public float getMulta() {
        return multa;
    }

    public void setMulta(float multa) {
        this.multa = multa;
    }

    public float getDesconto() {
        return desconto;
    }

    public void setDesconto(float desconto) {
        this.desconto = desconto;
    }

    public float getTaxa() {
        return taxa;
    }

    public void setTaxa(float taxa) {
        this.taxa = taxa;
    }

    public float getValorBaixa() {
        return valorBaixa;
    }

    public void setValorBaixa(float valorBaixa) {
        this.valorBaixa = valorBaixa;
    }

    public float getRepasseAutomatico() {
        return repasseAutomatico;
    }

    public void setRepasseAutomatico(float repasseAutomatico) {
        this.repasseAutomatico = repasseAutomatico;
    }

    public String getNrCtrBoleto() {
        return nrCtrBoleto;
    }

    public void setNrCtrBoleto(String nrCtrBoleto) {
        this.nrCtrBoleto = nrCtrBoleto;
    }

    public FTipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setFTipoDocumento(FTipoDocumento fTipoDocumento) {
        this.tipoDocumento = fTipoDocumento;
    }
    
    /**
     * <p>
     * <strong>Inserir Matricula Sócio</strong>
     * Para Inserir este Objeto basta adicinar " new MatriculaSócio() " na variável ( Somente Associativo ) 
     * Para demais Rotinas ( ex. Arrecadação ) adicionar " null " na variável
     * </p>
     * @return MatriculaSocio
     */
    public MatriculaSocios getMatriculaSocios() {
        if (id == -1 && matriculaSocios != null){
            if (beneficiario != null && beneficiario.getId() != -1){
                SociosDB dbs = new SociosDBToplink();
                Socios soc = dbs.pesquisaSocioPorPessoaAtivo(beneficiario.getId());
                if (soc.getId() != -1)
                    matriculaSocios = soc.getMatriculaSocios();
                else
                    matriculaSocios = null;
            }
        }
        return matriculaSocios;
    }

    public void setMatriculaSocios(MatriculaSocios matriculaSocios) {
        if (id == -1 && matriculaSocios != null){
            if (beneficiario != null && beneficiario.getId() != -1){
                SociosDB dbs = new SociosDBToplink();
                Socios soc = dbs.pesquisaSocioPorPessoaAtivo(beneficiario.getId());
                if (soc.getId() != -1)
                    matriculaSocios = soc.getMatriculaSocios();
                else
                    matriculaSocios = null;
            }
        }
        this.matriculaSocios = matriculaSocios;
    }    
}