DROP TABLE people IF EXISTS;
CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);

DROP TABLE account IF EXISTS;
CREATE TABLE account (
  email     VARCHAR(255) PRIMARY KEY,
  password  VARCHAR(255),
  name      VARCHAR(128),
  birth_day DATE,
  zip       VARCHAR(32),
  address   VARCHAR(255)
);


DROP SEQUENCE PEOPLE_SEQ IF EXISTS;
CREATE SEQUENCE PEOPLE_SEQ;

INSERT INTO account (email, password, name, birth_day, zip, address) VALUES ('aaa1@test.com', '****', 'taro yamamoto', parsedatetime('1980/01/01','yyyy/MM/dd') , '123-0001', '東京都');
INSERT INTO account (email, password, name, birth_day, zip, address) VALUES ('aaa2@test.com', '****', 'ken nakamuira', parsedatetime('1980/01/02','yyyy/MM/dd'), '123-0002', '千葉県');
INSERT INTO account (email, password, name, birth_day, zip, address) VALUES ('aaa3@test.com', '****', 'satoshi okamura', parsedatetime('1980/01/03','yyyy/MM/dd'), '123-0003', '埼玉県');
INSERT INTO account (email, password, name, birth_day, zip, address) VALUES ('aaa4@test.com', '****', 'yuji osawa', parsedatetime('1980/01/04','yyyy/MM/dd'), '123-0004', '神奈川県');
