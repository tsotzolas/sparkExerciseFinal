package rdf.joins;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.sql.AnalysisException;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;

/**
 * Created by tsotzo on 15/5/2017.
 */
public class RdfTesting {
    /**
     *
     * @param predicate1
     */
    private static String inputCSVPath = "/usr/lib/spark/bin/RDF/RDF/";
    private static String outputParquetPath = "hdfs://master:8020/test/temp11/";

    public static void testParquet(String predicate1,  SparkSession sparkSession) throws IOException {
        //The predicate will tell us the file that we must take
        //Φορτώνουμε το κάθε αρχείο σε ένα Dataset

        Dataset<Row> df1 = sparkSession.read().csv( inputCSVPath+ predicate1 + ".csv");
        Dataset<Row> parquet = sparkSession.read()
                .parquet("/home/user/test/p.parquet");

//        df1.write().parquet("/home/user/test/a");


        parquet.createOrReplaceTempView("tableName1");
        System.out.println("-------------------parquet----------------------------");
//
        sparkSession.sql("SELECT *  FROM tableName1").show();
    }


    public static void outPuttoParquet(String predicate1,  SparkSession sparkSession) throws IOException {
        //Read csv from HDFS
        Dataset<Row> df1 = sparkSession.read().csv("hdfs://master:8020/test/temp11/" + predicate1 + ".csv");

        //Write parquet to HDFS
        df1.write().parquet(outputParquetPath + predicate1);


        // Ορίζουμε το conf των αρχείων Hdfs
        Configuration myConf = new Configuration();

        // Ορίζουμε το path του hdfs
        myConf.set("fs.defaultFS", "hdfs://master:8020");

        FileSystem fs = FileSystem.get(myConf);
        FileStatus afs[] = fs.listStatus(new Path(outputParquetPath+predicate1));
        for (FileStatus aFile : afs) {
//            if (aFile.isDir()) {
//                fs.delete(aFile.getPath(), true);
//                // delete all directories and sub-directories (if any) in the output directory
//            } else {
            //Σβήνουμε το _SUCCESS αρχείο
                if (aFile.getPath().getName().contains("_SUCCESS")) {
                    System.out.println("-------------------delete----------------------------");
                    fs.delete(aFile.getPath(), true);
                }
                    // Μετονομάσουμε το αρχείο part-00000... σε δικό μας όνομα
                else if ((aFile.getPath().getName().contains("part-00000"))) {
                    System.out.println("-------------------rename----------------------------");
                    fs.rename(aFile.getPath(), new Path(outputParquetPath+predicate1 +"/"+predicate1 + ".parquet"));
                }else
                    System.out.println("------------------Nothing---------------");

            }


        Dataset<Row> sqlDF =sparkSession.sql("SELECT * FROM parquet.`"+outputParquetPath+predicate1+"/"+predicate1 + ".parquet"+"`");
        sqlDF.show();


        }

}
