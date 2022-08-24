/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mailing {
    private Address address;
    private Telegramma telegramma;

    public Mailing(Address address, Telegramma telegramma) {
        this.address = address;
        this.telegramma = telegramma;
    }
}
