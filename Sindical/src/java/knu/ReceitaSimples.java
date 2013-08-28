/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package knu;

public class ReceitaSimples {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected ReceitaSimples(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ReceitaSimples obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        knuJNI.delete_ReceitaSimples(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setCod_erro(int value) {
    knuJNI.ReceitaSimples_cod_erro_set(swigCPtr, this, value);
  }

  public int getCod_erro() {
    return knuJNI.ReceitaSimples_cod_erro_get(swigCPtr, this);
  }

  public void setDesc_erro(String value) {
    knuJNI.ReceitaSimples_desc_erro_set(swigCPtr, this, value);
  }

  public String getDesc_erro() {
    return knuJNI.ReceitaSimples_desc_erro_get(swigCPtr, this);
  }

  public void setCnpj(String value) {
    knuJNI.ReceitaSimples_cnpj_set(swigCPtr, this, value);
  }

  public String getCnpj() {
    return knuJNI.ReceitaSimples_cnpj_get(swigCPtr, this);
  }

  public void setNome_empresarial(String value) {
    knuJNI.ReceitaSimples_nome_empresarial_set(swigCPtr, this, value);
  }

  public String getNome_empresarial() {
    return knuJNI.ReceitaSimples_nome_empresarial_get(swigCPtr, this);
  }

  public void setSituacao_simples_nacional(String value) {
    knuJNI.ReceitaSimples_situacao_simples_nacional_set(swigCPtr, this, value);
  }

  public String getSituacao_simples_nacional() {
    return knuJNI.ReceitaSimples_situacao_simples_nacional_get(swigCPtr, this);
  }

  public void setSituacao_simei(String value) {
    knuJNI.ReceitaSimples_situacao_simei_set(swigCPtr, this, value);
  }

  public String getSituacao_simei() {
    return knuJNI.ReceitaSimples_situacao_simei_get(swigCPtr, this);
  }

  public void setOpcao_simples_anteriores(String value) {
    knuJNI.ReceitaSimples_opcao_simples_anteriores_set(swigCPtr, this, value);
  }

  public String getOpcao_simples_anteriores() {
    return knuJNI.ReceitaSimples_opcao_simples_anteriores_get(swigCPtr, this);
  }

  public void setOpcao_simei_anteriores(String value) {
    knuJNI.ReceitaSimples_opcao_simei_anteriores_set(swigCPtr, this, value);
  }

  public String getOpcao_simei_anteriores() {
    return knuJNI.ReceitaSimples_opcao_simei_anteriores_get(swigCPtr, this);
  }

  public void setAgendamentos(String value) {
    knuJNI.ReceitaSimples_agendamentos_set(swigCPtr, this, value);
  }

  public String getAgendamentos() {
    return knuJNI.ReceitaSimples_agendamentos_get(swigCPtr, this);
  }

  public void setEventos_futuros(String value) {
    knuJNI.ReceitaSimples_eventos_futuros_set(swigCPtr, this, value);
  }

  public String getEventos_futuros() {
    return knuJNI.ReceitaSimples_eventos_futuros_get(swigCPtr, this);
  }

  public void setHtml(String value) {
    knuJNI.ReceitaSimples_html_set(swigCPtr, this, value);
  }

  public String getHtml() {
    return knuJNI.ReceitaSimples_html_get(swigCPtr, this);
  }

  public ReceitaSimples() {
    this(knuJNI.new_ReceitaSimples(), true);
  }

}
