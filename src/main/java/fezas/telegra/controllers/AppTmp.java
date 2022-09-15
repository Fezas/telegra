/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppTmp {
    String name, ext, size, sec, exe, pp, numb;

    public AppTmp(String name, String ext, String size, String sec, String exe, String pp, String numb) {
        this.name = name;
        this.ext = ext;
        this.size = size;
        this.sec = sec;
        this.exe = exe;
        this.pp = pp;
        this.numb = numb;
    }
}
