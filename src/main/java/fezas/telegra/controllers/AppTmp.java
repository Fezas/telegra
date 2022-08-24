/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.controllers;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getExe() {
        return exe;
    }

    public void setExe(String exe) {
        this.exe = exe;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getNumb() {
        return numb;
    }

    public void setNumb(String numb) {
        this.numb = numb;
    }
}
