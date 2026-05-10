package Parsing;
import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
public class XMLDataInserter {
    private String loginUser;
    private String loginPassword;
    private String loginUrl;

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

    public XMLDataInserter() throws Exception {
        loadDatabaseConfig();

        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(loginUrl, loginUser, loginPassword);

        connection.setAutoCommit(false);
    }


}
