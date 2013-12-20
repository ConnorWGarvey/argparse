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

  boolean makeOption(List names, Closure closure) {
    if (closure.maximumNumberOfParameters == 1) {
      options.param(names, closure)
      return true
    }
    false
  }

  String name(arg) {
    if (arg.startsWith('--')) arg[2..-1]
    else if (arg.startsWith('-')) arg[1..2]
    else null
  }

  String names(args) {
    args.collect{name(it)}
  }

  String longestName(names) {
    def name = null
    names.each{aName->
      if (!name || aName.size() > name.size()) name = aName
    }
    name
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
