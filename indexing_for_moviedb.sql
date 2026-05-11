ALTER TABLE movies ADD FULLTEXT(title);
ALTER TABLE movies ADD INDEX idx_movies_director (director);
ALTER TABLE movies ADD INDEX idx_movies_year (year);

ALTER TABLE stars ADD INDEX idx_stars_name (name);

ALTER TABLE stars_in_movies ADD INDEX idx_sim_movie_id (movie_id);
ALTER TABLE stars_in_movies ADD INDEX idx_sim_star_id (star_id);

ALTER TABLE genres_in_movies ADD INDEX idx_gim_movie_id (movie_id);
ALTER TABLE genres_in_movies ADD INDEX idx_gim_genre_id (genre_id);

ALTER TABLE genres ADD INDEX idx_genres_name (name);