CREATE TABLE outbox_table (
	id serial PRIMARY KEY,
	aggregate VARCHAR ( 255 ),
	operation VARCHAR ( 255 ),
	payload VARCHAR,
	event_time VARCHAR ( 255 )
);

insert into outbox_table (id, aggregate, operation, payload, event_time) values (1, 'Consumer Loan', 'CREATE', '{\"event\": {\"type\":\"Mortgage Opening\",\"timestamp\":\"2023-11-20T10:00:00\",\"data\":{\"mortgageId\":\"ABC123\",\"customer\":\"John Doe\",\"amount\":200000,\"duration\": 20}}}','2023-11-20 10:00:00');
insert into outbox_table (id, aggregate, operation, payload, event_time) values (2, 'Consumer Loan', 'INSTALLMENT_PAYMENT', '{\"event\": {\"type\":\"Mortgage Opening\",\"timestamp\":\"2023-11-20T10:00:00\",\"data\":{\"mortgageId\":\"ABC123\",\"customer\":\"John Doe\",\"amount\":200000,\"duration\": 20}}}','2023-12-01 09:30:00');