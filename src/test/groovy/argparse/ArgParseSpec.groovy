package argparse

import spock.lang.Specification

class ArgParseSpec extends Specification {
  def parser = new ArgParser()
  
  def 'parse a flag'() {
    setup:
    conf.each{k,v->parser."$k"{->v}}
    when: def (actualOptions, actualArgs) = parser.parse(argsIn.split())
    then:
    actualOptions == options
    actualArgs == args
    where:
    conf    | argsIn   | options | args
    [a:'e'] | ''       | [:]     | []
    [a:'e'] | '-a'     | [a:'e'] | []
    [a:'e'] | '-a b'   | [a:'e'] | ['b']
    [a:'e'] | '-a b c' | [a:'e'] | ['b', 'c']
  }
}
