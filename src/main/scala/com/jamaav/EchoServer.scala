package com.jamaav

import com.twitter.finagle.Service
import com.twitter.conversions.time._
import com.twitter.util.Future
import java.net.InetSocketAddress
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.channel.OpenConnectionsThresholds

object EchoServer {
  def start() = {
    /**
     * A very simple service that simply echos its request back
     * as a response. Note that it returns a Future, since everything
     * in Finagle is asynchronous.
     */
    val service = new Service[String, String] {
      def apply(request: String) = Future.value(request)
    }
    
    val thresholds = OpenConnectionsThresholds(
		lowWaterMark = 500,
		highWaterMark = 2000,
		idleTimeout = 5 seconds
    )

    // Bind the service to port 8080
    ServerBuilder()
      .codec(StringCodec)
      .bindTo(new InetSocketAddress(8080))
      .name("echoserver")
      //.openConnectionsThresholds(thresholds)
      .build(service)
  }
}
