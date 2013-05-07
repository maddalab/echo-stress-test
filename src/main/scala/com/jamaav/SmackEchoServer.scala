package com.jamaav

import java.util.concurrent.atomic.AtomicInteger

import com.tumblr.benchmark.benchmarking.Benchpress
import com.tumblr.benchmark.reporting.BenchmarkReportingBase
import com.tumblr.benchmark.reporting.ReportGenerator
import com.tumblr.benchmark.reporting.ReportMarkup.count
import com.tumblr.benchmark.reporting.ReportMarkup.metrics
import com.tumblr.benchmark.reporting.ReportMarkup.throughput
import com.tumblr.benchmark.reporting.ReportMarkup.total
import com.tumblr.benchmark.reporting.ReportMarkup.walltime
import com.tumblr.benchmark.reporting.ReportStructure
import com.tumblr.benchmark.reporting.ReportSummaryGenerator

object SmackEchoServer {
  def benchmark() {
    val reporter = new BenchmarkReportingBase() {}
    val benchmark = Benchpress("conc-echo-server-test", reporter)
    val client = EchoClient

    val threadCounts = Seq(30, 60, 120)
    for (threadCount <- threadCounts) {
      println("Iteration starting for thread count " + threadCount)
      benchmark.iterations(100000).concurrent(threadCount).aggregateTiming.bench({
        client.send("Hello")
      })
      println("Iteration complete for thread count " + threadCount)
    }

    val reportFormat = ReportStructure("echo-server-test", Seq(
      metrics("*", Seq(walltime, total, count, throughput))))

    println((new ReportSummaryGenerator(ReportGenerator(reportFormat, reporter).generate())).generate())
  }
  
  def main(args:Array[String])  {
    SmackEchoServer.benchmark()
  }
}
