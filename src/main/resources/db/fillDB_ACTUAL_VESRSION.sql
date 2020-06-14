-- Auto-generated SQL script #202004021911
INSERT INTO role (role_name)
values ('Customer'),
       ('Doctor'),
       ('Pharmacist'),
       ('Administrator');


INSERT INTO public.login (login, "password", salt)
VALUES ('Ivanych', '08c67f4db2f7a05207a9bd8fab6465ea', '\>;ZnQm')
     , ('Petrova', 'dc740d61dbbbcbdecc4c90b574e254b2', ',q5[AyX;')
     , ('Zoba', '897733a73504422f06dabe21fcca9032', 'hDrai,^`')
     , ('kuzya', 'adc840e53bd5de285690274569621533', ']:YivO')
     , ('Olenka', '2cb2a3085da4ec83da3957d2a8e98c0c', 'N7^"X')
     , ('Doctor', 'e762fa3b1554deddd43d1a45f8b82ff9', 'i3I3(r[F');


INSERT INTO public.account (id, surname, "name", patronymic, phone, status, balance, fk_role, credit)
VALUES (1, 'Kuzin', 'Dmitry', 'Gennadyevich', '+375(29)5556433', true, NULL, 3, 0)
     , (2, 'Ivanov', 'Andrey', 'Viktorovich', '+375(29)5554433', true, 5000, 1, 0)
     , (3, 'Pertova', 'Anna', 'Ivanovna', '+375(29)5555433', true, NULL, 2, 0)
     , (4, 'Pertova', 'Anna', 'Ivanovna', '+375(29)5555433', true, 3600, 1, 0)
     , (5, 'Zobnin', 'Eugeny', 'Yuryevich', '+375(33)3336687', true, 540, 1, 0)
     , (46, 'Петропавловская', 'Олеся', '', '', true, 0, 1, 0)
;


INSERT INTO public.recipe (medicine, doctor_id, fk_customer, id_medicine_in_order, validity, need_extension, dosage)
VALUES ('New', 3, 5, NULL, NULL, true, '15/26')
     , ('Гроприносин', 3, 2, NULL, '2020-10-20', false, '500 мг')
     , ('АмброГексал', 3, 2, NULL, '2020-02-14', true, '7,5 мг/мл')
     , ('Гроприносин', 3, 5, 1, '2020-04-20', false, '500 мг')
     , ('АмброГексал', 3, 5, NULL, '2020-11-14', false, '7,5 мг/мл')
     , ('АмброГексал', 3, 6, 2, '2020-06-03', false, '7,5 мг/мл')
     , ('New', NULL, 6, NULL, NULL, true, '15/26')
     , ('Гроприносин', 3, 5, NULL, '2020-06-30', false, '500 мг')
;

INSERT INTO public.medicine ("name", indivisible_amount, amount, dosage, exp_date, recipe_required, price, added_by,
                             pharm_form)
VALUES ('Кагоцел', 10, 80, '12 мг', '2020-06-01', false, 827, 1, 'таблетки')
     , ('Гроприносин', 10, 110, '500 мг', '2022-06-01', true, 460, 1, 'таблетки')
     , ('Синупрет', 10, 20, '-', '2022-03-01', true, 300, 1, 'таблетки')
     , ('АмброГексал', 1, 7, '7,5 мг/мл', '2022-05-01', true, 610, 1, 'сироп')
     , ('Парацетамол', 1, 3, '30 мг/мл', '2020-05-01', false, 248, 1, 'сироп')
     , ('New', 50, 5000, '15/26', '2021-05-05', true, 299, 1, 'Krem')
;

INSERT INTO public."order" (fk_customer, payment_state, order_price, order_date, cart)
VALUES (2, false, 300, '2020-04-02 00:00:00.000', false)
     , (4, true, 600, '2020-04-02 00:00:00.000', false)
     , (5, true, 4039, '2020-02-22 00:00:00.000', true)
     , (6, false, 248, '2020-04-02 00:00:00.000', false)
     , (6, false, 460, '2020-04-15 00:00:00.000', false)
     , (34, false, 0, '2020-01-01 00:00:00.000', true)
;

INSERT INTO public.medicine_in_order (medicine, dosage, quantity, price, fk_order, indivisible_amount, recipe_required)
VALUES ('Лордес', '5 мг', 1, 0, 1, 10, false)
     , ('Синупрет', '-', 2, 300, 3, 10, true)
     , ('Синупрет', '-', 1, 300, 4, 10, true)
     , ('Парацетамол', '30 мг/мл', 1, 248, 5, 10, false)
     , ('Гроприносин', '500 мг', 1, 460, 6, 10, true)
     , ('Test', '15/26', 2, 44, 5, 10, false)
     , ('Test', '15/26', 1, 44, 1, 10, false)
     , ('Синупрет', '-', 1, 0, 1, 10, true)
     , ('  NewNew', '15/26', 5, 799, 1, 10, true)
     , ('АмброГексал', '7,5 мг/мл', 5, 610, 2, 1, true)
;

INSERT INTO public.active_med_in_cart (id_medicine, id_medicine_in_order)
VALUES (4, 61)
     , (5, 65)
     , (14, 62)
     , (24, 58)
;