#!/usr/bin/env python2.6

import datetime, json, sys, time, web
from threading import Thread
from status import Status

## "local" imports
import checks

## we'll factor this stuff out later

urls = (
    "/status.(json|xml)", "status",
    "/status", "status",
    "/checkin(.*)", "checkin",
    "/healthcheck", "healthcheck",
    "/(.*)", "default"
)

app = web.application(urls, globals())
global_status = Status()

class checkin:
    ## currently we only support HTTP and TCP health checks,
    ## and we always pass web.input().get("path") to the healthcheck.
    ## there's probably a better way to do this.
    HEALTH_CHECKS = { "tcp": checks.TcpHealthCheck,
                      "http": checks.HttpHealthCheck }
    
    def GET(self, *args):
        data = web.input(host=None, port=None, healthCheck=None)
        if data.host is None or data.port is None:
            return json.dumps({ "response_code": "500", "message": "Must specify host and port"})
        ## look up the healthcheck in the table; default to TCP
        if data.healthCheck is not None:
            health_check = self.HEALTH_CHECKS.get(data.healthCheck) or checks.TcpHealthCheck
        else:
            health_check = checks.TcpHealthCheck
        ## add the host to the global status object, along with the associated health check.
        global_status.add_host(data.host, data.port, health_check(path=data.get("path")))
        ## return something, um, useful.
        ret_val = { "checkin_time": str(datetime.datetime.now()), "host": data.host, "port": data.port }
        return json.dumps(ret_val)
        
class status:
    def GET(self, format="json"):
        ## return the current state of the onion.
        return global_status.get_status(format)

class default:
    def GET(self, *args):
        ## meh. this could be more useful.
        usage = { "endpoints": [
            "status",
            "checkin",
            "healthcheck" ] }
        return json.dumps(usage)

class healthcheck:
    def GET(self, *args):
        global_status.check_hosts()
        return json.dumps({"response_code": 200, "message": "healthcheck started at " + str(datetime.datetime.now())})


def check_health():
    ## an infinite healthcheck loop.
    app.request("/healthcheck")
    time.sleep(10)
    t = Thread(target=check_health)
    t.start()
    sys.exit(0)
       
def start():
    ## make sure to call the health check at the very beginning; it will loop forever.
    t = Thread(target=check_health)
    t.start()
    app.run()

if __name__ == "__main__":
    start()
    
