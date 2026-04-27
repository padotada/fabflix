import Classes.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;

// this is the project 2 freeze branch

@WebServlet("/movielist")
public class MoviesServlet extends HttpServlet {
    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set response mime type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String searchParam = request.getParameter("search");
        boolean isSearch = searchParam != null && searchParam.equals("true");

        // if browsing, search ones will be null, and vice versa
        String title = null;
        String year = null;
        String director = null;
        String star = null;

        String first = null;
        String genre = null;

        if(isSearch) {
            title = request.getParameter("title");
            year = request.getParameter("year");
            director = request.getParameter("director");
            star = request.getParameter("star");
        }
        else{
            first = request.getParameter("first");
            genre = request.getParameter("genre");
        }

        User user = (User) request.getSession().getAttribute("user");
        user.setTitle(title);
        user.setYear(year);
        user.setDirector(director);
        user.setStar(star);

        user.setFirst(first);
        user.setGenre(genre);

        user.setSearchMode(isSearch);


        int page_number = 1;
        if(request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {page_number = Integer.parseInt(request.getParameter("page"));}
        user.setPage(page_number);

        int num_records = 10;
        if(request.getParameter("records") != null && !request.getParameter("records").isEmpty()) {num_records = Integer.parseInt(request.getParameter("records"));}
        user.setNumRecords(num_records);


        String title_first = request.getParameter("title_first");
        if(title_first == null || !(title_first.equals("false") || title_first.equals("true"))) {title_first = "false";}
        user.setTitleFirst(title_first);

        int title_sort = 1;
        if(request.getParameter("title_sort") != null && !request.getParameter("title_sort").isEmpty()) {title_sort = Integer.parseInt(request.getParameter("title_sort"));}
        if(title_sort != 1 && title_sort != 0) {title_sort = 1;}
        user.setTitleSort(title_sort);

        int rating_sort = 1;
        if(request.getParameter("rating_sort") != null && !request.getParameter("rating_sort").isEmpty()) {rating_sort = Integer.parseInt(request.getParameter("rating_sort"));}
        if(rating_sort != 1 && rating_sort != 0) {rating_sort = 1;}
        user.setRatingSort(rating_sort);

        String order = " ORDER BY ";
        if(title_first.equals("true")) {
            order += "m.title ";
            order += title_sort == 1 ? "DESC" : "ASC";
            order += ", r.rating ";
            order += rating_sort == 1 ? "DESC" : "ASC";
        }
        else {
            order += "r.rating ";
            order += rating_sort == 1 ? "DESC" : "ASC";
            order += ", m.title ";
            order += title_sort == 1 ? "DESC" : "ASC";
        }


        // String queries
//        String movieQuery = """
//                SELECT m.id, m.title, m.year, m.director, r.rating FROM movies m
//                JOIN ratings r ON m.id = r.movie_id ORDER BY r.rating DESC LIMIT 20
//                """;
        StringBuilder movieQuery = new StringBuilder("""
            SELECT DISTINCT m.id, m.title, m.year, m.director, m.price, r.rating FROM movies m
            LEFT JOIN ratings r on m.id = r.movie_id
        """);

        String genreQuery = """
                SELECT g.name
                FROM genres_in_movies gim
                JOIN genres g ON gim.genre_id = g.id
                WHERE gim.movie_id = ?
                ORDER BY g.name
                LIMIT 3
                """;
        String starQuery = """
                SELECT s.id, s.name
                FROM stars_in_movies sim
                JOIN stars s ON sim.star_id = s.id
                WHERE sim.movie_id = ?
                ORDER BY s.name
                LIMIT 3
                """;

        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<String> params = new ArrayList<>();

        if (star != null && !star.trim().isEmpty()){
            movieQuery.append(" JOIN stars_in_movies sim ON m.id = sim.movie_id ");
            movieQuery.append(" JOIN stars s ON sim.star_id = s.id ");
        }
        if (genre != null && !genre.trim().isEmpty()) {
            movieQuery.append(" JOIN genres_in_movies gim ON m.id = gim.movie_id ");
            movieQuery.append(" JOIN genres g ON g.id = gim.genre_id ");
        }


        if (title != null && !title.trim().isEmpty()){
            conditions.add("m.title LIKE ?");
            params.add("%" + title + "%");
        }

        if (year != null && !year.trim().isEmpty()) {
            conditions.add("m.year = ?");
            params.add(year);
        }

        if (director != null && !director.trim().isEmpty()) {
            conditions.add("m.director LIKE ?");
            params.add("%" + director + "%");
        }

        if (star != null && !star.trim().isEmpty()) {
            conditions.add("s.name LIKE ?");
            params.add("%" + star + "%");
        }

        if (first != null && !first.trim().isEmpty()) {
            conditions.add("m.title LIKE ?");
            params.add(first + "%");
        }

        if (genre != null && !genre.trim().isEmpty()) {
            conditions.add("g.name = ?");
            params.add(genre);
        }

        if (!conditions.isEmpty()) {
            movieQuery.append(" WHERE ");
            movieQuery.append(String.join(" AND ", conditions));
        }

        movieQuery.append(order);
        movieQuery.append(" LIMIT ").append(num_records+1).append(" OFFSET ").append(num_records * (page_number - 1));




        // Get the PrintWriter for writing response
        PrintWriter jsonOutput = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = dataSource.getConnection();
//            System.out.println(movieQuery.toString());
            PreparedStatement movieStatement = connection.prepareStatement(movieQuery.toString());
            PreparedStatement genreStatement = connection.prepareStatement(genreQuery);
            PreparedStatement starStatement = connection.prepareStatement(starQuery)) {
            request.getServletContext().log("movieQuery = " + movieQuery.toString());
            request.getServletContext().log("params = " + params.toString());
            for(int i = 0; i < params.size(); ++i){
                movieStatement.setString(i + 1, params.get(i));
            }
            ResultSet resultSet = movieStatement.executeQuery();
            JsonArray jsonArray = new JsonArray();
            int count = 0;

            while (resultSet.next()) {
                count += 1;
                String movieId = resultSet.getString("id");
                String movieTitle = resultSet.getString("title");
                String movieYear = resultSet.getString("year");
                String movieDirector = resultSet.getString("director");
                String rating = resultSet.getString("rating");
                String movie_price = resultSet.getString("price");

                JsonObject movieObject = new JsonObject();
                movieObject.addProperty("movie_id", movieId);
                movieObject.addProperty("movie_title", movieTitle);
                movieObject.addProperty("movie_year", movieYear);
                movieObject.addProperty("movie_director", movieDirector);
                movieObject.addProperty("movie_rating", rating);
                movieObject.addProperty("movie_price", movie_price);

                JsonArray genres = new JsonArray();
                genreStatement.setString(1, movieId);
                try (ResultSet genreResultSet = genreStatement.executeQuery()) {
                    while(genreResultSet.next()){
                        String genreName = genreResultSet.getString("name");
                        genres.add(genreName);
                    }
                }
                movieObject.add("genres", genres);

                JsonArray starsList = new JsonArray();
                starStatement.setString(1, movieId);
                try (ResultSet starResultSet = starStatement.executeQuery()) {
                    while (starResultSet.next()) {
                        JsonObject starObj = new JsonObject();
                        String starId = starResultSet.getString("id");
                        String starName = starResultSet.getString("name");
                        starObj.addProperty("star_id", starId);
                        starObj.addProperty("star_name", starName);
                        starsList.add(starObj);
                    }
                }
                movieObject.add("stars", starsList);

                jsonArray.add(movieObject);
            }
            request.getServletContext().log("getting " + jsonArray.size() + " results");
            jsonOutput.write(jsonArray.toString());
            response.setStatus(200);
            resultSet.close();
        } catch (Exception e) {
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.htmlOutput
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.htmlOutput
             * This can help you debug your program after deploying it on AWS.
             */
            request.getServletContext().log("Error: ", e);
            response.setStatus(500);
            // error is stored in json object
            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            jsonOutput.write(error.toString());
        }finally {
            jsonOutput.close();
        }
    }
}
