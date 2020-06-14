create database pharmacy;


CREATE TABLE public.account
(
    id         int4    NOT NULL,
    surname    varchar NOT NULL,
    "name"     varchar NOT NULL,
    patronymic varchar NULL,
    phone      varchar NULL,
    status     bool    NULL,
    balance    int4    NULL     DEFAULT 0,
    fk_role    int4    NOT NULL,
    credit     int4    NOT NULL DEFAULT 0,
    CONSTRAINT account_pk PRIMARY KEY (id),
    CONSTRAINT account_fk FOREIGN KEY (id) REFERENCES login (id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT account_fk_1 FOREIGN KEY (fk_role) REFERENCES role (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE public.login
(
    id         int4    NOT NULL GENERATED ALWAYS AS IDENTITY,
    login      varchar NOT NULL,
    "password" varchar NOT NULL,
    salt       varchar NOT NULL,
    CONSTRAINT login_pk PRIMARY KEY (id),
    CONSTRAINT login_un UNIQUE (login)
);


CREATE TABLE public."role"
(
    id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
    role_name varchar NOT NULL,
    CONSTRAINT role_pk PRIMARY KEY (id),
    CONSTRAINT role_un UNIQUE (role_name)
);

CREATE TABLE public.medicine
(
    id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
    "name" varchar NOT NULL,
    indivisible_amount int4 NOT NULL, -- Indivisible amount of medicine for sell
    amount int4 NOT NULL,
    dosage varchar NOT NULL,
    exp_date date NOT NULL, -- Medicine expiration date
    recipe_required bool NOT NULL DEFAULT false,
    price int4 NOT NULL,
    added_by int4 NOT NULL,
    pharm_form varchar NOT NULL,
    CONSTRAINT medicine_pk PRIMARY KEY (id),
    CONSTRAINT medicine_un UNIQUE (name, dosage, exp_date, price)
);

CREATE TABLE public."order"
(
    order_id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
    fk_customer int4 NOT NULL,
    payment_state bool NOT NULL DEFAULT false,
    order_price int4 NOT NULL DEFAULT 0,
    order_date timestamp NULL,
    cart bool NOT NULL DEFAULT false,
    CONSTRAINT order_pk PRIMARY KEY (order_id),
    CONSTRAINT order_fk FOREIGN KEY (fk_customer) REFERENCES account(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE public.recipe
(
    recipe_id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
    medicine varchar NOT NULL,
    doctor_id int4 NULL,
    fk_customer int4 NOT NULL,
    id_medicine_in_order int4 NULL,
    validity date NULL,
    need_extension bool NOT NULL DEFAULT false,
    dosage varchar NULL,
    CONSTRAINT recipe_pk PRIMARY KEY (recipe_id),
    CONSTRAINT recipe_un UNIQUE (medicine, doctor_id, fk_customer, id_medicine_in_order, validity, dosage),
    CONSTRAINT recipe_fk FOREIGN KEY (fk_customer) REFERENCES account(id) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE public.medicine_in_order
(
    id int4 NOT NULL GENERATED ALWAYS AS IDENTITY,
    medicine varchar NOT NULL,
    dosage varchar NOT NULL,
    quantity int4 NOT NULL,
    price int4 NOT NULL,
    fk_order int4 NOT NULL,
    indivisible_amount int4 NOT NULL,
    recipe_required bool NOT NULL DEFAULT false,
    CONSTRAINT medicine_in_order_pk PRIMARY KEY (id),
    CONSTRAINT medicine_in_order_fk FOREIGN KEY (fk_order) REFERENCES "order"(order_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE public.active_med_in_cart (
    id_medicine int4 NOT NULL,
    id_medicine_in_order int4 NOT NULL,
    CONSTRAINT cart_un UNIQUE (id_medicine, id_medicine_in_order),
    CONSTRAINT active_med_in_cart_fk FOREIGN KEY (id_medicine_in_order) REFERENCES medicine_in_order(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT cart_fk FOREIGN KEY (id_medicine) REFERENCES medicine(id) ON UPDATE CASCADE ON DELETE CASCADE
);
COMMENT ON TABLE public.active_med_in_cart IS 'Table for check available medicine in cart';

CREATE TABLE public.favorite (
    customerid int4 NOT NULL,
    medicineid int4 NOT NULL,
    CONSTRAINT favorite_un UNIQUE (customerid, medicineid),
    CONSTRAINT favorite_fk FOREIGN KEY (customerid) REFERENCES account(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT favorite_fk_1 FOREIGN KEY (medicineid) REFERENCES medicine(id) ON UPDATE CASCADE ON DELETE CASCADE
);

