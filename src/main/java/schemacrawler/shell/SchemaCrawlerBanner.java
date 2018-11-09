package schemacrawler.shell;


import java.io.PrintStream;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;

import schemacrawler.Version;

public class SchemaCrawlerBanner
  implements Banner
{

  @Override
  public void printBanner(final Environment environment,
                          final Class<?> sourceClass,
                          final PrintStream printStream)
  {
    printStream
      .println(AnsiOutput.toString(AnsiColor.BRIGHT_YELLOW, Version.about()));
    printStream.println();
  }

}
