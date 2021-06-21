package structurestreaming
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object Lab06_file_sink
{
  def main(args:Array[String])=
  {
    val spark = SparkSession.builder().appName("Lab06-structstream").master("local").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    
     
    val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "transtopic")
    .option("group.id", "grptest")
    .load().select(col("value").cast("string"))
    
    val df1 =  df.select(split(col("value"), ",").alias("stud"))
    
    val df2 = df1.select(col("stud").getItem(0).alias("Studid"),col("stud").getItem(1).alias("StudName"),col("stud").getItem(2).alias("Mark1"),
        col("stud").getItem(3).alias("Mark2"),col("stud").getItem(4).alias("Mark3"))
    
    val df3 = df2.withColumn("current_dt", current_date())
    
    val df4 = df3.withColumn("TotalMarks", col("Mark1") + col("Mark2") + col("Mark3") )
    
    
    //file sink
    df4.writeStream.format("csv")
    .option("path", "file:/home/hduser/sparkstreamout")
     .option("checkpointLocation", "file:/tmp/sparkchkpoint")
    .outputMode("append") //For file sink, it supports only append
    .start()
    .awaitTermination()
    
  }
}

/*
  
 Read from Input sources -> Transformation -> Write into Sink 
 
Input Sources:
=============

Rate (for Testing): It will automatically generate data including 2 columns timestamp and value . This is generally used for testing purposes. 
Socket: This data source will listen to the specified socket and ingest any data into Spark Streaming.
File: This will listen to a particular directory as streaming data. It supports file formats like CSV, JSON, ORC, and Parquet.
Kafka: This will read data from Apache Kafka® and is compatible with Kafka broker versions 0.10.0 or higher 


Sink Types:
========== 

Console sink: Displays the content of the DataFrame to console
File sink: Stores the contents of a DataFrame in a file within a directory. Supported file formats are csv, json, orc, and parquet.
Kafka sink: Publishes data to a Kafka topic
Foreachbatch: allows you to specify a function that is executed on the output data of every micro-batch of the streaming query.
  
*/
