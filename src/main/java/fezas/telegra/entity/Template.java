/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Template {
    private Long id;
    private String title;
    private String body;
    private Timestamp date;
    private Boolean def;

    public Template(Long id, String title, String body, Timestamp date) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public Template() {

    }
}
