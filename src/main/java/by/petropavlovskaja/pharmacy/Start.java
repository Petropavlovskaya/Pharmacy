package by.petropavlovskaja.pharmacy;

import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
import by.petropavlovskaja.pharmacy.db.impl.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.sax.SAXResult;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Start {
    private static Logger logger = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) {

//        sum();


/*        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String requestDate = "2020-06-04";
        Date exp_date = null;
        try {
            exp_date = format.parse(requestDate);
        } catch (ParseException e) {
            logger.error("Can't parse request parameter Exp_date: " + exp_date + ". Error: " + e);
        }
        Date currentDate = new Date();

        if (currentDate.after(exp_date)) {
            System.out.println(false);
        }else
            System.out.println(true);*/

/*        Account account = new Account.AccountBuilder("Semenov", "Andrey", AccountRole.ADMIN).build();
        String login = "Sem22";
        String password = "123456";
        boolean insert = AccountDAO.getInstance().create(account, login, password);
        System.out.println("Insert into DB = " + insert);*/

/*        Account account = new Account.AccountBuilder("Петропавловская", "Олеся", AccountRole.ADMIN).build();
        String login = "Olesia123";
        String password = "998877";*/
//        boolean insert = AccountDAO.getInstance().create(account, login, password);
//        System.out.println(insert);


/*        logger.debug("Main class started");
        System.out.println("kuzma " + AccountDAO.getMd5Password("kuzma", "]:YivO"));
        System.out.println("Ivanych44v5 " + AccountDAO.getMd5Password("Ivanych44v5", "\\>;ZnQm"));
        System.out.println("Petrova3355 " + AccountDAO.getMd5Password("Petrova3355", "i3I3(r[F"));
        System.out.println("Petrova3355 " + AccountDAO.getMd5Password("Petrova3355", ",q5[AyX;"));
        System.out.println("eugen " + AccountDAO.getMd5Password("eugen", "hDrai,^`"));
        System.out.println("OlkaOlka " + AccountDAO.getMd5Password("OlkaOlka", "N7^\"X"));*/


    }


    public static void sum() {
/*        List<String> list1 = Arrays.asList("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6", "Tab7");
        List<String> list2 = Arrays.asList("Tab1", "Tab3", "Tab2", "Tab4", "Tab7", "Tab5", "Tab6");
        List<String> list3 = Arrays.asList("Tab1", "Tab1", "Tab1", "Tab2", "Tab2", "Tab6", "Tab7");
        List<String> list4 = Arrays.asList("Tab1", "Tab2", "Tab2", "Tab1", "Tab1", "Tab6", "Tab7");
        if (list1.containsAll(list2)) {
            System.out.println("+");
        }
        if (list3.containsAll(list4))
            System.out.println("++");*/


        Map<Order, List<String>> map1 = new TreeMap<>();
        Map<Order, List<String>> map2 = new TreeMap<>();

        List<String> forMap1 = Arrays.asList("Med1", "Med2");
        List<String> forMap11 = Arrays.asList("Med3", "Med4");
        List<String> forMap111 = Arrays.asList("Zakaz1", "Zakaz2");

        Order order1 = new Order(4);
        Order order2 = new Order(2);
        Order order3 = new Order(6);
        map1.put(order1, forMap1);
        map1.put(order1, forMap11);
//        map1.put(order3, forMap111);

        System.out.println(map1.toString());


/*        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        System.out.println(ts);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(sdf.format(timestamp));*/


    }


}