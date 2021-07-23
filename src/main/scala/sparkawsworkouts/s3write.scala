package sparkawsworkouts

import org.apache.spark.sql._


object s3write
{
  def main(args:Array[String])
  {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("s3write")
      .getOrCreate()
      
      spark.sparkContext.setLogLevel("ERROR")
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.awsAccessKeyId", "AKIAWUTLVBQQVTKNDFYJ")
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.awsSecretAccessKey", "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")
      spark.sparkContext.hadoopConfiguration.set("com.amazonaws.services.s3.enableV4", "true")
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.aws.credentials.provider","org.apache.hadoop.fs.s3a.BasicAWSCredentialsProvider") 
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.endpoint", "s3.amazonaws.com")
      
      val df = spark.read.option("header","false")
      .option("delimiter", ",")
      .option("inferschema", "true")
      .csv("file:/home/hduser/hive/data/txns").toDF("txnid","txndate","custid","amount","category","product","city","state","paymenttype")
      
         
     //df.write.mode("append").parquet("s3a://com.izbucket.datasets/trans-data")
      //df.write.mode("append").partitionBy("state").csv("s3a://com.izbucket.datasets/trans-data")
      
      df.write.mode("overwrite").csv("s3a://inceptezwd25/transdata")
      
      println("Written into AWS S3 storage")
     

  }
       
}