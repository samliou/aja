package org.aja.tej.test.spark.rdd

import org.apache.spark.SparkContext

/**
 * Created by mageswaran on 15/8/15.
 */

/*
 pipe
Takes the RDD data of each partition and sends it via stdin to a shell-command. The
resulting output of the command is captured and returned as a RDD of string values.

 */
object PipeExample {

  def useCases(sc: SparkContext) = {
    val a = sc . parallelize (1 to 9 , 3)
    a . pipe (" head -n 1") . collect
  }
}
