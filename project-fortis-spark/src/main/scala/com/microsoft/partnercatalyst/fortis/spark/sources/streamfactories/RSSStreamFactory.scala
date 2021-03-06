package com.microsoft.partnercatalyst.fortis.spark.sources.streamfactories

import com.github.catalystcode.fortis.spark.streaming.rss._
import com.microsoft.partnercatalyst.fortis.spark.sources.streamprovider.ConnectorConfig
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

class RSSStreamFactory extends StreamFactoryBase[RSSEntry] {

  override protected def canHandle(connectorConfig: ConnectorConfig): Boolean = {
    "RSS".equalsIgnoreCase(connectorConfig.name)
  }

  override protected def buildStream(ssc: StreamingContext, connectorConfig: ConnectorConfig): DStream[RSSEntry] = {
    import ParameterExtensions._

    val params = connectorConfig.parameters
    new RSSInputDStream(
      params.getTrustedSources,
      storageLevel = StorageLevel.MEMORY_ONLY,
      requestHeaders = Map(
        "User-Agent" -> params.getOrElse("userAgent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").toString
      ),
      connectTimeout = params.getOrElse("connectTimeout", "3000").toString.toInt,
      readTimeout = params.getOrElse("readTimeout", "9000").toString.toInt,
      pollingPeriodInSeconds = params.getOrElse("pollingPeriodInSeconds", "3600").toString.toInt,
      ssc = ssc
    )
  }

}
