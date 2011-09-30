#!/usr/bin/env python2.6

import json, os
from flask import Flask, Response, make_response

from trie import Trie


t = Trie()
words = open("words").read().split("\n")
[ t.add_word(word) for word in words ]

class Responder(object):
    def __init__(self):
        pass

    def respond(self, data):
        response = make_response(data)
        response.headers["Access-Control-Allow-Origin"] = "*"
        return response


cache = {}
class ChildFinder(object):
    def __init__(self, trie=None):
        self.trie = trie
    
    def find_kids(self,word):
        if word in cache:
            answer = { "children": cache.get(word) }
        else:
            kids = self.trie.get_child_words(word)
            answer = { "children": kids }
            cache[word] = kids
        answer["query"] = word
        return Responder().respond(json.dumps(answer))

class Counter(object):
    def __init__(self, trie=None):
        self.trie = trie
    
    def get_count(self, word):
        if word in cache:
            answer = { "value": len(cache.get(word)) }
        else:
            kids = self.trie.get_child_words(word)
            cache[word] = kids
            answer = { "value": len(kids) }
        answer["query"] = word
        return Responder().respond(json.dumps(answer))

    def get_child_count(self, word=""):
        if word is None:
            word=""
        answer = {}
        answer["query"] = word
        answer["children"] = {}
        total = 0
        for num in xrange(ord("a"), ord("z")+1):
            char = chr(num)
            new_word = word + char
            if new_word in cache:
                count = len(cache.get(new_word))
                total += count
                answer["children"][new_word] = count
            else:
                kids = self.trie.get_child_words(new_word)
                cache[new_word] = kids
                count = len(kids)
                total += count
                answer["children"][new_word] = count

        answer["total"] = total
        return Responder().respond(json.dumps(answer))


app = Flask(__name__)
cf = ChildFinder(t)
cn = Counter(t)
app.add_url_rule('/children/<word>', 'children', cf.find_kids)
app.add_url_rule('/count/<word>', 'count', cn.get_count)
app.add_url_rule('/summary/<word>', 'summary', cn.get_child_count)
app.add_url_rule('/summary', 'summary', cn.get_child_count)
app.add_url_rule('/summary/', 'summary', cn.get_child_count)
    
if __name__ == "__main__":
    listen_port = int(os.environ.get("PORT")) or 5000
    app.debug == True
    app.run(host="0.0.0.0", port=listen_port)
