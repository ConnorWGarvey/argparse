package argparse

import groovy.transform.ToString

/**
 * @since 2/28/14.
 */
@ToString class Flag extends Option {
  boolean active = false
  def value

  Flag(Map options=[:], List names, Closure function) {
    super(names, function)
    if (options.containsKey('default')) {
      this.value = options.default
      this.active = true
    } else this.value = false
  }

  boolean hasValue() {
    active || value
  }

  void setValue(value) {
    if (function) value = function.call(value)
    this.value = value
  }
}
