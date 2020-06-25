CREATE TABLE countries
(
    country_code VARCHAR(20) PRIMARY KEY,
    country_name VARCHAR(128) NOT NULL
);

CREATE TABLE shops
(
    id INT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    country VARCHAR(20) REFERENCES countries (country_code)
);

CREATE TABLE item_categories
(
    id INT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
);

CREATE TABLE items
(
    id INT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    category INT REFERENCES item_categories (id)
);

INSERT INTO countries (country_code, country_name)
VALUES ('GS', 'South Georgia and the South Sandwich Islands'),
       ('AE', 'United Arab Emirates'),
       ('AF', 'Afghanistan'),
       ('CA', 'Canada'),
       ('CC', 'Cocos (Keeling) Islands'),
       ('DE', 'Germany'),
       ('DK', 'Denmark'),
       ('ES', 'Spain'),
       ('GB', 'United Kingdom'),
       ('HU', 'Hungary'),
       ('RU', 'Russian Federation'),
       ('SG', 'Singapore'),
       ('US', 'United States'),
       ('UY', 'Uruguay'),
       ('UZ', 'Uzbekistan');

INSERT INTO shops (id, name, country)
VALUES (1, 'Alpha Market', 'RU'),
       (2, 'Beta store', 'US'),
       (3, 'Theta mart', 'US'),
       (4, 'Gamma groceries', 'CA'),
       (5, 'Kappa equipment', 'DE'),
       (6, 'Delta Market', 'GB'),
       (7, 'Zeta Market', 'ES');

INSERT INTO item_categories (id, name)
VALUES (1, 'Food'),
       (2, 'Sports equipment'),
       (3, 'Furniture'),
       (4, 'Stationery');

INSERT INTO items (id, name, category)
VALUES (1, 'Apple', 1),
       (2, 'Table', 3),
       (3, 'Pen', 4),
       (4, 'Beef', 1),
       (5, 'Chair', 3),
       (6, 'Pencil', 4),
       (7, 'Surfboard', 2);