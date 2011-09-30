MWAHAHA
=======

Management and Web Administration of Heartbeat-Aware High Availability
----------------------------------------------------------------------

This tool is meant to be a lightweight "discovery service" which acts as
a central place to register all of your network services.

In certain cases, it is either undesireable or impossible (I'm looking at
you, EC2) to use true IP-failover for high availability of services like
the wonderful HAProxy (http://haproxy.1wt.eu/). In these cases, you need a 
central service where your nodes can register when they wake up.

This service should be responsible for placing a newly registered service
(or a changed service) in the "inactive" state and then performing a 
health-check and promoting it to "active" if the health-check succeeds.

Similarly, it should be periodically iterating over all services it knows about
and checking them. Active services that fail a certain number of health
checks should be demoted, and inactive services that pass a certain number
should be promoted.

This service is provided by Mwahaha. Mwahaha exposes a REST interface
that makes it easy to register, deregister, and change nodes' service
definitions, as well as fetch the current status in either JSON or XML format.

For example, let's say you have three Apache instances running on three hosts,
and Mwahaha running on some uprivileged port on a fourth host:

 * voltaire.domain.com:80
 * camus.domain.com:80
 * montaigne.domain.com:80

 * locke.domain.com:8080

When voltaire's web service is online, a request should be sent to:

    http://locke.domain.com:8080/checkin?host=voltaire.domain.com&port=80

Then when you hit:

    http://locke.domain.com:8080/status.json

You will get JSON output representing the current state of the services on the 
network. you'll only see one host (voltaire) and it will be in the "inactive" 
state. Give it some time, though, and if voltaire passes its health check, it 
will be promoted to active.


