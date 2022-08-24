/*
 * Copyright (c) 2022. Stepantsov P.V.
 */

package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rank {
    Integer id;
    String nameRank, shortNameRank;

    public Rank(Integer id, String nameRank, String shortNameRank) {
        this.id = id;
        this.nameRank = nameRank;
        this.shortNameRank = shortNameRank;
    }

    public Rank() {
    }

    @Override
    public String toString() {
        return nameRank;
    }
}
