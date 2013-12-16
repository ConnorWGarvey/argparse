package argparse

class ArgParser {
  Map<String, Closure> flags = [:]
  Options options = new Options()

  static ArgParser accepting(Closure c) {
    ArgParser parser = new ArgParser()
    c.call(parser)
    parser
  }

  void add(Map options=[:], Object... argsIn) {
    List args = new LinkedList(argsIn as List)
    def names = []
    ListIterator iterator = args.listIterator()
    while (iterator.hasNext()) {
      Object arg = iterator.next()
      if (arg instanceof String) {
        names << arg
        iterator.remove()
      }
    }
    Object next = iterator.next()
    Closure closure
    if (next instanceof Closure) {
      closure = next
      iterator.remove()
    } else throw new IllegalArgumentException('Arguments may be a list of _names, optionally followed by a closure')
    if (iterator.hasNext())
      IllegalArgumentException('Arguments may be a list of _names, optionally followed by a closure')
    if (!makeOption(names, closure)) throw new IllegalArgumentException("Could not create option")
  }

  boolean makeFlag(String name, Closure closure) {
    if (closure.maximumNumberOfParameters == 0) {
      flags[name] = closure
      return closure
    }
    false
  }

  boolean makeOption(List names, Closure closure) {
    if (closure.maximumNumberOfParameters == 1) {
      options.create(names, closure)
      return true
    }
    false
  }

  String name(arg) {
    if (arg.startsWith('--')) arg[2..-1]
    else if (arg.startsWith('-')) arg[1..-1]
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

  boolean makeOption(Map opts, args) {
    if (!args.every{it.startsWith('-')}) return false
    def names = names(args)
    
    true
  }

  def methodMissing(String name, args) {
    args = args as List
    Map options = args[0] instanceof Map ? args.remove(0) : [:]
    Closure closure = args[0] instanceof Closure ? args.remove(0) : null
    if (closure) {
      if (makeFlag(name, closure)) return
      if (makeOption(name, closure)) return
  } else if (makeOption(options, args + ["--$name"])) return
    throw new MissingMethodException(name, ArgParser, args)
  }
  
  List parse(args) {
    def parsed = [:]
    def remainingArgs = []
    Iterator<String> i = args.iterator()
    while (i.hasNext()) {
      def arg = i.next()
      if (arg.startsWith('--')) {
        def name = arg[2..-1]
        if (options.containsKey(name)) {
          if (!i.hasNext()) throw new ArgParseException("Specify a _value for $name")
          parsed[name] = options[name](i.next())
        } else remainingArgs << arg
      } else if (arg.startsWith('-')) {
        def name = arg[1..-1]
        if (flags.containsKey(name)) {
          parsed[name] = flags[name]()
        } else if (options.containsKey(name)) {
          if (!i.hasNext()) throw new ArgParseException("Specify a _value for $name")
          parsed[name] = options[name](i.next())
        } else remainingArgs << arg
      } else {
        remainingArgs << arg
      }
    }
    [parsed, remainingArgs]
  }
}
