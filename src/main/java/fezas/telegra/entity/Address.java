package fezas.telegra.entity;

import javafx.scene.control.CheckBox;

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

    public Integer getTlgAddressId() {
        return tlgAddressId;
    }

    public String getTlgAddressCallsign() {
        return tlgAddressCallsign;
    }

    public String getTlgAddressName() {
        return tlgAddressName;
    }

    public String getTlgAddressPerson() {
        return tlgAddressPerson;
    }

    public String getTlgAddressPersonRespect() {
        return tlgAddressPersonRespect;
    }

    public CheckBox getRemark() {
        return remark;
    }

    public void setTlgAddressId(Integer tlgAddressId) {
        this.tlgAddressId = tlgAddressId;
    }

    public void setTlgAddressCallsign(String tlgAddressCallsign) {
        this.tlgAddressCallsign = tlgAddressCallsign;
    }

    public void setTlgAddressName(String tlgAddressName) {
        this.tlgAddressName = tlgAddressName;
    }

    public void setTlgAddressPerson(String tlgAddressPerson) {
        this.tlgAddressPerson = tlgAddressPerson;
    }

    public void setTlgAddressPersonRespect(String tlgAddressPersonRespect) {
        this.tlgAddressPersonRespect = tlgAddressPersonRespect;
    }

    public void setRemark(CheckBox remark) {
        this.remark = remark;
    }

    @Override
    public String toString()  {
        return this.tlgAddressCallsign + " " + this.tlgAddressName;
    }
}
