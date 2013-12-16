package argparse

import groovy.transform.InheritConstructors
import groovy.transform.PackageScope

class Options {
  List<Option> myOptions = []

  void create(Object... args) {
    def (names, closure) = parseCreateArgs(args)
    myOptions << new Option(names, closure)
  }

  private parseCreateArgs(Object... argsIn) {List args = new LinkedList(argsIn as List)
    ListIterator iterator = args.listIterator()
    def names = parseNames(iterator)
    def closure = parseClosure(iterator)
    [names, closure]
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

  private List parseNames(ListIterator iterator) {
    def names = []
    while (iterator.hasNext()) {
      Object arg = iterator.next()
      if (arg instanceof String) {
        names << arg
        iterator.remove()
      } else {
        iterator.previous()
        return names
      }
    }
    names
  }

  @PackageScope option(name) {
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

  static class Option {
    List<String> _names
    Closure _setter
    def _value

    Option(List _names, Closure _setter) {
      this._names = _names
      this._setter = _setter
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

    void set_value(value) {
      if (_setter) value = _setter.call(value)
      this._value = value
    }
  }

  @InheritConstructors static class KeyNotFoundException extends Exception {}
}
