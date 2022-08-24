/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
public class History {
    private Long historyId;
    private String historyText;
    private Timestamp historyDate;

    public History(Long historyId, String historyText, Timestamp historyDate) {
        this.historyId = historyId;
        this.historyText = historyText;
        this.historyDate = historyDate;
    }
}
