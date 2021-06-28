package sparkusecases

import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileStatus 
import org.apache.spark.sql.functions.input_file_name


object Usecase02 
{
  val historypath = "hdfs://localhost:54310/user/hduser/archivepath"
  val hdfspath = "hdfs://localhost:54310/user/hduser/custlandingpath"
  val path = new Path(s"$hdfspath")
  
  def main(args:Array[String])=
  {
   /*
    1. Read files from the hdfs path and check the files count
    2. If the files count > 5 then iterate and process 5 files for each iteration 
    3. Load 5 files into the dataframe and get the filename as an additional columns
    4. Write the data into the mysql database
    5. Move the files to the history folder    
    */
    
    
     
    val spark = SparkSession.builder().appName("Usecase02-SQL")
    .config("spark.sql.warehouse.dir","file:/tmp/warehouse")
    .master("local").enableHiveSupport().getOrCreate()    
    spark.sparkContext.setLogLevel("ERROR")
    
    val fs = path.getFileSystem(spark.sparkContext.hadoopConfiguration)
    
    
    val filelst =  fs.listStatus( new Path(hdfspath))
    
    val iteration = filelst.length / 5
    
    val lastset =  filelst.length % 5
    
    for( i <- 0 until iteration)
    {
      println("=====================")
      val arr = filelst.slice(i*5, (i+1)*5)
      processdata(arr)
    }
    if(lastset > 0)
    {
      
      processdata(filelst.slice(iteration*5 - 1, filelst.length))
    }
    
    def processdata(files:Array[FileStatus])=
    {
     files.foreach(x => println(x.getPath().getName))
     Loaddataintomysql(files)
     movefiletoarchive(files)
    }
   
    def movefiletoarchive(files:Array[FileStatus])=
    {
      for(file <- files)
       {
         val srcPath=file.getPath().toString()
         val destPath= new Path(historypath + "/" + file.getPath().getName.toString())
         fs.rename(file.getPath(),destPath)
       }
    }
    def Loaddataintomysql(files:Array[FileStatus])=
    {
      val filenames = files.map(x => x.getPath().toString())
      
      val df = spark.read.format("csv").load(filenames:_*).toDF("Custid","FName","LName","Age","Profession")
      val df1 = df.withColumn("filename", input_file_name)
      
      //write into mysql
      df1.write.format("jdbc").option("url","jdbc:mysql://localhost/custdb")
      .mode("append")
      .option("user","root")
      .option("password","Root123$")
      .option("dbtable","tblcustomerbatch")
      .option("driver","com.mysql.cj.jdbc.Driver").save()
    }
  } 
   
  
}