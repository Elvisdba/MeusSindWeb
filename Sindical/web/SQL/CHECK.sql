ALTER TABLE seg_usuario ADD CHECK (ds_login <> '');
ALTER TABLE pes_tipo_endereco ADD CHECK (ds_descricao <> '');
ALTER TABLE pes_tipo_documento ADD CHECK (ds_descricao <> '');
ALTER TABLE fin_tipo_documento ADD CHECK (ds_descricao <> '');
ALTER TABLE pes_profissao ADD CHECK (ds_profissao <> '');
ALTER TABLE hom_status ADD CHECK (ds_descricao <> '');
ALTER TABLE hom_demissao ADD CHECK (ds_descricao <> '');
ALTER TABLE pes_porte ADD CHECK (ds_descricao <> '');
ALTER TABLE fin_tipo_servico ADD CHECK (ds_descricao <> '');
ALTER TABLE eve_banda ADD CHECK (ds_descricao <> '');
ALTER TABLE soc_midia ADD CHECK (ds_descricao <> '');
ALTER TABLE hom_status ADD CHECK (ds_descricao <> '');
ALTER TABLE esc_componente_curricular ADD CHECK (ds_descricao <> '');
ALTER TABLE fin_indice ADD CHECK (ds_descricao <> '');
ALTER TABLE loc_genero ADD CHECK (ds_descricao <> '');
ALTER TABLE PES_TIPO_CENTRO_COMERCIAL ADD CHECK (ds_descricao <> '');
ALTER TABLE SOC_CONVENIO_GRUPO ADD CHECK (ds_descricao <> '');
ALTER TABLE CONV_MOTIVO_SUSPENCAO ADD CHECK (ds_descricao <> '');
ALTER TABLE ARR_GRUPO_CIDADE ADD CHECK (ds_descricao <> '');
ALTER TABLE AGE_GRUPO_AGENDA ADD CHECK (ds_descricao <> '');
ALTER TABLE sis_semana ADD CHECK (ds_descricao <> '');
ALTER TABLE sis_periodo ADD CHECK (ds_descricao <> '');
ALTER TABLE SEG_NIVEL ADD CHECK (ds_descricao <> '');
ALTER TABLE SEG_MODULO ADD CHECK (ds_descricao <> '');
ALTER TABLE SEG_EVENTO ADD CHECK (ds_descricao <> '');
ALTER TABLE SEG_DEPARTAMENTO ADD CHECK (ds_descricao <> '');
ALTER TABLE FIN_CONDICAO_PAGAMENTO ADD CHECK (ds_descricao <> '');
ALTER TABLE AGE_TIPO_TELEFONE ADD CHECK (ds_descricao <> '');
ALTER TABLE ARR_CONVENCAO ADD CHECK (ds_descricao <> '');
ALTER TABLE ARR_MOTIVO_INATIVACAO ADD CHECK (ds_descricao <> '');
ALTER TABLE ARR_REPIS_STATUS ADD CHECK (ds_descricao <> '');
ALTER TABLE EVE_STATUS ADD CHECK (ds_descricao <> '');
ALTER TABLE SOC_MOTIVO_INATIVACAO ADD CHECK (ds_descricao <> '');
ALTER TABLE ATE_OPERACAO ADD CHECK (ds_descricao <> '');
ALTER TABLE FIN_COBRANCA_TIPO ADD CHECK (ds_descricao <> '');
ALTER TABLE FIN_TIPO_PAGAMENTO ADD CHECK (ds_descricao <> '');
ALTER TABLE EST_PRODUTO ADD CHECK (ds_descricao <> '');
ALTER TABLE EST_TIPO ADD CHECK (ds_descricao <> '');
ALTER TABLE EST_GRUPO ADD CHECK (ds_descricao <> '');
ALTER TABLE EST_SUBGRUPO ADD CHECK (ds_descricao <> '');
ALTER TABLE EST_UNIDADE ADD CHECK (ds_descricao <> '');
ALTER TABLE SIS_COR ADD CHECK (ds_descricao <> '');