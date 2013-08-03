package argparse

class ArgParser {
  Map<String, Closure> flags = [:]
  
  def methodMissing(String name, args) {
    if (args.size() == 1) {
      def closure = args[0]
      if (closure instanceof Closure) {
        flags[name] = closure
      } else {
        throw new IllegalArgumentException("The value for $name must be a closure")
      }
    } else {
      throw new IllegalArgumentException("The value for $name must be a closure")
    }
  }
  
  Map<String, Object> parse(args) {
    def options = [:]
    Iterator<String> i = args.iterator()
    while (i.hasNext()) {
      def arg = i.next()
      if (arg.startsWith('-')) {
        def name = arg[1..-1]
        if (flags.containsKey(name)) {
          options[name] = flags[name]()
        }
      }
    }
    options
  }
}
