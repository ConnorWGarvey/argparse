package argparse

import groovy.transform.InheritConstructors
import groovy.transform.PackageScope

class Options {
  List myOptions = []

  void flag(Object... args) {
    def (options, names, closure) = parseArgs(args)
    myOptions << new Flag(names, closure)
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
      } else throw new IllegalArgumentException('Arguments may be a list of _names, optionally followed by a closure')
    }
    null
  }

  private Closure parseOptions(ListIterator iterator) {
    Object arg = iterator.hasNext() ? iterator.next() : null
    if (arg != null) {
      if (arg instanceof Map) {
        return arg
      } else iterator.previous()
    }
    null
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
      if (option._hasName(name)) return option
    }
  }

  def getAt(String name) {
    def option = option(name)
    if (option) option._value
    else null
  }

  def putAt(String name, Object value) {
    def option = option(name)
    if (option) option.set_value(value)
    else throw new KeyNotFoundException("Key not found: $name")
  }

  def propertyMissing(String name) {
    def option = option(name)
    if (option != null) return option._value
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
        findAll{it._hasValue()}.
        collectEntries{[it._name, it._value]}
  }

  static class Option {
    List<String> _names
    Closure _function
    def _value

    Option(List _names, Closure _function) {
      this._names = _names
      this._function = _function
    }

    String get_name() {
      def name = null
      _names.each{aName->
        if (!name || aName.size() > name.size()) name = aName
      }
      name
    }

    boolean _hasName(String name) {
      _names.contains name
    }

    boolean _hasValue() {
      _value != null
    }

    void set_value(value) {
      if (_function) value = _function.call(value)
      this._value = value
    }
  }

  static class Flag {
    List<String> _names
    Closure _function
    boolean _value

    Flag(List _names, Closure _function) {
      this._names = _names
      this._function = _function
    }

    String get_name() {
      def name = null
      _names.each{aName->
        if (!name || aName.size() > name.size()) name = aName
      }
      name
    }

    boolean _hasName(String name) {
      _names.contains name
    }

    boolean _hasValue() {
      _value
    }

    boolean on() {
      _value
    }

    void set_value(value) {
      if (_function) value = _function.call(value)
      this._value = value
    }
  }

  @InheritConstructors static class KeyNotFoundException extends Exception {}
}
