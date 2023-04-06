CREATE TABLE device (
	id serial PRIMARY KEY,
	fullname VARCHAR ( 255 ) UNIQUE NOT NULL
);


insert into device (id, fullname) values (1, 'foo11111');
insert into device (id, fullname) values (2, 'foo22222');