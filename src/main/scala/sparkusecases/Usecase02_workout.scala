package sparkusecases

import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileStatus 
import org.apache.spark.sql.functions.input_file_name


object Usecase02_workout 
{
  val historypath = "hdfs://localhost:54310/user/hduser/archivepath"
  val hdfspath = "hdfs://localhost:54310/user/hduser/custlandingpath"
  
  
  def main(args:Array[String])=
  {
  
    val spark = SparkSession.builder().appName("Usecase02-SQL")
    .config("spark.sql.warehouse.dir","file:/tmp/warehouse")
    .master("local").enableHiveSupport().getOrCreate()    
    spark.sparkContext.setLogLevel("ERROR")
    
    val path = new Path(hdfspath)
    val fs = path.getFileSystem(spark.sparkContext.hadoopConfiguration)
    
    
    //Read all the files in the custlandingpath
    val filelst =  fs.listStatus(path)
    
    //filelst.foreach(println)
    
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
       println("=====================")
      processdata(filelst.slice(iteration*5, filelst.length))
    }
    
    
    def processdata(files:Array[FileStatus])=
    {
     files.foreach(x => println(x.getPath().getName))
     
     //Load all files data into dataframe and write dataframe into mysql
     Loaddataintomysql(files)    
     
     //Move all files into archive folder
     movefiletoarchive(files)
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
    
    def movefiletoarchive(files:Array[FileStatus])=
    {
      for(file <- files)
       {
         val srcPath=file.getPath().toString()
         val destPath= new Path(historypath + "/" + file.getPath().getName.toString())
         fs.rename(file.getPath(),destPath)
       }
    } 
    
    
    
    
  }
   
  
}
