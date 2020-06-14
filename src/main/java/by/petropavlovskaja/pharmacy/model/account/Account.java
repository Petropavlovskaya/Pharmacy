package by.petropavlovskaja.pharmacy.model.account;

import by.petropavlovskaja.pharmacy.model.Medicine;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Account {
    private int id;
    private String surname;
    private String name;
    private String patronymic;
    private String phoneNumber;
    private AccountRole accountRole;
    private boolean status;


/*    // for test DELETE IT

    // for test DELETE IT
    public User(int id, String surname, String name) {
        this.id = id;
        this.surname = surname;
        this.name = name;
    }*/

    // method for create template User before search. Create InvalidUser with ID=-1
    public Account(int id) {
        this.id = id;
    }

    public Account(AccountBuilder accountBuilder) {
        this.id = accountBuilder.id;
        this.surname = accountBuilder.surname;
        this.name = accountBuilder.name;
        this.accountRole = accountBuilder.accountRole;
        this.patronymic = accountBuilder.patronymic;
        this.phoneNumber = accountBuilder.phoneNumber;
        this.status = accountBuilder.status;
    }

    public static class AccountBuilder {
        private int id;
        private String surname;
        private String name;
        private String patronymic;
        private String phoneNumber;
        private AccountRole accountRole;
        private boolean status = true;

        public AccountBuilder(String surname, String name, AccountRole accountRole) {
            this.surname = surname;
            this.name = name;
            this.accountRole = accountRole;
        }

        public AccountBuilder withId(int id) {
            this.id = id;
            return this;
        }

        public AccountBuilder withPatronymic(String patronymic) {
            this.patronymic = patronymic;
            return this;
        }

        public AccountBuilder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public AccountBuilder withStatus(boolean status) {
            this.status = status;
            return this;
        }

        public AccountRole getAccountRole() {
            return accountRole;
        }

        public Account build() {
            return new Account(this);
        }

    }

    public String getSurname() {
        return surname;
    }

    public String getName() {
        return name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isActive() {
        return status;
    }


    public int getId() {
        return id;
    }

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

    public static class AccountSurnameComparator implements Comparator<Account> {
        public int compare(Account a, Account b) {
            return a.getSurname().compareTo(b.getSurname());
        }
    }
    public static class AccountNameComparator implements Comparator<Account> {
        public int compare(Account a, Account b) {
            return a.getName().compareTo(b.getName());
        }
    }
    public static class AccountPatronymicComparator implements Comparator<Account> {
        public int compare(Account a, Account b) {
            if (a.getPatronymic()==null || b.getPatronymic()==null) {
                return 0;
            }
            return a.getPatronymic().compareTo(b.getPatronymic());
        }
    }
    public static class AccountPhoneComparator implements Comparator<Account> {
        public int compare(Account a, Account b) {
            if (a.getPhoneNumber()==null || b.getPhoneNumber()==null) {
                return 0;
            }
            return a.getPhoneNumber().compareTo(b.getPhoneNumber());
        }
    }


}
