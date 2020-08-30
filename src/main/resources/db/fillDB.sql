-- Auto-generated SQL script #202004021911
INSERT INTO role (role_name)
values ('Customer'),
       ('Doctor'),
       ('Pharmacist'),
       ('Administrator');


INSERT INTO public.login (login, "password", salt)
VALUES ('Ivanych', '08c67f4db2f7a05207a9bd8fab6465ea', '\>;ZnQm')
     , ('Petrova', 'dc740d61dbbbcbdecc4c90b574e254b2', ',q5[AyX;')
     , ('Zoba', '1c6fbc776e29068e0129d520f198b8d7', 'hDrai,^`')
     , ('kuzya', '8f29b357bcb15df92124cb6a4bb0c149', ']:YivO')
     , ('Olenka', '2cb2a3085da4ec83da3957d2a8e98c0c', 'N7^"X')
     , ('Doctor', 'e5c21ec67ea5f7178df71974819de0a0', 'i3I3(r[F');


INSERT INTO public.account (id, surname, "name", patronymic, phone, status, balance, fk_role, credit)
VALUES (1, 'Kuzin', 'Dmitry', 'Gennadyevich', '+375(29)5556433', true, NULL, 3, 0)
     , (2, 'Ivanov', 'Andrey', 'Viktorovich', '+375(29)5554433', true, 5000, 1, 0)
     , (3, 'Perova', 'Anna', 'Ivanovna', '+375(29)555-54-33', true, NULL, 2, 0)
     , (4, 'Pertova', 'Anna', 'Ivanovna', '+375(29)5555433', true, 3600, 1, 0)
     , (5, 'Zobnin', 'Eugeny', 'Yuryevich', '+375(33)333-66-87', true, 1898, 1, 0)
     , (6, 'Matuz', 'Olga', 'Olegovna', '+375(44)4789966', true, 2000, 1, 0)
;


INSERT INTO public.recipe (medicine, doctor_id, fk_customer, id_medicine_in_order, validity, need_extension, dosage)
VALUES ('Гроприносин', 3, 2, NULL, '2020-10-20', false, '500 мг')
     , ('АмброГексал', 3, 6, 2, '2020-06-03', false, '7,5 мг/мл')
     , ('Гроприносин', NULL, 5, NULL, NULL, true, '500 мг')
     , ('Виброцил', 3, 5, 44, '2020-09-29', false, '-')
     , ('Виброцил', NULL, 5, NULL, NULL, true, '-')
     , ('Немозол', 3, 5, NULL, '2020-10-29', false, '400 мг')
;

INSERT INTO public.medicine ("name", indivisible_amount, amount, dosage, exp_date, recipe_required, price, added_by,
                             pharm_form)
VALUES ('Кагоцел', 10, 80, '12 мг', '2020-06-01', false, 827, 1, 'таблетки')
     , ('Гроприносин', 10, 5, '500 мг', '2022-06-01', true, 460, 1, 'таблетки')
     , ('АмброГексал', 1, 4, '7,5 мг/мл', '2022-05-01', true, 610, 1, 'сироп')
     , ('Парацетамол', 1, 3, '30 мг/мл', '2020-05-01', false, 248, 1, 'сироп')
     , ('Фолиевая кислота', 10, 86, '1 мг', '2022-10-12', false, 11, 1, 'таблетки')
     , ('Синупрет', 10, 20, '-', '2021-12-17', true, 300, 1, 'таблетки')
     , ('Флуцинар', 1, 32, '-', '2021-04-03', false, 689, 1, 'мазь')
     , ('Ибуфен', 10, 119, '200 мг', '2021-11-01', false, 500, 1, 'Капсулы')
     , ('Виброцил', 1, 74, '-', '2022-03-01', true, 1303, 1, 'капли назальные')
     , ('Немозол', 1, 53, '400 мг', '2022-07-01', true, 347, 1, 'таблетка')
;
INSERT INTO public.medicine ("name", indivisible_amount, amount, dosage, exp_date, recipe_required, price, added_by,
                             pharm_form)
VALUES ('Уголь биоактивированный', 10, 249, '250 мг', '2022-01-01', false, 36, 1, 'таблетки')
     , ('Афлубин', 1, 7, '-', '2022-03-01', false, 1251, 1, 'капли для приема внутрь')
     , ('Парацетамол', 1, 77, '30 мг/мл', '2023-07-01', false, 372, 1, 'сироп')
     , ('Парацетамол', 10, 31, '500 мг', '2023-02-01', false, 77, 1, 'таблетки')
     , ('Анаферон', 10, 40, '-', '2020-08-26', false, 836, 1, 'таблетки')
     , ('Ромашки цветки измельченные', 1, 8, '-', '2023-04-01', false, 271, 1, 'измельченное сырье')
     , ('Стрептоцид', 1, 35, '100 мг/г', '2022-10-01', false, 264, 1, 'мазь для наружного применения')
     , ('Бинт марлевый стерильный', 1, 81, '-', '2021-11-01', false, 98, 1, '100 мм * 5,0 м')
     , ('Пантенол', 1, 13, 'декспантенол 5%', '2022-07-01', false, 533, 1, 'крем')
;

INSERT INTO public."order" (fk_customer, order_price, order_date, cart)
VALUES (5, 0, '2020-02-22 00:00:00.000', true)
     , (6, 2264, '2020-03-18 00:00:00.000', true)
     , (4, 600, '2020-04-02 00:00:00.000', false)
     , (2, 300, '2020-04-02 00:00:00.000', false)
     , (6, 248, '2020-04-02 00:00:00.000', false)
     , (6, 460, '2020-04-15 00:00:00.000', false)
     , (5, 4039, '2020-06-18 00:00:00.000', false)
     , (5, 1220, '2020-06-19 00:00:00.000', false)
     , (5, 343, '2020-06-19 00:00:00.000', false)
     , (5, 598, '2020-08-29 00:00:00.000', false)
     , (5, 719, '2020-08-30 00:00:00.000', false)
     , (5, 145, '2020-08-30 00:00:00.000', false)
     , (5, 1303, '2020-08-30 00:00:00.000', false)
;

INSERT INTO public.medicine_in_order (medicine, dosage, quantity, price, fk_order, indivisible_amount, recipe_required,
                                      exp_date)
VALUES ('Лордес', '5 мг', 1, 0, 1, 10, false, '2020-03-08 00:00:00.000')
     , ('Синупрет', '-', 2, 0, 3, 10, true, '2021-12-17 00:00:00.000')
     , ('Синупрет', '-', 1, 300, 4, 10, true, '2021-12-17 00:00:00.000')
     , ('Парацетамол', '30 мг/мл', 1, 248, 5, 10, false, '2020-05-01 00:00:00.000')
     , ('Гроприносин', '500 мг', 1, 460, 6, 10, true, '2022-06-01')
     , ('Синупрет', '-', 8, 0, 1, 10, true, '2021-12-17 00:00:00.000')
     , ('АмброГексал', '7,5 мг/мл', 5, 610, 2, 1, true, '2022-05-01 00:00:00.000')
     , ('АмброГексал', '7,5 мг/мл', 2, 610, 36, 1, true, '2022-05-01 00:00:00.000')
     , ('Кагоцел', '12 мг', 1, 0, 1, 10, false, '2020-06-01 00:00:00.000')
     , ('АмброГексал', '7,5 мг/мл', 1, 0, 1, 1, true, '2022-05-01 00:00:00.000')
     , ('Ибуфен', '200 мг', 1, 500, 40, 10, false, '2021-11-01 00:00:00.000')
     , ('Бинт марлевый стерильный', '-', 1, 98, 40, 1, false, '2021-11-01 00:00:00.000')
     , ('Ромашки цветки измельченные', '-', 1, 271, 41, 1, false, '2023-04-01 00:00:00.000')
     , ('Бинт марлевый стерильный', '-', 3, 98, 41, 1, false, '2021-11-01 00:00:00.000')
     , ('Парацетамол', '500 мг', 2, 77, 41, 10, false, '2023-02-01 00:00:00.000')
     , ('Уголь биоактивированный', '250 мг', 1, 36, 43, 10, false, '2022-01-01 00:00:00.000')
     , ('Фолиевая кислота', '1 мг', 1, 11, 43, 10, false, '2022-10-12 00:00:00.000')
     , ('Бинт марлевый стерильный', '-', 1, 98, 43, 1, false, '2021-11-01 00:00:00.000')
     , ('Виброцил', '-', 1, 1303, 44, 1, true, '2022-03-01 00:00:00.000')
;

INSERT INTO public.active_med_in_cart (id_medicine, id_medicine_in_order)
VALUES (2, 108)
     , (5, 65)
     , (5, 112)
;