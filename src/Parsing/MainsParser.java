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

    private Element getDirectChild(Element parent, String tagName) {
        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element child = (Element) children.item(i);

                if (child.getTagName().equals(tagName)) {
                    return child;
                }
            }
        }

        return null;
    }

    private List<Element> getDirectChildren(Element parent, String tagName) {
        List<Element> result = new ArrayList<>();
        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                Element child = (Element) children.item(i);

                if (child.getTagName().equals(tagName)) {
                    result.add(child);
                }
            }
        }

        return result;
    }

    private String getDirectTextValue(Element parent, String tagName) {
        Element child = getDirectChild(parent, tagName);
        if (child == null) {
            return null;
        }
        String text = child.getTextContent();
        if (text == null) {
            return null;
        }
        text = text.trim();
        if (text.isEmpty()) {
            return null;
        }
        return text;
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
        Element directorElement = getDirectChild(element, "director");
        String director = null;
        if (directorElement != null) {
            director = getDirectTextValue(directorElement, "dirname");
        }
        Element filmsElement = getDirectChild(element, "films");
        List<Movie> movieList = new ArrayList<>();
        if (filmsElement == null) {
            System.err.println("<directorfilms> missing <films> for director=" + director);
            return movieList;
        }
        List<Element> filmElements = getDirectChildren(filmsElement, "film");
        for (Element film : filmElements) {
            Movie movie = convertDomElementToMovie(film);
            if (movie == null) {
                continue;
            }
            if (director == null || director.trim().isEmpty()) {
                System.err.println("Movie missing director: fid="
                        + movie.getId() + ", title=" + movie.getTitle());
                continue;
            }
            movie.setDirector(director.trim());
            movieList.add(movie);
        }

        return movieList;
    }

    private Integer parseIntegerOrNull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Movie convertDomElementToMovie(Element element) {
        String id = getDirectTextValue(element, "fid");
        String title = getDirectTextValue(element, "t");
        String yearText = getDirectTextValue(element, "year");
        if (id == null || id.trim().isEmpty()) {
            System.err.println("Movie missing <fid>: title=" + title);
            return null;
        }

        if (title == null || title.trim().isEmpty() || title.equals("NKT")) {
            System.err.println("Movie missing/unknown <t>: fid=" + id + ", title=" + title);
            return null;
        }
        Integer year = parseIntegerOrNull(yearText);
        if (year == null) {
            System.err.println("Skipping movie because year is missing/invalid: fid="
                    + id + ", title=" + title + ", year=" + yearText);
            return null;
        }
        Movie movie = new Movie(id.trim(), title.trim(), year, null);
        movie.setGenres(getGenresOfFilm(element));

        return movie;
    }

    private List<String> getGenresOfFilm(Element filmElement) {
        List<String> genres = new ArrayList<>();
        Element catsElement = getDirectChild(filmElement, "cats");
        if (catsElement == null) {
            return genres;
        }
        List<Element> catElements = getDirectChildren(catsElement, "cat");
        for (Element catElement : catElements) {
            String genre = catElement.getTextContent();

            if (genre == null || genre.trim().isEmpty()) {
                continue;
            }
            genre = genre.trim();
            if (genre_inconsistences.containsKey(genre)) {
                System.err.print("Genre " + genre);
                genre = genre_inconsistences.get(genre);
                System.err.println(" replaced with " + genre + " for consistency");
            }
            genres.add(genre);
        }

        return genres;
    }

//    private String getTextValue(Element element, String tagName) {
//        NodeList nodeList = element.getElementsByTagName(tagName);
//        if (nodeList.getLength() == 1) {
//            // here we expect only one <Name> would present in the <Employee>
//            if(nodeList.item(0).getFirstChild() == null) {return null;}
//            return nodeList.item(0).getFirstChild().getNodeValue();
//        }
//        return null;
//    }

//    private int getIntValue(Element ele, String tagName) {
//        return Integer.parseInt(getTextValue(ele, tagName));
//    }

    private void printEmployeeData(List<Movie> movieList) {
        System.out.println("Total parsed " + movieList.size() + " employees");
        for (Movie movie : movieList) {
            System.out.println("\t" + movie.toString());
        }
    }
}
