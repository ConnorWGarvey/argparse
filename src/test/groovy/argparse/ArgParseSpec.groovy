package argparse

import spock.lang.Specification

class ArgParseSpec extends Specification {
  def parser = new ArgParser()
  
  def 'parse a flag'() {
    setup:
    def expected = 'expected'
    parser.arg{'expected'}
    when: def actual = parser.parse(['-arg'])
    then:
    actual == [arg:expected]
  }
}
