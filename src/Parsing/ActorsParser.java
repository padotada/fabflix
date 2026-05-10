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

class ActorsParser {
    private String file_path;

    ActorsParser(String file_path) {
        this.file_path = file_path;
    }

    public List<Actor> runParser() throws IOException, ParserConfigurationException, SAXException {
        Document document = parseXmlFile();
        List<Actor> actorList = convertDocumentIntoActors(document);
        return actorList;
    }

    private Document parseXmlFile() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        // Create an object that can build a DOM from an XML file
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        // Parses the XML file and creates a DOM
        return documentBuilder.parse(file_path);
    }

    private List<Actor> convertDocumentIntoActors(Document document) {
        Element rootDocumentElement = document.getDocumentElement();
        List<Actor> actorList = new ArrayList<>();
        NodeList nodeList = rootDocumentElement.getElementsByTagName("actor");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            actorList.add(convertDomElementToActor(element));
        }
        return actorList;
    }

    private Actor convertDomElementToActor(Element element) {
        String name = getTextValue(element, "stagename");
        int dob;
        try {
            dob = getIntValue(element, "dob");
        }
        catch(NumberFormatException e) {
            System.err.println("Invalid dob for actor " + name);
            dob = -1;
        }

        return new Actor(name, dob);
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

    private void printEmployeeData(List<Actor> employeeList) {

    }
}
