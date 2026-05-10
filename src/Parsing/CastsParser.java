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

class CastsParser {
    private String file_path;

    CastsParser(String file_path) {
        this.file_path = file_path;
    }

    public List<ActorMoviePair> runParser() throws IOException, ParserConfigurationException, SAXException {
        Document document = parseXmlFile();
        List<ActorMoviePair> pairList = convertDocumentIntoMovies(document);
        return pairList;
    }

    private Document parseXmlFile() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(file_path);
    }

    private List<ActorMoviePair> convertDocumentIntoMovies(Document document) {
        Element rootDocumentElement = document.getDocumentElement();
        List<ActorMoviePair> pairList = new ArrayList<>();
        NodeList nodeList = rootDocumentElement.getElementsByTagName("dirfilms");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element dirFilms = (Element) nodeList.item(i);
            pairList.addAll(convertDirfilmsIntoPairs(dirFilms));

        }
        return pairList;
    }


    private List<ActorMoviePair> convertDirfilmsIntoPairs(Element element)
    {
        List<ActorMoviePair> pairList = new ArrayList<>();
        NodeList filmcList = element.getElementsByTagName("filmc");
        for (int i = 0; i < filmcList.getLength(); i++) {
            Element film = (Element) filmcList.item(i);
            NodeList castList = film.getElementsByTagName("m");
            for (int j = 0; j < castList.getLength(); j++) {
                Element actor = (Element) castList.item(j);
                ActorMoviePair pair = convertDomElementToPair(actor);
                if(pair != null) {
                    pairList.add(pair);
                }
            }
        }
        return pairList;
    }

    private ActorMoviePair convertDomElementToPair(Element element) {
        String actor = getTextValue(element, "a");
        String movie_id = getTextValue(element, "f");

        if(actor == null && movie_id == null) {
            System.err.println("Cast with no data on movie and actor");
            return null;
        }
        else if(movie_id == null) {
            System.err.println("Cast with no movie id for actor " + actor);
            return null;
        }
        else if(actor == null) {
            System.err.println("Cast with no actor name for movie " + movie_id);
            return null;
        }

        if(actor.equals("sa")){return null;}

        return new ActorMoviePair(actor, movie_id);
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

    public void printPairData(List<ActorMoviePair> pairList) {
        System.out.println("Total parsed " + pairList.size() + " employees");
        for (ActorMoviePair pair : pairList) {
            System.out.println("\t" + pair.toString());
        }
    }
}
