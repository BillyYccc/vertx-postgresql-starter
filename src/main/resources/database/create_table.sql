DROP TABLE IF EXISTS book;
CREATE TABLE book(
    id SERIAL4 NOT NULL PRIMARY KEY,
    title VARCHAR(100),
    category VARCHAR(50),
    publication_date DATE
);