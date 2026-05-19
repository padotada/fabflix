package Parsing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class CastsParser {
    private String file_path;
    private InconsistencyReporter reporter;

    CastsParser(String file_path, InconsistencyReporter reporter) {
        this.file_path = file_path;
        this.reporter = reporter;
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

    public List<ActorMoviePair> runParser() throws IOException, ParserConfigurationException, SAXException {
        Document document = parseXmlFile();
        List<ActorMoviePair> pairList = convertDocumentIntoPairs(document);
        return pairList;
    }

    private Document parseXmlFile() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Reader reader = new InputStreamReader(new FileInputStream(file_path), StandardCharsets.ISO_8859_1);
        InputSource inputSource = new InputSource(reader);
        inputSource.setEncoding("ISO-8859-1");
        return documentBuilder.parse(file_path);
    }

    private List<ActorMoviePair> convertDocumentIntoPairs(Document document) {
        Element rootDocumentElement = document.getDocumentElement();
        List<ActorMoviePair> pairList = new ArrayList<>();
        NodeList nodeList = rootDocumentElement.getElementsByTagName("dirfilms");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element dirFilms = (Element) nodeList.item(i);
            pairList.addAll(convertDirfilmsIntoPairs(dirFilms));

        }
        return pairList;
    }


    private List<ActorMoviePair> convertDirfilmsIntoPairs(Element dirFilmsElement) {
        List<ActorMoviePair> pairList = new ArrayList<>();
        List<Element> filmcElements = getDirectChildren(dirFilmsElement, "filmc");
        for (Element filmcElement : filmcElements) {
            List<Element> castElements = getDirectChildren(filmcElement, "m");
            for (Element castElement : castElements) {
                ActorMoviePair pair = convertDomElementToPair(castElement);
                if (pair != null) {
                    pairList.add(pair);
                }
            }
        }
        return pairList;
    }

    private ActorMoviePair convertDomElementToPair(Element element) {
        String actor = getDirectTextValue(element, "a");
        String movieId = getDirectTextValue(element, "f");

        if (actor != null) {
            actor = actor.trim();
        }
        if (movieId != null) {
            movieId = movieId.trim();
        }
        if ((actor == null || actor.isEmpty()) && (movieId == null || movieId.isEmpty())) {
            reporter.report("Cast missing both <a> and <f>");
            return null;
        } else if (movieId == null || movieId.isEmpty()) {
            reporter.report("Cast missing <f> movie id for actor");
            return null;
        } else if (actor == null || actor.isEmpty()) {
            reporter.report("Cast missing <a> actor name for movie_id");
            return null;
        }
        if (actor.equals("sa")) {
            reporter.report("Skipping unknown actor 'sa' for movie_id");
            return null;
        }
        return new ActorMoviePair(actor, movieId);
    }
}
