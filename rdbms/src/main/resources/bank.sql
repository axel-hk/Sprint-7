
create table bank
(
    id bigserial constraint bank_pk primary key,
    amount int constraint positive_amount check (amount >= 0),
    version int
);

insert into bank (id, amount, version) values
(1, 50000, 0),
(2, 50000, 0),
(3, 500, 0),
(4, 10000, 0);