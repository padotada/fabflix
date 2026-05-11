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
import java.nio.charset.StandardCharsets;
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

    private Document parseXmlFile() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Reader reader = new InputStreamReader(new FileInputStream(file_path), StandardCharsets.ISO_8859_1);
        InputSource inputSource = new InputSource(reader);
        inputSource.setEncoding("ISO-8859-1");
        return documentBuilder.parse(file_path);
    }

    private List<Actor> convertDocumentIntoActors(Document document) {
        Element rootDocumentElement = document.getDocumentElement();
        List<Actor> actorList = new ArrayList<>();
        NodeList nodeList = rootDocumentElement.getElementsByTagName("actor");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            Actor actor = convertDomElementToActor(element);
            if(actor != null) actorList.add(actor);
        }
        return actorList;
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
    private Actor convertDomElementToActor(Element element) {
        String name = getTextValue(element, "stagename");
        if(name == null || name.trim().isEmpty()){
            System.err.println("Actor missing <stagename>");
            return null;
        }
        String dobText = getDirectTextValue(element, "dob");
        Integer dob = parseIntegerOrNull(dobText);

        return new Actor(name.trim(), dob);
    }

    private String getTextValue(Element element, String tagName) {
//        NodeList nodeList = element.getElementsByTagName(tagName);
//        if (nodeList.getLength() == 1) {
//            // here we expect only one <Name> would present in the <Employee>
//            if(nodeList.item(0).getFirstChild() == null) {return null;}
//            return nodeList.item(0).getFirstChild().getNodeValue();
//        }
//        return null;
        return getDirectTextValue(element, tagName);
    }

//    private int getIntValue(Element ele, String tagName) {
//        return Integer.parseInt(getTextValue(ele, tagName));
//    }

    private void printEmployeeData(List<Actor> employeeList) {

    }
}
