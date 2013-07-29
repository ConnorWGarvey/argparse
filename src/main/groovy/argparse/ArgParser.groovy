package argparse

class ArgParser {
  Set flags = [] as Set

  def methodMissing(String name, args) {
    def first = args[0]
    if (first instanceof Closure) {
      flags[name] = first
    }
  }

  def parse(args) {
    Iterator<String> i = args.iterator()
    while (i.hasNext()) {
      def arg = i.next()
      if (i.startsWith('-')) {
      }
    }
  }
}
