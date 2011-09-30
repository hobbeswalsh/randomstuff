#!/usr/bin/env python2.6

import time

class LetterNode(object):
    def __init__(self, data=None):
        self.data = data
        self.terminal = False
        self.children = list()

    def __repr__(self):
        return "LetterNode {0}".format(self.data)

    def __str__(self):
        return "LetterNode {0}".format(self.data)

    def __eq__(self, other):
        return hash(self) == hash(other)

    def __hash__(self):
        return hash(self.data)

    def add_child(self, node):
        self.children.append(node)

    def make_terminal(self):
        self.terminal = True

    def is_terminal(self):
        return self.terminal

    def get(self, node):
        return self.children[self.children.index(node)]
    
class Trie(object):
    root = LetterNode()

    def add_word(self, word, node=root):
        if len(word) == 0:
            return None
        letter = word[0]
        ln = LetterNode(letter)
        if ln in node.children:
            new_node = node.get(ln)
            if len(word) == 1:
                new_node.make_terminal()
            else:
                self.add_word(word[1:], new_node)
        else:
            if len(word) == 1:
                ln.make_terminal()
            node.add_child(ln)
            self.add_word(word[1:], ln)


    def find_word(self, word, node=root):
        if len(word) == 0:
            return False
        ln = LetterNode(word[0])
        if len(word) == 1:
            if ln in node.children:
                new_node = node.get(ln)
                return new_node.is_terminal()
            else:
                return False

        if ln not in node.children:
            return False
        else:
            new_node = node.get(ln)
            return self.find_word(word[1:], new_node)


    def enumerate_children(self, string):
        if len(string) == 0:
            return 0
        node = self.find_node(string)
        if node is None:
            return 0
        direct_descendants = 0
        if node.is_terminal():
            direct_descendants += 1

        kids_kids = list()
        for child in node.children:
            new_string = string + child.data
            kids_kids.append(self.enumerate_children(new_string))
        return direct_descendants + sum(kids_kids)
    
    def find_node(self, string, node=root):
        if len(string) == 0:
            return self.root
        ln = LetterNode(string[0])
        if ln in node.children:
            if len(string) == 1:
                return node.get(ln)
            else:
                return self.find_node(string[1:], node.get(ln))


    def get_child_words(self, word):
        if len(word) == 0:
            return []
        node = self.find_node(word)
        if node is None:
            return []
        words = []

        if node.is_terminal():
            words.append(word)

        kid_words = list()
        for child in node.children:
            new_word = word + child.data
            words.extend(self.get_child_words(new_word))
        return words


    def autocomplete(self, word, node=root):
        if len(word) == 0:
            return False
        return self.get_child_words(word)


        
if __name__ == "__main__":
    
    #"""
    t = Trie()
    words = open("/usr/share/dict/words").read().split("\n")
    added = 0
    before = time.time()
    for word in words:
        t.add_word(word)
        added += 1
        if added % 100 == 0:
            added = 0
            now = time.time()
            total = now - before
            average = total / 100
            print "It takes an average of %s seconds to add 100 words" % (average)
    
    
    while True:
        sought = raw_input("Enter word to find: ")
        now = time.time()
        found = t.find_word(sought)
        #found = t.autocomplete(sought)
        after = time.time()
        if found:
            print "It took me {0} seconds to find {1}".format(after - now, sought)
        else:
            print "It took me {0} seconds to fail to find {1}".format(after - now, sought)
        #"""
