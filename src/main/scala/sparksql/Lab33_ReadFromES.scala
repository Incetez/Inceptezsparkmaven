package sparksql
import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark.sql._

object Lab33_ReadFromES {
  
  def main(args:Array[String])=
  {
    val spark = SparkSession.builder()
    .config("spark.es.nodes","localhost") .config("spark.es.port","9200")
    .appName("spark-ES").master("local[*]").getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val df = spark.read
            .format("org.elasticsearch.spark.sql")
            .load("trans/docs")
     
    df.show()       
   
  }
  
}