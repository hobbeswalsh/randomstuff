#!/usr/bin/env python2.6

import socket, urllib
import results

### HealthCheck base class
class HealthCheck(object):
    
    def __init__(self, **kwargs):
        for k,v in kwargs.items():
            setattr(self,k,v)
        
    def __str__(self):
        return "BaseHealthCheck"
    
    def check(self):
        pass
    
    def succeed(self, host):
        result = results.SuccessfulHealthCheckResult(host)
        return result
    
    def fail(self, host):
        result = results.FailedHealthCheckResult(host)
        return result
        

## TCP health check
class TcpHealthCheck(HealthCheck):
    
    def __str__(self):
        return "TCPHealthCheck"
    
    def __hash__(self):
        return hash(str(self))
   
    ## just open the port.
    def check(self, host):
        address_tuple = (str(host.name), str(host.port))
        try:
            s = socket.create_connection(address_tuple)
            return self.succeed(host)
        except:
            return self.fail(host)

## HTTP health check
class HttpHealthCheck(HealthCheck):
    
    def __str__(self):
        return "HTTPHealthCheck"

    def check(self, host):
        hostname = str(host.name)
        port     = str(host.port)
        path     = str((self.path or "/"))
        ## perform a GET request and make sure the response code is 200
        try:
            url = "http://{0}.:{1}{2}".format(hostname, port, path)
            result = urllib.urlopen(url)
            if result.code == 200:
                return self.succeed(host)
            else:
                return self.fail(host)
        except:
            return self.fail(host)

## does this belong in another package?
class HealthCheckHistory(list):
    
    def append(self,obj):
        list.append(self, obj)
        if len(self) > 100:
            self.remove(self[0])
        return None
