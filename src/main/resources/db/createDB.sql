create database pharmacy;


CREATE TABLE public.account (
	id int NOT NULL,
	surname varchar NOT NULL,
	name varchar NOT NULL,
	patronymic varchar NULL,
	phone_number varchar NULL,
	status boolean NULL DEFAULT true, -- added default
	balance int NULL DEFAULT 0,
	fk_role int NOT NULL,
	CONSTRAINT account_pk PRIMARY KEY (id)
);

-- Column comments
COMMENT ON COLUMN public.account.status IS 'true - active, false - blocked';

CREATE TABLE public.login (
	id int NOT NULL GENERATED ALWAYS AS IDENTITY,
	login varchar NOT NULL,
	"password" varchar NOT NULL,
	CONSTRAINT login_pk PRIMARY KEY (login),
	CONSTRAINT login_fk FOREIGN KEY (id) REFERENCES public.account(id) ON DELETE CASCADE
);
ALTER TABLE public.account ADD CONSTRAINT account_fk FOREIGN KEY (login_id) REFERENCES login(id) ON UPDATE CASCADE ON DELETE cascade DEFERRABLE INITIALLY deferred;
ALTER TABLE public.login ADD CONSTRAINT login_fk
    FOREIGN KEY (id) REFERENCES public.account(id)
        ON UPDATE CASCADE ON DELETE cascade DEFERRABLE INITIALLY deferred;
CREATE TABLE public."role" (
	id int NOT NULL GENERATED ALWAYS AS IDENTITY,
	role_name varchar NOT NULL,
	CONSTRAINT role_pk PRIMARY KEY (id)
);
COMMENT ON TABLE public."role" IS 'limited number of roles. On the program field is Enum.';

CREATE TABLE public.medicine (
	id int NOT NULL GENERATED ALWAYS AS IDENTITY,
	"name" varchar NOT NULL,
	indivisible_amount int NOT NULL,
	amount int NOT NULL,
	dosage varchar NULL,
	pharm_form varchar NOT NULL,
	exp_date date NOT NULL,
	recipe_required boolean NOT NULL DEFAULT false,
	price int NOT NULL,
	added_by varchar NOT NULL,
	CONSTRAINT medicine_pk PRIMARY KEY ("name",dosage,exp_date,price)
);
-- Column comments
COMMENT ON COLUMN public.medicine.indivisible_amount IS 'Indivisible amount of medicine for sell';
COMMENT ON COLUMN public.medicine.exp_date IS 'Medicine expiration date';

CREATE TABLE public."order" (
	id int NOT NULL GENERATED ALWAYS AS IDENTITY,
	fk_customer int NOT NULL,
	payment_state boolean NOT NULL DEFAULT false,
	order_price int NOT NULL DEFAULT 0,
	order_date timestamp NOT NULL,
	status boolean NOT NULL DEFAULT false,
	CONSTRAINT order_pk PRIMARY KEY (id)
);
-- Column comments
COMMENT ON COLUMN public."order".cart IS 'false - order isn''t completed/delivered';

CREATE TABLE public.recipe (
    id int NOT NULL GENERATED ALWAYS AS IDENTITY,
	medicine varchar NOT NULL,
	fk_doctor int NOT NULL,
	fk_customer int NOT NULL,
	id_medicine_in_order int NULL,
	validity date NOT NULL,
	need_extension boolean NOT NULL DEFAULT false,
	CONSTRAINT recipe_pk PRIMARY KEY (medicine,fk_doctor,fk_customer)
);
-- Column comments
COMMENT ON COLUMN public.recipe.need_extension IS 'true - recipe needs extension';

CREATE TABLE public.medicine_in_order (
	id int NOT NULL GENERATED ALWAYS AS IDENTITY,
	medicine varchar NOT NULL,
	dosage varchar NOT NULL,
	quantity int NOT NULL,
	price int NOT NULL,
	fk_order int NOT NULL,
	CONSTRAINT medicine_in_order_pk PRIMARY KEY (medicine,fk_order)
);

ALTER TABLE public.account ADD CONSTRAINT account_fk FOREIGN KEY (fk_role) REFERENCES public."role"(id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE public.recipe ADD CONSTRAINT recipe_fk FOREIGN KEY (fk_customer) REFERENCES public.account(id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE public."order" ADD CONSTRAINT order_fk FOREIGN KEY (fk_customer) REFERENCES public.account(id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE public.medicine_in_order ADD CONSTRAINT medicine_in_order_fk FOREIGN KEY (fk_order) REFERENCES public."order"(id) ON DELETE CASCADE ON UPDATE CASCADE;






