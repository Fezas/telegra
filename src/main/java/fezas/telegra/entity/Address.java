package fezas.telegra.entity;

import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private Integer tlgAddressId;
    private String tlgAddressCallsign;
    private String tlgAddressName;
    private String tlgAddressPerson;
    private String tlgAddressPersonRespect;
    private CheckBox remark;

    public Address(Integer tlgAddressId, String tlgAddressCallsign,
                   String tlgAddressName, String tlgAddressPerson,
                   String tlgAddressPersonRespect, String remark) {
        this.tlgAddressId = tlgAddressId;
        this.tlgAddressCallsign = tlgAddressCallsign;
        this.tlgAddressName = tlgAddressName;
        this.tlgAddressPerson = tlgAddressPerson;
        this.tlgAddressPersonRespect = tlgAddressPersonRespect;
        this.remark = new CheckBox();
    }

    public Address() {
    }

    @Override
    public String toString()  {
        return this.tlgAddressCallsign + " " + this.tlgAddressName;
    }
}
