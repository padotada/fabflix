import Classes.User;
import MongoRetrieval.MongoMovies;
import MongoRetrieval.MoviePojo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/movielist")
public class MoviesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set response mime type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get the PrintWriter for writing response
        PrintWriter jsonOutput = response.getWriter();

        try {
            String searchParam = request.getParameter("search");
            boolean isSearch = searchParam != null && searchParam.equals("true");

            String title = null;
            String year = null;
            String director = null;
            String star = null;

            String first = null;
            String genre = null;

            if (isSearch) {
                title = request.getParameter("title");
                year = request.getParameter("year");
                director = request.getParameter("director");
                star = request.getParameter("star");
            } else {
                first = request.getParameter("first");
                genre = request.getParameter("genre");
            }

            User user = (User) request.getSession().getAttribute("user");

            if (user != null) {
                user.setTitle(title);
                user.setYear(year);
                user.setDirector(director);
                user.setStar(star);

                user.setFirst(first);
                user.setGenre(genre);

                user.setSearchMode(isSearch);
            }

            int pageNumber = 1;
            if (request.getParameter("page") != null && !request.getParameter("page").isEmpty()) {
                pageNumber = Integer.parseInt(request.getParameter("page"));
            }

            int numRecords = 10;
            if (request.getParameter("records") != null && !request.getParameter("records").isEmpty()) {
                numRecords = Integer.parseInt(request.getParameter("records"));
            }

            String titleFirst = request.getParameter("title_first");
            if (titleFirst == null || !(titleFirst.equals("false") || titleFirst.equals("true"))) {
                titleFirst = "false";
            }

            int titleSort = 1;
            if (request.getParameter("title_sort") != null && !request.getParameter("title_sort").isEmpty()) {
                titleSort = Integer.parseInt(request.getParameter("title_sort"));
            }
            if (titleSort != 1 && titleSort != 0) {
                titleSort = 1;
            }

            int ratingSort = 1;
            if (request.getParameter("rating_sort") != null && !request.getParameter("rating_sort").isEmpty()) {
                ratingSort = Integer.parseInt(request.getParameter("rating_sort"));
            }
            if (ratingSort != 1 && ratingSort != 0) {
                ratingSort = 1;
            }

            if (user != null) {
                user.setPage(pageNumber);
                user.setNumRecords(numRecords);
                user.setTitleFirst(titleFirst);
                user.setTitleSort(titleSort);
                user.setRatingSort(ratingSort);
            }

            MongoMovies mongoMovies = new MongoMovies();

            if (isSearch) {
                mongoMovies.addTitleFilter(title);
                mongoMovies.addYearFilter(year);
                mongoMovies.addDirectorFilter(director);
                mongoMovies.addStarFilter(star);
            } else {
                mongoMovies.addFirstLetterFilter(first);
                mongoMovies.addGenreFilter(genre);
            }

            mongoMovies.setPagination(pageNumber, numRecords);
            mongoMovies.setSort(titleFirst, titleSort, ratingSort);

            List<MoviePojo> movies = mongoMovies.retrieveMovies();

            JsonArray jsonArray = new JsonArray();

            for (MoviePojo movie : movies) {
                jsonArray.add(movie.toJson());
            }

            request.getServletContext().log("getting " + jsonArray.size() + " results from MongoDB");

            jsonOutput.write(jsonArray.toString());
            response.setStatus(200);

        } catch (Exception e) {
            request.getServletContext().log("MongoDB movie retrieval error: ", e);
            response.setStatus(500);

            JsonObject error = new JsonObject();
            error.addProperty("errorMessage", e.getMessage());
            jsonOutput.write(error.toString());

        }finally {
            jsonOutput.close();
        }
    }
}
