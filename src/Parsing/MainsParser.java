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
    private int genre_changes = 0;
    private InconsistencyReporter reporter;
    // use this to map abbreviated genres to existing ones
    private Map<String, String> genre_inconsistences = new HashMap<String, String>();

    MainsParser(String file_path, InconsistencyReporter reporter) {
        this.file_path = file_path;
        this.reporter = reporter;
        genre_inconsistences.put("Dram", "Drama");
        genre_inconsistences.put("Susp", "Suspense");
        genre_inconsistences.put("susp", "Suspense");
        genre_inconsistences.put("Romt", "Romance");
        genre_inconsistences.put("Musc", "Musical");
        genre_inconsistences.put("Myst", "Mystery");
        genre_inconsistences.put("Comd", "Comedy");
        genre_inconsistences.put("Docu", "Documentary");
        genre_inconsistences.put("Advt", "Adventure");
        genre_inconsistences.put("Actn", "Action");
        genre_inconsistences.put("noir", "Noir");

        genre_inconsistences.put("West", "Western");
        genre_inconsistences.put("Fant", "Fantasy");
        genre_inconsistences.put("ScFi", "Sci-Fi");
        genre_inconsistences.put("Horr", "Horror");
        genre_inconsistences.put("BioP", "Biopic");
        genre_inconsistences.put("Hist", "History");
        genre_inconsistences.put("Biop", "Biopic");
        genre_inconsistences.put("DRam", "Drama");
        genre_inconsistences.put("Surr", "Surreal");
        genre_inconsistences.put("surreal", "Surreal");

        genre_inconsistences.put("Crim", "Crime");
        genre_inconsistences.put("Romt Comd", "Romantic Comedy");
        genre_inconsistences.put("BioG", "Biography");
        genre_inconsistences.put("West1", "Western");
        genre_inconsistences.put("SciF", "Sci-Fi");
        genre_inconsistences.put("stage musical", "Musical");
        genre_inconsistences.put("Muusc", "Musical");
        genre_inconsistences.put("CnRbb", "CnRb");
        genre_inconsistences.put("Draam", "Drama");
        genre_inconsistences.put("ram", "Drama");

        genre_inconsistences.put("Dramn", "Drama");
        genre_inconsistences.put("Comdx", "Comedy");
        genre_inconsistences.put("comd", "Comedy");
        genre_inconsistences.put("DRAM", "Drama");
        genre_inconsistences.put("romt", "Romance");
        genre_inconsistences.put("Muscl", "Musical");
        genre_inconsistences.put("dram", "Drama");
        genre_inconsistences.put("S.F.", "Sci-Fi");
        genre_inconsistences.put("actn", "Action");
        genre_inconsistences.put("musc", "Musical");

        genre_inconsistences.put("BiopP", "Biopic");
        genre_inconsistences.put("DraM", "Drama");
        genre_inconsistences.put("Bio", "Biography");
        genre_inconsistences.put("AvGa", "Avant Garde");
        genre_inconsistences.put("Comd West", "Comedy Western");
        genre_inconsistences.put("Scfi", "Sci-Fi");
        genre_inconsistences.put("Dramd", "Drama");
        genre_inconsistences.put("fant", "Fantasy");
        genre_inconsistences.put("Dram>", "Drama");
        genre_inconsistences.put("porn", "Porn");

        genre_inconsistences.put("Hor", "Horror");
        genre_inconsistences.put("BioPx", "Biopic");
        genre_inconsistences.put("Axtn", "Action");
        genre_inconsistences.put("SxFi", "Sci-Fi");
        genre_inconsistences.put("BioPP", "Biopic");
        genre_inconsistences.put("Ducu", "Documentary");
        genre_inconsistences.put("Sati", "Satire");
        genre_inconsistences.put("Faml", "Family");
        genre_inconsistences.put("Noir Comd Romt", "Noir Romantic Comedy");
        genre_inconsistences.put("Romt Actn", "Romantic Action");

        genre_inconsistences.put("Dram Docu", "Drama Documentary");
        genre_inconsistences.put("Romt. Comd", "Romantic Comedy");
        genre_inconsistences.put("Dram.Actn", "Drama Action");
        genre_inconsistences.put("Noir Comd", "Noir Comedy");
        genre_inconsistences.put("Romt Dram", "Romantic Drama");
        genre_inconsistences.put("Docu Dram", "Drama Documentary");
        genre_inconsistences.put("Psych Dram", "Psychological Drama");

        genre_inconsistences.put("Porb", "Porn");
        genre_inconsistences.put("Psyc", "Psychological");
        genre_inconsistences.put("RomtAdvt", "Romantic Adventure");
        genre_inconsistences.put("Comd Noir", "Noir Comedy");
        genre_inconsistences.put("Romt Fant", "Romantic Fantasy");
        genre_inconsistences.put("Surl", "Surreal");
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
            reporter.report("<directorfilms> missing <films> for director");
            return movieList;
        }
        List<Element> filmElements = getDirectChildren(filmsElement, "film");
        for (Element film : filmElements) {
            Movie movie = convertDomElementToMovie(film);
            if (movie == null) {
                continue;
            }
            if (director == null || director.trim().isEmpty()) {
                reporter.report("Movie missing director");
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
            reporter.report("Movie with missing <fid>>");
            return null;
        }

        if (title == null || title.trim().isEmpty() || title.equals("NKT")) {
            reporter.report("Movie missing/unknown <t>");

            return null;
        }
        Integer year = parseIntegerOrNull(yearText);
        if (year == null) {
            reporter.report("Year is missing/invalid");
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
                genre_changes += 1;
                genre = genre_inconsistences.get(genre);
            }


            switch (genre) {
                case "Romantic Fantasy" -> {
                    genres.add("Fantasy");
                    genres.add("Romance");
                }
                case "Romantic Adventure" -> {
                    genres.add("Adventure");
                    genres.add("Romance");
                }
                case "Psychological Drama" -> {
                    genres.add("Drama");
                    genres.add("Psychological");
                }
                case "Romantic Comedy" -> {
                    genres.add("Comedy");
                    genres.add("Romance");
                }
                case "Comedy Western" -> {
                    genres.add("Western");
                    genres.add("Comedy");
                }
                case "Noir Romantic Comedy" -> {
                    genres.add("Noir");
                    genres.add("Romance");
                    genres.add("Comedy");
                }
                case "Romantic Action" -> {
                    genres.add("Romance");
                    genres.add("Action");
                }
                case "Drama Action" -> {
                    genres.add("Drama");
                    genres.add("Action");
                }
                case "Noir Comedy" -> {
                    genres.add("Noir");
                    genres.add("Comedy");
                }
                case "Romantic Drama" -> {
                    genres.add("Romance");
                    genres.add("Drama");
                }
                case "Drama Documentary" -> {
                    genres.add("Drama");
                    genres.add("Documentary");
                }
                default -> genres.add(genre);
            }
        }

        return genres;
    }

    public int getGenre_changes(){return genre_changes;}
}
