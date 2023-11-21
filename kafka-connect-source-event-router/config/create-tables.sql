CREATE TABLE outbox_table (
	id serial PRIMARY KEY,
	aggregate VARCHAR ( 255 ) NOT NULL,
	operation VARCHAR ( 255 ) NOT NULL,
	payload VARCHAR NOT NULL,
	event_time VARCHAR ( 255 ) NOT NULL
);

insert into outbox_table (id, aggregate, operation, payload, event_time) values (1, 'Consumer Loan', 'CREATE', '{\"event\": {\"type\":\"Mortgage Opening\",\"timestamp\":\"2023-11-20T10:00:00\",\"data\":{\"mortgageId\":\"ABC123\",\"customer\":\"John Doe\",\"amount\":200000,\"duration\": 20}}}','2023-11-20 10:00:00');
insert into outbox_table (id, aggregate, operation, payload, event_time) values (2, 'Consumer Loan', 'INSTALLMENT_PAYMENT', '{\"event\": {\"type\":\"Mortgage Opening\",\"timestamp\":\"2023-11-20T10:00:00\",\"data\":{\"mortgageId\":\"ABC123\",\"customer\":\"John Doe\",\"amount\":200000,\"duration\": 20}}}','2023-12-01 09:30:00');
insert into outbox_table (id, aggregate, operation, payload, event_time) values (3, 'Consumer Loan', 'EARLY_LOAN_CLOSURE', '{\"event\":{\"type\":\"Early Loan Closure\",\"timestamp\":\"2023-11-25T14:15:00\",\"data\":{\"mortgageId\":\"ABC12\",\"closureAmount\":150000,\"closureDate\":\"2023-11-25\",\"paymentMethod\":\"Bank Transfer\",\"transactionNumber\":\"PQR456\"}}}','2023-11-25 09:30:00');