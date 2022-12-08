package org.example

import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.rdd._

object App {


  def joining(sc: SparkContext) = {

    val addressLines = sc.textFile("src/main/scala/Addresses.in")
    val floorPlanLines = sc.textFile("src/main/scala/FloorPlans.in")
    val salesLines = sc.textFile("src/main/scala/Sales.in")

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
    sales.join(addresses)
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
        val arceLot = ploorPlan(2).trim
        val houseSize = ploorPlan(3).trim

        //(saleStatus,price,bed,bath,arceLot,fullAddress,street,city,state,zip,houseSize,soldDate)
        (saleStatus+","+price+","+bed+","+bath+","+arceLot+","+fullAddress+","+street+","+city+","+state+","+zip+","+houseSize+","+soldDate).mkString
      })
    .repartition(1) // needed so that the outputs will be written to just 1 file
    .saveAsTextFile("out")
      /*the 2 following lines are for testing and debugging
       *.repartition(1) // needed so that the outputs will be written to just 1 file
       *.saveAsTextFile("out")*/
  }
  
  // The method takes in an RDD of 12 columns and 2 ints. The functions are the placement of the desired columns to filter indexing 1-12, the same indexing as tuples
  // The output is the filtered RDD 
  // if need to increase or decrease RDD size, adjust the input/output format accordingly as well as the match case operations
  def helper(rdd: RDD[(String, String, String, String, String, String, String, String, String, String, String, String)],
             x: Int, y: Int): RDD[(String, String, String, String, String, String, String, String, String, String, String, String)] = {
    var filteredRdd = rdd
    
    // filter based on the first int input. Removes the rows that have an empty value in the x column
    x match {
      case 1 => (filteredRdd = rdd.filter(row => !row._1.isEmpty))
      case 2 => (filteredRdd = rdd.filter(row => !row._2.isEmpty))
      case 3 => (filteredRdd = rdd.filter(row => !row._3.isEmpty))
      case 4 => (filteredRdd = rdd.filter(row => !row._4.isEmpty))
      case 5 => (filteredRdd = rdd.filter(row => !row._5.isEmpty))
      case 6 => (filteredRdd = rdd.filter(row => !row._6.isEmpty))
      case 7 => (filteredRdd = rdd.filter(row => !row._7.isEmpty))
      case 8 => (filteredRdd = rdd.filter(row => !row._8.isEmpty))
      case 9 => (filteredRdd = rdd.filter(row => !row._9.isEmpty))
      case 10 => (filteredRdd = rdd.filter(row => !row._10.isEmpty))
      case 11 => (filteredRdd = rdd.filter(row => !row._11.isEmpty))
      case 12 => (filteredRdd = rdd.filter(row => !row._12.isEmpty))
    }
    
    // filter based on the second int input. Removes the rows that have an empty value in the y column
    y match {
      case 1 => (filteredRdd = filteredRdd.filter(row => !row._1.isEmpty))
      case 2 => (filteredRdd = filteredRdd.filter(row => !row._2.isEmpty))
      case 3 => (filteredRdd = filteredRdd.filter(row => !row._3.isEmpty))
      case 4 => (filteredRdd = filteredRdd.filter(row => !row._4.isEmpty))
      case 5 => (filteredRdd = filteredRdd.filter(row => !row._5.isEmpty))
      case 6 => (filteredRdd = filteredRdd.filter(row => !row._6.isEmpty))
      case 7 => (filteredRdd = filteredRdd.filter(row => !row._7.isEmpty))
      case 8 => (filteredRdd = filteredRdd.filter(row => !row._8.isEmpty))
      case 9 => (filteredRdd = filteredRdd.filter(row => !row._9.isEmpty))
      case 10 => (filteredRdd = filteredRdd.filter(row => !row._10.isEmpty))
      case 11 => (filteredRdd = filteredRdd.filter(row => !row._11.isEmpty))
      case 12 => (filteredRdd = filteredRdd.filter(row => !row._12.isEmpty))
    }


    return filteredRdd;
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("Final_Proj")
      //comment out if running on ambari server
      .setMaster("local[5]")
    val sc = new SparkContext(conf)

    joining(sc);
  }

}
