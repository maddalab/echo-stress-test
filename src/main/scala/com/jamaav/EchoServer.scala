package com.jamaav

import com.twitter.finagle.Service
import com.twitter.conversions.time._
import com.twitter.util.Future
import java.net.InetSocketAddress
import com.twitter.finagle.builder.{ Server, ServerBuilder }
import com.twitter.finagle.channel.OpenConnectionsThresholds
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.ConsoleHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import java.util.logging.Level

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
      .openConnectionsThresholds(thresholds)
      .logChannelActivity(true)
      .logger(logger)
      .build(service)
  }

  def logger = {
    val log = Logger.getLogger("my.logger");
    log.setLevel(Level.ALL);
    val handler = new ConsoleHandler();
    handler.setFormatter(new SimpleFormatter());
    handler.setLevel(Level.ALL)
    log.addHandler(handler);
    log.fine("logging to console");
    log
  }
}
