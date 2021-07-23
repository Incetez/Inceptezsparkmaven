package sparksql
import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark.sql._
import org.apache.spark.sql.functions._

object Lab32_WritetoES {
  
  def main(args:Array[String])=
  {
    val spark = SparkSession.builder()
    .config("spark.es.nodes","localhost") .config("spark.es.port","9200")
    .appName("spark-ES").master("local[*]").getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val df = spark.read
            .format("csv")
            .option("delimiter", "\t")
            .option("header",true)
            .option("InferSchema",true)
            .load("file:/home/hduser/Inceptezbatchwd27.tsv")
   val df1 = df.select(col("id"),col("name"),col("mailid"),col("mobile"),col("yrsofexp"),trim(col("curtechnology")).alias("curtechnology"),trim(col("technology")).alias("technology"),trim(col("curdomain")).alias("curdomain"),trim(col("domain")).alias("domain"),trim(col("curreason")).alias("curreason"),trim(col("reason")).alias("reason"))
   df1.saveToEs("inceptezwd27/docs",Map("es.mapping.id"->"id"))  
   
   //df.show(100)         
   println("Written into ES")
   
  }
  
}