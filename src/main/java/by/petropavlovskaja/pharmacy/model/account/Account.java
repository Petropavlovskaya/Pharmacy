package by.petropavlovskaja.pharmacy.model.account;

import java.io.Serializable;
import java.util.Comparator;

/** Class for account entity. Has next properties:
 * <b>serialVersionUID</b>, <b>id</b>, <b>surname</b>, <b>name</b>, <b>patronymic</b>,
 * <b>phoneNumber</b>, <b>accountRole</b> and <b>status</b>
 */
public class Account implements Serializable {
    /** Property - serial version UID */
    private static final long serialVersionUID = 8487635767584478029L;
    /** Property - account ID */
    private int id;
    /** Property - account surname */
    private String surname;
    /** Property - account name */
    private String name;
    /** Property - account patronymic */
    private String patronymic;
    /** Property - account phone number */
    private String phoneNumber;
    /** Property - account role {@link AccountRole}*/
    private AccountRole accountRole;
    /** Property - account status */
    private boolean status;

    /** Create entity of class {@link Account#Account(AccountBuilder)}
     * @param id - account ID
     */
    // method for create template User before search. Create InvalidUser with ID=-1
    public Account(int id) {
        this.id = id;
    }

    /** Create entity of class {@link Account#Account(int)}
     * @param accountBuilder - account builder {@link Account.AccountBuilder}
     */
    public Account(AccountBuilder accountBuilder) {
        this.id = accountBuilder.id;
        this.surname = accountBuilder.surname;
        this.name = accountBuilder.name;
        this.accountRole = accountBuilder.accountRole;
        this.patronymic = accountBuilder.patronymic;
        this.phoneNumber = accountBuilder.phoneNumber;
        this.status = accountBuilder.status;
    }

    /** The nested class for build the account entity  */
    public static class AccountBuilder {
        /** Property - account ID */
        private int id;
        /** Property - account surname */
        private String surname;
        /** Property - account name */
        private String name;
        /** Property - account patronymic */
        private String patronymic;
        /** Property - account phone number */
        private String phoneNumber;
        /** Property - account role {@link AccountRole}*/
        private AccountRole accountRole;
        /** Property - account status */
        private boolean status = true;

        /** Create entity of nested class
         * @param surname - account surname
         * @param name - account name
         * @param accountRole - account role
         */
        public AccountBuilder(String surname, String name, AccountRole accountRole) {
            this.surname = surname;
            this.name = name;
            this.accountRole = accountRole;
        }

        /** Create entity of nested class with account ID
         * @param id - account ID
         * @return - account builder instance
         */
        public AccountBuilder withId(int id) {
            this.id = id;
            return this;
        }

        /** Create entity of nested class with account patronymic
         * @param patronymic - account patronymic
         * @return - account builder instance
         */
        public AccountBuilder withPatronymic(String patronymic) {
            this.patronymic = patronymic;
            return this;
        }

        /** Create entity of nested class with account phone number
         * @param phoneNumber - account phone number
         * @return - account builder instance
         */
        public AccountBuilder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        /** Create entity of nested class with account status
         * @param status - account status
         * @return - account builder instance
         */
        public AccountBuilder withStatus(boolean status) {
            this.status = status;
            return this;
        }

        /** The method of getting the account role value
         * @return - an account role value
         */
        public AccountRole getAccountRole() {
            return accountRole;
        }

        /** The method for build account entity
         * @return - an account entity
         */
        public Account build() {
            return new Account(this);
        }
    }

    /**
     * The method for setting the account surname
     *
     * @param surname - the account surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * The method for setting the account name
     *
     * @param name - the account name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The method for setting the account patronymic
     *
     * @param patronymic - the account patronymic
     */
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    /**
     * The method for setting the account phone number
     *
     * @param phoneNumber - the account phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /** The method of getting the account surname value
     * @return - an account surname value
     */
    public String getSurname() {
        return surname;
    }

    /** The method of getting the account name value
     * @return - an account name value
     */
    public String getName() {
        return name;
    }

    /** The method of getting the account patronymic value
     * @return - an account patronymic value
     */
    public String getPatronymic() {
        return patronymic;
    }

    /** The method of getting the account phone number value
     * @return - an account phone number value
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /** The method of getting the account status value
     * @return - an account status value
     */
    public boolean isActive() {
        return status;
    }

    /** The method of getting the account ID value
     * @return - an account ID value
     */
    public int getId() {
        return id;
    }

    /** The method of getting the account account role value {@link AccountRole}
     * @return - an account account role value
     */
    public AccountRole getAccountRole() {
        return accountRole;
    }

    @Override
    public String toString() {
        return "\n id=" + id +
                ", surname='" + surname + '\'' +
                ", name='" + name + '\'' +
                ", patronymic='" + patronymic + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userRole=" + accountRole +
                ", status=" + status;
    }

    /** The nested class for compare accounts entity {@link Account.AccountNameComparator},
     * {@link Account.AccountPatronymicComparator}, {@link Account.AccountPhoneComparator}
     */
    public static class AccountSurnameComparator implements Comparator<Account> {

        /**
         * The method compare accounts by surname
         *
         * @param a - one account
         * @param b - another account
         * @return - difference between two accounts
         */
        public int compare(Account a, Account b) {
            return a.getSurname().compareTo(b.getSurname());
        }
    }
    /** The nested class for compare accounts entity {@link Account.AccountSurnameComparator},
     * {@link Account.AccountPatronymicComparator}, {@link Account.AccountPhoneComparator}
     */
    public static class AccountNameComparator implements Comparator<Account> {

        /**
         * The method compare accounts by name
         *
         * @param a - one account
         * @param b - another account
         * @return - difference between two accounts
         */
        public int compare(Account a, Account b) {
            return a.getName().compareTo(b.getName());
        }
    }
    /** The nested class for compare accounts entity {@link Account.AccountNameComparator},
     * {@link Account.AccountSurnameComparator}, {@link Account.AccountPhoneComparator}
     */
    public static class AccountPatronymicComparator implements Comparator<Account> {

        /**
         * The method compare accounts by patronymic
         *
         * @param a - one account
         * @param b - another account
         * @return - difference between two accounts
         */
        public int compare(Account a, Account b) {
            if (a.getPatronymic() == null || b.getPatronymic() == null) {
                return 0;
            }
            return a.getPatronymic().compareTo(b.getPatronymic());
        }
    }
    /** The nested class for compare accounts entity {@link Account.AccountNameComparator},
     * {@link Account.AccountPatronymicComparator}, {@link Account.AccountSurnameComparator}
     */
    public static class AccountPhoneComparator implements Comparator<Account> {

        /**
         * The method compare accounts by phone number
         *
         * @param a - one account
         * @param b - another account
         * @return - difference between two accounts
         */
        public int compare(Account a, Account b) {
            if (a.getPhoneNumber() == null || b.getPhoneNumber() == null) {
                return 0;
            }
            return a.getPhoneNumber().compareTo(b.getPhoneNumber());
        }
    }
}