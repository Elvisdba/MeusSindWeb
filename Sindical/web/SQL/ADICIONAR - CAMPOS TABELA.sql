-- SEG_REGISTRO --
 
-- update: 2013-07-31
-- edited by: Bruno Vieira da Silva

ALTER TABLE seg_registro ADD COLUMN  ds_obs_ficha_social character varying(8000);
ALTER TABLE seg_registro ADD COLUMN dt_atualiza_homologacao date;

UPDATE seg_registro SET dt_atualiza_homologacao = '1900-01-01' WHERE id = 1 AND dt_atualiza_homologacao is null;

-- update: 2013-07-31
-- edited by: Bruno Vieira da Silva

ALTER TABLE seg_registro ADD COLUMN is_boleto_web boolean;
UPDATE seg_registro SET is_boleto_web = false WHERE id = 1 AND is_boleto_web is null;
ALTER TABLE seg_registro ADD COLUMN is_repis_web boolean;
UPDATE seg_registro SET is_repis_web = false WHERE id = 1 AND is_repis_web is null;
ALTER TABLE seg_registro ADD COLUMN is_agendamento_web boolean;
UPDATE seg_registro SET is_agendamento_web = false WHERE id = 1 AND is_agendamento_web is null;

-- *****************************************************************************

ALTER TABLE fin_movimento
  ADD CONSTRAINT fk_fin_movimento_id_tipo_documento FOREIGN KEY (id_tipo_documento)
      REFERENCES fin_tipo_documento (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;


-- Table: sis_contador_acessos

-- DROP TABLE sis_contador_acessos;

CREATE TABLE sis_contador_acessos
(
  id serial NOT NULL,
  nr_acesso integer,
  id_pessoa integer NOT NULL,
  id_rotina integer NOT NULL,
  CONSTRAINT sis_contador_acessos_pkey PRIMARY KEY (id),
  CONSTRAINT fk_sis_contador_acessos_id_pessoa FOREIGN KEY (id_pessoa)
      REFERENCES seg_usuario (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_sis_contador_acessos_id_rotina FOREIGN KEY (id_rotina)
      REFERENCES seg_rotina (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sis_contador_acessos
  OWNER TO postgres;


-- *****************************************************************************

-- sis_semana
-- Criate: 2013-07-24
-- Last edition: 2013-07-24 - by: Bruno Vieira

CREATE TABLE sis_semana
(
  id serial NOT NULL,
  ds_descricao character varying(15),
  CONSTRAINT sis_semana_pkey PRIMARY KEY (id),
  CONSTRAINT sis_semana_ds_descricao_key UNIQUE (ds_descricao)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE sis_semana
  OWNER TO postgres;

-- HOM_HORARIOS
-- update: 2013-07-26
-- edited by: Bruno Vieira da Silva

ALTER TABLE hom_horarios ADD COLUMN id_semana integer;
ALTER TABLE hom_horarios
  ADD CONSTRAINT fk_hom_horarios_id_semana FOREIGN KEY (id_semana)
     REFERENCES sis_semana (id) MATCH SIMPLE      
	ON UPDATE NO ACTION 
	ON DELETE NO ACTION;

UPDATE hom_horarios SET id_semana = 2 WHERE id_semana is null

-- *****************************************************************************

-- HOM_CANCELAR_HORARIO 
-- update: 2013-07-26
-- edited by: Bruno Vieira da Silva

ALTER TABLE hom_cancelar_horario ADD COLUMN id_horarios integer;
ALTER TABLE hom_cancelar_horario
  ADD CONSTRAINT fk_hom_cancelar_horario_id_horarios FOREIGN KEY (id_horarios)
     REFERENCES hom_horarios (id) MATCH SIMPLE      
	ON UPDATE NO ACTION 
	ON DELETE NO ACTION;

-- *****************************************************************************

-- HOM_AGENDAMENTO
-- update: 2013-07-31
-- edited by: Bruno Vieira da Silva

ALTER TABLE hom_agendamento ADD COLUMN dt_emissao date;
UPDATE hom_agendamento SET dt_emissao = dt_data WHERE dt_emissao is null;

-- seg_usuario_acesso  -- 
-- update: 2013-08-05
-- edited by: Bruno Vieira da Silva

ALTER TABLE seg_usuario_acesso ADD COLUMN is_permite boolean;

-- sis_relatorios
-- update: 2013-08-07
-- edited by: Bruno Vieira da Silva

ALTER TABLE sis_relatorios ADD COLUMN ds_qry_ordem character varying(1000);
ALTER TABLE sis_relatorios ADD COLUMN ds_qry character varying(1000);


-- seg_rotina -- 
-- update: 2013-08-07
-- edited by: Bruno Vieira da Silva

ALTER TABLE seg_rotina ADD COLUMN is_ativo boolean;
-- UPDATE seg_rotina SET is_ativo = true WHERE is_ativo is null;

-- seg_usuario  -- 
-- update: 2013-08-14
-- edited by: Bruno Vieira da Silva

ALTER TABLE seg_usuario ADD COLUMN is_ativo boolean;
-- UPDATE seg_usuario SET is_ativo = true WHERE is_ativo is null;

-- age_telefone -- 
-- update: 2013-09-04
-- edited by: Bruno Vieira da Silva

ALTER TABLE age_telefone ADD COLUMN  ds_ddi character varying(2);
ALTER TABLE age_telefone ADD COLUMN  ds_ddd character varying(2);

-- fin_lote -- 
-- update: 2013-09-09
-- edited by: Bruno Vieira da Silva

ALTER TABLE fin_servico_valor ADD COLUMN nr_taxa double precision;
ALTER TABLE fin_servico_valor ALTER COLUMN nr_taxa SET NOT NULL;