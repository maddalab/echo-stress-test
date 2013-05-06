echo-stress-test
================

A simple test of Echo Server in finagle with different connection options.

This test demonstrates the effect of a bad implementation used for queue management in IdleConnectionFilter which is
in turn used for connections thresholds logic.

Changing the EchoServer to use open connection thresholds which are such that the 'low water mark' not be breached.

In the branch 'fix-thresholds-failing' we address the issue.
