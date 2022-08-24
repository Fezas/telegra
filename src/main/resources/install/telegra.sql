create table USERS
(
    USER_ID        INTEGER auto_increment,
    USER_LOGIN     VARCHAR(16),
    USER_PASSWORD  VARCHAR(10),
    USER_ROLE      VARCHAR(20),
    USER_TELEPHONE VARCHAR(30),
    USER_POSITION  VARCHAR(100),
    constraint PK_USERS
        primary key (USER_ID)
);

create unique index USERS_PK
    on USERS (USER_ID);

INSERT INTO PUBLIC.USERS (USER_ID, USER_LOGIN, USER_PASSWORD, USER_ROLE, USER_TELEPHONE, USER_POSITION) VALUES (1, 'Степанцов П.В.', 'f', 'администратор', '+7 (4242) 286-04-64 доб.25', 'Техник УС в/ч 6542');

create table TLG_SUPERVISOR
(
    TLG_SUPERVISOR_ID        INTEGER auto_increment,
    TLG_SUPERVISOR_POSITION  VARCHAR(60)           not null,
    TLG_SUPERVISOR_LASTNAME  VARCHAR(20)           not null,
    TLG_SUPERVISOR_TELEPHONE VARCHAR(30)           not null,
    TLG_SUPERVISOR_DEFAULT   BOOLEAN default FALSE not null,
    constraint PK_TLG_SUPERVISOR
        primary key (TLG_SUPERVISOR_ID)
);

create unique index TLG_SUPERVISOR_PK
    on TLG_SUPERVISOR (TLG_SUPERVISOR_ID);
	
create table TLG_TYPE
(
    TLG_TYPE_ID          INTEGER auto_increment,
    TLG_TYPE_NAME        VARCHAR(40),
    TLG_TYPE_DESCRIPTION TEXT,
    constraint PK_TLG_TYPE
        primary key (TLG_TYPE_ID)
);

comment on table TLG_TYPE is 'Особенности телеграмм по назначению, способу оформлени¤, подачи или доставки.';

create unique index TLG_TYPE_PK
    on TLG_TYPE (TLG_TYPE_ID);

create table PROPERTY
(
    ID_USER                 LONG                  not null,
    PATH_PDF                VARCHAR(100),
    PATH_TLG                VARCHAR(100),
    MIN_STRINGS_ON_NEW_PAGE INT         default 5 not null,
    NUMBER_COMPUTER         VARCHAR(20) default STRINGDECODE('\u042d\u0412\u041c00-000'),
    constraint PROPERTY_PK
        primary key (ID_USER)
);

comment on table PROPERTY is 'Таблица настроек программы';

comment on column PROPERTY.MIN_STRINGS_ON_NEW_PAGE is 'минимальное количество строк на новой странице';

comment on column PROPERTY.NUMBER_COMPUTER is 'Номер текущей машины';

create table TEMPLATE
(
    TEMPLATE_ID    BIGINT auto_increment
        primary key,
    TEMPLATE_BODY  TEXT                  not null,
    TEMPLATE_DATE  TIMESTAMP             not null,
    TEMPLATE_TITLE VARCHAR               not null,
    TEMPLATE_DEF   BOOLEAN default FALSE not null
);

comment on table TEMPLATE is 'Шаблоны телеграмм';

create unique index TEMPLATE_TEMPLATE_ID_UINDEX
    on TEMPLATE (TEMPLATE_ID);

INSERT INTO PUBLIC.TEMPLATE (TEMPLATE_ID, TEMPLATE_BODY, TEMPLATE_DATE, TEMPLATE_TITLE, TEMPLATE_DEF) VALUES (1, '<figure class="table" style="width:100%;"><table><tbody><tr><td>[qr]</td><td style="vertical-align:top;"><p style="text-align:right;"><strong><u>[гриф]</u></strong></p><p style="text-align:right;">[ПУНКТЫ]п. [пункт] Перечня[ПУНКТЫ]</p></td></tr></tbody></table></figure><p style="text-align:center;">[отступ]</p><p style="text-align:center;">[отступ]</p><p style="text-align:center;">[отступ]</p><figure class="table" style="width:100%;"><table><tbody><tr><td><p style="text-align:center;"><strong>ТЕЛЕГРАММА [категория]</strong></p></td></tr></tbody></table></figure><p style="text-align:center;">[отступ]</p><p>[тип]</p><p>[название]</p><figure class="table" style="float:left;width:100%;"><table><tbody><tr><td colspan="3">[АДРЕСА]</td></tr><tr><td style="width:44px;">&nbsp;</td><td>[позывной]</td><td>[адрес]</td></tr><tr><td>&nbsp;</td><td>&nbsp;</td><td>[адресат]</td></tr><tr><td colspan="3">[АДРЕСА]</td></tr></tbody></table></figure><p>[текст]</p><p>&nbsp;</p><p>[ПРИЛОЖЕНИЯ] К телеграмме имеется: [колво_прилож]</p><p>[прилож][ПРИЛОЖЕНИЯ]</p><p style="text-align:center;">[отступ]</p><figure class="table" style="width:100%;"><table><tbody><tr><td><p style="text-align:center;">[долж_руководителя]</p></td></tr><tr><td><p style="text-align:right;">[фам_руководителя]</p></td></tr></tbody></table></figure><hr><figure class="table" style="width:100%;"><table><tbody><tr><td style="vertical-align:bottom;"><p>[долж_исполнителя] [фам_исполнителя]</p><p>Тел. [тлф_исполнителя]</p><p>[дата]</p><p>[эвм]</p></td></tr></tbody></table></figure>', '2021-12-19 18:09:22.982000', 'default', false);

create table TLG_ADDRESS
(
    TLG_ADDRESS_ID             BIGINT auto_increment,
    TLG_ADDRESS_CALLSIGN       VARCHAR(20)  not null,
    TLG_ADDRESS_NAME           VARCHAR(100) not null,
    TLG_ADDRESS_PERSON         VARCHAR(20),
    TLG_ADDRESS_PERSON_RESPECT VARCHAR(60),
    constraint TLG_ADDRESS_PK
        primary key (TLG_ADDRESS_ID)
);

comment on table TLG_ADDRESS is 'Адрес, по которому должна быть доставлена телеграмма, с указанием наименовани¤ адресата (адресатов).';

comment on column TLG_ADDRESS.TLG_ADDRESS_PERSON is 'Фамилия адресата';

create unique index TLG_ADDRESS_PK
    on TLG_ADDRESS (TLG_ADDRESS_ID);

create table TLG_CATEGORY
(
    TLG_CATEGORY_ID          INTEGER auto_increment,
    TLG_CATEGORY_NAME        VARCHAR(20) not null,
    TLG_CATEGORY_DESCRIPTION TEXT        not null,
    constraint PK_TLG_CATEGORY
        primary key (TLG_CATEGORY_ID)
);

create unique index TLG_CATEGORY_PK
    on TLG_CATEGORY (TLG_CATEGORY_ID);
	
create table TLG_SECRECY
(
    TLG_SECRECY_ID   INTEGER auto_increment,
    TLG_SECRECY_NAME VARCHAR(26) not null,
    constraint PK_TLG_SECRECY
        primary key (TLG_SECRECY_ID)
);

comment on table TLG_SECRECY is 'Особенности телеграмм по назначению, способу оформления, подачи или доставки.';

create unique index TLG_SECRECY_PK
    on TLG_SECRECY (TLG_SECRECY_ID);

INSERT INTO PUBLIC.TLG_SECRECY (TLG_SECRECY_ID, TLG_SECRECY_NAME) VALUES (1, 'ДСП');
INSERT INTO PUBLIC.TLG_SECRECY (TLG_SECRECY_ID, TLG_SECRECY_NAME) VALUES (2, 'Секретно');

create table TLG_SECRECY_PARAGRAPH
(
    TLG_SECRECY_PARAGRAPH_ID   INTEGER not null,
    TLG_SECRECY_PARAGRAPH_TEXT TEXT,
    constraint PK_TLG_SECRECY_PARAGRAPH
        primary key (TLG_SECRECY_PARAGRAPH_ID)
);

comment on table TLG_SECRECY_PARAGRAPH is 'Особенности телеграмм по назначению, способу оформления, подачи или доставки.';

create unique index TLG_SECRECY_PARAGRAPH_PK
    on TLG_SECRECY_PARAGRAPH (TLG_SECRECY_PARAGRAPH_ID);
	
create table TLG_STATE
(
    TLG_STATE_ID     INTEGER auto_increment,
    TLG_TLG_STATE_ID INTEGER,
    TLG_STATE_TITLE  VARCHAR(30) not null,
    TLG_STATE_TEXT   TEXT,
    constraint PK_TLG_STATE
        primary key (TLG_STATE_ID),
    constraint FK_TLG_STAT_IN_TLG_STAT
        foreign key (TLG_TLG_STATE_ID) references TLG_STATE (TLG_STATE_ID)
            on update cascade on delete cascade
);

create unique index TLG_STATE_PK
    on TLG_STATE (TLG_STATE_ID);

create index IN_FK
    on TLG_STATE (TLG_TLG_STATE_ID);

create table TLG_APPLICATION
(
    TLG_APP_ID   BIGINT auto_increment,
    TLG_APP_NAME VARCHAR(50) not null,
    TLG_APP_EXT  VARCHAR(20) not null,
    TLG_APP_SIZE VARCHAR(20) not null,
    constraint PK_TLG_APPLICATION
        primary key (TLG_APP_ID)
);

create unique index TLG_APPLICATION_PK
    on TLG_APPLICATION (TLG_APP_ID);

create table INCLUDE_APP
(
    TLG_APP_ID BIGINT not null,
    TLG_ID     BIGINT not null
);

create unique index INCLUDE_APP_PK
    on INCLUDE_APP (TLG_APP_ID, TLG_ID);

create index INCLUDE_APP_FK
    on INCLUDE_APP (TLG_APP_ID);

create index INCLUDE_APP2_FK
    on INCLUDE_APP (TLG_ID);

alter table INCLUDE_APP
    add constraint PK_INCLUDE_APP
        primary key (TLG_APP_ID, TLG_ID);

create table TLG
(
    TLG_ID            BIGINT auto_increment,
    TLG_STATE_ID      INTEGER default null,
    TLG_CATEGORY_ID   INTEGER default null,
    TLG_SECRECY_ID    INTEGER default null,
    TLG_SUPERVISOR_ID INTEGER default null,
    TLG_TITLE         TEXT,
    TLG_TEXT          TEXT,
    TLG_DATE_GREATE   TIMESTAMP,
    TLG_DATE_INPUT    TIMESTAMP,
    TLG_DATE_EDIT     TIMESTAMP,
    TLG_NUMBER        VARCHAR(20),
    TLG_READ          BOOLEAN default FALSE not null,
    TLG_DRAFT         BOOLEAN default FALSE not null,
    TLG_SAMPLE        BOOLEAN default FALSE not null,
    TLG_ARCHIVE       BOOLEAN default FALSE,
    TLG_VERSION       INTEGER default 0,
    TLG_APPS          TEXT,
    constraint PK_TLG
        primary key (TLG_ID),
    constraint FK_TLG_GREATE_TLG_EXEC
        foreign key (TLG_SUPERVISOR_ID) references TLG_SUPERVISOR (TLG_SUPERVISOR_ID)
            on update cascade on delete set default,
    constraint FK_TLG_INPUT_TLG_CATE
        foreign key (TLG_CATEGORY_ID) references TLG_CATEGORY (TLG_CATEGORY_ID)
            on update cascade on delete set default,
    constraint FK_TLG_SECRECY_TLG_SECR
        foreign key (TLG_SECRECY_ID) references TLG_SECRECY (TLG_SECRECY_ID)
            on update cascade on delete set default,
    constraint FK_TLG_TLG_STATE_TLG_STAT
        foreign key (TLG_STATE_ID) references TLG_STATE (TLG_STATE_ID)
            on update cascade on delete set default
);

comment on column TLG.TLG_VERSION is 'версия телеграммы';

create unique index TLG_PK
    on TLG (TLG_ID);

create index INPUT_FK
    on TLG (TLG_CATEGORY_ID);

create index SECRECY_FK
    on TLG (TLG_SECRECY_ID);

create index GREATE_FK
    on TLG (TLG_SUPERVISOR_ID);

create index TLG_STATE_FK
    on TLG (TLG_STATE_ID);

create table TYPE_IN_TLG
(
    TLG_ID      BIGINT  not null,
    TLG_TYPE_ID INTEGER not null,
    constraint PK_TYPE_IN_TLG
        primary key (TLG_ID, TLG_TYPE_ID),
    constraint FK_TYPE_IN__TYPE_IN_T_TLG
        foreign key (TLG_ID) references TLG (TLG_ID)
            on update cascade on delete cascade,
    constraint FK_TYPE_IN__TYPE_IN_T_TLG_TYPE
        foreign key (TLG_TYPE_ID) references TLG_TYPE (TLG_TYPE_ID)
            on update cascade on delete cascade
);

create unique index TYPE_IN_TLG_PK
    on TYPE_IN_TLG (TLG_ID, TLG_TYPE_ID);

create index TYPE_IN_TLG_FK
    on TYPE_IN_TLG (TLG_ID);

create index TYPE_IN_TLG2_FK
    on TYPE_IN_TLG (TLG_TYPE_ID);

create table MAILING
(
    TLG_ADDRESS_ID BIGINT not null,
    TLG_ID         BIGINT not null,
    constraint FK_MAILING_MAILING2_TLG
        foreign key (TLG_ID) references TLG (TLG_ID)
            on update cascade on delete cascade,
    constraint FK_MAILING_MAILING_TLG_ADDR
        foreign key (TLG_ADDRESS_ID) references TLG_ADDRESS (TLG_ADDRESS_ID)
            on update cascade on delete cascade
);

create index MAILING2_FK
    on MAILING (TLG_ID);

create unique index MAILING_PK
    on MAILING (TLG_ADDRESS_ID, TLG_ID);

create index MAILING_FK
    on MAILING (TLG_ADDRESS_ID);

alter table MAILING
    add constraint PK_MAILING
        primary key (TLG_ADDRESS_ID, TLG_ID);

create table PARS_IN_TLGS
(
    TLG_ID                   BIGINT  not null,
    TLG_SECRECY_PARAGRAPH_ID INTEGER not null,
    constraint FK_IN_PRGRA_IN_PRGRAP_TLG
        foreign key (TLG_ID) references TLG (TLG_ID)
            on update cascade on delete cascade,
    constraint FK_IN_PRGRA_IN_PRGRAP_TLG_SECR
        foreign key (TLG_SECRECY_PARAGRAPH_ID) references TLG_SECRECY_PARAGRAPH (TLG_SECRECY_PARAGRAPH_ID)
            on update cascade on delete cascade
);

create unique index IN_PRGRAPH_PK
    on PARS_IN_TLGS (TLG_ID, TLG_SECRECY_PARAGRAPH_ID);

create index IN_PRGRAPH_FK
    on PARS_IN_TLGS (TLG_ID);

create index IN_PRGRAPH2_FK
    on PARS_IN_TLGS (TLG_SECRECY_PARAGRAPH_ID);

alter table PARS_IN_TLGS
    add constraint PK_IN_PRGRAPH
        primary key (TLG_ID, TLG_SECRECY_PARAGRAPH_ID);

