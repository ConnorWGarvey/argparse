package argparse

/**
 * Created by cgarvey on 12/13/13.
 */
class ArgParsed {
  static void main(String[] args) {
    def parser = new ArgParser()
    parser.option{it * 2}
  }
}
