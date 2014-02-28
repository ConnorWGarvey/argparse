package argparse

import spock.lang.FailsWith
import spock.lang.Specification
import spock.lang.Unroll

@Unroll class ArgParserSpec extends Specification {
  def parser = new ArgParser()

  def 'construct default'() {
    expect:
    ['-h','--help'].every{parser.options.has(it)}
    ['h','help'].every{parser.options.option(it).function.is(BuiltIns.help())}
  }

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
    options == [a:'b']
    options.a
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
    when: def (options, args) = parser.parse(['arg1', '--duplicate', 'a', 'arg2', '--identity', 'identity', '--truth',
        'arg3'] as String[])
    then:
    options.duplicate == 'aa'
    options.identity == 'identity'
    options.truth
    args == ['arg1', 'arg2', 'arg3']
  }

  def 'parse a flag with default and non-default values set to default'() {
    setup: parser.flag('-a', default:'off'){'on'}
    when: def (options, args) = parser.parse('hi')
    then:
    options.a == 'off'
    args == ['hi']
  }

  def 'parse a flag with default and non-default values set to non-default'() {
    setup: parser.flag('-a', default:'off'){'on'}
    when: def (options, args) = parser.parse('-a')
    then:
    options.a == 'on'
    args.isEmpty()
  }

  def 'example 2 from readme'() {
    parser = ArgParser.accepting { p ->
      p.flag('truncate', 't', default:{it}){{arg->arg.take(2)}}
    }
    when:
    def (options, args) = parser.parse(argsIn.split())
    args = args.collect{options.truncate(it)}
    then:
    options.truncate
    args == [expected]
    where:
    argsIn    | expected
    'some'    | 'some'
    'some -t' | 'so'
  }
}
