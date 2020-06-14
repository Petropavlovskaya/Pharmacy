-- Auto-generated SQL script #202004021911
INSERT INTO role (role_name) values ('Customer'), ('Doctor'), ('Pharmacist'), ('Administrator');

INSERT INTO public.login (login, password) VALUES
('kuzya', 'kuzma'), ('Ivanych','Ivanych44v5'),
('Doc','Petrova3355'), ('Petrova','Petrova3355'),
('Zoba','eugen'), ('Olenka','OlkaOlka');

INSERT INTO public.account ( id, surname, name, patronymic, phone, status, balance, fk_role) VALUES
(1, 'Kuzin', 'Dmitry','Gennadyevich','+375(29)5556433', true, NULL, 3),
(2, 'Ivanov', 'Andrey','Viktorovich','+375(29)5554433', true, 5000, 1),
(3, 'Pertova', 'Anna','Ivanovna','+375(29)5555433', true, NULL, 2),
(4, 'Pertova', 'Anna','Ivanovna','+375(29)5555433', true, 3600, 1),
(5, 'Zobnin', 'Eugeny','Yuryevich','+375(33)3336687', true, 540, 1),
(6, 'Matuz', 'Olga','Olegovna','+375(44)4789966', true, 2000, 1);

INSERT INTO public.medicine (name, indivisible_amount, amount, dosage, exp_date, recipe_required, price, added_by, pharm_form ) VALUES
('Лордес', 10, 60,'5 мг', '2021-10-01', false, 899, 1, 'таблетки'),
('Кагоцел', 10, 80,'12 мг', '2020-06-01', false, 827, 1, 'таблетки'),
('Гроприносин', 10, 110,'500 мг', '2022-06-01', true, 460, 1, 'таблетки'),
('Синупрет', 10, 20,'-', '2022-03-01', false, 300, 1, 'таблетки'),
('АмброГексал', 1, 7,'7,5 мг/мл', '2022-05-01', true, 610, 1, 'сироп'),
('Парацетамол', 1, 3,'30 мг/мл', '2020-05-01', false, 248, 1, 'сироп');

INSERT INTO public.order (fk_customer, payment_state, order_price, order_date, cart) VALUES
(5, true, 1607, '2020-02-22', true),
(6, true, 2264, '2020-03-18', true),
(4, true, 600, '2020-04-02', false),
(2, false, 300, '2020-04-02', false),
(6,false,248,'2020-04-02',false);

INSERT INTO public.recipe (medicine ,fk_doctor ,fk_customer ,id_medicine_in_order ,validity ,need_extension) VALUES
('Гроприносин', 3, 2, NULL, '2020-10-20', false),
('АмброГексал', 3, 2, NULL, '2020-02-14', true),
('Гроприносин', 3, 5, 1, '2020-04-20', true),
('АмброГексал', 3, 5, NULL, '2020-11-14', false),
('АмброГексал', 3, 5, 2, '2020-06-03', false);

INSERT INTO public.medicine_in_order (fk_order, medicine, dosage, quantity, price) VALUES
(1, 'Гроприносин', '500 мг', 1, 460),
(1, 'Лордес', '5 мг', 1, 899),
(1, 'Парацетамол', '30 мг/мл', 1, 248),
(2, 'АмброГексал', '7,5 мг/мл', 1, 610),
(2, 'Кагоцел', '12 мг', 2, 827),
(3, 'Синупрет', '-', 2, 300),
(4, 'Синупрет', '-', 1, 300),
(5, 'Парацетамол','30 мг/мл',1,248);





