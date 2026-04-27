import Classes.User;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/return_movielist")
public class GoBackServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
                PrintWriter jsonOutput = response.getWriter();

                JsonObject userQuery = new JsonObject();
                User user = (User) request.getSession().getAttribute("user");
                if (user == null) {
                        JsonArray jsonArray = new JsonArray();
                        userQuery.addProperty("search", true);
                        userQuery.addProperty("title", "A");
                        userQuery.addProperty("year", "");
                        userQuery.addProperty("director", "");
                        userQuery.addProperty("star", "");
                        jsonArray.add(userQuery);
                        jsonOutput.println(jsonArray);
                        jsonOutput.close();
                        return;
                }

                userQuery.addProperty("search", user.isSearchMode());
                if(user.isSearchMode())
                {
                        userQuery.addProperty("title", user.getTitle());
                        userQuery.addProperty("year", user.getYear());
                        userQuery.addProperty("director", user.getDirector());
                        userQuery.addProperty("star", user.getStar());
                }
                else {
                        userQuery.addProperty("first", user.getFirst());
                        userQuery.addProperty("genre", user.getGenre());
                }

                userQuery.addProperty("page", user.getPage());
                userQuery.addProperty("records", user.getNumRecords());

                userQuery.addProperty("title_first", user.getTitleFirst());
                userQuery.addProperty("title_sort", user.getTitleSort());
                userQuery.addProperty("rating_sort", user.getRatingSort());


                JsonArray jsonArray = new JsonArray();
                jsonArray.add(userQuery);
                jsonOutput.println(jsonArray);
                jsonOutput.close();
        }
}


