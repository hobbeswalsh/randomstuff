#!/usr/bin/env python2.6

import time
import checks

## A host should have a name and a port.
class Host:
    def __init__(self, name, port):
        self.name           = name
        self.port           = port
        self.healthchecks   = set()
        self.last_check     = int(time.time())
        self.history        = checks.HealthCheckHistory()
        self.healthy        = False
    
    def __eq__(self, other):
        return self.name == other.name and self.port == other.port
    
    def __hash__(self):
        return hash(str(self.name) + str(self.port))

    ## add a heathcheck to the list already associated with this host.
    def add_healthcheck(self, hc):
        self.healthchecks.add(hc)
    
    ## for serialization
    def to_dict(self):
        return { "name": self.name, "port": self.port }

    ## perform all healthchecks on this host.
    def check(self):
        for healthcheck in self.healthchecks:
            self.history.append(healthcheck.check(self))

	## reset all knowledge of prior healthchecks. this will put the host into a
	## non-OK state.
    def reset(self):
        self.history = checks.HealthCheckHistory()
        self.healthy = False
	
	## find out whether the host is OK. eventually we should probably store this in mongo...
    @property
    def ok(self):
        if len(self.history) == 0:
            ## no history yet. host can't be healthy!
            self.healthy = False
            return False
        results = [ result.success for result in self.history ]

        ## if we're not healthy, we need three successes to become OK
        if not self.healthy:
            if False not in results[-3:]:
                self.healthy = True
                return True
            else:
                return False
        ## if we're already healthy
        else:
            ## if the last two results are failures, we are in a bad state
            if True not in results[-2:]:
                self.healthy = False
                return False
            ## if either of the results in the last two checks is OK, we're OK.
            else:
                return True
		
