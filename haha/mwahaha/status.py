#!/usr/bin/env python2.6

from host import Host
import checks, persistence

import Queue, datetime, json, time
import xml.etree.ElementTree as etree
from threading import Thread


### Global Status object.          
class Status:
    def __init__(self):
        self.known_hosts = dict()
        self.id = None

    ## just return a list of hosts.
    @property
    def hosts(self):
        return self.known_hosts.keys()
    
    ## ugly, but it works.    
    def get_status_xml(self):
        status = etree.Element("status")
        good_hosts = etree.Element("goodHosts")
        bad_hosts = etree.Element("badHosts")
        status.append(good_hosts)
        status.append(bad_hosts)
        
        for host in self.hosts:
            if host.ok:
                good_hosts.append(etree.Element("host", host.to_dict()))
            else:
                bad_hosts.append(etree.Element("host", host.to_dict()))

        return etree.tostring(status)
    
    ## with json we just need to convert ourselves to a dictionary.            
    def get_status_json(self):
        return json.dumps(self.to_dict())
    
    ## currently we support json and xml. what's next, Thrift? YAML?
    def get_status(self, type="json"):
        if type == "json":
            return self.get_status_json()
        elif type == "xml":
            return self.get_status_xml()
        else:
            return json.dumps({})
   
    ## add a host to our list of known hosts. if the host is already in there, check to 
    ## see if we need to add a new healthcheck.
    def add_host(self, name, port, healthcheck):
        h = Host(name,port)
        all_hosts = self.hosts
        if h in all_hosts:
            host = all_hosts[all_hosts.index(h)]
            if healthcheck not in host.healthchecks:
                host.add_healthcheck(healthcheck)
                host.reset()
            else:
                return True
        else:
            h.add_healthcheck(healthcheck)
            self.known_hosts[h] = datetime.datetime.now()
 
    ## cheap-o serializer
    def to_dict(self):
        healthy = list()
        unhealthy = list()
        for host in self.hosts:
            if host.ok:
                healthy.append(host.to_dict())
            else:
                unhealthy.append(host.to_dict())
        return { "healthy": healthy, "unhealthy": unhealthy }
    
    ## this should just get called from /healthcheck
    def check_hosts(self):
        for host in self.hosts:
            t = Thread(target=host.check)
            t.start()
        p = persistence.MongoPersistence()
        self.id = p.save(self)
