/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TmpAddress {
    private String call, name, pers, resp;

    public TmpAddress(String call, String name, String pers, String resp) {
        this.call = call;
        this.name = name;
        this.pers = pers;
        this.resp = resp;
    }
}
