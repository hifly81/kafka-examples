CREATE TABLE maintenance (
	id serial PRIMARY KEY,
	maintenance VARCHAR ( 255 ) UNIQUE NOT NULL
);

CREATE TABLE device (
	id serial PRIMARY KEY,
	fullname VARCHAR ( 255 ) UNIQUE NOT NULL
);



insert into maintenance (id, maintenance) values (1, '2023-03-01 15:00:00 16:00:00');

insert into device (id, fullname) values (1, 'foo11111');
insert into device (id, fullname) values (2, 'foo22222');
insert into device (id, fullname) values (10, 'foo1010101010');
insert into device (id, fullname) values (15, 'foo1515151515');