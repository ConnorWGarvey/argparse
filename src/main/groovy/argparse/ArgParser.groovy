package argparse

class ArgParser {
  Options options = new Options()

  static ArgParser accepting(Closure c) {
    ArgParser parser = new ArgParser()
    c.call(parser)
    parser
  }

  void param(Object... args) {
    options.param(args)
  }

  void flag(Object... args) {
    options.flag(args)
  }

  List parse(Iterable args) {
    def remainingArgs = []
    Iterator<String> i = args.iterator()
    while (i.hasNext()) {
      def arg = i.next()
      if (options.has(arg)) {
        if (options.isFlag(arg)) options[arg] = true
        else {
          if (!i.hasNext()) throw new ArgParseException("Please provide a value for $arg")
          options[arg] = i.next()
        }
      } else {
        remainingArgs << arg
      }
    }
    [options.values(), remainingArgs]
  }

  List parse(String... args) {
    parse(args as List)
  }
}
