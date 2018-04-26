CREATE TABLE book(
    id INT NOT NULL PRIMARY KEY,
    title VARCHAR(100),
    category VARCHAR(50),
    publication_date DATE
);
INSERT INTO book VALUES(1, 'Effective java', 'java', '2009-01-01');
INSERT INTO book VALUES(2, 'Thinking in java', 'java', '2006-02-20');