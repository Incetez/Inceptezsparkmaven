package sparkusecases

import org.apache.spark.sql.SparkSession

object Usecase01 
{
   def main(args:Array[String])=
  {
    //Read from mysql table and write into hive table
    val spark = SparkSession.builder().appName("Usecase01-SQL")    
    .master("local").enableHiveSupport().getOrCreate()
    
    spark.sparkContext.setLogLevel("ERROR")
    
     val df = spark.read.format("jdbc")
    .option("url","jdbc:mysql://localhost/custdb")
    .option("user","root")
    .option("password","Root123$")
    .option("dbtable","tblorders")
    .option("driver","com.mysql.cj.jdbc.Driver").load()
    
    //writ into hive table
    df.write.mode("append").saveAsTable("tblorderdata1")
  }
  
}