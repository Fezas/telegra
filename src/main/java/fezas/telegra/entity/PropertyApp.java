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
    private String pathTMP;
    private String pathDOC;
    private Integer minStringsOnNewPage;
    private String numberComputer;

    public PropertyApp() {
    }

    public PropertyApp(Long idUser, String pathPDF, String pathTLG, String pathTMP,
                       String pathDOC, Integer minStringsOnNewPage, String numberComputer) {
        this.idUser = idUser;
        this.pathPDF = pathPDF;
        this.pathTLG = pathTLG;
        this.pathTMP = pathTMP;
        this.pathDOC = pathDOC;
        this.minStringsOnNewPage = minStringsOnNewPage;
        this.numberComputer = numberComputer;
    }
}

