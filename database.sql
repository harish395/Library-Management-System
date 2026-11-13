-- Run this script in your Oracle user (example: pavi)

CREATE TABLE users (
    user_id NUMBER PRIMARY KEY,
    fullname VARCHAR2(100),
    username VARCHAR2(50) UNIQUE NOT NULL,
    email VARCHAR2(100),
    password VARCHAR2(100) NOT NULL,
    age NUMBER,
    gender VARCHAR2(20),
    department VARCHAR2(50),
    interests VARCHAR2(300),
    address VARCHAR2(500),
    profile_photo VARCHAR2(200),
    role VARCHAR2(20) DEFAULT 'USER' NOT NULL
);

CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE OR REPLACE TRIGGER users_trigger
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF :NEW.user_id IS NULL THEN
        :NEW.user_id := users_seq.NEXTVAL;
    END IF;
END;
/

CREATE TABLE books (
    book_id NUMBER PRIMARY KEY,
    book_name VARCHAR2(150) NOT NULL,
    author VARCHAR2(100),
    category VARCHAR2(80),
    publisher VARCHAR2(100),
    isbn VARCHAR2(50),
    quantity NUMBER DEFAULT 1,
    available_quantity NUMBER DEFAULT 1,
    price NUMBER(10,2)
);

CREATE SEQUENCE books_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE OR REPLACE TRIGGER books_trigger
BEFORE INSERT ON books
FOR EACH ROW
BEGIN
    IF :NEW.book_id IS NULL THEN
        :NEW.book_id := books_seq.NEXTVAL;
    END IF;
END;
/

CREATE TABLE issued_books (
    issue_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    book_id NUMBER NOT NULL,
    issue_date DATE DEFAULT SYSDATE,
    due_date DATE,
    return_date DATE,
    fine NUMBER(10,2) DEFAULT 0,
    status VARCHAR2(20) DEFAULT 'ISSUED',
    CONSTRAINT fk_issue_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_issue_book FOREIGN KEY (book_id) REFERENCES books(book_id)
);

CREATE SEQUENCE issued_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE OR REPLACE TRIGGER issued_trigger
BEFORE INSERT ON issued_books
FOR EACH ROW
BEGIN
    IF :NEW.issue_id IS NULL THEN
        :NEW.issue_id := issued_seq.NEXTVAL;
    END IF;
END;
/

CREATE TABLE activities (
    activity_id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    activity VARCHAR2(300),
    activity_date DATE DEFAULT SYSDATE,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE SEQUENCE activities_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE OR REPLACE TRIGGER activities_trigger
BEFORE INSERT ON activities
FOR EACH ROW
BEGIN
    IF :NEW.activity_id IS NULL THEN
        :NEW.activity_id := activities_seq.NEXTVAL;
    END IF;
END;
/

-- Admin login:
-- username: admin
-- password: admin123
INSERT INTO users
(fullname, username, email, password, age, gender, department, interests, address, role)
VALUES
('Library Admin', 'admin', 'admin@library.com', 'admin123', 30, 'Other',
 'ADMIN', 'Management', 'Library Office', 'ADMIN');

COMMIT;
