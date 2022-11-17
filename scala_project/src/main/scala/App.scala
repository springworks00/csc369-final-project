import org.apache.spark.SparkContext._
import scala.io._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.rdd._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import scala.collection._

object App {
  def Q1(sc: SparkContext): Unit = {
    val lines = sc.textFile("src/main/scala/input");
    val nums = lines.flatMap(line => line.split(' '));

    var isFirst = true;

    nums.map(_.trim().toInt)
      .filter(_ % 3 == 0)
      .countByValue()
      .foreach(value => {
        if(isFirst) {
          isFirst = false;
          print(value._1 + " appears " + value._2 + " times");
        }
        else
          print(", " + value._1 + " appears " + value._2 + " times");
      });

  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("NameOfApp").setMaster("local[4]") //don't use setMaster() if running on server
    val sc = new SparkContext(conf)

    Q1(sc);
  }
}
