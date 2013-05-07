package com.jamaav

import com.twitter.finagle.builder.ClientBuilder
import java.net.InetSocketAddress
import com.twitter.finagle.Service
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Logger
import java.util.logging.Level
import java.util.logging.ConsoleHandler
import java.util.logging.SimpleFormatter

object EchoClient {
  val failures = new AtomicLong(0)
  val successes = new AtomicLong(0)

  def send(msg: String) {
    // Construct a client, and connect it to localhost:8080
    val client: Service[String, String] = ClientBuilder()
      .codec(StringCodec)
      .hosts(new InetSocketAddress("t.dev", 8080))
      .hostConnectionLimit(20)
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
      .build()

    // Issue a newline-delimited request, respond to the result
    // asynchronously:
    client(msg) onSuccess { result =>
      successes.incrementAndGet()
    } onFailure { error =>
      failures.incrementAndGet()
    } ensure {
      // All done! Close TCP connection(s):
      client.close()
    }
  }
}
