package com.jamaav

import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.HttpResponseStatus.OK
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.conversions.time.intToTimeableNumber
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.channel.OpenConnectionsThresholds
import com.twitter.finagle.http.Http
import com.twitter.util.Future
import com.twitter.finagle.stats.JavaLoggerStatsReceiver

object HttpServer {
  val count = new AtomicLong(0)

  val service = new Service[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest) = {
      count.incrementAndGet()
      val response = new DefaultHttpResponse(HTTP_1_1, OK)
      response.setContent(copiedBuffer("hello world", UTF_8))
      Future.value(response)
    }
  }
  
  def start() = {
    
    val thresholds = OpenConnectionsThresholds(
      lowWaterMark = 500,
      highWaterMark = 2000,
      idleTimeout = 5 seconds)

    // Bind the service to port 8080
    ServerBuilder()
      .codec(Http())
      .bindTo(new InetSocketAddress(8080))
      .name("httpserver")
      .openConnectionsThresholds(thresholds)
      .reportTo(JavaLoggerStatsReceiver())
      .logger({
        val log = Logger.getLogger("")
        val slog = Logger.getLogger("Finagle")
        slog.addHandler({
          val handler = new ConsoleHandler()
          handler.setFormatter(new SimpleFormatter())
          handler.setLevel(Level.ALL)
          handler
        })
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