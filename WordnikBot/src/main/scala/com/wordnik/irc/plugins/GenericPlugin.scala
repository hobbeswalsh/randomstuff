package com.wordnik.irc.plugins

import scala.actors.Actor

trait GenericPlugin extends Actor

trait LoggingPlugin extends Actor

/*
 * None of this suff works yet, but I want to have it here in case I decide to
 * break up the functionality of the interfaces the way I did with PyBo
 */

trait CommandWatcher 

trait MessageWatcher

trait JoinWatcher
