/*
 * Copyright (c) $originalComment.match("Copyright \(c\) (\d+)", 1, "-")2022. Stepantsov P.V.
 */

package fezas.telegra.util;

public class InstallSQL {
    private static final String sql = """
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
                    
            INSERT INTO PUBLIC.TLG_SECRECY (TLG_SECRECY_NAME) VALUES ('Не секретно');
            INSERT INTO PUBLIC.TLG_SECRECY (TLG_SECRECY_NAME) VALUES ('ДСП');
            INSERT INTO PUBLIC.TLG_SECRECY (TLG_SECRECY_NAME) VALUES ('Секретно');     
            create table TLG_CATEGORY
            (
                TLG_CATEGORY_ID          INTEGER auto_increment,
                TLG_CATEGORY_NAME        VARCHAR(20) not null,
                TLG_CATEGORY_DESCRIPTION TEXT(200)   not null,
                constraint PK_TLG_CATEGORY
                    primary key (TLG_CATEGORY_ID)
            );
                        
            create unique index TLG_CATEGORY_PK
                on TLG_CATEGORY (TLG_CATEGORY_ID);
                        
            INSERT INTO PUBLIC.TLG_CATEGORY (TLG_CATEGORY_NAME, TLG_CATEGORY_DESCRIPTION) VALUES ('обыкновенная', 'доставка в течении рабочего дня');
            INSERT INTO PUBLIC.TLG_CATEGORY (TLG_CATEGORY_NAME, TLG_CATEGORY_DESCRIPTION) VALUES ('срочная', 'доставка в течении 1 часа');
            INSERT INTO PUBLIC.TLG_CATEGORY (TLG_CATEGORY_NAME, TLG_CATEGORY_DESCRIPTION) VALUES ('Метеорит', 'доставка 20 минут');
            INSERT INTO PUBLIC.TLG_CATEGORY (TLG_CATEGORY_NAME, TLG_CATEGORY_DESCRIPTION) VALUES ('Пульсар', 'доставка 5 минут');
                        
            create table TLG_STATE
            (
                TLG_STATE_ID     INTEGER auto_increment,
                TLG_TLG_STATE_ID INTEGER,
                TLG_STATE_TITLE  VARCHAR(30) not null,
                TLG_STATE_TEXT   TEXT,
                constraint PK_TLG_STATE
                    primary key (TLG_STATE_ID),
                constraint FK_TLG_STAT_IN_TLG_STAT
                    foreign key (TLG_STATE_ID) references TLG_STATE (TLG_STATE_ID)
                        on update cascade on delete cascade
            );
                        
            create unique index TLG_STATE_PK
                on TLG_STATE (TLG_STATE_ID);
                        
            create index IN_FK
                on TLG_STATE (TLG_TLG_STATE_ID);
            
            create table RANK
            (
                ID              INT auto_increment,
                NAME_RANK       VARCHAR not null,
                SHORT_NAME_RANK VARCHAR not null,
                constraint RANK_PK
                    primary key (ID)
            );
            
            INSERT INTO PUBLIC.RANK (NAME_RANK, SHORT_NAME_RANK) VALUES ('прапорщик', 'пр-к');
			INSERT INTO PUBLIC.RANK (NAME_RANK, SHORT_NAME_RANK) VALUES ('старший прапорщик', 'ст. пр-к');
			INSERT INTO PUBLIC.RANK (NAME_RANK, SHORT_NAME_RANK) VALUES ('лейтенант', 'л-т');
			INSERT INTO PUBLIC.RANK (NAME_RANK, SHORT_NAME_RANK) VALUES ('старший лейтенант', 'ст. л-т'); 
			
			                            
            create table TLG_SUPERVISOR
            (
                TLG_SUPERVISOR_ID        INTEGER auto_increment,
                TLG_SUPERVISOR_POSITION  VARCHAR(60)           not null,
                TLG_SUPERVISOR_LASTNAME  VARCHAR(20)           not null,
                TLG_SUPERVISOR_TELEPHONE VARCHAR(30)           not null,
                TLG_SUPERVISOR_DEFAULT   BOOLEAN default FALSE not null,
                TLG_SUPERVISOR_RANK      INT                   not null,
                constraint PK_TLG_SUPERVISOR
                    primary key (TLG_SUPERVISOR_ID),
                constraint TLG_SUPERVISOR_RANK_FK
                    foreign key (TLG_SUPERVISOR_RANK) references RANK (ID)
                        on update cascade on delete cascade
            );
                        
            create unique index TLG_SUPERVISOR_PK
                on TLG_SUPERVISOR (TLG_SUPERVISOR_ID);
                        
            INSERT INTO PUBLIC.TLG_SUPERVISOR (TLG_SUPERVISOR_POSITION, TLG_SUPERVISOR_LASTNAME, TLG_SUPERVISOR_TELEPHONE, TLG_SUPERVISOR_DEFAULT, TLG_SUPERVISOR_RANK) VALUES ('Оперативный дежурный в/ч 5698', 'В.Конопко', '53-60', false, 1);
            INSERT INTO PUBLIC.TLG_SUPERVISOR (TLG_SUPERVISOR_POSITION, TLG_SUPERVISOR_LASTNAME, TLG_SUPERVISOR_TELEPHONE, TLG_SUPERVISOR_DEFAULT, TLG_SUPERVISOR_RANK) VALUES ('Оперативный дежурный в/ч 5698', 'Частный П.П.', '56-80', false, 2);
            INSERT INTO PUBLIC.TLG_SUPERVISOR (TLG_SUPERVISOR_POSITION, TLG_SUPERVISOR_LASTNAME, TLG_SUPERVISOR_TELEPHONE, TLG_SUPERVISOR_DEFAULT, TLG_SUPERVISOR_RANK) VALUES ('Оперативный дежурный в/ч 5698', 'Козлов А.Н.', '56-80', true, 3);
                        
            create table TLG_TYPE
            (
                TLG_TYPE_ID          INTEGER auto_increment,
                TLG_TYPE_NAME        VARCHAR(40),
                TLG_TYPE_DESCRIPTION TEXT(200),
                constraint PK_TLG_TYPE
                    primary key (TLG_TYPE_ID)
            );
                        
            comment on table TLG_TYPE is 'Особенности телеграмм по назначению, способу оформлени¤, подачи или доставки.';
                        
            create unique index TLG_TYPE_PK
                on TLG_TYPE (TLG_TYPE_ID);
                        
            INSERT INTO PUBLIC.TLG_TYPE (TLG_TYPE_NAME, TLG_TYPE_DESCRIPTION) VALUES ('с уведомлением', '');
            INSERT INTO PUBLIC.TLG_TYPE (TLG_TYPE_NAME, TLG_TYPE_DESCRIPTION) VALUES ('с доставкой в срок', '');
            INSERT INTO PUBLIC.TLG_TYPE (TLG_TYPE_NAME, TLG_TYPE_DESCRIPTION) VALUES ('оперативная информация', '');
                        
            create table TLG_SECRECY_PARAGRAPH
            (
                TLG_SECRECY_PARAGRAPH_ID   INTEGER not null,
                TLG_SECRECY_PARAGRAPH_TEXT TEXT(800),
                constraint PK_TLG_SECRECY_PARAGRAPH
                    primary key (TLG_SECRECY_PARAGRAPH_ID)
            );
                        
            comment on table TLG_SECRECY_PARAGRAPH is 'Особенности телеграмм по назначению, способу оформления, подачи или доставки.';
                        
            create unique index TLG_SECRECY_PARAGRAPH_PK
                on TLG_SECRECY_PARAGRAPH (TLG_SECRECY_PARAGRAPH_ID);
                        
            INSERT INTO PUBLIC.TLG_SECRECY_PARAGRAPH (TLG_SECRECY_PARAGRAPH_ID, TLG_SECRECY_PARAGRAPH_TEXT) VALUES (1, 'Сведения, раскрывающие план применения Вооруженных Сил Российской Федерации, оперативные планы применения (планы боевого применения) войск, содержание мероприятий, касающихся военных действий и их обеспечения, боевого управления или перевода с мирного на военное время, а также боевые задачи носителям ядерного оружия.');
            INSERT INTO PUBLIC.TLG_SECRECY_PARAGRAPH (TLG_SECRECY_PARAGRAPH_ID, TLG_SECRECY_PARAGRAPH_TEXT) VALUES (5, 'Сведения, раскрывающие планы, направленность или содержание мероприятий оперативной, боевой или мобилизационной подготовки войск');
            INSERT INTO PUBLIC.TLG_SECRECY_PARAGRAPH (TLG_SECRECY_PARAGRAPH_ID, TLG_SECRECY_PARAGRAPH_TEXT) VALUES (9, 'Сведения о планах строительства (совершенствования), развитии, численности, боевом составе, боевых возможностях или количестве войск, состоянии боевой готовности войск, состоянии боевого обеспечения, составе дежурных сил (средств) и состоянии их готовности');
            INSERT INTO PUBLIC.TLG_SECRECY_PARAGRAPH (TLG_SECRECY_PARAGRAPH_ID, TLG_SECRECY_PARAGRAPH_TEXT) VALUES (17, 'Сведения о тактико-технических требованиях, тактико-технических характеристиках, возможностях боевого применения вооружения, военной техники');
                        
            create table TLG_ADDRESS
            (
                TLG_ADDRESS_ID             BIGINT auto_increment,
                TLG_ADDRESS_CALLSIGN       VARCHAR(20)  not null,
                TLG_ADDRESS_NAME           VARCHAR(100) not null,
                TLG_ADDRESS_PERSON         VARCHAR(40),
                TLG_ADDRESS_PERSON_RESPECT VARCHAR(60),
                constraint TLG_ADDRESS_PK
                    primary key (TLG_ADDRESS_ID)
            );
                        
            comment on table TLG_ADDRESS is 'Адрес, по которому должна быть доставлена телеграмма, с указанием наименовани¤ адресата (адресатов).';
                        
            create unique index TLG_ADDRESS_PK
                on TLG_ADDRESS (TLG_ADDRESS_ID);
                        
            INSERT INTO PUBLIC.TLG_ADDRESS (TLG_ADDRESS_CALLSIGN, TLG_ADDRESS_NAME, TLG_ADDRESS_PERSON, TLG_ADDRESS_PERSON_RESPECT) VALUES ('Опека-15', 'Начальнику ОП КРТМ в/ч 5689', 'г.Хабаровск, ул. Снежная 16', 'Уважаемый Константин Владимирович!');
            INSERT INTO PUBLIC.TLG_ADDRESS (TLG_ADDRESS_CALLSIGN, TLG_ADDRESS_NAME, TLG_ADDRESS_PERSON, TLG_ADDRESS_PERSON_RESPECT) VALUES ('Пустошь-13', 'Оперативный отдел в/ч 5689', 'г.Владимир', '');
                        
                        
            create table TLG
            (
                TLG_ID            BIGINT auto_increment,
                TLG_STATE_ID      INTEGER default NULL,
                TLG_CATEGORY_ID   INTEGER default NULL,
                TLG_SECRECY_ID    INTEGER default NULL,
                TLG_SUPERVISOR_ID INTEGER default NULL,
                TLG_TITLE         TEXT(200),
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
                TLG_RESPECT       BOOLEAN default FALSE not null,
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
                        
            INSERT INTO PUBLIC.TLG (TLG_STATE_ID, TLG_CATEGORY_ID, TLG_SECRECY_ID, TLG_SUPERVISOR_ID, TLG_TITLE, TLG_TEXT, TLG_DATE_GREATE, TLG_DATE_INPUT, TLG_DATE_EDIT, TLG_NUMBER, TLG_READ, TLG_DRAFT, TLG_SAMPLE, TLG_ARCHIVE, TLG_VERSION, TLG_APPS, TLG_RESPECT) VALUES (null, 1, 2, 3, 'Ответ на исходящий 56/98-23 от 16.06.2022', '<p class="tlg-text">Сущность «тип телеграммы». Телеграммы, имеющие особенности по назначению, способу оформления, подачи или доставки, разделяются на следующие виды:&nbsp;</p><p class="tlg-text">"с уведомлением о вручении телеграфом" (с отметкой "уведомление телеграфом");&nbsp;</p><p class="tlg-text">"с уведомлением о вручении телеграфом "срочное" (с отметкой "уведомление телеграфом срочное");&nbsp;</p><p class="tlg-text">"с доставкой в срок, указанный отправителем" (с отметкой "вручить (дата)");&nbsp;</p><p class="tlg-text">"с доставкой в населенный пункт (поселение), не имеющий телеграфной, факсимильной (телефонной) связи" (с отметкой "почтой заказное");&nbsp;</p><p class="tlg-text">"метео" (с отметкой "метео");&nbsp;</p><p class="tlg-text">"переводная" (с отметкой "переводная");&nbsp;</p><p class="tlg-text">"схемная" (с отметкой "схема (номер)").Также могут вводится дополнительные особенности телеграмм по назначению, способу оформления, подачи или доставки. Для поддержания функции справки в программе необходимо хранения пояснения к каждому типу. Телеграмма может иметь один или несколько </p>', '2022-05-02 14:10:29.741000', null, null, null, true, false, false, false, 0, null, true);
                        
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
                        
            INSERT INTO PUBLIC.TYPE_IN_TLG (TLG_ID, TLG_TYPE_ID) VALUES (1, 3);
                        
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
                        
            INSERT INTO PUBLIC.MAILING (TLG_ADDRESS_ID, TLG_ID) VALUES (1, 1);
            INSERT INTO PUBLIC.MAILING (TLG_ADDRESS_ID, TLG_ID) VALUES (2, 1);
                        
            create table TLG_APPLICATION
            (
                APP_ID   BIGINT auto_increment,
                APP_NAME VARCHAR(50)                                                                                                                                 not null,
                APP_EXT  VARCHAR(20)                                                                                                                                 not null,
                APP_SIZE VARCHAR(20)                                                                                                                                 not null,
                APP_SEC  INT,
                TLG_ID   LONG,
                APP_EXE  VARCHAR(50) default STRINGDECODE('\\u0442\\u0435\\u043a\\u0441\\u0442\\u043e\\u0432\\u044b\\u0439 \\u0440\\u0435\\u0434\\u0430\\u043a\\u0442\\u043e\\u0440') not null,
                APP_NUMB VARCHAR(30)                                                                                                                                 not null,
                constraint PK_TLG_APPLICATION
                    primary key (APP_ID),
                constraint TLG_APPLICATION_TLG_SECRECY_TLG_SECRECY_ID_FK
                    foreign key (APP_SEC) references TLG_SECRECY (TLG_SECRECY_ID)
                        on update cascade
            );
                        
            create unique index TLG_APPLICATION_PK
                on TLG_APPLICATION (APP_ID);
                        
            INSERT INTO PUBLIC.TLG_APPLICATION (APP_NAME, APP_EXT, APP_SIZE, APP_SEC, TLG_ID, APP_EXE, APP_NUMB) VALUES ('123123123', 'jpg', '865474', 2, 1, 'Просмотр изображений', '56987');
                        
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
                        
            INSERT INTO PUBLIC.PARS_IN_TLGS (TLG_ID, TLG_SECRECY_PARAGRAPH_ID) VALUES (1, 1);
            INSERT INTO PUBLIC.PARS_IN_TLGS (TLG_ID, TLG_SECRECY_PARAGRAPH_ID) VALUES (1, 9);
            INSERT INTO PUBLIC.PARS_IN_TLGS (TLG_ID, TLG_SECRECY_PARAGRAPH_ID) VALUES (1, 17);
                        
            create table PARS_IN_APPS
            (
                APP_ID                   BIGINT not null,
                TLG_SECRECY_PARAGRAPH_ID INT    not null,
                constraint PARS_IN_APPS_TLG_APPLICATION_APP_ID_FK
                    foreign key (APP_ID) references TLG_APPLICATION (APP_ID)
                        on update cascade on delete cascade,
                constraint PARS_IN_APPS_TLG_SECRECY_PARAGRAPH_TLG_SECRECY_PARAGRAPH_ID_FK
                    foreign key (TLG_SECRECY_PARAGRAPH_ID) references TLG_SECRECY_PARAGRAPH (TLG_SECRECY_PARAGRAPH_ID)
                        on update cascade on delete cascade
            );
                        
            create table PROPERTY
            (
                ID_USER                 LONG                  not null,
                PATH_PDF                VARCHAR(120),
                PATH_TLG                VARCHAR(120),
                PATH_TMP                VARCHAR(120),
                PATH_DOC                VARCHAR(120),
                MIN_STRINGS_ON_NEW_PAGE INT         default 5 not null,
                NUMBER_COMPUTER         VARCHAR(20) default STRINGDECODE('\\u042d\\u0412\\u041c00-000'),
                constraint PROPERTY_PK
                    primary key (ID_USER)
            );

            comment on table PROPERTY is 'Таблица настроек программы';

            comment on column PROPERTY.PATH_TMP is 'Путь к шаблону';

            comment on column PROPERTY.MIN_STRINGS_ON_NEW_PAGE is 'минимальное количество строк на новой странице';

            comment on column PROPERTY.NUMBER_COMPUTER is 'Номер текущей машины';

            INSERT INTO PUBLIC.PROPERTY (ID_USER, PATH_PDF, PATH_TLG, PATH_TMP, MIN_STRINGS_ON_NEW_PAGE, NUMBER_COMPUTER) VALUES (1, 'C:\\Users\\Fezas\\PDF', 'C:\\Users\\Fezas\\TLG', 'C:\\Users\\Fezas', 5, 'ЭВМ00-000');
            
            create table USERS
            (
                USER_ID        INTEGER auto_increment,
                USER_LOGIN     VARCHAR(16),
                USER_PASSWORD  VARCHAR(10),
                USER_ROLE      VARCHAR(20),
                USER_TELEPHONE VARCHAR(30),
                USER_POSITION  VARCHAR(100),
                USER_FIO       VARCHAR(30) not null,
                constraint PK_USERS
                    primary key (USER_ID)
            );
                        
            create unique index USERS_PK
                on USERS (USER_ID);
                        
            INSERT INTO PUBLIC.USERS (USER_LOGIN, USER_PASSWORD, USER_ROLE, USER_TELEPHONE, USER_POSITION, USER_FIO) VALUES ('admin', 'admin', 'администратор', '2826-666666 доб.14', 'Оператор ОС', 'Степанцов П.В.');
            INSERT INTO PUBLIC.USERS (USER_LOGIN, USER_PASSWORD, USER_ROLE, USER_TELEPHONE, USER_POSITION, USER_FIO) VALUES ('operator', '1234', 'пользователь', '8-4242-165-08-33', 'Техник УС', 'Кудряшов В.В.');
                      
                        """;

    public String getSql() {
        return sql;
    }
}