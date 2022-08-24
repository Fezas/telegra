package fezas.telegra.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long userId;
    private String userLogin, userPassword, userTelephone, userPosition, userRole, userFIO;
    public User() {
    }

    public User(Long userId, String userLogin, String userPassword, String userTelephone, String userPosition, String userRole, String userFIO) {
        this.userId = userId;
        this.userLogin = userLogin;
        this.userPassword = userPassword;
        this.userTelephone = userTelephone;
        this.userPosition = userPosition;
        this.userRole = userRole;
        this.userFIO = userFIO;
    }

    @Override
    public String toString() {
        return
                ", userPosition='" + userPosition + '\'' +
                ", userFIO='" + userFIO + '\'' +
                "userTelephone='" + userTelephone + '\'';
    }
}
