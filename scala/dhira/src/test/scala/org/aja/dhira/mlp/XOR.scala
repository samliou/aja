package org.aja.dhira.mlp

import org.aja.dhira.core.LabeledPoint
import org.aja.dhira.core.Types.{DblMatrix, DoubleList}
import org.aja.dhira.utils.DisplayUtils
import org.apache.log4j.Logger

import scala.util.{Failure, Random, Success, Try}

/**
 * Backup
 import org.aja.dhira.core.LabeledPoint
import org.aja.dhira.core.Types.{DblMatrix, DoubleList}
import org.aja.dhira.utils.DisplayUtils
import org.apache.log4j.Logger

import scala.util.{Random, Try, Success, Failure}

 */
/**
 * Created by mdhandapani on 27/7/15.
 */
object XOR {
  private val ALPHA = 0.85
  private val ETA = 0.01
    val SIZE_HIDDEN_LAYER = 2
    val TEST_SIZE = 30
    val NUM_EPOCHS = 2500
    val NOISE_RATIO = 0.7
    val EPS = 1e-4

    val name: String = "ConfigTest"
    protected lazy val logger = Logger.getLogger(this.name)

    def main(args: Array[String]): Unit = {

      val inputs = LabeledPoint[DoubleList](
        Array(
        Array(0.0,0.0), Array(0.0,1.0), Array(1.0,0.0), Array(1.0,1.0)
      ))
      val outputs = LabeledPoint[DoubleList](
        Array(
          Array(0.0), Array(1.0), Array(1.0), Array(0.0)
        ))

      val features: LabeledPoint[DoubleList] = LabeledPoint(inputs)
      val labels = LabeledPoint(outputs).toArray

      println(s"features :\n")
      features.toArray.map(_.mkString("\t")).foreach(println)

      println(s"labels: \n")
      labels.toArray.map(_.mkString("\t")).foreach(println)

      eval(ALPHA, ETA, features, labels)
      println("Test completed!")
    }


  private def eval(
                    alpha: Double,
                    eta: Double,
                    features: LabeledPoint[DoubleList],
                    labels: DblMatrix): Int =
    _eval(alpha, eta, features, labels)

  private def _eval(alpha: Double, eta: Double, features: DblMatrix, labels: DblMatrix): Int = {
    implicit val mlpObjective = new MLPMain.MLPBinClassifier
    Try {
      println(s"labels size: ")
      println(labels.size)

//      (0.001 until 0.01 by 0.002).foreach(x => {
//        val _alpha = if (alpha < 0.0) x else ALPHA
//        val _eta = if (eta < 0.0) x else ETA
        val config = Config(ALPHA, ETA, Array[Int](SIZE_HIDDEN_LAYER), NUM_EPOCHS, EPS)

        val mlp = MLPMain[Double](config, features, labels)
        println("Asserting the model")
        assert(mlp.model != None,
          s"$name run failed for eta = $eta and alpha = $alpha")
        DisplayUtils.show(s"$name run for eta = $eta and alpha = $alpha", logger)
        //DisplayUtils.show(s"$name run for eta = $eta and alpha = $alpha ${mlp.model.get.toString}", logger)

        Try {
          val predictedValue = mlp.model.get.getOutput(Array(1.0, 1.0))
          println(s"Lets see what our model says about (0,1): ")
          predictedValue.drop(1).foreach(println)
        } match {
          case Success(y) => y
          case Failure(e) => {println("Can't predict"); Array.empty[Double] }
        }
     // })
      1
    } match {
      case Success(n) => n
      case Failure(e) => DisplayUtils.error(s"$name run", logger, e)
    }
  }

}


object XOR_old {

  val ALPHA = 0.9
  val ETA = 0.1
  val SIZE_HIDDEN_LAYER = 5
  val TEST_SIZE = 30
  val NUM_EPOCHS = 250
  val NOISE_RATIO = 0.7
  val EPS = 1e-4

  val name: String = "ConfigTest"
  protected lazy val logger = Logger.getLogger(this.name)

  def main(args: Array[String]) {

    val noise = () => NOISE_RATIO*Random.nextDouble
    val f1 = (x: Double) => x*(1.0 + noise())
    val f2 = (x: Double) => x*x*(1.0 + noise())

    def vec1(x: Double): DoubleList = Array[Double](f1(x), noise(), f2(x), noise())
    def vec2(x: Double): DoubleList = Array[Double](noise(), noise())

    val x = LabeledPoint[DoubleList](Array.tabulate(TEST_SIZE)(vec1(_)))
    val y = LabeledPoint[DoubleList](Array.tabulate(TEST_SIZE)(vec2(_) ))

    val features: LabeledPoint[DoubleList] = LabeledPoint.normalize(x).get
    val labels = LabeledPoint.normalize(y).get.toArray


    println(s"features :")
    features.toArray.map(_.mkString("\t")).foreach(println)

    println(s"labels")
    labels.toArray.map(_.mkString("\t")).foreach(println)

    eval(-1.0, ETA, features, labels)

    println("Test completed!")

  }

  private def eval(
                    alpha: Double,
                    eta: Double,
                    features: LabeledPoint[DoubleList],
                    labels: DblMatrix): Int =
    _eval(alpha, eta, features, labels)

  private def _eval(alpha: Double, eta: Double, features: DblMatrix, labels: DblMatrix): Int = {
    implicit val mlpObjective = new MLPMain.MLPBinClassifier

    Try {
      println(s"labels size: ")
      println(labels.size)
//      val arr = features.toArray
//      println(arr.map(_.mkString(" ")).mkString("\n"))
//
//      val array = features.toArray
//      println(array.map(_.mkString(" ")).mkString("\n"))

      (0.001 until 0.01 by 0.002).foreach( x =>  {
        val _alpha = if(alpha < 0.0)  x else ALPHA
        val _eta = if(eta < 0.0) x else ETA
        val config = Config(_alpha, _eta, Array[Int](SIZE_HIDDEN_LAYER), NUM_EPOCHS, EPS)

        val mlp = MLPMain[Double](config, features, labels)
        println("Asserting the model")
        assert( mlp.model != None,
          s"$name run failed for eta = $eta and alpha = $alpha")
        DisplayUtils.show(s"$name run for eta = $eta and alpha = $alpha ${mlp.model.get.toString}",
          logger)
      })
      1
    } match {
      case Success(n) => n
      case Failure(e) => DisplayUtils.error(s"$name run", logger, e)
    }
  }
}