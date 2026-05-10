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
    private Integer getNullableIntValue(Element element, String tagName, String context){
        String text = getTextValue(element, tagName);
        if(text == null || text.trim().isEmpty()){
            return null;
        }
        text = text.trim();
        try{
            return Integer.parseInt(text);
        }catch(NumberFormatException e){
            System.err.println("Invalid Integer for <" + tagName+">: value="+
                    text+", context="+context);
            return null;
        }
    }
    private Actor convertDomElementToActor(Element element) {
        String name = getTextValue(element, "stagename");
        if(name == null || name.trim().isEmpty()){
            System.err.println("Actor missing <stagename>");
            return null;
        }
        Integer dob = getNullableIntValue(element, "dob", "actor=" + name);

        return new Actor(name.trim(), dob);
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

//    private int getIntValue(Element ele, String tagName) {
//        return Integer.parseInt(getTextValue(ele, tagName));
//    }

    private void printEmployeeData(List<Actor> employeeList) {

    }
}
