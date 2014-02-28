package argparse

import groovy.transform.InheritConstructors
import groovy.transform.PackageScope
import groovy.transform.ToString

@ToString class Options {
  List myOptions = []

  void flag(Object... args) {
    def (options, names, closure) = parseArgs(args)
    myOptions << new Flag(options, names, closure)
  }

  boolean has(Map options=[:], String name) {
    option(name, requireLeadingDashes:true)
  }

  boolean isFlag(String name) {
    option(name) instanceof Flag
  }

  void param(Object... args) {
    def (options, names, closure) = parseArgs(args)
    myOptions << new Option(names, closure)
  }

  private parseArgs(Object... argsIn) {List args = new LinkedList(argsIn as List)
    ListIterator iterator = args.listIterator()
    def options = parseOptions(iterator)
    def names = parseNames(iterator)
    def closure = parseClosure(iterator)
    [options, names, closure]
  }

  private Closure parseClosure(ListIterator iterator) {
    Object arg = iterator.hasNext() ? iterator.next() : null
    if (arg != null) {
      if (arg instanceof Closure) {
        return arg
      } else throw new IllegalArgumentException('Arguments may be a map (optional), then a list of names, then a closure (optional)')
    }
    null
  }

  private Map parseOptions(ListIterator iterator) {
    Object arg = iterator.hasNext() ? iterator.next() : null
    if (arg != null) {
      if (arg instanceof Map) {
        return arg
      } else iterator.previous()
    }
    [:]
  }

  private List parseNames(ListIterator iterator) {
    def names = []
    while (iterator.hasNext()) {
      Object arg = iterator.next()
      if (arg instanceof String) {
        names << parseName(arg)
        iterator.remove()
      } else {
        iterator.previous()
        return names
      }
    }
    names
  }

  private String parseName(Map options=[:], String name) {
    if (name.startsWith('--')) name[2..-1]
    else if (name.startsWith('-')) {
      if (name.size() != 2) throw new ArgParseException(
          "Options starting with '-' can only be 1 character. $name is too long")
      name[1..1]
    } else if (options.requireLeadingDashes) null
    else name
  }

  @PackageScope option(Map options=[:], name) {
    name = parseName(options, name)
    if (!name) return null
    for (option in myOptions) {
      if (option.hasName(name)) return option
    }
  }

  def getAt(String name) {
    def option = option(name)
    if (option) option.value
    else null
  }

  def putAt(String name, Object value) {
    def option = option(name)
    if (option) option.setValue(value)
    else throw new KeyNotFoundException("Key not found: $name")
  }

  def propertyMissing(String name) {
    def option = option(name)
    if (option != null) return option.value
    throw new MissingPropertyException(name, ArgParser)
  }

  def propertyMissing(String name, Object value) {
    try {
      putAt(name, value)
    } catch (KeyNotFoundException ex) {
      throw new MissingPropertyException(name, Options)
    }
  }

  def values() {
    myOptions.
        findAll{it.hasValue()}.
        collectEntries{[it.name, it.value]}
  }

  @InheritConstructors static class KeyNotFoundException extends Exception {}
}
