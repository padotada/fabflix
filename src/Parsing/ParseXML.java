package Parsing;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Scanner;

public class ParseXML {


    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter mains243 file path: ");
        String main_path = input.nextLine();

        System.out.println("Enter casts124 file path: ");
        String cast_path = input.nextLine();

        System.out.println("Enter actors63 file path: ");
        String actor_path = input.nextLine();

        MainsParser domParser = new MainsParser(main_path);
        ActorsParser actorsParser = new ActorsParser(actor_path);
        CastsParser castParser = new CastsParser(cast_path);


    }
}
