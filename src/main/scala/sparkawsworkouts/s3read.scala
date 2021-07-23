package sparkawsworkouts

import org.apache.spark.sql._


object s3read
{
  def main(args:Array[String])
  {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("s3read")
      .getOrCreate()
      
      spark.sparkContext.setLogLevel("ERROR")
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.awsAccessKeyId", "XXXXXXXXXXXX")
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.awsSecretAccessKey", "Xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
      spark.sparkContext.hadoopConfiguration.set("com.amazonaws.services.s3.enableV4", "true")
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.aws.credentials.provider","org.apache.hadoop.fs.s3a.BasicAWSCredentialsProvider") 
      spark.sparkContext.hadoopConfiguration.set("fs.s3a.endpoint", "s3.amazonaws.com")
      
      val df = spark.read.option("header","false")
       .option("delimiter", ",")
       .option("inferschema", "true")
       .csv("s3a://inceptezwd25/transdata")
       //.csv("file:/home/hduser/hive/data/custs")
       df.show(false)
  }
       
}