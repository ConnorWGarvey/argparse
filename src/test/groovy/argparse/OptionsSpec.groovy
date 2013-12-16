package argparse

import spock.lang.Specification

class OptionsSpec extends Specification {
  Options options = new Options()

  def 'create with one name'() {
    setup: options.create('name')
    when: options['name'] = '_value'
    then:
    options['name'] == '_value'
    options.name == '_value'
  }

  def 'create with two names'() {
    setup: options.create('name1', 'name2')
    when: options.name1 = '_value'
    then: options.name2 == '_value'
  }

  def 'create with a transformer'() {
    setup: options.create('name1', 'name2'){it*2}
    when: options.name1 = 3
    then: options.name2 == 6
  }

  def 'create with a function'() {
    setup:
    def multiplier = 2
    options.create('name'){value->{->value*multiplier}}
    when: options.name = 3
    then: options.name.call() == 6
  }

  def 'create with a function retains variable references'() {
    setup:
    def multiplier = new Multiplier(multiplier:2)
    options.create('name'){value->{->multiplier.multiply(value)}}
    when:
    options.name = 3
    multiplier.multiplier = 3
    then: options.name.call() == 9
  }

  def 'options.name finds the longest name'() {
    options.create('a', 'ab', 'abc')
    expect: options.option('a')._name == 'abc'
  }

  static class Multiplier {
    def multiplier
    int multiply(value) {
      value*multiplier
    }
  }
}
