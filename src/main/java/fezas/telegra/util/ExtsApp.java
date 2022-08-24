/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.util;

import java.util.HashMap;

public class ExtsApp {
    public HashMap<String, String> exts = new HashMap<>();
    public ExtsApp() {
        exts.put("accdb", "Microsoft Access");
        exts.put("accde", "Microsoft Access");
        exts.put("accdr", "Microsoft Access");
        exts.put("accdt", "Microsoft Access");

        exts.put("doc", "Microsoft Word");
        exts.put("docm", "Microsoft Word");
        exts.put("docx", "Microsoft Word");

        exts.put("xls", "Microsoft Excel");
        exts.put("xlsx", "Microsoft Excel");

        exts.put("eml", "Outlook Express");
        exts.put("exe", "Microsoft Windows");

        exts.put("gif", "Просмотр изображений");
        exts.put("jpg", "Просмотр изображений");
        exts.put("jpeg", "Просмотр изображений");
        exts.put("png", "Просмотр изображений");
        exts.put("bmp", "Просмотр изображений");

        exts.put("rar", "WinRar");
        exts.put("zip", "WinRar");

        exts.put("txt", "Notepad");
        exts.put("crl", "CorelDraw");

        exts.put("pdf", "Acrobat Reader");
        exts.put("html", "IExplorer");
    }

    public String appNameFromExt(String ext) {
        if (exts.containsKey(ext)) {
            return exts.get(ext);
        } else return null;
    }

}
