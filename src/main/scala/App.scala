package org.example

import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.log4j.Logger
import org.apache.log4j.Level

object App {
  def joining(sc: SparkContext): org.apache.spark.rdd.RDD[(String, String, String, String, String, String, String, String, String, String, String)] = {
    val addressLines = sc.textFile("src/main/scala/Addresses.in")
    //val addressLines = sc.textFile("input/Addresses.in")

    val floorPlanLines = sc.textFile("src/main/scala/FloorPlans.in")
    //val floorPlanLines = sc.textFile("input/FloorPlans.in")

    val salesLines = sc.textFile("src/main/scala/Sales.in")
    //val salesLines = sc.textFile("input/Sales.in")

    //regex: ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)" ===> split string ignoring ""

    // format: (houseId,"houseId, fullAddress, street, city, state, zip"])
    val addresses = addressLines
      .map(line => (line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",-1)(0).trim,line))
      .partitionBy(new HashPartitioner(3000)).persist()

    // format: (floorPlanId, [bed, bath, arce_lot, house_size]))
    val floorPlans = floorPlanLines
      .map(line =>(line.split(",")(0).trim, line.split(",",3)(2).split(",",-1)))
      .partitionBy(new HashPartitioner(3000)).persist()

    // format: (houseId, "saleId, houseId, floorPlanId,saleSatus, price, soldDate")
    val sales = salesLines
      .map(line =>(line.split(",")(1).trim,line))
      .partitionBy(new HashPartitioner(3000)).persist()

    //Join Sales.in and Addresses.in
    return sales.join(addresses)
      //reformat output: (floorPlanId, ( "saleId, houseId, floorPlanId,saleSatus, price, soldDate",
      //                                 "houseId, fullAddress, street, city, state, zip"))
      .map(sale => {
        val floorPlanId = sale._2._1.split(",")(2).trim
        (floorPlanId, sale._2)
      })
//Join FloorPlans.in
      .join(floorPlans)
      .map(data => {
        //data = (floorPlanId, (( "saleId, houseId, floorPlanId,saleSatus, price, soldDate",
        //                        "houseId, fullAddress, street, city, state, zip"),
        //                      [bed, bath, arce_lot, house_size]))
        val sale = data._2._1._1//.split(",",4)(3).split(",",-1);
        val address = data._2._1._2
        val ploorPlan = data._2._2

        // address: [houseId, fullAddress, street, city, state, zip]
        val splitAddrLine = address.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)",-1)
        val fullAddress = splitAddrLine(1).trim
        val street = splitAddrLine(2).trim
        val city = splitAddrLine(3).trim
        val state = splitAddrLine(4).trim
        val zip = splitAddrLine(5)

        // sale: [saleId, houseId, floorPlanId,saleSatus, price, soldDate]
        val splitSaleLine = sale.split(",",4)(3).split(",",-1);
        val saleStatus = splitSaleLine(0).trim
        val price = splitSaleLine(1).trim
        val soldDate = splitSaleLine(2).trim
        //val saleid = data._2._1._1.split(",",2)(0).trim

         //floorplan: [bed, bath, arce_lot, house_size]
        val bed = ploorPlan(0).trim
        val bath = ploorPlan(1).trim
        val acreLot = ploorPlan(2).trim
        val houseSize = ploorPlan(3).trim

        // 11-String Tuple
        (saleStatus,price,bed,bath,acreLot,street,city,state,zip,houseSize,soldDate)
      })
      /*the 2 following lines are for testing and debugging
       *.repartition(1) // needed so that the outputs will be written to just 1 file
       *.saveAsTextFile("out")*/
  }
  def filterNonNumeric(data: org.apache.spark.rdd.RDD[(String, String, String, String, String, String, String, String, String, String, String)]): org.apache.spark.rdd.RDD[(String, String, String, String, String)] = {
    data.map({
      case (saleStatus,price,bed,bath,acreLot,street,city,state,zip,houseSize,soldDate)
        => (price, bed, bath, acreLot, houseSize)
    })
  }

  def makePriceRegression(joined: org.apache.spark.rdd.RDD[(String, String, String, String, String)], xname: String): String = {
    val price_ok = joined.filter(row => !row._1.isEmpty)
    val data = xname match {
      case "price"     => price_ok.filter(row => !row._1.isEmpty).map(a => (a._1.toDouble, a._1.toDouble))
      case "bed"       => price_ok.filter(row => !row._2.isEmpty).map(a => (a._1.toDouble, a._2.toDouble))
      case "bath"      => price_ok.filter(row => !row._3.isEmpty).map(a => (a._1.toDouble, a._3.toDouble))
      case "acreLot"   => price_ok.filter(row => !row._4.isEmpty).map(a => (a._1.toDouble, a._4.toDouble))
      case "houseSize" => price_ok.filter(row => !row._5.isEmpty).map(a => (a._1.toDouble, a._5.toDouble))
    }
    data.persist()

    val n = data.map(_ => 1).sum()

    // column averages
    val tmp1 = data
      .map(a => (a._1, 1))
      .reduce({ case (a, b) => (a._1 + b._1, a._2 + b._2) })
    val ymean = 1.0*tmp1._1 / tmp1._2

    val tmp2 = data
      .map(a => (a._2, 1))
      .reduce({ case (a, b) => (a._1 + b._1, a._2 + b._2) })
    val xmean = 1.0*tmp2._1 / tmp2._2

    // cross-deviation sums
    val xycds = data
      .map({ case (y, x) => y * x })
      .reduce({ (x, y) => x + y }) + (n * ymean * xmean)

    val xxcds = data
      .map({ case (_, x) => x * x })
      .reduce({ (x, y) => x + y }) + (n * xmean * xmean)

    // regression coefficients
    var m = xycds / xxcds
    var b = ymean - (m * xmean)

    m = (math rint m * 100) / 100
    b = (math rint b * 100) / 100

    return f"price($xname) ~= $m*$xname + $b"
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("Final_Proj")
      //comment out if running on ambari server
      //.setMaster("local[5]")
    val sc = new SparkContext(conf)

    println("joining data...")
    val joined = joining(sc);
    val filtered = filterNonNumeric(joined)

    filtered.persist()

    println("calculating regression...")

    println(makePriceRegression(filtered, "price"));
    println(makePriceRegression(filtered, "bed"));
    println(makePriceRegression(filtered, "bath"));
    println(makePriceRegression(filtered, "acreLot"));
    println(makePriceRegression(filtered, "houseSize"));
  }
}
