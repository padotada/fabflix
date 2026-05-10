DROP PROCEDURE IF EXISTS add_movie;

DELIMITER //

CREATE PROCEDURE add_movie(
    IN movie_title VARCHAR(100),
    IN movie_year INT,
    IN movie_director VARCHAR(100),
    IN star_name VARCHAR(100),
    IN genre_name VARCHAR(100),
    OUT status_message VARCHAR(255)
)

BEGIN
    DECLARE existing_movie_count INT DEFAULT 0;

    DECLARE new_movie_id VARCHAR(10);
    DECLARE max_movie_id VARCHAR(10);
    DECLARE next_movie_num INT;

    DECLARE selected_star_id VARCHAR(10);
    DECLARE max_star_id VARCHAR(10);
    DECLARE next_star_num INT;

    DECLARE selected_genre_id INT;

    SELECT COUNT(*)
    INTO existing_movie_count
    FROM movies
    WHERE title = movie_title
      AND year = movie_year
      AND director = movie_director;

    IF existing_movie_count > 0 THEN
        SET status_message = 'Movie already exists. No changes were made.';
    ELSE
        SELECT MAX(id)
        INTO max_movie_id
        FROM movies
        WHERE id LIKE 'tt%';

        IF max_movie_id IS NULL THEN
            SET next_movie_num = 1;
        ELSE
            SET next_movie_num = CAST(SUBSTRING(max_movie_id, 3) AS UNSIGNED) + 1;
        END IF;

        SET new_movie_id = CONCAT('tt', LPAD(next_movie_num, 7, '0'));

        INSERT INTO movies(id, title, year, director)
        VALUES(new_movie_id, movie_title, movie_year, movie_director);

        SELECT id
        INTO selected_star_id
        FROM stars
        WHERE name = star_name
        LIMIT 1;

        IF selected_star_id IS NULL THEN
            SELECT MAX(id)
            INTO max_star_id
            FROM stars
            WHERE id LIKE 'nm%';

            IF max_star_id IS NULL THEN
                SET next_star_num = 1;
            ELSE
                SET next_star_num = CAST(SUBSTRING(max_star_id, 3) AS UNSIGNED) + 1;
            END IF;

            SET selected_star_id = CONCAT('nm', LPAD(next_star_num, 7, '0'));

            INSERT INTO stars(id, name, birth_year)
            VALUES(selected_star_id, star_name, NULL);
        END IF;

        SELECT id
        INTO selected_genre_id
        FROM genres
        WHERE name = genre_name
        LIMIT 1;

        IF selected_genre_id IS NULL THEN
            INSERT INTO genres(name)
            VALUES(genre_name);

            SET selected_genre_id = LAST_INSERT_ID();
        END IF;

        INSERT INTO stars_in_movies(star_id, movie_id)
        VALUES(selected_star_id, new_movie_id);

        INSERT INTO genres_in_movies(genre_id, movie_id)
        VALUES(selected_genre_id, new_movie_id);

        SET status_message = CONCAT(
                'Movie added successfully. Movie ID: ', new_movie_id,
                ', Star ID: ', selected_star_id,
                ', Genre ID: ', selected_genre_id
                             );
    END IF;
END //
DELIMITER ;