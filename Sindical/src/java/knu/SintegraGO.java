/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package knu;

public class SintegraGO {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected SintegraGO(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SintegraGO obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        knuJNI.delete_SintegraGO(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setCod_erro(int value) {
    knuJNI.SintegraGO_cod_erro_set(swigCPtr, this, value);
  }

  public int getCod_erro() {
    return knuJNI.SintegraGO_cod_erro_get(swigCPtr, this);
  }

  public void setAtividade_economica(String value) {
    knuJNI.SintegraGO_atividade_economica_set(swigCPtr, this, value);
  }

  public String getAtividade_economica() {
    return knuJNI.SintegraGO_atividade_economica_get(swigCPtr, this);
  }

  public void setBairro(String value) {
    knuJNI.SintegraGO_bairro_set(swigCPtr, this, value);
  }

  public String getBairro() {
    return knuJNI.SintegraGO_bairro_get(swigCPtr, this);
  }

  public void setCep(String value) {
    knuJNI.SintegraGO_cep_set(swigCPtr, this, value);
  }

  public String getCep() {
    return knuJNI.SintegraGO_cep_get(swigCPtr, this);
  }

  public void setCnpj(String value) {
    knuJNI.SintegraGO_cnpj_set(swigCPtr, this, value);
  }

  public String getCnpj() {
    return knuJNI.SintegraGO_cnpj_get(swigCPtr, this);
  }

  public void setComplemento(String value) {
    knuJNI.SintegraGO_complemento_set(swigCPtr, this, value);
  }

  public String getComplemento() {
    return knuJNI.SintegraGO_complemento_get(swigCPtr, this);
  }

  public void setData_consulta(String value) {
    knuJNI.SintegraGO_data_consulta_set(swigCPtr, this, value);
  }

  public String getData_consulta() {
    return knuJNI.SintegraGO_data_consulta_get(swigCPtr, this);
  }

  public void setData_situacao_cadastral(String value) {
    knuJNI.SintegraGO_data_situacao_cadastral_set(swigCPtr, this, value);
  }

  public String getData_situacao_cadastral() {
    return knuJNI.SintegraGO_data_situacao_cadastral_get(swigCPtr, this);
  }

  public void setDesc_erro(String value) {
    knuJNI.SintegraGO_desc_erro_set(swigCPtr, this, value);
  }

  public String getDesc_erro() {
    return knuJNI.SintegraGO_desc_erro_get(swigCPtr, this);
  }

  public void setIe(String value) {
    knuJNI.SintegraGO_ie_set(swigCPtr, this, value);
  }

  public String getIe() {
    return knuJNI.SintegraGO_ie_get(swigCPtr, this);
  }

  public void setLogradouro(String value) {
    knuJNI.SintegraGO_logradouro_set(swigCPtr, this, value);
  }

  public String getLogradouro() {
    return knuJNI.SintegraGO_logradouro_get(swigCPtr, this);
  }

  public void setMunicipio(String value) {
    knuJNI.SintegraGO_municipio_set(swigCPtr, this, value);
  }

  public String getMunicipio() {
    return knuJNI.SintegraGO_municipio_get(swigCPtr, this);
  }

  public void setNumero(String value) {
    knuJNI.SintegraGO_numero_set(swigCPtr, this, value);
  }

  public String getNumero() {
    return knuJNI.SintegraGO_numero_get(swigCPtr, this);
  }

  public void setObservacoes(String value) {
    knuJNI.SintegraGO_observacoes_set(swigCPtr, this, value);
  }

  public String getObservacoes() {
    return knuJNI.SintegraGO_observacoes_get(swigCPtr, this);
  }

  public void setRazao(String value) {
    knuJNI.SintegraGO_razao_set(swigCPtr, this, value);
  }

  public String getRazao() {
    return knuJNI.SintegraGO_razao_get(swigCPtr, this);
  }

  public void setRegime(String value) {
    knuJNI.SintegraGO_regime_set(swigCPtr, this, value);
  }

  public String getRegime() {
    return knuJNI.SintegraGO_regime_get(swigCPtr, this);
  }

  public void setSituacao_cadastral(String value) {
    knuJNI.SintegraGO_situacao_cadastral_set(swigCPtr, this, value);
  }

  public String getSituacao_cadastral() {
    return knuJNI.SintegraGO_situacao_cadastral_get(swigCPtr, this);
  }

  public void setTelefone(String value) {
    knuJNI.SintegraGO_telefone_set(swigCPtr, this, value);
  }

  public String getTelefone() {
    return knuJNI.SintegraGO_telefone_get(swigCPtr, this);
  }

  public void setUf(String value) {
    knuJNI.SintegraGO_uf_set(swigCPtr, this, value);
  }

  public String getUf() {
    return knuJNI.SintegraGO_uf_get(swigCPtr, this);
  }

  public void setHtml(String value) {
    knuJNI.SintegraGO_html_set(swigCPtr, this, value);
  }

  public String getHtml() {
    return knuJNI.SintegraGO_html_get(swigCPtr, this);
  }

  public SintegraGO() {
    this(knuJNI.new_SintegraGO(), true);
  }

}
