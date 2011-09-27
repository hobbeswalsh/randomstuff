class MwahahaPersistence(object):
    
    def save(self,status):
        pass

## this is really a stub class right now. it's dumb, but it makes it so we don't lose state on a restart.
## of course, no state is loaded on start-up, so.... we lose it anyway.
class MongoPersistence(MwahahaPersistence):
    def __init__(self, db_host="localhost", db_port="27017", db_name="status"):
        self.db_name = db_name
        self.handle = self.connect(db_host, db_port)
        
    def connect(self, host, port):
        import pymongo
        try:
            return pymongo.Connection(host, int(port))
        except:
            return None
        
    def save(self, status):
        if self.handle is None:
            print "No DB handle!"
            return None
        document = status.to_dict()
        if status.id is not None:
            document['_id'] = status.id
        db = self.handle[self.db_name]
        c = db['status']
        return c.save(document)
