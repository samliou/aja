package org.aja.tej.examples.spark.rdd

import org.aja.tej.utils.TejUtils
import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 12/8/15.
 */

/**
The aggregate-method provides an interface for performing highly customized reductions
and aggregations with a RDD. However, due to the way Scala and Spark execute and
process data, care must be taken to achieve deterministic behavior.

The following list contains a few observations we made while experimenting with aggregate:
• The reduce and combine functions have to be commutative and associative.
• As can be seen from the function definition below, the output of the combiner must
be equal to its input. This is necessary because Spark will chain-execute it.
• The zero value is the initial value of the U component when either seqOp or combOp
are executed for the first element of their domain of influence. Depending on what
you want to achieve, you may have to change it. However, to make your code
deterministic, make sure that your code will yield the same result regardless of the
number or size of partitions.
• Do not assume any execution order for either partition computations or combining
partitions.

• The neutral zeroValue is applied at the beginning of each sequence of reduces
within the individual partitions and again when the output of separate partitions
is combined.

• Why have two separate combine functions? The first functions maps the input
values into the result space. Note that the aggregation data type (1st input and
output) can be different (U != T ). The second function reduces these mapped
values in the result space.
• Why would one want to use two input data types? Let us assume we do an ar-
chaeological site survey using a metal detector. While walking through the site
we take GPS coordinates of important findings based on the output of the metal
detector. Later, we intend to draw an image of a map that highlights these lo-
cations using the aggregate function. In this case the zeroValue could be an area
map with no highlights. The possibly huge set of input data is stored as GPS
coordinates across many partitions. seqOp could convert the GPS coordinates to
map coordinates and put a marker on the map at the respective position. combOp
will receive these highlights as partial maps and combine them into a single final
output map. */

object AggregateExample extends App{
  //Goes into constructor

  def useCases(sc: SparkContext) = {
    println("Aggregate Example usages: ")
    val aggr1 = sc.parallelize ( List (1 ,2 ,3 ,4 ,5 ,6) , 2)
    val res1 = aggr1.aggregate (0) ( math.max (_ , _ ) , _ + _ )
    println(res1) //9

    val aggr2 = sc.parallelize(List("a","b","c","d","e","f") ,2)
    val res2 = aggr2.aggregate ("")( _ + _ , _ + _ )
    println(res2) //abcdef
    val res3 = aggr2.aggregate ("x")( _ + _ , _ + _ )
    println(res3) //xxabcxdef

    val aggr3 = sc.parallelize ( List ("12" ,"23" ,"345" ,"4567") ,2)
    val res4 = aggr3.aggregate("")((x,y) => math.max(x.length , y.length).toString, (x,y)=> x + y)
    println(res4) //42

    val res5 = aggr3.aggregate("")((x,y)=> math.min (x.length, y.length).toString, (x,y) => x + y)
    println(res5) //11

    val aggr4 = sc . parallelize ( List ("12" ,"23" ,"345" ,"") ,2)
    val res6 = aggr4.aggregate("")((x, y) => math.min(x.length,y.length).toString, (x,y)=>x + y)
    println(res6) //10

    //  The main issue with the code above is that the result of the inner min is a string of
    //  length 1. The zero in the output is due to the empty string being the last string in the
    //  list. We see this result because we are not recursively reducing any further within the
    //    partition for the final string.

    val aggr5 = sc . parallelize ( List ("12" ,"23" ,"" ,"345") ,2)
    val res7 = aggr5.aggregate("")((x, y) => math.min(x.length,y.length).toString, (x,y)=>x + y)
    println(res7) //11

    //  In contrast to the previous example, this example has the empty string at the beginning
    //  of the second partition. This results in length of zero being input to the second reduce
    //    which then upgrades it a length of 1. (Warning: The above example shows bad design
    //    since the output is dependent on the order of the data inside the partitions.)
  }

  useCases(TejUtils.getSparkContext(this.getClass.getSimpleName))

}