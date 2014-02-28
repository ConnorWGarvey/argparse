package argparse

import groovy.transform.Memoized

/**
 * @since  2/28/14.
 */
class BuiltIns {
  @Memoized static Closure help() {
    {->
      println 'help'
      System.exit(0)
    }
  }
}
