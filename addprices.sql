ALTER TABLE movies ADD COLUMN price DECIMAL(5,2);
UPDATE movies
SET price = ROUND(5 + RAND(122) * 15, 2);
ALTER TABLE sales ADD COLUMN quantity integer DEFAULT 1;