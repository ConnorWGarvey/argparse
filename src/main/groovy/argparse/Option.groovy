package argparse

/**
 * Created by cgarvey on 2/28/14.
 */
class Option {
  List<String> names
  Closure function
  def value

  Option(List names, Closure function) {
    this.names = names
    this.function = function
  }

  String getName() {
    def name = null
    names.each{aName->
      if (!name || aName.size() > name.size()) name = aName
    }
    name
  }

  boolean hasName(String name) {
    names.contains name
  }

  boolean hasValue() {
    value != null
  }

  void setValue(value) {
    if (function) value = function.call(value)
    this.value = value
  }
}
