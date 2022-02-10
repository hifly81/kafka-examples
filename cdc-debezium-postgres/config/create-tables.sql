CREATE TABLE accounts (
	user_id serial PRIMARY KEY,
	username VARCHAR ( 50 ) UNIQUE NOT NULL,
	password VARCHAR ( 50 ) NOT NULL,
	email VARCHAR ( 255 ) UNIQUE NOT NULL,
	created_on TIMESTAMP NOT NULL,
    last_login TIMESTAMP
);


insert into accounts (user_id, username, password, email, created_on, last_login) values (1, 'foo', 'bar', 'foo@bar.com', current_timestamp, current_timestamp);
insert into accounts (user_id, username, password, email, created_on, last_login) values (2, 'foo2', 'bar2', 'foo2@bar.com', current_timestamp, current_timestamp);