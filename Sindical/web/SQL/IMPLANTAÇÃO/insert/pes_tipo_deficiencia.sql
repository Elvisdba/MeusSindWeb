-- arr_repis_status
-- Criate: 2013-08-02
-- Last edition: 2013-08-02 - by: Bruno Vieira

-- DELETE FROM pes_tipo_deficiencia

INSERT INTO pes_tipo_deficiencia (id, ds_descricao) SELECT 0, 'Nenhuma' WHERE NOT EXISTS ( SELECT id FROM pes_tipo_deficiencia WHERE id = 0);
INSERT INTO pes_tipo_deficiencia (id, ds_descricao) SELECT 1, 'Visual' WHERE NOT EXISTS ( SELECT id FROM pes_tipo_deficiencia WHERE id = 1);
INSERT INTO pes_tipo_deficiencia (id, ds_descricao) SELECT 2, 'Motora' WHERE NOT EXISTS ( SELECT id FROM pes_tipo_deficiencia WHERE id = 2);
INSERT INTO pes_tipo_deficiencia (id, ds_descricao) SELECT 3, 'Mental' WHERE NOT EXISTS ( SELECT id FROM pes_tipo_deficiencia WHERE id = 3);
INSERT INTO pes_tipo_deficiencia (id, ds_descricao) SELECT 4, 'Auditiva' WHERE NOT EXISTS ( SELECT id FROM pes_tipo_deficiencia WHERE id = 4);
SELECT setval('pes_tipo_deficiencia_id_seq', max(id)) FROM pes_tipo_deficiencia;
