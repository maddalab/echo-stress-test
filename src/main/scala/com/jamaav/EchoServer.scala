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
import com.twitter.finagle.stats.JavaLoggerStatsReceiver

object EchoServer {
  val count = new AtomicLong(0)
  def start() = {
    val service = new Service[String, String] {
      def apply(request: String) = {
        count.incrementAndGet()
        Future.value(request)
      }
    }

    val thresholds = OpenConnectionsThresholds(
      lowWaterMark = 1,
      highWaterMark = 2,
      idleTimeout = 5 seconds)

    // Bind the service to port 8080
    ServerBuilder()
      .codec(StringCodec)
      .bindTo(new InetSocketAddress(8080))
      .name("echoserver")
      .reportTo(JavaLoggerStatsReceiver())
      .openConnectionsThresholds(thresholds)
      .logger({
        val log = Logger.getLogger("")
        val slog = Logger.getLogger("Finagle")
        log.addHandler({
          val handler = new ConsoleHandler()
          handler.setFormatter(new SimpleFormatter())
          handler.setLevel(Level.ALL)
          handler
        })
        slog.addHandler({
          val handler = new ConsoleHandler()
          handler.setFormatter(new SimpleFormatter())
          handler.setLevel(Level.ALL)
          handler
        })
        log.setLevel(Level.ALL)
        slog.setLevel(Level.ALL)
        println("Attempting print to console")
        slog.info("stat logger configured for maximum effect")
        log.info("All loggers configured for maximum effect")
        log
      })
      .logChannelActivity(false)
      .build(service)
  }
}
