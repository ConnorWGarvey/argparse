package argparse

import spock.lang.Specification
import spock.lang.Unroll

@Unroll class ArgParseSpec extends Specification {
  def parser = new ArgParser()

  def 'parse a flag for #conf and #argsIn'() {
    setup: conf.each{k,v->parser."$k"{->v}}
    when: def (actualOptions, actualArgs) = parser.parse(argsIn.split())
    then:
    actualOptions == options
    actualArgs == args
    where:
    conf           | argsIn    | myOptions        | args
    [a:'e']        | ''        | [:]            | []
    [a:'e']        | '-a'      | [a:'e']        | []
    [a:'e']        | '-a b'    | [a:'e']        | ['b']
    [a:'e']        | '-a b c'  | [a:'e']        | ['b', 'c']
    [a:'e', b:'f'] | '-a'      | [a:'e']        | []
    [a:'e', b:'f'] | '-a -b'   | [a:'e', b:'f'] | []
    [a:'e', b:'f'] | '-a b -b' | [a:'e', b:'f'] | ['b']
  }

  def 'parse an option'() {
    setup: conf.each{name->parser."$name"{it}}
    when: def (actualOptions, actualArgs) = parser.parse(argsIn.split())
    then:
    actualOptions == options
    actualArgs == args
    where:
    conf  | argsIn   | myOptions | args
    ['a'] | '-a e'   | [a:'e'] | []
    ['a'] | '-a e f' | [a:'e'] | ['f']
  }

  def 'parse an invalid option'() {
    setup:
    conf.each{name->parser."$name"{it}}
    when: parser.parse(argsIn.split())
    then:
    thrown ArgParseException
    where:
    conf  | argsIn
    ['a'] | '-a'
  }

  def 'parse normal stuff'() {
    setup:
    def parser = ArgParser.accepting {p->
      p.timesTwo{Integer.parseInt(it) * 2}
      p.add('-i', '--identity')
      p.otherIdentity('-o')
    }
    when: def (options, args) = parser.parse(['--timesTwo', 6, '--identity', 'identity'] as String[])
    then:
    options.timesTwo == 12
    options.identity == 'identity'
  }
}
