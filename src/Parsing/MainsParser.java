package Parsing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MainsParser {
    private String file_path;

    // use this to map abbreviated genres to existing ones
    private Map<String, String> genre_inconsistences = new HashMap<String, String>();

    MainsParser(String file_path) {
        this.file_path = file_path;
    }

    public List<Movie> runParser() throws IOException, ParserConfigurationException, SAXException {
        Document document = parseXmlFile();
        List<Movie> movieList = convertDocumentIntoMovies(document);
        return movieList;
    }

    private Document parseXmlFile() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Reader reader = new InputStreamReader(new FileInputStream(file_path), StandardCharsets.ISO_8859_1);
        InputSource inputSource = new InputSource(reader);
        inputSource.setEncoding("ISO-8859-1");
        return documentBuilder.parse(file_path);
    }

    private List<Movie> convertDocumentIntoMovies(Document document) {
        Element rootDocumentElement = document.getDocumentElement();
        List<Movie> movieList = new ArrayList<>();

        NodeList nodeList = rootDocumentElement.getElementsByTagName("directorfilms");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            movieList.addAll(convertDirectorFilmsIntoMovies(element));
        }
        return movieList;
    }

    private List<Movie> convertDirectorFilmsIntoMovies(Element element)
    {
        String director = getTextValue(element, "dirname");
        List<Movie> movieList = new ArrayList<>();
        NodeList nodeList = element.getElementsByTagName("film");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element film = (Element) nodeList.item(i);
            Movie movie = convertDomElementToMovie(film);
            if (movie == null) {
                continue;
            }
            if(director == null || director.trim().isEmpty()){
                System.err.println("Movie missing director: fid=" + movie.getId()
                        + ", title=" + movie.getTitle());
                continue;
            }
            movie.setDirector(director);
            movieList.add(movie);
        }
        return movieList;
    }

    private Integer getNullableIntValue(Element element, String tagName, String context) {
        String text = getTextValue(element, tagName);

        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        text = text.trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer for <" + tagName + ">: value="
                    + text + ", context=" + context);
            return null;
        }
    }

    private Movie convertDomElementToMovie(Element element) {
        String id = getTextValue(element, "fid");
        String title = getTextValue(element, "t");
        String yearText = getTextValue(element, "year");
        if (id == null || id.trim().isEmpty()) {
            System.err.println("Movie missing <fid>: title=" + title);
            return null;
        }

        if (title == null || title.trim().isEmpty() || title.equals("NKT")) {
            System.err.println("Movie missing/unknown <t>: fid=" + id + ", title=" + title);
            return null;
        }
        Integer year = getNullableIntValue(element, "year", "movie fid=" + id + ", title=" + title);
        if (year == null) {
            System.err.println("Skipping movie because year is missing/invalid: fid="
                    + id + ", title=" + title + ", year=" + yearText);
            return null;
        }
        Movie movie = new Movie(id.trim(), title.trim(), year, null);
        movie.setGenres(getGenresOfFilm(element));

        return movie;
    }

    private List<String> getGenresOfFilm(Element element) {
        List<String> genres = new ArrayList<>();
        NodeList nodeList = element.getElementsByTagName("cat");
        for (int i = 0; i < nodeList.getLength(); i++) {
            if(nodeList.item(i).getFirstChild() == null) {continue;}
            String genre = nodeList.item(i).getFirstChild().getNodeValue();
            if(genre_inconsistences.containsKey(genre)) {
                System.err.print("Genre " + genre);
                genre = genre_inconsistences.get(genre);
                System.err.println(" replaced with " + genre + " for consistency");
            }
            genres.add(genre);
        }
        return genres;
    }

    private String getTextValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() == 1) {
            // here we expect only one <Name> would present in the <Employee>
            if(nodeList.item(0).getFirstChild() == null) {return null;}
            return nodeList.item(0).getFirstChild().getNodeValue();
        }
        return null;
    }

    private int getIntValue(Element ele, String tagName) {
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    private void printEmployeeData(List<Movie> movieList) {
        System.out.println("Total parsed " + movieList.size() + " employees");
        for (Movie movie : movieList) {
            System.out.println("\t" + movie.toString());
        }
    }
}
