package fezas.telegra.entity;

import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParagraphEntity {
    private Integer secrecyParagraphId;
    private String secrecyParagraphText;
    private CheckBox remark;
    public ParagraphEntity(Integer secrecyParagraphId, String secrecyParagraphText, String remark) {
        this.secrecyParagraphId = secrecyParagraphId;
        this.secrecyParagraphText = secrecyParagraphText;
        this.remark = new CheckBox();
    }

    public ParagraphEntity() {

    }

    @Override
    public String toString() {
        return secrecyParagraphText;
    }
}
