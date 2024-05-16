create table notifications(
    id_notification integer primary key,
    id_agravo integer,
    idade_paciente integer,
    data_notification DATE,
    data_nascimento DATE,
    classificacao varchar(2),
    sexo varchar(2),
    id_bairro integer,
    nome_bairro varchar(100),
    evolucao varchar(20),
    semanaEpidemiologica integer
)