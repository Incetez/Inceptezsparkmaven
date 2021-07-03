package sparkusecases
import java.net.{URL, HttpURLConnection}
import java.util.Properties
import scala.io.Source
import java.io.{File,FileNotFoundException}
import org.apache.log4j.Logger
import java.time.format.DateTimeFormatter
import java.time.LocalDate
object commons {

@throws(classOf[java.io.IOException])
@throws(classOf[java.net.SocketTimeoutException])
def getdatafromurl(url: String,connectTimeout: Int = 5000,readTimeout: Int = 5000,requestMethod: String = "GET") =
{
    import java.net.{URL, HttpURLConnection}
    val connection = (new URL(url)).openConnection.asInstanceOf[HttpURLConnection]
    connection.setConnectTimeout(connectTimeout)
    connection.setReadTimeout(readTimeout)
    connection.setRequestMethod(requestMethod)
    val inputStream = connection.getInputStream
    val content = scala.io.Source.fromInputStream(inputStream).mkString
    if (inputStream != null) inputStream.close
    content
}

def getparaminfo(paramfile:String)=
{
    Source.fromFile(paramfile).getLines().map(_.split(",").toList)
}
def getconfiginfo(confilefile:String)=
{
  
  val file = new File(confilefile);
  println(file.getCanonicalPath)  
  val properties = new Properties()
  if (file.getAbsoluteFile().exists()) 
  {
    
    val source = Source.fromFile(file.getAbsolutePath())
    println(confilefile)
    properties.load(source.bufferedReader())
  }
  else 
  {
    
     throw new FileNotFoundException("Properties file cannot be loaded")
  }
  properties
}

}