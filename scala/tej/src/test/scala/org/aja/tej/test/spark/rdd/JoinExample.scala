package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 join[Pair]
Performs an inner join using two key-value RDDs. Please note that the keys must be
generally comparable to make this work.

 */
object JoinExample {

  def useCases(sc: SparkContext) = {

    val a = sc . parallelize ( List (" dog " ," salmon " ," salmon " , " rat " , " elephant") , 3)
    val b = a . keyBy ( _.length )

    val c = sc . parallelize ( List (" dog " ," cat " ," gnu " ," salmon " ," rabbit " ," turkey " ," wolf " ," bear " ," bee ") , 3)
    val d = c . keyBy ( _ . length )

    b . join ( d ) . collect
  }
}
