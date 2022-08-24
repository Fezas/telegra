package fezas.telegra.entity;

import javafx.scene.control.CheckBox;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDoc {
    private Long appId;
    private String appName;
    private String appExt;
    private String appSize;
    private Secrecy appSec;
    private Long tlgId;
    private String appExe;
    private String appNumb;
    private CheckBox remark;
    public ApplicationDoc() {
    }

    public ApplicationDoc(Long appId, String appName, String appExt, String appSize, Secrecy appSec, Long tlgId, String appExe, String appNumb, String remark) {
        this.appId = appId;
        this.appName = appName;
        this.appExt = appExt;
        this.appSize = appSize;
        this.appSec = appSec;
        this.tlgId = tlgId;
        this.appExe = appExe;
        this.appNumb = appNumb;
        this.remark = new CheckBox();
    }
}
