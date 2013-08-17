package argparse

class ArgParser {
  Map<String, Closure> flags = [:]
  Map<String, Closure> options = [:]

  Closure parameter(args) {
    if (args.size() == 1) {
      def closure = args[0]
      if (closure instanceof Closure) {
        return closure
      }
    }
    null
  }

  boolean makeFlag(String name, Closure closure) {
    if (closure.maximumNumberOfParameters == 0) {
      flags[name] = closure
      return closure
    }
    false
  }

  boolean makeOption(String name, Closure closure) {
    if (closure.maximumNumberOfParameters == 1) {
      options[name] = closure
      return closure
    }
    false
  }

  def methodMissing(String name, args) {
    Closure closure = parameter(args)
    if (closure) {
      if (makeFlag(name, closure)) return
      if (makeOption(name, closure)) return
    }
    throw new MissingMethodException(name, ArgParser, args)
  }
  
  List parse(args) {
    def parsed = [:]
    def remainingArgs = []
    Iterator<String> i = args.iterator()
    while (i.hasNext()) {
      def arg = i.next()
      if (arg.startsWith('-')) {
        def name = arg[1..-1]
        if (flags.containsKey(name)) {
          parsed[name] = flags[name]()
        } else if (options.containsKey(name)) {
          if (!i.hasNext()) throw new ArgParseException("Specify a value for " + name)
          parsed[name] = options[name](i.next())
        }
      } else {
        remainingArgs << arg
      }
    }
    [parsed, remainingArgs]
  }
}
