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

    void runExample() throws IOException, ParserConfigurationException, SAXException {
        Document document = parseXmlFile();
        List<Movie> movieList = convertDocumentIntoMovies(document);
        printEmployeeData(movieList);
    }

    private Document parseXmlFile() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // Create an object that can build a DOM from an XML file
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        // Parses the XML file and creates a DOM
        return documentBuilder.parse(file_path);
    }

    private List<Movie> convertDocumentIntoMovies(Document document) {
        Element rootDocumentElement = document.getDocumentElement();
        List<Movie> movieList = new ArrayList<>();
        // get a nodelist of employee Elements, parse each into Employee object
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
        }
        return movieList;
    }

    private Movie convertDomElementToMovie(Element element) {
        String name = getTextValue(element, "Name");
        String type = element.getAttribute("type");
        int id = getIntValue(element, "Id");
        int age = getIntValue(element, "Age");
        return new Movie();
    }

    private String getTextValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            // here we expect only one <Name> would present in the <Employee>
            return nodeList.item(0).getFirstChild().getNodeValue();
        }
        return null;
    }

    private int getIntValue(Element ele, String tagName) {
        return Integer.parseInt(getTextValue(ele, tagName));
    }

    private void printEmployeeData(List<Movie> employeeList) {
        System.out.println("Total parsed " + employeeList.size() + " employees");
        for (Movie employee : employeeList) {
            System.out.println("\t" + employee.toString());
        }
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        MainsParser domParserExample = new MainsParser();
        domParserExample.runExample();
    }
}
