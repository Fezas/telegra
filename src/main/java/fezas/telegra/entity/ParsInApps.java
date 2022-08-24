/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter
@Setter
public class ParsInApps {
    private Optional<ApplicationDoc> applicationDoc;
    private ParagraphEntity paragraph;

    public ParsInApps(Optional<ApplicationDoc> applicationDoc, ParagraphEntity paragraph) {
        this.applicationDoc = applicationDoc;
        this.paragraph = paragraph;
    }
}
