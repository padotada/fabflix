package Parsing;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
public class XMLDataInserter {
    private String loginUser;
    private String loginPassword;
    private String loginUrl;
    private static final int BATCH_SIZE = 1000;
    private InconsistencyReporter reporter;
    private Connection connection;

    private Set<String> existingMovieIds = new HashSet<>();
    private Map<String, String> starNameToId = new HashMap<>();
    private Map<String, Integer> genreNameToId = new HashMap<>();
    private Set<String> existingStarMoviePairs = new HashSet<>();
    private Set<String> existingGenreMoviePairs = new HashSet<>();

    private int nextStarNumber = 1000000;

    private void loadDatabaseConfig() throws Exception{
        loginUrl = System.getenv("DB_URL");
        loginUser = System.getenv("DB_USER");
        loginPassword = System.getenv("DB_PASSWORD");
        if (loginUrl == null || loginUrl.trim().isEmpty()) {
            throw new RuntimeException("Missing environment variable: DB_URL");
        }

        if (loginUser == null || loginUser.trim().isEmpty()) {
            throw new RuntimeException("Missing environment variable: DB_USER");
        }

        if (loginPassword == null || loginPassword.trim().isEmpty()) {
            throw new RuntimeException("Missing environment variable: DB_PASSWORD");
        }
    }

    public XMLDataInserter(InconsistencyReporter reporter) throws Exception {
        this.reporter = reporter;
        loadDatabaseConfig();

        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(loginUrl, loginUser, loginPassword);

        connection.setAutoCommit(false);
    }

    public void insertData(List<Movie> movies, List<Actor> actors, List<ActorMoviePair> castPairs) throws SQLException{
        try{
            loadExistingData();
            insertMovies(movies);
            insertActors(actors);
            insertGenresAndGenreMovieLinks(movies);
            insertStarMovieLinks(castPairs);
            connection.commit();
            System.out.println("XML data inserted successfully.");
        }catch(SQLException e){
            connection.rollback();
            reporter.report("Database insertion failed. Rolling back changes.");
            throw e;
        }finally{
            connection.close();
        }
    }

    private void loadExistingData() throws SQLException {
        loadExistingMovies();
        loadExistingStars();
        loadExistingGenres();
        loadExistingStarMoviePairs();
        loadExistingGenreMoviePairs();
        loadNextStarNumber();

        System.out.println("Loaded existing database records into memory.");
    }

    private void loadExistingMovies() throws SQLException {
        String query = "SELECT id FROM movies";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                existingMovieIds.add(rs.getString("id"));
            }
        }
    }

    private void loadExistingStars() throws SQLException {
        String query = "SELECT id, name FROM stars";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");

                if (name != null) {
                    starNameToId.put(name.trim(), id);
                }
            }
        }
    }

    private void loadExistingGenres() throws SQLException {
        String query = "SELECT id, name FROM genres";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                if (name != null) {
                    genreNameToId.put(name.trim(), id);
                }
            }
        }
    }

    private void loadExistingStarMoviePairs() throws SQLException {
        String query = "SELECT star_id, movie_id FROM stars_in_movies";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                String starId = rs.getString("star_id");
                String movieId = rs.getString("movie_id");
                existingStarMoviePairs.add(starId + "#" + movieId);
            }
        }
    }

    private void loadExistingGenreMoviePairs() throws SQLException {
        String query = "SELECT genre_id, movie_id FROM genres_in_movies";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                int genreId = rs.getInt("genre_id");
                String movieId = rs.getString("movie_id");
                existingGenreMoviePairs.add(genreId + "#" + movieId);
            }
        }
    }

    private void loadNextStarNumber() throws SQLException {
        String query = "SELECT id FROM stars WHERE id LIKE 'nm%'";

        int maxNumber = 0;

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                String id = rs.getString("id");

                try {
                    int number = Integer.parseInt(id.substring(2));
                    maxNumber = Math.max(maxNumber, number);
                } catch (Exception e) {
                    // Ignore weird star IDs that do not follow nmXXXXXXX format
                }
            }
        }

        nextStarNumber = maxNumber + 1;
    }

    private void insertMovies(List<Movie> movies) throws SQLException {
        String query = "INSERT INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)";

        int count = 0;
        int inserted = 0;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Movie movie : movies) {
                if (movie == null) {
                    continue;
                }

                String movieId = movie.getId();

                if (movieId == null || movieId.trim().isEmpty()) {
                    reporter.report("Movie with missing id");
                    continue;
                }

                movieId = movieId.trim();

                if (existingMovieIds.contains(movieId)) {
                    reporter.report("Movie already exists");
                    continue;
                }

                if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                    reporter.report("Skipping movie with missing title");
                    continue;
                }

                if (movie.getDirector() == null || movie.getDirector().trim().isEmpty()) {
                    reporter.report("Skipping movie with missing director");
                    continue;
                }

                statement.setString(1, movieId);
                statement.setString(2, movie.getTitle().trim());

                if (movie.getYear() == null) {
                    reporter.report("Skipping movie because year is missing/invalid");
                    continue;
                }

                statement.setInt(3, movie.getYear());

                statement.setString(4, movie.getDirector().trim());

                statement.addBatch();

                existingMovieIds.add(movieId);
                count++;
                inserted++;

                if (count % BATCH_SIZE == 0) {
                    statement.executeBatch();
                }
            }

            statement.executeBatch();
        }

        System.out.println("Inserted movies: " + inserted);
    }

    private void insertActors(List<Actor> actors) throws SQLException {
        String query = "INSERT INTO stars(id, name, birth_year) VALUES (?, ?, ?)";

        int count = 0;
        int inserted = 0;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Actor actor : actors) {
                if (actor == null) {
                    continue;
                }

                String name = actor.getName();

                if (name == null || name.trim().isEmpty()) {
                    reporter.report("Actor missing stagename");
                    continue;
                }

                name = name.trim();

                if (starNameToId.containsKey(name)) {
                    reporter.report("Actor already exists");
                    continue;
                }

                String newStarId = generateStarId();

                statement.setString(1, newStarId);
                statement.setString(2, name);

                if (actor.getBirth_year() == null) {
                    statement.setNull(3, Types.INTEGER);
                } else {
                    statement.setInt(3, actor.getBirth_year());
                }

                statement.addBatch();

                starNameToId.put(name, newStarId);
                count++;
                inserted++;

                if (count % BATCH_SIZE == 0) {
                    statement.executeBatch();
                }
            }

            statement.executeBatch();
        }

        System.out.println("Inserted actors/stars: " + inserted);
    }

    private void insertGenresAndGenreMovieLinks(List<Movie> movies) throws SQLException {
        int insertedGenres = 0;
        int insertedLinks = 0;

        String insertGenreQuery = "INSERT INTO genres(name) VALUES (?)";
        String insertGenreMovieQuery = "INSERT INTO genres_in_movies(genre_id, movie_id) VALUES (?, ?)";

        try (PreparedStatement insertGenreStatement = connection.prepareStatement(insertGenreQuery, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement insertGenreMovieStatement = connection.prepareStatement(insertGenreMovieQuery)) {

            int linkBatchCount = 0;

            for (Movie movie : movies) {
                if (movie == null || movie.getId() == null) {
                    continue;
                }

                String movieId = movie.getId().trim();

                if (!existingMovieIds.contains(movieId)) {
                    reporter.report("Cannot link genres. Movie does not exist");
                    continue;
                }

                for (String genre : movie.getGenres()) {
                    if (genre == null || genre.trim().isEmpty()) {
                        reporter.report("Empty genre");
                        continue;
                    }

                    genre = genre.trim();

                    Integer genreId = genreNameToId.get(genre);

                    if (genreId == null) {
                        insertGenreStatement.setString(1, genre);
                        insertGenreStatement.executeUpdate();

                        try (ResultSet generatedKeys = insertGenreStatement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                genreId = generatedKeys.getInt(1);
                                genreNameToId.put(genre, genreId);
                                insertedGenres++;
                            } else {
                                reporter.report("Failed to get generated genre id");
                                continue;
                            }
                        }
                    }

                    String pairKey = genreId + "#" + movieId;

                    if (existingGenreMoviePairs.contains(pairKey)) {
                        reporter.report("Genre/movie pair already exists");
                        continue;
                    }

                    insertGenreMovieStatement.setInt(1, genreId);
                    insertGenreMovieStatement.setString(2, movieId);
                    insertGenreMovieStatement.addBatch();

                    existingGenreMoviePairs.add(pairKey);
                    linkBatchCount++;
                    insertedLinks++;

                    if (linkBatchCount % BATCH_SIZE == 0) {
                        insertGenreMovieStatement.executeBatch();
                    }
                }
            }

            insertGenreMovieStatement.executeBatch();
        }

        System.out.println("Inserted genres: " + insertedGenres);
        System.out.println("Inserted genres_in_movies links: " + insertedLinks);
    }

    private void insertStarMovieLinks(List<ActorMoviePair> castPairs) throws SQLException {
        String query = "INSERT INTO stars_in_movies(star_id, movie_id) VALUES (?, ?)";

        int count = 0;
        int inserted = 0;

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (ActorMoviePair pair : castPairs) {
                if (pair == null) {
                    continue;
                }

                String actorName = pair.getActorName();
                String movieId = pair.getMovieId();

                if (actorName == null || actorName.trim().isEmpty()) {
                    reporter.report("Cast pair missing actor name");
                    continue;
                }

                if (movieId == null || movieId.trim().isEmpty()) {
                    reporter.report("Cast pair missing movie id");
                    continue;
                }

                actorName = actorName.trim();
                movieId = movieId.trim();

                String starId = starNameToId.get(actorName);

                if (starId == null) {
                    reporter.report("Actor from casts.xml not found in stars table");
                    continue;
                }

                if (!existingMovieIds.contains(movieId)) {
                    reporter.report("Movie from casts.xml not found in movies table");
                    continue;
                }

                String pairKey = starId + "#" + movieId;

                if (existingStarMoviePairs.contains(pairKey)) {
                    reporter.report("Star/movie pair already exists");
                    continue;
                }

                statement.setString(1, starId);
                statement.setString(2, movieId);
                statement.addBatch();

                existingStarMoviePairs.add(pairKey);
                count++;
                inserted++;

                if (count % BATCH_SIZE == 0) {
                    statement.executeBatch();
                }
            }

            statement.executeBatch();
        }

        System.out.println("Inserted stars_in_movies links: " + inserted);
    }

    private String generateStarId() {
        String id = String.format("nm%07d", nextStarNumber);
        nextStarNumber++;
        return id;
    }
}
