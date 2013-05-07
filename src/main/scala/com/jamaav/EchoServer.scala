package com.jamaav

import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Logger
import com.twitter.conversions.time.intToTimeableNumber
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.channel.OpenConnectionsThresholds
import com.twitter.util.Future
import java.util.logging.Level
import java.util.logging.ConsoleHandler
import java.util.logging.SimpleFormatter

object EchoServer {
  val count = new AtomicLong(0)
  def start() = {
    /**
     * A very simple service that simply echos its request back
     * as a response. Note that it returns a Future, since everything
     * in Finagle is asynchronous.
     */
    val service = new Service[String, String] {
      def apply(request: String) = {
        count.incrementAndGet()
        Future.value(request)
      }
    }

    val thresholds = OpenConnectionsThresholds(
      lowWaterMark = 500,
      highWaterMark = 2000,
      idleTimeout = 5 seconds)

    // Bind the service to port 8080
    ServerBuilder()
      .codec(StringCodec)
      .bindTo(new InetSocketAddress(8080))
      .name("echoserver")
      //.openConnectionsThresholds(thresholds)
      .logger({
        val log = Logger.getLogger("")
        log.setLevel(Level.WARNING)
        val handler = new ConsoleHandler()
        handler.setFormatter(new SimpleFormatter())
        handler.setLevel(Level.WARNING)
        log.addHandler(handler)
        log.info("Logging to console")
        log
      })
      .logChannelActivity(false)
      .build(service)
  }
}
