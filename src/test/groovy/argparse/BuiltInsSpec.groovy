package argparse

import org.gmock.GMockController
import spock.lang.Specification

/**
 * @since 2/28/14.
 */
class BuiltInsSpec extends Specification {
  def gmock = new GMockController()


  def 'prints help and exits'() {
    def system = gmock.mock(System)
    system.static.exit(0) // Spock doesn't support mocking non-Groovy statics, like System.exit, so use Gmock
    when:
    gmock.play {
      BuiltIns.help().call()
    }
    then: true
  }
}
