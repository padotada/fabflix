DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE IF NOT EXISTS moviedb;
CREATE TABLE movies (
    id varchar(10) PRIMARY KEY,
    title varchar(100) NOT NULL,
    year integer NOT NULL,
    director varchar(100) NOT NULL
);

CREATE TABLE stars (
    id varchar(10) PRIMARY KEY,
    `name` varchar(100) NOT NULL,
    birth_year integer
);

CREATE TABLE stars_in_movies (
    star_id varchar (10) NOT NULL,
    movie_id varchar (10) NOT NULL,
    FOREIGN KEY(star_id) REFERENCES stars(id),
    FOREIGN KEY(movie_id) REFERENCES movies(id)
);

CREATE TABLE genres (
    id integer AUTO_INCREMENT PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE genres_in_movies (
    genre_id integer NOT NULL,
    movie_id varchar(10) NOT NULL,
    FOREIGN KEY (genre_id) REFERENCES genres(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

CREATE TABLE credit_cards (
    id varchar(20) PRIMARY KEY,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    expiration date NOT NULL
);

CREATE TABLE customers (
    id integer AUTO_INCREMENT PRIMARY KEY,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    credit_card_id varchar(20) NOT NULL,
    address varchar(200) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(20) NOT NULL,
    FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id)
);

CREATE TABLE sales (
    id integer AUTO_INCREMENT PRIMARY KEY,
    customer_id integer,
    movie_id varchar(10),
    sale_date date,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

CREATE TABLE ratings (
    movie_id varchar(10),
    rating float,
    vote_count integer,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);