package argparse

import spock.lang.FailsWith
import spock.lang.Specification
import spock.lang.Unroll

@Unroll class OptionsSpec extends Specification {
  Options options = new Options()

  def 'create with one name'() {
    setup: options.param('name')
    when: options['name'] = '_value'
    then:
    options['name'] == '_value'
    options.name == '_value'
  }

  def 'create with two names'() {
    setup: options.param('name1', 'name2')
    when: options.name1 = '_value'
    then: options.name2 == '_value'
  }

  def 'create with a transformer'() {
    setup: options.param('name1', 'name2'){it*2}
    when: options.name1 = 3
    then: options.name2 == 6
  }

  def 'create with --name'() {
    setup: options.param('--name')
    when: options.name = 'value'
    then: options.name == 'value'
  }

  def 'create and set with --name'() {
    setup: options.param('--name')
    when: options['--name'] = 'value'
    then: options.name == 'value'
  }

  @FailsWith(ArgParseException) def 'create with -abc'() {
    expect: options.param('-abc')
  }

  def 'create with -a'() {
    setup: options.param('-a')
    when: options.a = 'value'
    then: options.a == 'value'
  }

  def 'create and set with -a'() {
    setup: options.param('-a')
    when: options['-a'] = 'value'
    then: options.a == 'value'
  }

  def 'create with a function'() {
    setup:
    def multiplier = 2
    options.param('name'){value->{->value*multiplier}}
    when: options.name = 3
    then: options.name.call() == 6
  }

  def 'create with a function retains variable references'() {
    setup:
    def multiplier = new Multiplier(multiplier:2)
    options.param('name'){value->{->multiplier.multiply(value)}}
    when:
    options.name = 3
    multiplier.multiplier = 3
    then: options.name.call() == 9
  }

  def 'flag with -a'() {
    setup: options.flag('-a')
    when: options['-a'] = true
    then: options.a
  }

  def 'flag with -a that does not get set'() {
    setup: options.flag('-a')
    expect: !options.a
  }

  def 'has true for param with a'() {
    when: options.param('-a')
    then: options.has('-a')
  }

  def 'has true for param with -a'() {
    when: options.param('-a')
    then: options.has('-a')
  }

  def 'has true for flag with -a'() {
    when: options.flag('-a')
    then: options.has('-a')
  }

  def 'has false when no leading dash'() {
    when: options.flag('-a')
    then: !options.has('a')
  }

  def 'has false'() {
    expect: !options.has('a')
  }

  def 'isFlag false'() {
    when: options.flag('-a')
    then: options.isFlag('a')
  }

  def 'isFlag true'() {
    when: options.param('-a')
    then: !options.isFlag('a')
  }

  def '#type name finds the longest name'() {
    options."$method"('a', 'ab', 'abc')
    expect: options.option('a')._name == 'abc'
    where:
    type     | method
    'option' | 'param'
    'flag'   | 'flag'
  }

  def 'flag with a default value of #value'() {
    when: options.flag('a', default:value){'b'}
    then:
    options.a == value
    options.values() == [a:value]
    where:
    value << ['a', false, true]
  }

  def 'flag with a default value of #defaultValue overridden to #value'() {
    options.flag('a', default:defaultValue){value}
    when: options.a = true
    then:
    options.a == value
    options.values() == [a:value]
    where:
    defaultValue | value
    'a'          | 'b'
    false        | true
  }

  def 'values when a value has not been set'() {
    options.param('param')
    options.flag('flag')
    expect: options.values() == [:]
  }

  def 'values when values have been set'() {
    options.param('param')
    options.flag('flag')
    when:
    options.param = 'value'
    options.flag = true
    then: options.values() == [param:'value', flag:true]
  }

  static class Multiplier {
    def multiplier
    int multiply(value) {
      value*multiplier
    }
  }
}
