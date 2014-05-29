



INSERT INTO sis_email_prioridade (id, ds_descricao) SELECT 1,'NORMAL' WHERE NOT EXISTS ( SELECT id FROM sis_email_prioridade WHERE id = 1);
INSERT INTO sis_email_prioridade (id, ds_descricao) SELECT 2,'BAIXA' WHERE NOT EXISTS ( SELECT id FROM sis_email_prioridade WHERE id = 2);
INSERT INTO sis_email_prioridade (id, ds_descricao) SELECT 3,'ALTA' WHERE NOT EXISTS ( SELECT id FROM sis_email_prioridade WHERE id = 3);
SELECT setval('sis_email_prioridade_id_seq', max(id)) FROM sis_email_prioridade;
