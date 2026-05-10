package Parsing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MainsParser {
    private String file_path;

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
        List<Movie> movieList = new ArrayList<>();
        NodeList nodeList = element.getElementsByTagName("film");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element film = (Element) nodeList.item(i);
            movieList.add(convertDomElementToMovie(film));
        }
        return movieList;
    }

    private Movie convertDomElementToMovie(Element element) {
        String id = getTextValue(element, "fid");

        return new Movie();
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

    }
}
