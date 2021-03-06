package org.aja.tej.examples.sparksql

import org.aja.tej.utils.TejUtils
import org.apache.spark.sql.SQLContext

/**
 * Created by mageswaran on 18/10/15.
 */
object TweetsDataBase {
  def main(args: Array[String]) {

    val sc = TejUtils.getSparkContext(this.getClass.getSimpleName)

    val sqlContext = new SQLContext(sc)

    val tweetsDF = sqlContext.read.json("data/tweets/tweets*/part-*")

    tweetsDF.show()
    tweetsDF.printSchema()

    println("Number of tweets: " + tweetsDF.count)

    tweetsDF.write.format("parquet").save("output/tweets/parquet")
  }
}



//Schema
//Official reference: https://dev.twitter.com/overview/api/tweets
//https://gist.github.com/robjohnson/702360
/*


root
|-- contributorsIDs: array (nullable = true)
|    |-- element: string (containsNull = true)
|-- createdAt: string (nullable = true)
|-- currentUserRetweetId: long (nullable = true)
|-- geoLocation: struct (nullable = true)
|    |-- latitude: double (nullable = true)
|    |-- longitude: double (nullable = true)
|-- hashtagEntities: array (nullable = true)
|    |-- element: struct (containsNull = true)
|    |    |-- end: long (nullable = true)
|    |    |-- start: long (nullable = true)
|    |    |-- text: string (nullable = true)
|-- id: long (nullable = true)
|-- inReplyToScreenName: string (nullable = true)
|-- inReplyToStatusId: long (nullable = true)
|-- inReplyToUserId: long (nullable = true)
|-- isFavorited: boolean (nullable = true)
|-- isPossiblySensitive: boolean (nullable = true)
|-- isTruncated: boolean (nullable = true)
|-- mediaEntities: array (nullable = true)
|    |-- element: struct (containsNull = true)
|    |    |-- displayURL: string (nullable = true)
|    |    |-- end: long (nullable = true)
|    |    |-- expandedURL: string (nullable = true)
|    |    |-- id: long (nullable = true)
|    |    |-- mediaURL: string (nullable = true)
|    |    |-- mediaURLHttps: string (nullable = true)
|    |    |-- sizes: struct (nullable = true)
|    |    |    |-- 0: struct (nullable = true)
|    |    |    |    |-- height: long (nullable = true)
|    |    |    |    |-- resize: long (nullable = true)
|    |    |    |    |-- width: long (nullable = true)
|    |    |    |-- 1: struct (nullable = true)
|    |    |    |    |-- height: long (nullable = true)
|    |    |    |    |-- resize: long (nullable = true)
|    |    |    |    |-- width: long (nullable = true)
|    |    |    |-- 2: struct (nullable = true)
|    |    |    |    |-- height: long (nullable = true)
|    |    |    |    |-- resize: long (nullable = true)
|    |    |    |    |-- width: long (nullable = true)
|    |    |    |-- 3: struct (nullable = true)
|    |    |    |    |-- height: long (nullable = true)
|    |    |    |    |-- resize: long (nullable = true)
|    |    |    |    |-- width: long (nullable = true)
|    |    |-- start: long (nullable = true)
|    |    |-- type: string (nullable = true)
|    |    |-- url: string (nullable = true)
|-- place: struct (nullable = true)
|    |-- boundingBoxCoordinates: array (nullable = true)
|    |    |-- element: array (containsNull = true)
|    |    |    |-- element: struct (containsNull = true)
|    |    |    |    |-- latitude: double (nullable = true)
|    |    |    |    |-- longitude: double (nullable = true)
|    |-- boundingBoxType: string (nullable = true)
|    |-- country: string (nullable = true)
|    |-- countryCode: string (nullable = true)
|    |-- fullName: string (nullable = true)
|    |-- id: string (nullable = true)
|    |-- name: string (nullable = true)
|    |-- placeType: string (nullable = true)
|    |-- url: string (nullable = true)
|-- retweetCount: long (nullable = true)
|-- retweetedStatus: struct (nullable = true)
|    |-- contributorsIDs: array (nullable = true)
|    |    |-- element: string (containsNull = true)
|    |-- createdAt: string (nullable = true)
|    |-- currentUserRetweetId: long (nullable = true)
|    |-- geoLocation: struct (nullable = true)
|    |    |-- latitude: double (nullable = true)
|    |    |-- longitude: double (nullable = true)
|    |-- hashtagEntities: array (nullable = true)
|    |    |-- element: struct (containsNull = true)
|    |    |    |-- end: long (nullable = true)
|    |    |    |-- start: long (nullable = true)
|    |    |    |-- text: string (nullable = true)
|    |-- id: long (nullable = true)
|    |-- inReplyToScreenName: string (nullable = true)
|    |-- inReplyToStatusId: long (nullable = true)
|    |-- inReplyToUserId: long (nullable = true)
|    |-- isFavorited: boolean (nullable = true)
|    |-- isPossiblySensitive: boolean (nullable = true)
|    |-- isTruncated: boolean (nullable = true)
|    |-- mediaEntities: array (nullable = true)
|    |    |-- element: struct (containsNull = true)
|    |    |    |-- displayURL: string (nullable = true)
|    |    |    |-- end: long (nullable = true)
|    |    |    |-- expandedURL: string (nullable = true)
|    |    |    |-- id: long (nullable = true)
|    |    |    |-- mediaURL: string (nullable = true)
|    |    |    |-- mediaURLHttps: string (nullable = true)
|    |    |    |-- sizes: struct (nullable = true)
|    |    |    |    |-- 0: struct (nullable = true)
|    |    |    |    |    |-- height: long (nullable = true)
|    |    |    |    |    |-- resize: long (nullable = true)
|    |    |    |    |    |-- width: long (nullable = true)
|    |    |    |    |-- 1: struct (nullable = true)
|    |    |    |    |    |-- height: long (nullable = true)
|    |    |    |    |    |-- resize: long (nullable = true)
|    |    |    |    |    |-- width: long (nullable = true)
|    |    |    |    |-- 2: struct (nullable = true)
|    |    |    |    |    |-- height: long (nullable = true)
|    |    |    |    |    |-- resize: long (nullable = true)
|    |    |    |    |    |-- width: long (nullable = true)
|    |    |    |    |-- 3: struct (nullable = true)
|    |    |    |    |    |-- height: long (nullable = true)
|    |    |    |    |    |-- resize: long (nullable = true)
|    |    |    |    |    |-- width: long (nullable = true)
|    |    |    |-- start: long (nullable = true)
|    |    |    |-- type: string (nullable = true)
|    |    |    |-- url: string (nullable = true)
|    |-- place: struct (nullable = true)
|    |    |-- boundingBoxCoordinates: array (nullable = true)
|    |    |    |-- element: array (containsNull = true)
|    |    |    |    |-- element: struct (containsNull = true)
|    |    |    |    |    |-- latitude: double (nullable = true)
|    |    |    |    |    |-- longitude: double (nullable = true)
|    |    |-- boundingBoxType: string (nullable = true)
|    |    |-- country: string (nullable = true)
|    |    |-- countryCode: string (nullable = true)
|    |    |-- fullName: string (nullable = true)
|    |    |-- id: string (nullable = true)
|    |    |-- name: string (nullable = true)
|    |    |-- placeType: string (nullable = true)
|    |    |-- url: string (nullable = true)
|    |-- retweetCount: long (nullable = true)
|    |-- source: string (nullable = true)
|    |-- text: string (nullable = true)
|    |-- urlEntities: array (nullable = true)
|    |    |-- element: struct (containsNull = true)
|    |    |    |-- displayURL: string (nullable = true)
|    |    |    |-- end: long (nullable = true)
|    |    |    |-- expandedURL: string (nullable = true)
|    |    |    |-- start: long (nullable = true)
|    |    |    |-- url: string (nullable = true)
|    |-- user: struct (nullable = true)
|    |    |-- createdAt: string (nullable = true)
|    |    |-- description: string (nullable = true)
|    |    |-- descriptionURLEntities: array (nullable = true)
|    |    |    |-- element: string (containsNull = true)
|    |    |-- favouritesCount: long (nullable = true)
|    |    |-- followersCount: long (nullable = true)
|    |    |-- friendsCount: long (nullable = true)
|    |    |-- id: long (nullable = true)
|    |    |-- isContributorsEnabled: boolean (nullable = true)
|    |    |-- isFollowRequestSent: boolean (nullable = true)
|    |    |-- isGeoEnabled: boolean (nullable = true)
|    |    |-- isProtected: boolean (nullable = true)
|    |    |-- isVerified: boolean (nullable = true)
|    |    |-- lang: string (nullable = true)
|    |    |-- listedCount: long (nullable = true)
|    |    |-- location: string (nullable = true)
|    |    |-- name: string (nullable = true)
|    |    |-- profileBackgroundColor: string (nullable = true)
|    |    |-- profileBackgroundImageUrl: string (nullable = true)
|    |    |-- profileBackgroundImageUrlHttps: string (nullable = true)
|    |    |-- profileBackgroundTiled: boolean (nullable = true)
|    |    |-- profileBannerImageUrl: string (nullable = true)
|    |    |-- profileImageUrl: string (nullable = true)
|    |    |-- profileImageUrlHttps: string (nullable = true)
|    |    |-- profileLinkColor: string (nullable = true)
|    |    |-- profileSidebarBorderColor: string (nullable = true)
|    |    |-- profileSidebarFillColor: string (nullable = true)
|    |    |-- profileTextColor: string (nullable = true)
|    |    |-- profileUseBackgroundImage: boolean (nullable = true)
|    |    |-- screenName: string (nullable = true)
|    |    |-- showAllInlineMedia: boolean (nullable = true)
|    |    |-- statusesCount: long (nullable = true)
|    |    |-- timeZone: string (nullable = true)
|    |    |-- translator: boolean (nullable = true)
|    |    |-- url: string (nullable = true)
|    |    |-- utcOffset: long (nullable = true)
|    |-- userMentionEntities: array (nullable = true)
|    |    |-- element: struct (containsNull = true)
|    |    |    |-- end: long (nullable = true)
|    |    |    |-- id: long (nullable = true)
|    |    |    |-- name: string (nullable = true)
|    |    |    |-- screenName: string (nullable = true)
|    |    |    |-- start: long (nullable = true)
|-- source: string (nullable = true)
|-- text: string (nullable = true)
|-- urlEntities: array (nullable = true)
|    |-- element: struct (containsNull = true)
|    |    |-- displayURL: string (nullable = true)
|    |    |-- end: long (nullable = true)
|    |    |-- expandedURL: string (nullable = true)
|    |    |-- start: long (nullable = true)
|    |    |-- url: string (nullable = true)
|-- user: struct (nullable = true)
|    |-- createdAt: string (nullable = true)
|    |-- description: string (nullable = true)
|    |-- descriptionURLEntities: array (nullable = true)
|    |    |-- element: string (containsNull = true)
|    |-- favouritesCount: long (nullable = true)
|    |-- followersCount: long (nullable = true)
|    |-- friendsCount: long (nullable = true)
|    |-- id: long (nullable = true)
|    |-- isContributorsEnabled: boolean (nullable = true)
|    |-- isFollowRequestSent: boolean (nullable = true)
|    |-- isGeoEnabled: boolean (nullable = true)
|    |-- isProtected: boolean (nullable = true)
|    |-- isVerified: boolean (nullable = true)
|    |-- lang: string (nullable = true)
|    |-- listedCount: long (nullable = true)
|    |-- location: string (nullable = true)
|    |-- name: string (nullable = true)
|    |-- profileBackgroundColor: string (nullable = true)
|    |-- profileBackgroundImageUrl: string (nullable = true)
|    |-- profileBackgroundImageUrlHttps: string (nullable = true)
|    |-- profileBackgroundTiled: boolean (nullable = true)
|    |-- profileBannerImageUrl: string (nullable = true)
|    |-- profileImageUrl: string (nullable = true)
|    |-- profileImageUrlHttps: string (nullable = true)
|    |-- profileLinkColor: string (nullable = true)
|    |-- profileSidebarBorderColor: string (nullable = true)
|    |-- profileSidebarFillColor: string (nullable = true)
|    |-- profileTextColor: string (nullable = true)
|    |-- profileUseBackgroundImage: boolean (nullable = true)
|    |-- screenName: string (nullable = true)
|    |-- showAllInlineMedia: boolean (nullable = true)
|    |-- statusesCount: long (nullable = true)
|    |-- timeZone: string (nullable = true)
|    |-- translator: boolean (nullable = true)
|    |-- url: string (nullable = true)
|    |-- utcOffset: long (nullable = true)
|-- userMentionEntities: array (nullable = true)
|    |-- element: struct (containsNull = true)
|    |    |-- end: long (nullable = true)
|    |    |-- id: long (nullable = true)
|    |    |-- name: string (nullable = true)
|    |    |-- screenName: string (nullable = true)
|    |    |-- start: long (nullable = true)

 */