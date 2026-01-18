-- 1
CREATE OR REPLACE FUNCTION find_movie_with_min_genre()
RETURNS integer AS $$
DECLARE
    movie_id integer;
BEGIN
    SELECT id
    INTO movie_id
    FROM movies
    WHERE genre = (
        SELECT MIN(genre) FROM movies
    )
    LIMIT 1;

    RETURN movie_id;
END;
$$ LANGUAGE plpgsql;


-- 2
CREATE OR REPLACE FUNCTION count_movies_by_golden_palm(count_value integer)
RETURNS bigint AS $$
DECLARE
    movie_count bigint;
BEGIN
    SELECT count(*)
    INTO movie_count
    FROM movies
    WHERE goldenpalmcount = count_value;

    RETURN movie_count;
END;
$$ LANGUAGE plpgsql;


-- 3
CREATE OR REPLACE FUNCTION count_movies_genre_less_than(genre_value varchar)
RETURNS bigint AS $$
DECLARE
    movie_count bigint;
BEGIN
    SELECT count(*)
    INTO movie_count
    FROM movies
    WHERE genre < genre_value;

    RETURN movie_count;
END;
$$ LANGUAGE plpgsql;


-- 4
CREATE OR REPLACE FUNCTION find_screenwriters_with_no_oscars()
RETURNS SETOF integer AS $$
BEGIN
    RETURN QUERY
    SELECT screenwriter_id FROM movies WHERE screenwriter_id IS NOT NULL
    
    EXCEPT
    
    SELECT screenwriter_id FROM movies WHERE oscarscount > 0;
END;
$$ LANGUAGE plpgsql;


-- 5
CREATE OR REPLACE FUNCTION redistribute_oscars_by_genre(
    source_genre_in varchar,
    dest_genre_in varchar
)
RETURNS integer AS $$
DECLARE
    total_oscars_to_move integer;
    dest_movie_count integer;
    oscars_per_movie integer;
BEGIN
    IF source_genre_in = dest_genre_in THEN
        RAISE EXCEPTION 'Исходный и конечный жанры не могут совпадать';
    END IF;

    SELECT COALESCE(SUM(oscarscount), 0)
    INTO total_oscars_to_move
    FROM movies
    WHERE genre = source_genre_in;

    IF total_oscars_to_move = 0 THEN
        RETURN 0;
    END IF;

    SELECT count(*)
    INTO dest_movie_count
    FROM movies
    WHERE genre = dest_genre_in;

    IF dest_movie_count = 0 THEN
        RAISE EXCEPTION 'Не найдено фильмов в конечном жанре для перераспределения';
    END IF;
    
    oscars_per_movie := total_oscars_to_move / dest_movie_count;
    
    IF oscars_per_movie = 0 THEN
         RETURN 0;
    END IF;

    UPDATE movies
    SET oscarscount = oscarscount + oscars_per_movie
    WHERE genre = dest_genre_in;

    UPDATE movies
    SET oscarscount = 0
    WHERE genre = source_genre_in;

    RETURN oscars_per_movie * dest_movie_count;
END;
$$ LANGUAGE plpgsql;