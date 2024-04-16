CREATE TABLE cust_db (
	c_key decimal(11,0) NOT NULL,
	c_status char(1),
	c_date date,
	PRIMARY KEY (c_key) CONSTRAINT cust_db_pk
);

INSERT INTO cust_db (c_key, c_status, c_date) VALUES(1111, 'Z','2022-04-18');
INSERT INTO cust_db (c_key, c_status, c_date) VALUES(2222, 'Z','2021-04-18');
INSERT INTO cust_db (c_key, c_status, c_date) VALUES(3333, 'Z','2020-04-18');
INSERT INTO cust_db (c_key, c_status, c_date) VALUES(4444, 'Z','2019-04-18');
INSERT INTO cust_db (c_key, c_status, c_date) VALUES(5555, 'Z','2018-04-18');
INSERT INTO cust_db (c_key, c_status, c_date) VALUES(6666, 'Z','2017-04-18');