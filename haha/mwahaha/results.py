#!/usr/bin/env python2.6

### HealthCheckResult base class
class HealthCheckResult(object):
    success = None
    def __init__(self, host):
        self.host    = host

## Children of the base class
class SuccessfulHealthCheckResult(HealthCheckResult):
    success = True

class FailedHealthCheckResult(HealthCheckResult):
    success = False
