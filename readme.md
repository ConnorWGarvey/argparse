ArgParse
========

Making command line argument parsing more Groovy

Usage
-----

ArgParse provides three ways to accept arguments.

  1. **Parameters**: `command --name value` or `command -n value`
  2. **Flags**: `command --name` or `command -n`
  3. **Arguments**: `command value`
    * Parameters, flags and arguments may be provided in any order, or even mixed together

### Example 1

The parameters to `parser.parse` are equivalent to the command `command --parameter value --flag arg`

    def parser = ArgParser.accepting { p ->
      p.param('parameter', 'p')
      p.flag('flag', 'f')
    }

    def (options, args) = parser.parse('--parameter', 'value', '--flag', 'arg')

    Result:
    options == [parameter:'value', flag:true]
    args == ['arg']

### Example 2

Flags can contain values, in this case a closure that will either truncate or not truncate other arguments.  This
command is equivalent to `command -t something`

    parser = ArgParser.accepting { p ->
      p.flag('truncate', 't', default:{it}){{arg->arg.take(2)}}
    }

    def (options, args) = parser.parse('-t', 'something')
    args = args.collect{options.truncate(it)}

    Result:
    options == [truncate:{arg->arg.take(2)}]
    args == ['so']
