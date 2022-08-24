/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.entity;

import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Type {
    private Integer typeId;
    private String typeName;
    private String typeDesc;
    private CheckBox remark;
    public Type() {
    }

    public Type(Integer typeId, String typeName, String typeDesc, String remark) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.typeDesc = typeDesc;
        this.remark = new CheckBox();
    }
}
