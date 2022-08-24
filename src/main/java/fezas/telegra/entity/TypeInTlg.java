/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeInTlg {
    private Telegramma telegramma;
    private Type type;

    public TypeInTlg(Telegramma telegramma, Type type) {
        this.telegramma = telegramma;
        this.type = type;
    }
}
