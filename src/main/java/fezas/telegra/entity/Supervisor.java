/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Supervisor {
    private Integer id;
    private String position;
    private String lastname;
    private String telephone;
    private Rank rank;
    private String rankName;
    private boolean def;

    public Supervisor(Integer id, String position, String lastname, String telephone, Rank rank, boolean def) {
        this.id = id;
        this.position = position;
        this.lastname = lastname;
        this.telephone = telephone;
        this.rank = rank;
        this.def = def;
    }

    public Supervisor() {
    }

    @Override
    public String toString() {
        return position + " " + lastname;
    }
}
