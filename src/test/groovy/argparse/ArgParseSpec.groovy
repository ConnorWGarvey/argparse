package argparse

import spock.lang.FailsWith
import spock.lang.Specification
import spock.lang.Unroll

@Unroll class ArgParseSpec extends Specification {
  def parser = new ArgParser()

  def 'parse a flag for #conf and #argsIn'() {
    setup: conf.each{parser.flag(it)}
    when: def (actualOptions, actualArgs) = parser.parse(argsIn.split())
    then:
    actualOptions == options.collectEntries{[it, true]}
    actualArgs == args
    where:
    conf       | argsIn    | options    | args
    ['a']      | ''        | []         | []
    ['a']      | '-a'      | ['a']      | []
    ['a']      | '-a b'    | ['a']      | ['b']
    ['a']      | '-a b c'  | ['a']      | ['b', 'c']
    ['a', 'b'] | '-a'      | ['a']      | []
    ['a', 'b'] | '-a -b'   | ['a', 'b'] | []
    ['a', 'b'] | '-a b -b' | ['a', 'b'] | ['b']
  }

  def 'parse a flag with a function'() {
    setup: parser.flag('a'){'b'}
    when: def (options, args) = parser.parse('-a', '-b')
    then:
    options.a
    options == [a:true]
    args == ['-b']
  }

  def 'parse an option with args #argsIn'() {
    setup: conf.each{parser.param(it)}
    when: def (actualOptions, actualArgs) = parser.parse(argsIn.split())
    then:
    actualOptions == options
    actualArgs == args
    where:
    conf  | argsIn   | options | args
    ['a'] | '-a e'   | [a:'e'] | []
    ['a'] | '-a e f' | [a:'e'] | ['f']
  }

  def 'parse an option with a function'() {
    setup: parser.param('a'){it*2}
    when: def (options, args) = parser.parse('-a', 'ta', '-b')
    then:
    options.a == 'tata'
    options == [a:'tata']
    args == ['-b']
  }

  @FailsWith(ArgParseException) def 'parse an invalid option'() {
    parser.param('a')
    expect: parser.parse('-a')
  }

  def 'parse normal stuff'() {
    setup:
    def parser = ArgParser.accepting {p->
      p.param('--duplicate', {it*2})
      p.param('identity')
      p.flag('truth')
    }
    when: def (options, args) = parser.parse(['--duplicate', 'a', '--identity', 'identity', '--truth'] as String[])
    then:
    options.duplicate == 'aa'
    options.identity == 'identity'
    options.truth
  }
}
