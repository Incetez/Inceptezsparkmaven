package sparkusecases
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration
import java.net.{URL, HttpURLConnection}
import java.util.Properties
import scala.io.Source
import java.io.{File,FileNotFoundException}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Column
import org.apache.spark.sql.DataFrame

object Usecase04_workout 
{
  def main(args:Array[String])=
  {
    try 
      {
        if(args.length >= 2)
        {
          val spark = SparkSession.builder().appName("Usecase03-SQL")
          .master("local").getOrCreate()    
          spark.sparkContext.setLogLevel("ERROR")
          //filfile:/home/hduser/weatherdataprocess/application.properties 
          val confile = args(0).trim()
          
          //file:/home/hduser/weatherdataprocess/cityinfo 
          val paramfile = args(1).trim()
          
          //List[Array(1,Agartala,Tripura,23.50,91.16),Array(2,Agra,Uttar Pradesh,27.11,78.01)...]
          val pinfo = commons.getparaminfo(paramfile).toList
          
          
          val prop = commons.getconfiginfo(confile)
          
          //http://api.openweathermap.org/data/2.5/weather?lat=[lat]&lon=[lon]&appid=[apikey]
          val rawhistoryurl = prop.getProperty("historyapi")
          
          
          val historyurllist = pinfo.map(param =>
            {
              rawhistoryurl.replace("[lat]", param(3)).replace("[lon]", param(4)).replace("[apikey]",prop.getProperty("apikey"))
            })
            
            historyurllist.foreach(println)
         
          val rdd = spark.sparkContext.parallelize(historyurllist)
          
          val rddjsondata = rdd.map(url => 
            {
            val data = commons.getdatafromurl(url)
            Thread.sleep(2000)
            data            
            })
            
        import spark.implicits._
        val df = spark.read.json(rddjsondata.toDS())
        df.printSchema
        df.show()
        
        val df1 = df.select("base","clouds.all","cod","coord.lat","coord.lon","dt","id","main.feels_like","main.grnd_level")
            
        }
        else
        {
          println("cityinfo and application need to pass as arguments")
        }
      }
         catch 
        {
        case ioe: java.io.IOException =>  
          {
            println("URL not found exception")
          }
        case ste: java.net.SocketTimeoutException => 
          {
            
            println("Connection timeout")
          }
        case fnf: FileNotFoundException =>
          {
           
            println("File not found error")
          }
        case ex: IllegalArgumentException =>
          {
            println(ex)
          }
      }
  }
  
}