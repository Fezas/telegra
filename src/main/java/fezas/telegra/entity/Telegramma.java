/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
@Getter
@Setter
public class Telegramma {
    private Long tlgId;
    private Category category;
    private Secrecy secrecy;
    private Supervisor supervisor;
    private String title;
    private String text;
    private Timestamp tlgDateGreate;
    private Timestamp tlgDateInput;
    private Timestamp  tlgDateEdit;
    private String tlgNumber;
    private Boolean tlgRead;
    private Boolean tlgDraft;
    private Boolean tlgSample;
    private Boolean tlgArchive;
    private Boolean tlgRespect;
    private Integer tlgVersion;
    private CheckBox remark;
    private String formatTlgDateGreate;
    private String apps;
    public Telegramma() {
    }

    public Telegramma(Long tlgId, Category category, Secrecy secrecy, Supervisor supervisor,
                      String  title, String text,
                      Timestamp tlgDateGreate, Timestamp tlgDateInput,
                      Timestamp tlgDateEdit, String tlgNumber,
                      Boolean tlgRead, Boolean tlgDraft,
                      Boolean tlgSample, Boolean tlgArchive, Boolean tlgRespect,
                      Integer tlgVersion, String apps, String remark) {
        this.tlgId = tlgId;
        this.category = category;
        this.secrecy = secrecy;
        this.supervisor = supervisor;
        this.title = title;
        this.text = text;
        this.tlgDateGreate = tlgDateGreate;
        this.tlgDateInput = tlgDateInput;
        this.tlgDateEdit = tlgDateEdit;
        this.tlgNumber = tlgNumber;
        this.tlgRead = tlgRead;
        this.tlgDraft = tlgDraft;
        this.tlgSample = tlgSample;
        this.tlgArchive = tlgArchive;
        this.tlgRespect = tlgRespect;
        this.tlgVersion = tlgVersion;
        this.remark = new CheckBox();
        this.apps = apps;
        this.formatTlgDateGreate = tlgDateGreate
                .toLocalDateTime()
                .atZone(ZoneId.of("+08:00"))
                .format(DateTimeFormatter
                        .ofPattern( "dd.MM.yy HH:mm:ss" )
                        .withLocale(new Locale("ru")));
    }
}
