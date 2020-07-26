package by.petropavlovskaja.pharmacy;

import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.model.account.Customer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class EntityRepository {
    public static final MedicineInOrder MEDICINE_IN_ORDER1 = new MedicineInOrder(78, "Лордес", 10, "5 мг", false, 5, 789, 8);
    public static final MedicineInOrder MEDICINE_IN_ORDER2 = new MedicineInOrder(79, "Синупрет", 10, "-", true, 2, 1002, 8);
    public static final MedicineInOrder MEDICINE_IN_ORDER3 = new MedicineInOrder(104, "АмброГексал", 1, "7,5 мг/мл", true, 1, 544, 12);
    //    public static final MedicineInOrder MEDICINE_IN_ORDER4 = new MedicineInOrder(1, "Test", 50, "7,5 мг/мл", true, 11, 544, 12);
    public static final Recipe RECIPE1 = new Recipe(1, "New", "15/26", 44, 1, 184, null, true);
    public static final Recipe RECIPE2 = new Recipe(7, "Гроприносин", "500 мг", 73, 16, 12, null, false);
    public static final Recipe RECIPE3 = new Recipe(8, "Гроприносин", "500 мг", 2, 5, 1, null, false);
    //    public static final Recipe RECIPE4 = new Recipe(17, "Синупрет", "-", 2, 5, 1, null, false);
    public static final Medicine MEDICINE1 = new Medicine(1, "Парацетамол", 1, 39, "30 мг/мл", null, false, 299, 3, "сироп");
    public static final Medicine MEDICINE2 = new Medicine(2, "Гроприносин", 10, 111, "500 мг", null, true, 460, 41, "таблетки");
    public static final Medicine MEDICINE3 = new Medicine(3, "Кагоцел", 10, 41, "12 мг", null, false, 827, 14, "таблетки");
    public static final Medicine MEDICINE4 = new Medicine(6, "Синупрет", 10, 23, "-", null, true, 7, 11, "таблетки");

    public static final Account ACCOUNT1 = new Account.AccountBuilder("Kuzin", "Dmitry", AccountRole.DOCTOR)
            .withId(1).withPatronymic("Gennadyevich").withPhoneNumber("+375(29)5556433").withStatus(true).build();
    public static final Account ACCOUNT2 = new Account.AccountBuilder("Zobnin", "Eugeny", AccountRole.CUSTOMER)
            .withId(2).withPatronymic("Yuryevich").withPhoneNumber("+375(33)3336687").withStatus(false).build();
    public static final Account ACCOUNT3 = new Account.AccountBuilder("Pertova", "Anna", AccountRole.DOCTOR)
            .withId(3).withPatronymic("Ivanovna").withPhoneNumber("+375(29)5555433").withStatus(true).build();
    public static final Customer CUSTOMER1 = new Customer(new Account.AccountBuilder("Zobnin", "Eugeny", AccountRole.CUSTOMER)
            .withId(2).withPatronymic("Yuryevich").withPhoneNumber("+375(33)3336687").withStatus(false));
}
