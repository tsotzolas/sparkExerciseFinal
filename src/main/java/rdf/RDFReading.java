package rdf;

import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.SparkSession;
import rdf.basicGraphPatterns.FindBasicGraphPatterns;
import rdf.joins.RdfJoins;
import rdf.joins.RdfJoinsSimpleText;
import utils.RdfTrasformation;

import java.io.IOException;

import static utils.ReadPropertiesFile.readConfigProperty;
import static utils.ReadPropertiesFile.readRunProperty;

/**
 * Created by tsotzolas on 02/05/2017.
 * Ειναι η main κλαση για να μπορεί να τρέχει στο Spark
 * Όλες οι ρυθμίσεις γίνονται σε συνεργασίας με τα αρχεία config.properties και run.properties
 * Για να το τρέξουμε σε ένα Spark Cluster
 * spark-submit --class rdf.RDFReading  --master spark://<master's_ip>:<master's_port>  sparkExerciseFinal-1.0-SNAPSHOT.jar
 * spark-submit --class rdf.RDFReading  --master spark://10.0.0.5:7077  sparkExerciseFinal-1.0-SNAPSHOT.jar
 */
public class RDFReading {
    private static SparkSession sparkSession;

    public static void main(String[] args) throws IOException, AnalysisException {


        //Create the Spark session
        sparkSession = SparkSession.builder().appName("RDFreader").getOrCreate();

        //Transformations
        if ("true".equals(readRunProperty("parceTxtToVP"))) {
            RdfTrasformation.parceTxtToVP();
        }

        if ("true".equals(readRunProperty("tranformCSVtoParquet"))) {
            RdfTrasformation.tranformCSVtoParquet(readConfigProperty("CSVFileName"), readConfigProperty("inputCSVPath"), readConfigProperty("outputParquetPath"), sparkSession);
        }


        //Basic Graph Patterns
        if ("true".equals(readRunProperty("findObject"))) {
            //s1 p1 ?o
            FindBasicGraphPatterns.findObject("<http://data.bgs.ac.uk/id/Geochronology/DivisionList/CAA>", "0", sparkSession, readConfigProperty("BGPInputFileType"));
        }
        if ("true".equals(readRunProperty("findSubject"))) {
            //?s p1 o1
            FindBasicGraphPatterns.findSubject("<http://www.w3.org/2004/02/skos/core#OrderedCollection>", "0", sparkSession, readConfigProperty("BGPInputFileType"));
        }

        if ("true".equals(readRunProperty("findSubjectObject"))) {

            //?s p1 ?o
            FindBasicGraphPatterns.findSubjectObject("0", sparkSession, readConfigProperty("BGPInputFileType"));
        }


        //Joins with Vertical Partitioning
        if ("true".equals(readRunProperty("findSubjectSubjectJoin"))) {

            //?s p1 o1
            //?s p2 o2
            //Πρέπει να βγάλει a
            RdfJoins.findSubjectSubjectJoin(readConfigProperty("predicate1SS")
                    , readConfigProperty("predicate2SS")
                    , readConfigProperty("object1SS")
                    , readConfigProperty("object2SS")
                    , sparkSession
                    , readConfigProperty("joinInputFileType"));
        }

        if ("true".equals(readRunProperty("findObjectObjectJoin"))) {
            //s1 p1 ?o
            //s2 p1 ?o
            //Πρέπει να βγάλει a
            RdfJoins.findObjectObjectJoin(readConfigProperty("predicate1OO")
                    , readConfigProperty("predicate2OO")
                    , readConfigProperty("subject1OO")
                    , readConfigProperty("subject2OO")
                    , sparkSession
                    , readConfigProperty("joinInputFileType"));

        }

        if ("true".equals(readRunProperty("findObjectSubjectJoin"))) {
            //s1 p1 ?o
            //?s p2 o2
            //Πρέπει να βγάλει c
            RdfJoins.findObjectSubjectJoin(readConfigProperty("predicate1OS")
                    , readConfigProperty("predicate2OS")
                    , readConfigProperty("subject1OS")
                    , readConfigProperty("object2OS")
                    , sparkSession, readConfigProperty("joinInputFileType"));
        }


        if ("true".equals(readRunProperty("findSubjectObjectJoin"))) {
            //?s p2 o2
            //s1 p1 ?o
        RdfJoins.findSubjectObjectJoin(readConfigProperty("predicate1SO")
                ,readConfigProperty("predicate2SO")
                ,readConfigProperty("object1SO")
                ,readConfigProperty("subject2SO")
                ,sparkSession
                ,readConfigProperty("joinInputFileType"));
        }

        //Joins with Simple Text Files

        if ("true".equals(readRunProperty("findSubjectSubjectJoinSimpleText"))) {

            //?s p1 o1
            //?s p2 o2

            RdfJoinsSimpleText.findSubjectSubjectJoin(readConfigProperty("file1SSSimpleText")
                    , readConfigProperty("file2SSSimpleText")
                    , readConfigProperty("predicate1SSSimpleText")
                    , readConfigProperty("predicate2SSSimpleText")
                    , readConfigProperty("object1SSSimpleText")
                    , readConfigProperty("object2SSSimpleText")
                    , sparkSession
                    , readConfigProperty("joinInputFileTypeSimpleText"));
        }

        if ("true".equals(readRunProperty("findObjectObjectJoinSimpleText"))) {
            //s1 p1 ?o
            //s2 p1 ?o
            //Πρέπει να βγάλει
            RdfJoinsSimpleText.findObjectObjectJoin(readConfigProperty("file100SimpleText")
                    , readConfigProperty("file200SimpleText")
                    , readConfigProperty("predicate1OOSimpleText")
                    , readConfigProperty("predicate2OOSimpleText")
                    , readConfigProperty("subject1OOSimpleText")
                    , readConfigProperty("subject2OOSimpleText")
                    , sparkSession
                    , readConfigProperty("joinInputFileTypeSimpleText"));

        }

        if ("true".equals(readRunProperty("findObjectSubjectJoinSimpleText"))) {
            //s1 p1 ?o
            //?s p2 o2
            //Πρέπει να βγάλει
            RdfJoinsSimpleText.findObjectSubjectJoin(readConfigProperty("file1OSSimpleText")
                    , readConfigProperty("file2OSSimpleText")
                    , readConfigProperty("predicate1OSSimpleText")
                    , readConfigProperty("predicate2OSSimpleText")
                    , readConfigProperty("subject1OSSimpleText")
                    , readConfigProperty("object2OSSimpleText")
                    , sparkSession, readConfigProperty("joinInputFileTypeSimpleText"));
        }


        if ("true".equals(readRunProperty("findSubjectObjectSimpleText"))) {
            //?s p2 o2
            //s1 p1 ?o
            RdfJoinsSimpleText.findSubjectObjectJoin(readConfigProperty("file1SOSimpleText")
                    ,readConfigProperty("file2SOSimpleText")
                    ,readConfigProperty("predicate1SOSimpleText")
                    ,readConfigProperty("predicate2SOSimpleText")
                    , readConfigProperty("object1SOSimpleText")
                    , readConfigProperty("subject2SOSimpleText")
                    ,sparkSession
                    ,readConfigProperty("joinInputFileTypeSimpleText"));
        }
    }

}
