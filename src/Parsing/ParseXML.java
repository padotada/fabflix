package Parsing;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;

public class ParseXML {


    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter mains243 file path: ");
        String main_path = input.nextLine();

        System.out.println("Enter actors63 file path: ");
        String actor_path = input.nextLine();

        System.out.println("Enter casts124 file path: ");
        String cast_path = input.nextLine();


        MainsParser domParser = new MainsParser(main_path);
        ActorsParser actorsParser = new ActorsParser(actor_path);
        CastsParser castParser = new CastsParser(cast_path);

        File inconsistency_report = new File( "src/Parsing/inconsistency_report.txt");
        PrintStream printStream = new PrintStream(new FileOutputStream(inconsistency_report, false));
        System.setErr(printStream);

        System.err.println("-----Beginning parse of mains243.xml-----");


        System.err.println("-----Beginning parse of actors63.xml-----");


        System.err.println("-----Beginning parse of casts124.xml-----");
        List<ActorMoviePair> castList = castParser.runParser();
    }
}
