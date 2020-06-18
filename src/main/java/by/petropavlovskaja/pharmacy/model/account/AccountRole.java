package by.petropavlovskaja.pharmacy.model.account;

import java.io.Serializable;

public enum AccountRole implements Serializable {
    CUSTOMER("Клиент", 1),
    DOCTOR("Доктор", 2),
    PHARMACIST("Фармацевт", 3),
    ADMIN("Администратор", 4);

    private String name;
    private int id;

    AccountRole(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
