package argparse

class ArgParser {
  Map<String, Closure> flags = [:]

  boolean makeFlag(String name, args) {
    if (args.size() == 1) {
      def closure = args[0]
      if (closure instanceof Closure) {
        if (closure.maximumNumberOfParameters == 0) {
          flags[name] = closure
          return true
        }
      }
    }
    return false
  }

  def methodMissing(String name, args) {
    if (makeFlag(name, args)) return
    throw new MissingMethodException(name, ArgParser, args)
  }
  
  List parse(args) {
    def options = [:]
    def remainingArgs = []
    Iterator<String> i = args.iterator()
    while (i.hasNext()) {
      def arg = i.next()
      if (arg.startsWith('-')) {
        def name = arg[1..-1]
        if (flags.containsKey(name)) {
          options[name] = flags[name]()
        }
      } else {
        remainingArgs << arg
      }
    }
    [options, remainingArgs]
  }
}
