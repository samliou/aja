package org.aja.tej.examples.sparksql.dataframe

import org.aja.tej.examples.sparksql.dataframe.DataFrameBasics.CustomerDetails
import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.{SQLContext,Column}
import org.apache.spark.sql.GroupedData
import org.apache.spark.sql.functions._

/**
  * Created by mdhandapani on 6/1/16.
  */
object Aggregation  extends App {

  val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

  val sqlContext = new SQLContext(sc)
  import sqlContext.implicits._ //To convert RDD -> DF

  val sessionDF = Seq(
    ("day1", "user1", "session1", 100.0),
    ("day1", "user1", "session2", 200.0),
    ("day1", "user1", "session3", 300.0),
    ("day1", "user1", "session4", 400.0),
    ("day2", "user1", "session4", 90.0)
  ).toDF("day", "userId", "sessionId", "purchaseTotal")

  val groupedSessions = sessionDF.groupBy("day", "userId", "sessionId")

  val groupedSessionsDF = groupedSessions.agg(countDistinct("sessionId"), sum("purchaseTotal"))

  //val groupedSessionsDF1 = groupedSessions.agg($"day", $"userId", countDistinct("sessionId") -> "count", sum("purchaseTotal") -> "sum")

  groupedSessionsDF.foreach(println)

  ////////////////////////////////////////////////////////

  //Lets us defice the scehme for table using case classes
  //Let us skip the units for purchase
  case class CustomerDetails(id: Long, name: String, state: String, purchaseAmt: Double, discountAmt: Double)

  val customers = Seq(
    CustomerDetails(1, "Mageswaran", "TN", 15000.00, 150),
    CustomerDetails(2, "Michael", "JR", 24000.00, 300),
    CustomerDetails(3, "Antony Leo", "TN", 10000.00, 50),
    CustomerDetails(4, "Arun", "TN", 18000.00, 90),
    CustomerDetails(5, "Venkat", "ANDRA", 5000.00, 0),
    CustomerDetails(6, "Sathis", "TN", 150000.00, 3000)
  )

  val customerDF = sc.parallelize(customers, 4).toDF()

  // groupBy() produces a GroupedData, and you can't do much with
  // one of those other than aggregate it -- you can't even print it

  // basic form of aggregation assigns a function to
  // each non-grouped column -- you map each column you want
  // aggregated to the name of the aggregation function you want
  // to use
  //
  // automatically includes grouping columns in the DataFrame

  println("*** basic form of aggregation")
  customerDF.groupBy("state").agg("discountAmt" -> "max").show()

  // you can turn of grouping columns using the SQL context's
  // configuration properties

  println("*** this time without grouping columns")
  sqlContext.setConf("spark.sql.retainGroupColumns", "false")
  customerDF.groupBy("state").agg("discountAmt" -> "max").show()

  //
  // When you use $"somestring" to refer to column names, you use the
  // very flexible column-based version of aggregation, allowing you to make
  // full use of the DSL defined in org.apache.spark.sql.functions --
  // this version doesn't automatically include the grouping column
  // in the resulting DataFrame, so you have to add it yourself.
  //

  println("*** Column based aggregation")
  // you can use the Column object to specify aggregation
  customerDF.groupBy("state").agg(max($"discountAmt")).show()

  println("*** Column based aggregation plus grouping columns")
  // but this approach will skip the grouped columns if you don't name them
  customerDF.groupBy("state").agg($"state", max($"discountAmt")).show()

  // Think of this as a user-defined aggregation function -- written in terms
  // of more primitive aggregations
  def stddevFunc(c: Column): Column =
    sqrt(avg(c * c) - (avg(c) * avg(c)))

  println("*** Sort-of a user-defined aggregation function")
  customerDF.groupBy("state").agg($"state", stddevFunc($"discountAmt")).show()

  // there are some special short cuts on GroupedData to aggregate
  // all numeric columns
  println("*** Aggregation short cuts")
  customerDF.groupBy("state").count().show()

}


/*

/usr/lib/jvm/java-8-oracle/bin/java -Didea.launcher.port=7533 -Didea.launcher.bin.path=/home/mdhandapani/Desktop/idea-IC-143.1184.17/bin -Dfile.encoding=UTF-8 -classpath /usr/lib/jvm/java-8-oracle/jre/lib/charsets.jar:/usr/lib/jvm/java-8-oracle/jre/lib/deploy.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/cldrdata.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/dnsns.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/jaccess.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/jfxrt.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/localedata.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/nashorn.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/sunec.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/sunjce_provider.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/sunpkcs11.jar:/usr/lib/jvm/java-8-oracle/jre/lib/ext/zipfs.jar:/usr/lib/jvm/java-8-oracle/jre/lib/javaws.jar:/usr/lib/jvm/java-8-oracle/jre/lib/jce.jar:/usr/lib/jvm/java-8-oracle/jre/lib/jfr.jar:/usr/lib/jvm/java-8-oracle/jre/lib/jfxswt.jar:/usr/lib/jvm/java-8-oracle/jre/lib/jsse.jar:/usr/lib/jvm/java-8-oracle/jre/lib/management-agent.jar:/usr/lib/jvm/java-8-oracle/jre/lib/plugin.jar:/usr/lib/jvm/java-8-oracle/jre/lib/resources.jar:/usr/lib/jvm/java-8-oracle/jre/lib/rt.jar:/home/mdhandapani/aja/target/scala-2.11/classes:/home/mdhandapani/.ivy2/cache/antlr/antlr/jars/antlr-2.7.7.jar:/home/mdhandapani/.ivy2/cache/aopalliance/aopalliance/jars/aopalliance-1.0.jar:/home/mdhandapani/.ivy2/cache/asm/asm/jars/asm-3.2.jar:/home/mdhandapani/.ivy2/cache/com.101tec/zkclient/jars/zkclient-0.3.jar:/home/mdhandapani/.ivy2/cache/com.clearspring.analytics/stream/jars/stream-2.7.0.jar:/home/mdhandapani/.ivy2/cache/com.esotericsoftware.kryo/kryo/bundles/kryo-2.21.jar:/home/mdhandapani/.ivy2/cache/com.esotericsoftware.minlog/minlog/jars/minlog-1.2.jar:/home/mdhandapani/.ivy2/cache/com.esotericsoftware.reflectasm/reflectasm/jars/reflectasm-1.07-shaded.jar:/home/mdhandapani/.ivy2/cache/com.fasterxml.jackson.core/jackson-annotations/bundles/jackson-annotations-2.4.4.jar:/home/mdhandapani/.ivy2/cache/com.fasterxml.jackson.core/jackson-core/bundles/jackson-core-2.4.4.jar:/home/mdhandapani/.ivy2/cache/com.fasterxml.jackson.core/jackson-databind/bundles/jackson-databind-2.4.4.jar:/home/mdhandapani/.ivy2/cache/com.fasterxml.jackson.module/jackson-module-scala_2.11/bundles/jackson-module-scala_2.11-2.4.4.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/core/jars/core-1.1.2.jar:/home/mdhandapani/.ivy2/cache/com.github.rwl/jtransforms/jars/jtransforms-2.4.0.jar:/home/mdhandapani/.ivy2/cache/com.google.code.findbugs/jsr305/jars/jsr305-1.3.9.jar:/home/mdhandapani/.ivy2/cache/com.google.code.gson/gson/jars/gson-2.3.jar:/home/mdhandapani/.ivy2/cache/com.google.guava/guava/bundles/guava-14.0.1.jar:/home/mdhandapani/.ivy2/cache/com.google.inject/guice/jars/guice-3.0.jar:/home/mdhandapani/.ivy2/cache/com.google.protobuf/protobuf-java/bundles/protobuf-java-2.5.0.jar:/home/mdhandapani/.ivy2/cache/com.googlecode.javaewah/JavaEWAH/jars/JavaEWAH-0.3.2.jar:/home/mdhandapani/.ivy2/cache/com.jolbox/bonecp/bundles/bonecp-0.8.0.RELEASE.jar:/home/mdhandapani/.ivy2/cache/com.ning/compress-lzf/bundles/compress-lzf-1.0.3.jar:/home/mdhandapani/.ivy2/cache/com.sun.istack/istack-commons-runtime/jars/istack-commons-runtime-2.16.jar:/home/mdhandapani/.ivy2/cache/com.sun.jersey/jersey-core/bundles/jersey-core-1.9.jar:/home/mdhandapani/.ivy2/cache/com.sun.jersey/jersey-json/bundles/jersey-json-1.9.jar:/home/mdhandapani/.ivy2/cache/com.sun.jersey/jersey-server/bundles/jersey-server-1.9.jar:/home/mdhandapani/.ivy2/cache/com.sun.jersey.contribs/jersey-guice/jars/jersey-guice-1.9.jar:/home/mdhandapani/.ivy2/cache/com.sun.jersey.jersey-test-framework/jersey-test-framework-grizzly2/jars/jersey-test-framework-grizzly2-1.9.jar:/home/mdhandapani/.ivy2/cache/com.sun.xml.bind/jaxb-core/jars/jaxb-core-2.2.7.jar:/home/mdhandapani/.ivy2/cache/com.sun.xml.bind/jaxb-impl/jars/jaxb-impl-2.2.7.jar:/home/mdhandapani/.ivy2/cache/com.sun.xml.fastinfoset/FastInfoset/jars/FastInfoset-1.2.12.jar:/home/mdhandapani/.ivy2/cache/com.thoughtworks.paranamer/paranamer/jars/paranamer-2.6.jar:/home/mdhandapani/.ivy2/cache/com.twitter/chill-java/jars/chill-java-0.5.0.jar:/home/mdhandapani/.ivy2/cache/com.twitter/chill_2.11/jars/chill_2.11-0.5.0.jar:/home/mdhandapani/.ivy2/cache/com.twitter/parquet-hadoop-bundle/jars/parquet-hadoop-bundle-1.6.0.jar:/home/mdhandapani/.ivy2/cache/com.typesafe.akka/akka-remote_2.11/jars/akka-remote_2.11-2.3.11.jar:/home/mdhandapani/.ivy2/cache/com.typesafe.akka/akka-slf4j_2.11/jars/akka-slf4j_2.11-2.3.11.jar:/home/mdhandapani/.ivy2/cache/com.yammer.metrics/metrics-core/jars/metrics-core-2.2.0.jar:/home/mdhandapani/.ivy2/cache/commons-beanutils/commons-beanutils/jars/commons-beanutils-1.7.0.jar:/home/mdhandapani/.ivy2/cache/commons-beanutils/commons-beanutils-core/jars/commons-beanutils-core-1.8.0.jar:/home/mdhandapani/.ivy2/cache/commons-cli/commons-cli/jars/commons-cli-1.2.jar:/home/mdhandapani/.ivy2/cache/commons-codec/commons-codec/jars/commons-codec-1.10.jar:/home/mdhandapani/.ivy2/cache/commons-collections/commons-collections/jars/commons-collections-3.2.1.jar:/home/mdhandapani/.ivy2/cache/commons-configuration/commons-configuration/jars/commons-configuration-1.6.jar:/home/mdhandapani/.ivy2/cache/commons-dbcp/commons-dbcp/jars/commons-dbcp-1.4.jar:/home/mdhandapani/.ivy2/cache/commons-digester/commons-digester/jars/commons-digester-1.8.jar:/home/mdhandapani/.ivy2/cache/commons-httpclient/commons-httpclient/jars/commons-httpclient-3.1.jar:/home/mdhandapani/.ivy2/cache/commons-io/commons-io/jars/commons-io-2.4.jar:/home/mdhandapani/.ivy2/cache/commons-lang/commons-lang/jars/commons-lang-2.6.jar:/home/mdhandapani/.ivy2/cache/commons-logging/commons-logging/jars/commons-logging-1.1.3.jar:/home/mdhandapani/.ivy2/cache/commons-net/commons-net/jars/commons-net-2.2.jar:/home/mdhandapani/.ivy2/cache/commons-pool/commons-pool/jars/commons-pool-1.5.4.jar:/home/mdhandapani/.ivy2/cache/io.dropwizard.metrics/metrics-core/bundles/metrics-core-3.1.2.jar:/home/mdhandapani/.ivy2/cache/io.dropwizard.metrics/metrics-graphite/bundles/metrics-graphite-3.1.2.jar:/home/mdhandapani/.ivy2/cache/io.dropwizard.metrics/metrics-json/bundles/metrics-json-3.1.2.jar:/home/mdhandapani/.ivy2/cache/io.dropwizard.metrics/metrics-jvm/bundles/metrics-jvm-3.1.2.jar:/home/mdhandapani/.ivy2/cache/io.netty/netty/bundles/netty-3.8.0.Final.jar:/home/mdhandapani/.ivy2/cache/io.netty/netty-all/jars/netty-all-4.0.29.Final.jar:/home/mdhandapani/.ivy2/cache/javax.inject/javax.inject/jars/javax.inject-1.jar:/home/mdhandapani/.ivy2/cache/javax.jdo/jdo-api/jars/jdo-api-3.0.1.jar:/home/mdhandapani/.ivy2/cache/javax.transaction/jta/jars/jta-1.1.jar:/home/mdhandapani/.ivy2/cache/javax.xml.bind/jaxb-api/jars/jaxb-api-2.2.7.jar:/home/mdhandapani/.ivy2/cache/javax.xml.bind/jsr173_api/jars/jsr173_api-1.0.jar:/home/mdhandapani/.ivy2/cache/javolution/javolution/bundles/javolution-5.5.1.jar:/home/mdhandapani/.ivy2/cache/jline/jline/jars/jline-2.12.jar:/home/mdhandapani/.ivy2/cache/joda-time/joda-time/jars/joda-time-2.5.jar:/home/mdhandapani/.ivy2/cache/log4j/apache-log4j-extras/bundles/apache-log4j-extras-1.2.17.jar:/home/mdhandapani/.ivy2/cache/log4j/log4j/bundles/log4j-1.2.17.jar:/home/mdhandapani/.ivy2/cache/net.hydromatic/eigenbase-properties/bundles/eigenbase-properties-1.1.5.jar:/home/mdhandapani/.ivy2/cache/net.java.dev.jets3t/jets3t/jars/jets3t-0.7.1.jar:/home/mdhandapani/.ivy2/cache/net.jpountz.lz4/lz4/jars/lz4-1.3.0.jar:/home/mdhandapani/.ivy2/cache/net.razorvine/pyrolite/jars/pyrolite-4.4.jar:/home/mdhandapani/.ivy2/cache/net.sf.opencsv/opencsv/jars/opencsv-2.3.jar:/home/mdhandapani/.ivy2/cache/net.sf.py4j/py4j/jars/py4j-0.8.2.1.jar:/home/mdhandapani/.ivy2/cache/net.sourceforge.f2j/arpack_combined_all/jars/arpack_combined_all-0.1-javadoc.jar:/home/mdhandapani/.ivy2/cache/net.sourceforge.f2j/arpack_combined_all/jars/arpack_combined_all-0.1.jar:/home/mdhandapani/.ivy2/cache/org.antlr/ST4/jars/ST4-4.0.4.jar:/home/mdhandapani/.ivy2/cache/org.antlr/antlr-runtime/jars/antlr-runtime-3.4.jar:/home/mdhandapani/.ivy2/cache/org.antlr/stringtemplate/jars/stringtemplate-3.2.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.avro/avro/jars/avro-1.7.7.jar:/home/mdhandapani/.ivy2/cache/org.apache.avro/avro-ipc/jars/avro-ipc-1.7.7.jar:/home/mdhandapani/.ivy2/cache/org.apache.avro/avro-ipc/jars/avro-ipc-1.7.7-tests.jar:/home/mdhandapani/.ivy2/cache/org.apache.avro/avro-mapred/jars/avro-mapred-1.7.7-hadoop2.jar:/home/mdhandapani/.ivy2/cache/org.apache.calcite/calcite-avatica/jars/calcite-avatica-1.2.0-incubating.jar:/home/mdhandapani/.ivy2/cache/org.apache.calcite/calcite-core/jars/calcite-core-1.2.0-incubating.jar:/home/mdhandapani/.ivy2/cache/org.apache.calcite/calcite-linq4j/jars/calcite-linq4j-1.2.0-incubating.jar:/home/mdhandapani/.ivy2/cache/org.apache.commons/commons-compress/jars/commons-compress-1.4.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.commons/commons-lang3/jars/commons-lang3-3.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.commons/commons-math/jars/commons-math-2.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.commons/commons-math3/jars/commons-math3-3.4.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.curator/curator-client/bundles/curator-client-2.4.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.curator/curator-framework/bundles/curator-framework-2.4.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.curator/curator-recipes/bundles/curator-recipes-2.4.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.derby/derby/jars/derby-10.10.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.flume/flume-ng-auth/jars/flume-ng-auth-1.6.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.flume/flume-ng-configuration/jars/flume-ng-configuration-1.6.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.flume/flume-ng-core/jars/flume-ng-core-1.6.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.flume/flume-ng-sdk/jars/flume-ng-sdk-1.6.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-annotations/jars/hadoop-annotations-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-auth/jars/hadoop-auth-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-client/jars/hadoop-client-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-common/jars/hadoop-common-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-hdfs/jars/hadoop-hdfs-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-mapreduce-client-app/jars/hadoop-mapreduce-client-app-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-mapreduce-client-common/jars/hadoop-mapreduce-client-common-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-mapreduce-client-core/jars/hadoop-mapreduce-client-core-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-mapreduce-client-jobclient/jars/hadoop-mapreduce-client-jobclient-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-mapreduce-client-shuffle/jars/hadoop-mapreduce-client-shuffle-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-yarn-api/jars/hadoop-yarn-api-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-yarn-client/jars/hadoop-yarn-client-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-yarn-common/jars/hadoop-yarn-common-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.hadoop/hadoop-yarn-server-common/jars/hadoop-yarn-server-common-2.2.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.httpcomponents/httpclient/jars/httpclient-4.3.2.jar:/home/mdhandapani/.ivy2/cache/org.apache.httpcomponents/httpcore/jars/httpcore-4.3.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.ivy/ivy/jars/ivy-2.4.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.kafka/kafka-clients/jars/kafka-clients-0.8.2.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.kafka/kafka_2.11/jars/kafka_2.11-0.8.2.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.mesos/mesos/jars/mesos-0.21.1-shaded-protobuf.jar:/home/mdhandapani/.ivy2/cache/org.apache.mina/mina-core/bundles/mina-core-2.0.4.jar:/home/mdhandapani/.ivy2/cache/org.apache.parquet/parquet-column/jars/parquet-column-1.7.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.parquet/parquet-common/jars/parquet-common-1.7.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.parquet/parquet-encoding/jars/parquet-encoding-1.7.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.parquet/parquet-format/jars/parquet-format-2.3.0-incubating.jar:/home/mdhandapani/.ivy2/cache/org.apache.parquet/parquet-generator/jars/parquet-generator-1.7.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.parquet/parquet-hadoop/jars/parquet-hadoop-1.7.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.parquet/parquet-jackson/jars/parquet-jackson-1.7.0.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-bagel_2.11/jars/spark-bagel_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-catalyst_2.11/jars/spark-catalyst_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-core_2.11/jars/spark-core_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-graphx_2.11/jars/spark-graphx_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-hive_2.11/jars/spark-hive_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-launcher_2.11/jars/spark-launcher_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-mllib_2.11/jars/spark-mllib_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-network-common_2.11/jars/spark-network-common_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-network-shuffle_2.11/jars/spark-network-shuffle_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-repl_2.11/jars/spark-repl_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-sql_2.11/jars/spark-sql_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-streaming-flume-sink_2.11/jars/spark-streaming-flume-sink_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-streaming-flume_2.11/jars/spark-streaming-flume_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-streaming-kafka_2.11/jars/spark-streaming-kafka_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-streaming-twitter_2.11/jars/spark-streaming-twitter_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-streaming_2.11/jars/spark-streaming_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.spark/spark-unsafe_2.11/jars/spark-unsafe_2.11-1.5.1.jar:/home/mdhandapani/.ivy2/cache/org.apache.thrift/libfb303/jars/libfb303-0.9.2.jar:/home/mdhandapani/.ivy2/cache/org.apache.thrift/libthrift/jars/libthrift-0.9.2.jar:/home/mdhandapani/.ivy2/cache/org.apache.zookeeper/zookeeper/jars/zookeeper-3.4.5.jar:/home/mdhandapani/.ivy2/cache/org.codehaus.groovy/groovy-all/jars/groovy-all-2.1.6.jar:/home/mdhandapani/.ivy2/cache/org.codehaus.jackson/jackson-core-asl/jars/jackson-core-asl-1.9.13.jar:/home/mdhandapani/.ivy2/cache/org.codehaus.jackson/jackson-jaxrs/jars/jackson-jaxrs-1.8.8.jar:/home/mdhandapani/.ivy2/cache/org.codehaus.jackson/jackson-mapper-asl/jars/jackson-mapper-asl-1.9.13.jar:/home/mdhandapani/.ivy2/cache/org.codehaus.jackson/jackson-xc/jars/jackson-xc-1.8.8.jar:/home/mdhandapani/.ivy2/cache/org.codehaus.janino/commons-compiler/jars/commons-compiler-2.7.8.jar:/home/mdhandapani/.ivy2/cache/org.codehaus.janino/janino/jars/janino-2.7.8.jar:/home/mdhandapani/.ivy2/cache/org.codehaus.jettison/jettison/bundles/jettison-1.1.jar:/home/mdhandapani/.ivy2/cache/org.datanucleus/datanucleus-api-jdo/jars/datanucleus-api-jdo-3.2.6.jar:/home/mdhandapani/.ivy2/cache/org.datanucleus/datanucleus-core/jars/datanucleus-core-3.2.10.jar:/home/mdhandapani/.ivy2/cache/org.datanucleus/datanucleus-rdbms/jars/datanucleus-rdbms-3.2.9.jar:/home/mdhandapani/.ivy2/cache/org.eclipse.jetty.orbit/javax.servlet/orbits/javax.servlet-3.0.0.v201112011016.jar:/home/mdhandapani/.ivy2/cache/org.iq80.snappy/snappy/jars/snappy-0.2.jar:/home/mdhandapani/.ivy2/cache/org.jodd/jodd-core/jars/jodd-core-3.5.2.jar:/home/mdhandapani/.ivy2/cache/org.jpmml/pmml-agent/jars/pmml-agent-1.1.15.jar:/home/mdhandapani/.ivy2/cache/org.jpmml/pmml-model/jars/pmml-model-1.1.15.jar:/home/mdhandapani/.ivy2/cache/org.jpmml/pmml-schema/jars/pmml-schema-1.1.15.jar:/home/mdhandapani/.ivy2/cache/org.json/json/jars/json-20090211.jar:/home/mdhandapani/.ivy2/cache/org.json4s/json4s-ast_2.11/jars/json4s-ast_2.11-3.2.10.jar:/home/mdhandapani/.ivy2/cache/org.json4s/json4s-core_2.11/jars/json4s-core_2.11-3.2.10.jar:/home/mdhandapani/.ivy2/cache/org.json4s/json4s-jackson_2.11/jars/json4s-jackson_2.11-3.2.10.jar:/home/mdhandapani/.ivy2/cache/org.mortbay.jetty/jetty/jars/jetty-6.1.26.jar:/home/mdhandapani/.ivy2/cache/org.mortbay.jetty/jetty-util/jars/jetty-util-6.1.26.jar:/home/mdhandapani/.ivy2/cache/org.mortbay.jetty/servlet-api/jars/servlet-api-2.5-20110124.jar:/home/mdhandapani/.ivy2/cache/org.objenesis/objenesis/jars/objenesis-1.2.jar:/home/mdhandapani/.ivy2/cache/org.roaringbitmap/RoaringBitmap/bundles/RoaringBitmap-0.4.5.jar:/home/mdhandapani/.ivy2/cache/org.scala-lang/scala-compiler/jars/scala-compiler-2.11.7.jar:/home/mdhandapani/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.7.jar:/home/mdhandapani/.ivy2/cache/org.scala-lang/scala-reflect/jars/scala-reflect-2.11.7.jar:/home/mdhandapani/.ivy2/cache/org.scala-lang/scalap/jars/scalap-2.11.0.jar:/home/mdhandapani/.ivy2/cache/org.scala-lang.modules/scala-parser-combinators_2.11/bundles/scala-parser-combinators_2.11-1.0.4.jar:/home/mdhandapani/.ivy2/cache/org.scala-lang.modules/scala-xml_2.11/bundles/scala-xml_2.11-1.0.4.jar:/home/mdhandapani/.ivy2/cache/org.scalanlp/breeze-macros_2.11/jars/breeze-macros_2.11-0.11.2.jar:/home/mdhandapani/.ivy2/cache/org.scalanlp/breeze_2.11/jars/breeze_2.11-0.11.2.jar:/home/mdhandapani/.ivy2/cache/org.slf4j/jcl-over-slf4j/jars/jcl-over-slf4j-1.7.10.jar:/home/mdhandapani/.ivy2/cache/org.slf4j/jul-to-slf4j/jars/jul-to-slf4j-1.7.10.jar:/home/mdhandapani/.ivy2/cache/org.slf4j/slf4j-api/jars/slf4j-api-1.7.6.jar:/home/mdhandapani/.ivy2/cache/org.slf4j/slf4j-log4j12/jars/slf4j-log4j12-1.7.10.jar:/home/mdhandapani/.ivy2/cache/org.sonatype.sisu.inject/cglib/jars/cglib-2.2.1-v20090111.jar:/home/mdhandapani/.ivy2/cache/org.spark-project.hive/hive-exec/jars/hive-exec-1.2.1.spark.jar:/home/mdhandapani/.ivy2/cache/org.spark-project.hive/hive-metastore/jars/hive-metastore-1.2.1.spark.jar:/home/mdhandapani/.ivy2/cache/org.spark-project.spark/unused/jars/unused-1.0.0.jar:/home/mdhandapani/.ivy2/cache/org.spire-math/spire-macros_2.11/jars/spire-macros_2.11-0.7.4.jar:/home/mdhandapani/.ivy2/cache/org.spire-math/spire_2.11/jars/spire_2.11-0.7.4.jar:/home/mdhandapani/.ivy2/cache/org.tachyonproject/tachyon-client/jars/tachyon-client-0.7.1.jar:/home/mdhandapani/.ivy2/cache/org.tachyonproject/tachyon-underfs-hdfs/jars/tachyon-underfs-hdfs-0.7.1.jar:/home/mdhandapani/.ivy2/cache/org.tachyonproject/tachyon-underfs-local/jars/tachyon-underfs-local-0.7.1.jar:/home/mdhandapani/.ivy2/cache/org.tukaani/xz/jars/xz-1.0.jar:/home/mdhandapani/.ivy2/cache/org.twitter4j/twitter4j-core/jars/twitter4j-core-3.0.3.jar:/home/mdhandapani/.ivy2/cache/org.twitter4j/twitter4j-stream/jars/twitter4j-stream-3.0.3.jar:/home/mdhandapani/.ivy2/cache/org.uncommons.maths/uncommons-maths/jars/uncommons-maths-1.2.2a.jar:/home/mdhandapani/.ivy2/cache/org.xerial.snappy/snappy-java/bundles/snappy-java-1.1.1.7.jar:/home/mdhandapani/.ivy2/cache/oro/oro/jars/oro-2.0.8.jar:/home/mdhandapani/.ivy2/cache/stax/stax-api/jars/stax-api-1.0.1.jar:/home/mdhandapani/.ivy2/cache/xmlenc/xmlenc/jars/xmlenc-0.52.jar:/home/mdhandapani/.ivy2/cache/com.databricks/spark-csv_2.11/jars/spark-csv_2.11-1.3.0.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil/jniloader/jars/jniloader-1.1.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/native_ref-java/jars/native_ref-java-1.1.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/native_system-java/jars/native_system-java-1.1.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_ref-linux-armhf/jars/netlib-native_ref-linux-armhf-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_ref-linux-i686/jars/netlib-native_ref-linux-i686-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_ref-linux-x86_64/jars/netlib-native_ref-linux-x86_64-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_ref-osx-x86_64/jars/netlib-native_ref-osx-x86_64-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_ref-win-i686/jars/netlib-native_ref-win-i686-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_ref-win-x86_64/jars/netlib-native_ref-win-x86_64-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_system-linux-armhf/jars/netlib-native_system-linux-armhf-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_system-linux-i686/jars/netlib-native_system-linux-i686-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_system-linux-x86_64/jars/netlib-native_system-linux-x86_64-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_system-osx-x86_64/jars/netlib-native_system-osx-x86_64-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_system-win-i686/jars/netlib-native_system-win-i686-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.fommil.netlib/netlib-native_system-win-x86_64/jars/netlib-native_system-win-x86_64-1.1-natives.jar:/home/mdhandapani/.ivy2/cache/com.github.wookietreiber/scala-chart_2.11/jars/scala-chart_2.11-0.5.0.jar:/home/mdhandapani/.ivy2/cache/com.typesafe/config/bundles/config-1.3.0.jar:/home/mdhandapani/.ivy2/cache/com.typesafe.akka/akka-actor_2.11/jars/akka-actor_2.11-2.4.0.jar:/home/mdhandapani/.ivy2/cache/com.univocity/univocity-parsers/jars/univocity-parsers-1.5.1.jar:/home/mdhandapani/.ivy2/cache/junit/junit/jars/junit-4.8.2.jar:/home/mdhandapani/.ivy2/cache/org.apache.commons/commons-csv/jars/commons-csv-1.1.jar:/home/mdhandapani/.ivy2/cache/org.jfree/jcommon/jars/jcommon-1.0.23.jar:/home/mdhandapani/.ivy2/cache/org.jfree/jfreechart/jars/jfreechart-1.0.19.jar:/home/mdhandapani/.ivy2/cache/org.scala-lang.modules/scala-swing_2.11/bundles/scala-swing_2.11-1.0.2.jar:/home/mdhandapani/.ivy2/cache/org.scalanlp/breeze-natives_2.11/jars/breeze-natives_2.11-0.11.2.jar:/home/mdhandapani/Desktop/idea-IC-143.1184.17/lib/idea_rt.jar com.intellij.rt.execution.application.AppMain org.aja.tej.examples.sparksql.dataframe.Aggregation
16/02/03 17:46:41 WARN Utils: Your hostname, VID-Android resolves to a loopback address: 127.0.1.1; using 10.90.18.182 instead (on interface eth1)
16/02/03 17:46:41 WARN Utils: Set SPARK_LOCAL_IP if you need to bind to another address
16/02/03 17:46:49 WARN MetricsSystem: Using default name DAGScheduler for source because spark.app.id is not set.
[Stage 1:==================>                                     (67 + 4) / 200][day2,user1,session4,1,90.0]
[Stage 1:========================>                               (89 + 5) / 200][day1,user1,session1,1,100.0]
[day1,user1,session2,1,200.0]
[day1,user1,session3,1,300.0]
[day1,user1,session4,1,400.0]
*** basic form of aggregation
+-----+----------------+
|state|max(discountAmt)|
+-----+----------------+
|   TN|          3000.0|
|ANDRA|             0.0|
|   JR|           300.0|
+-----+----------------+

*** this time without grouping columns
[Stage 9:=====================================================> (194 + 4) / 199]+----------------+
|max(discountAmt)|
+----------------+
|          3000.0|
|             0.0|
|           300.0|
+----------------+

*** Column based aggregation
[Stage 13:==============================================>       (173 + 4) / 199]+----------------+
|max(discountAmt)|
+----------------+
|          3000.0|
|             0.0|
|           300.0|
+----------------+

*** Column based aggregation plus grouping columns
+-----+----------------+
|state|max(discountAmt)|
+-----+----------------+
|   TN|          3000.0|
|ANDRA|             0.0|
|   JR|           300.0|
+-----+----------------+

*** Sort-of a user-defined aggregation function
+-----+--------------------------------------------------------------------------------+
|state|SQRT((avg((discountAmt * discountAmt)) - (avg(discountAmt) * avg(discountAmt))))|
+-----+--------------------------------------------------------------------------------+
|   TN|                                                              1257.6838831757366|
|ANDRA|                                                                             0.0|
|   JR|                                                                             0.0|
+-----+--------------------------------------------------------------------------------+

*** Aggregation short cuts
+-----+
|count|
+-----+
|    4|
|    1|
|    1|
+-----+




 */
