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
        // Scanner input = new Scanner(System.in);
        // System.out.println("Enter mains243 file path: ");
        String main_path = System.getenv("MAIN_PATH");

        // System.out.println("Enter actors63 file path: ");
        String actor_path = System.getenv("ACTOR_PATH");

        // System.out.println("Enter casts124 file path: ");
        String cast_path = System.getenv("CAST_PATH");

        InconsistencyReporter reporter = new InconsistencyReporter();
        MainsParser mainsParser = new MainsParser(main_path, reporter);
        ActorsParser actorsParser = new ActorsParser(actor_path, reporter);
        CastsParser castParser = new CastsParser(cast_path, reporter);

        System.out.println("-----Beginning parse of mains243.xml-----");
        long startTime = System.nanoTime();
        List<Movie> movieList = mainsParser.runParser();
        long endTime = System.nanoTime();
        System.out.println("Parsing movies took: " + (endTime - startTime)/ 1000000 + " ms");

        System.out.println("-----Beginning parse of actors63.xml-----");
        startTime = System.nanoTime();
        List<Actor> actorList = actorsParser.runParser();
        endTime = System.nanoTime();
        System.out.println("Parsing actors took: " + (endTime - startTime)/ 1000000 + " ms");

        System.out.println("-----Beginning parse of casts124.xml-----");
        startTime = System.nanoTime();
        List<ActorMoviePair> castList = castParser.runParser();
        endTime = System.nanoTime();
        System.out.println("Parsing casts took: " + (endTime - startTime)/ 1000000 + " ms");

        System.out.println("Parsed movies: " + movieList.size());
        System.out.println("Parsed actors: " + actorList.size());
        System.out.println("Parsed cast pairs: " + castList.size());


        System.out.println("-----Beginning database insertion-----");
        startTime = System.nanoTime();

        try {
            XMLDataInserter inserter = new XMLDataInserter(reporter);
            inserter.insertData(movieList, actorList, castList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        endTime = System.nanoTime();
        System.out.println("Database insertion took: " + (endTime - startTime) / 1000000 + " ms");

        for (int i = 0; i < mainsParser.getGenre_changes(); i++)
        {reporter.report("Genre names changed for consistency");}

        try {
            reporter.writeReport("src/Parsing/inconsistency_report.txt");
            System.out.println("Inconsistency report written to src/Parsing/inconsistency_report.txt");
        } catch (Exception e) {
            System.err.println("Failed to write inconsistency report.");
            e.printStackTrace();
        }
    }


}
