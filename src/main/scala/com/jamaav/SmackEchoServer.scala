package com.jamaav

import com.tumblr.benchmark.benchmarking.Benchpress
import com.tumblr.benchmark.reporting.BenchmarkReportingBase
import com.tumblr.benchmark.reporting.ReportSummaryGenerator
import com.tumblr.benchmark.reporting.ReportGenerator
import com.tumblr.benchmark.reporting.ReportStructure
import com.tumblr.benchmark.reporting.ReportMarkup._

object SmackEchoServer {
  def benchmark() {
    val reporter = new BenchmarkReportingBase() {}
    val benchmark = Benchpress("conc-echo-server-test", reporter)
    val client = EchoClient

    val threadCounts = Seq(2, 4, 8)
    for (threadCount <- threadCounts) {
      benchmark.iterations(10000).concurrent(threadCount).aggregateTiming.bench({
        client.send("Hello")
      })
    }

    val reportFormat = ReportStructure("echo-server-test", Seq(
      metrics("*", Seq(walltime, total, count, throughput))))

    println((new ReportSummaryGenerator(ReportGenerator(reportFormat, reporter).generate())).generate())

  }
}