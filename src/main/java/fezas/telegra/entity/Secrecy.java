/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Secrecy {
    Integer secerecyId;
    String secrecyName;

    public Secrecy(Integer secerecyId, String secrecyName) {
        this.secerecyId = secerecyId;
        this.secrecyName = secrecyName;
    }

    public Secrecy() {
    }

    @Override
    public String toString() {
        return secrecyName;
    }
}
