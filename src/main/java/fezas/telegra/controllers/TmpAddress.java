/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.controllers;

public class TmpAddress {
    private String call, name, pers, resp;

    public TmpAddress(String call, String name, String pers, String resp) {
        this.call = call;
        this.name = name;
        this.pers = pers;
        this.resp = resp;
    }

    public String getCall() {
        return call;
    }

    public String getName() {
        return name;
    }

    public String getPers() {
        return pers;
    }

    public String getResp() {
        return resp;
    }
}
