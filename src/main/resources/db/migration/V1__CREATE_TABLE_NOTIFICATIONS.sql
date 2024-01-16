create table notifications(
    id_notification integer primary key,
    id_agravo integer,
    data_notification DATE,
    data_nascimento DATE,
    classificacao varchar(2),
    sexo varchar(2),
    nome_bairro varchar(100),
    evolucao varchar(20)
)