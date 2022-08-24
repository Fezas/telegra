/*
 * Copyright (c) $originalComment.match("Copyright \(c\) (\d+)", 1, "-")2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PropertyApp {
    private Long idUser;
    private String pathPDF;
    private String pathTLG;
    private String pathDOC;
    private Integer minStringsOnNewPage;
    private String numberComputer;
    private String service;

    public PropertyApp() {
    }

    public PropertyApp(Long idUser, String pathPDF, String pathTLG, String pathDOC, Integer minStringsOnNewPage, String numberComputer, String service) {
        this.idUser = idUser;
        this.pathPDF = pathPDF;
        this.pathTLG = pathTLG;
        this.pathDOC = pathDOC;
        this.minStringsOnNewPage = minStringsOnNewPage;
        this.numberComputer = numberComputer;
        this.service = service;
    }
}

