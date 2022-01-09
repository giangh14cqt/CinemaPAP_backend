
--
create table users
(
    user_id NUMBER GENERATED ALWAYS AS IDENTITY,
    firstName VARCHAR2 (40) NOT NULL ,
    lastName VARCHAR2 (40) NOT NULL,
    login VARCHAR2 (40) NOT NULL unique,
    password VARCHAR2 (40) NOT NULL,
    available number default 1
);
ALTER TABLE users ADD (
  CONSTRAINT users_pk PRIMARY KEY (user_id));

create table Cinemas
(
    cinema_ID NUMBER GENERATED ALWAYS AS IDENTITY,
    manager_ID number,
    name varchar2(100) not null,
    website varchar2(60) ,
    phoneNumber varchar2(20) ,
    email varchar2(30) ,
    address varchar2(100),
    available number default 1
);
ALTER TABLE Cinemas ADD (
  CONSTRAINT Cinemas_pk PRIMARY KEY (cinema_ID));

create table rooms
(
    room_id NUMBER GENERATED ALWAYS AS IDENTITY,
    name varchar2(40) not null,
    rowsNumber number(4),
    seatsInRowNumber number(4),
    cinema_ID NUMBER NOT NULL CONSTRAINT cin_room_fk REFERENCES cinemas (cinema_ID),
    available number default 1
);
ALTER TABLE rooms ADD (
  CONSTRAINT rooms_pk PRIMARY KEY (room_id));

create table seats
(
    seat_id NUMBER GENERATED ALWAYS AS IDENTITY,
    room_ID NUMBER NOT NULL CONSTRAINT rm_seat_fk REFERENCES rooms (room_ID),
    positionX number(4) NOT NULL,
    positionY number(4) NOT NULL,
    type VARCHAR2 (40) NOT NULL,
    available number default 1
);
ALTER TABLE seats ADD (
  CONSTRAINT seats_pk PRIMARY KEY (seat_id));

create table MovieCategories
(
    MovieCategory_id NUMBER GENERATED ALWAYS AS IDENTITY,
    name varchar2(100) not null,
    description varchar2(100),
    available number default 1
);
ALTER TABLE MovieCategories ADD (
  CONSTRAINT MovieCategories_pk PRIMARY KEY (MovieCategory_id));

create table movies
(
    movie_ID  NUMBER GENERATED ALWAYS AS IDENTITY,
    length  NUMBER (4) not null,
    ageRestriction varchar2(100),
    cinema_ID number CONSTRAINT cin_mov_fk REFERENCES cinemas (cinema_ID),
    name varchar2(30) not null,
    description varchar2(100),
    movieCate_id number constraint mov_cate_fk REFERENCES MovieCategories (MovieCategory_id),
    available number default 1
);
ALTER TABLE movies ADD (
  CONSTRAINT movies_pk PRIMARY KEY (movie_ID));

create table Schedules
(
    Schedule_id NUMBER GENERATED ALWAYS AS IDENTITY,
    datetime number not null,
    movie_ID NUMBER NOT NULL CONSTRAINT mov_sched_fk REFERENCES movies (movie_ID),
    room_ID NUMBER NOT NULL CONSTRAINT room_sched_fk REFERENCES rooms (room_ID),
    openSale number not null,
    closeSale number not null,
    seatLeft number(4) not null,
    available number default 1
);
ALTER TABLE Schedules ADD (
  CONSTRAINT Schedules_pk PRIMARY KEY (Schedule_id));

create table Purchases
(
    Purchase_id NUMBER GENERATED ALWAYS AS IDENTITY,
    datetime NUMBER not null,
    amount float(2) not null,
    paymentMethod varchar2(40),
    currency VARCHAR2(10),
    schedule_id NUMBER NOT NULL CONSTRAINT sched_purch_fk REFERENCES Schedules (Schedule_id),
    available number default 1
);
ALTER TABLE Purchases ADD (
  CONSTRAINT Purchases_pk PRIMARY KEY (Purchase_id));

create table TicketTypes
(
    TicketType_id NUMBER GENERATED ALWAYS AS IDENTITY,
    name VARCHAR2 (40) NOT NULL,
    price float(2) not null,
    cinema_ID NUMBER NOT NULL CONSTRAINT cin_tick_type_fk REFERENCES cinemas (cinema_ID),
    available number default 1
);
ALTER TABLE TicketTypes ADD (
  CONSTRAINT TicketTypes_pk PRIMARY KEY (TicketType_id));

create table Tickets
(
    Ticket_id NUMBER GENERATED ALWAYS AS IDENTITY,
    Purchase_id NUMBER NOT NULL CONSTRAINT purch_tick_fk REFERENCES Purchases (Purchase_id),
    seat_id NUMBER NOT NULL CONSTRAINT seat_tick_fk REFERENCES seats (seat_id),
    TicketType_id NUMBER NOT NULL CONSTRAINT type_tick_fk REFERENCES TicketTypes (TicketType_id),
    schedule_id NUMBER NOT NULL CONSTRAINT sched_tick_fk REFERENCES Schedules (Schedule_id),
    available number default 1
);
ALTER TABLE Tickets ADD (
  CONSTRAINT Tickets_pk PRIMARY KEY (Ticket_id));


CREATE OR REPLACE TRIGGER tg_add_seats
AFTER INSERT on rooms FOR EACH ROW WHEN (new.rowsNumber is not null and new.seatsInRowNumber is not null)
DECLARE
   curRow number;
   curCol number := 0;
   typeSeat varchar2(40);
   rmid number;
   rowsNum number;
BEGIN
    for curRow in 1 .. :new.rowsNumber
    loop
        for curCol in 1 .. :new.seatsInRowNumber
        loop
            insert into seats values (default, :new.room_id, curCol, curRow, 'comfort', default);
            dbms_output.put_line(curRow || '  ' || curCol);
        end loop;
    end loop;
--    if 
    rmid := :new.room_id;
    rowsNum := :new.rowsNumber;
    update seats
    set type = 'luxury' where room_id = rmid and positionY > 4 / 5 * rowsNum;
END;
/


CREATE OR REPLACE TRIGGER tg_delete_seats
BEFORE DELETE on rooms FOR EACH ROW WHEN (new.rowsNumber is not null and new.seatsInRowNumber is not null)
DECLARE

BEGIN
    NULL;
END;
/


CREATE OR REPLACE TRIGGER tg_update_seats
AFTER UPDATE on rooms FOR EACH ROW WHEN (new.rowsNumber is not null and new.seatsInRowNumber is not null)
DECLARE

BEGIN
    NULL;
END;
/


--Function to check if we can add a schedule without conflict. 1: if we can, 0: if we can't
create or replace function can_create_schedule(mv_id number, rm_id number, date_start number, open_sale number, close_sale number)
return number
as
    date_end    number;
    tmp_start   number;
    tmp_end     number;
begin
    select length*60*1000 + date_start into date_end
    from movies
    where mv_id = movie_id;
    for schedule in (select * from schedules where rm_id = room_id and available = 1)
    loop
        select schedule.datetime into tmp_start from dual;
        select schedule.datetime + m.length*60*1000 into tmp_end
        from movies m where m.movie_id = schedule.movie_id;
        if (date_start between tmp_start and tmp_end) or (date_end between tmp_start and tmp_end) then
            return 0;
        end if;
    end loop;
    return 1;
end;
/


--describe users;
--describe cinemas;
--describe movies;
--describe rooms;
--describe seats;
--describe MovieCategories;
--describe Schedules;
--describe Tickets;
--describe Purchases;
--describe TicketTypes;
----
--drop table cinemas CASCADE CONSTRAINTS;
--drop table movies CASCADE CONSTRAINTS;
--drop table rooms CASCADE CONSTRAINTS;
--drop table users CASCADE CONSTRAINTS;
--drop table seats CASCADE CONSTRAINTS;
--drop table MovieCategories;
--drop table Schedules CASCADE CONSTRAINTS;
--drop table Tickets;
--drop table Purchases CASCADE CONSTRAINTS;
--drop table TicketTypes;
--
--select * from users;
--select * from cinemas;
--select * from movies;
--select * from rooms;
--select * from seats;
--select * from MovieCategories;
--select * from Schedules;
--select * from Tickets;
--select * from Purchases;
--select * from TicketTypes;
--