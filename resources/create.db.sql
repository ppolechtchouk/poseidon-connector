DROP DATABASE IF EXISTS poseidondb;
DROP ROLE IF EXISTS poseidon;


CREATE ROLE poseidon LOGIN PASSWORD 'poseidon';
CREATE DATABASE poseidondb OWNER poseidon;

\connect poseidondb poseidon

CREATE TRUSTED PROCEDURAL LANGUAGE 'plpgsql'
  HANDLER plpgsql_call_handler
  VALIDATOR plpgsql_validator;

CREATE SCHEMA poseidon AUTHORIZATION poseidon;

CREATE TABLE poseidon.frames (
       id_frame SERIAL PRIMARY KEY,

       field1 VARCHAR(8), -- TID
       field2 VARCHAR(8), -- invoice code
       field3 VARCHAR(6), -- trace nr
       field4 VARCHAR(10), -- onum
       field5 CHAR(6), -- date POS YYMMDD
       field6 CHAR(6), -- time POS HHMMSS
       field7 CHAR(2), -- txn code
       field8 VARCHAR(4), -- receipt number
       field9 VARCHAR(6), -- txn trace number for cancellation

       field10 CHAR(6), -- original date POS YYMMDD
       field11 CHAR(6), -- original time POS HHMMSS
       field12 CHAR(6), -- original date POSEIDON YYMMDD
       field13 CHAR(9), -- original time POSEIDON HHMMSSTTT
       field14 VARCHAR(12), -- amount in smalles unit - i.e. cents for EUR. Negative amounts have - on the left
       field15 VARCHAR(3), -- currency code or 0
       field16 VARCHAR(3), -- kind of card
       field17 VARCHAR(2), -- card type
       field18 VARCHAR(2), -- authentication type
       field19 VARCHAR(2), -- transaction type

       field20 CHAR(2), -- transaction sub-type 1
       field21 CHAR(2), -- transaction sub-type 2
       field22 VARCHAR(19), -- PAN 2
       field23 VARCHAR(8), -- Banking No
       field24 VARCHAR(10), -- Account No
       field25 VARCHAR(4),  -- card sequence No or 0 
       field26 VARCHAR(4), -- card expiry date YYMM or 0 if none
       field27 VARCHAR(15), -- merchant No
       field28 CHAR(1), -- capture mode
       field29 VARCHAR(3), -- POS mode


       field30 VARCHAR(2), -- transaction result
       field31 VARCHAR(4), -- transaction sub-result
       field32 CHAR(1), -- data telecom
       field33 VARCHAR(6), -- Authorization host
       field34 VARCHAR(6), -- routing KA
       field35 VARCHAR(5), -- routing AS
       field36 VARCHAR(10), -- AID parameter
       field37 VARCHAR(8), -- AID
       field38 VARCHAR(2), -- netlines
       field39 VARCHAR(5), -- PID - process id

       field40 VARCHAR(15), -- telephone No or IP
       field41 VARCHAR(6), -- card ID (type)
       field42 VARCHAR, -- frame padding?

       is_synced BOOLEAN DEFAULT FALSE -- true if the transaction has been synced with the e-Portal DB
)

