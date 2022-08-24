/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
@Getter
@Setter
public class ParsInTlgs {
    private Optional<Telegramma> telegramma;
    private ParagraphEntity paragraph;

    public ParsInTlgs(Optional<Telegramma> telegramma, ParagraphEntity paragraph) {
        this.telegramma = telegramma;
        this.paragraph = paragraph;
    }

}
