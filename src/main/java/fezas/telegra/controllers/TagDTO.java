/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class TagDTO {
    private String title, grif, pp, cat, text, visor, visorrank, visorfam, exec, execf, exect, idrm;
    private ArrayList<String> pars;

    public TagDTO(String title, String grif, String pp, String cat, ArrayList<String> pars,
                  String visor, String visorrank, String visorfam, String exec, String execf, String exect, String idrm) {
        this.title = title;
        this.grif = grif;
        this.pp = pp;
        this.cat = cat;
        this.pars = pars;
        this.visor = visor;
        this.visorrank = visorrank;
        this.visorfam = visorfam;
        this.exec = exec;
        this.execf = execf;
        this.exect = exect;
        this.idrm = idrm;
    }
}
