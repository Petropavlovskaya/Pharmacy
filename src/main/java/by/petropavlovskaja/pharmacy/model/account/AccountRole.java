package by.petropavlovskaja.pharmacy.model.account;

import java.io.Serializable;

/**
 * Enumeration account roles. Has property <b>CUSTOMER</b>, <b>DOCTOR</b>, <b>PHARMACIST</b>, <b>ADMIN</b>
 */
public enum AccountRole implements Serializable {
    /**
     * Property - customer
     */
    CUSTOMER("Клиент", 1),
    /**
     * Property - doctor
     */
    DOCTOR("Доктор", 2),
    /**
     * Property - pharmacist
     */
    PHARMACIST("Фармацевт", 3),
    /**
     * Property - administrator
     */
    ADMIN("Администратор", 4);

    /**
     * Property - name
     */
    private String name;
    /**
     * Property - ID
     */
    private int id;

    /**
     * Constructor - create account role
     *
     * @param name - account name
     * @param id   - account ID
     */
    AccountRole(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * The method of getting a name of account role value
     *
     * @return - a name of account role value
     */
    public String getName() {
        return name;
    }

    /**
     * The method of getting an ID of account role value
     *
     * @return - an ID of account role value
     */
    public int getId() {
        return id;
    }
}
