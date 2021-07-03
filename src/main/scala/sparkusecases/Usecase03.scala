package sparkusecases

import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileStatus 
import org.apache.spark.sql.functions.input_file_name
import org.apache.log4j.Logger

//SCD2 usecase

object Usecase03
{
  val logger = Logger.getLogger(this.getClass.getName)
  def main(args:Array[String])=
  {
    val spark = SparkSession.builder().appName("Usecase03-SQL")
    .config("hive.metastore.uris","thrift://localhost:9083")
    .master("local").enableHiveSupport().getOrCreate()    
    spark.sparkContext.setLogLevel("ERROR")
    
    val tbl_change = spark.sql("select * from retail.tbl_change")
    
    val tbl_main = spark.sql("select * from retail.tbl_main")
    
    tbl_main.createTempView("emain")
    tbl_change.createTempView("echange")
    val edf = spark.sql("select empid,max(effectivedt) as edate from emain group by empid having count(*) > 1")
    edf.show()
    edf.createTempView("empmdate")
    val edf1 = spark.sql("""select A.*,case when A.effectivedt = B.edate then null else B.edate end as enddate 
                            from emain A left outer join empmdate B on A.empid = B.empid""") 
    edf1.show()
    edf1.createTempView("empfinal")
    val df = spark.sql("""select A.empid,A.name,A.employer,A.effectivedt,A.city,case when A.enddate is null then B.effectivedt else A.enddate end as enddate 
                        from empfinal A left outer join echange B on A.empid = B.empid""")
    val df1 = spark.sql("select *,null as endate from echange")
    val df2 = df.union(df1)
    //val jdf = empmain.join(empchange,empmain.col("empid") === empchange.col("empid"),"leftOuter")
    //val dfs = jdf.select(empmain.col("empid"),empchange.col("empid"))
    df2.show()
  
    
  } 
   
  
}