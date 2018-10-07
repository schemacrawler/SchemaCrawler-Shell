/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.shell;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import sf.util.SchemaCrawlerLogger;

@ShellComponent
@ShellCommandGroup("3. Filter Commands")
public class FilterCommands
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(FilterCommands.class.getName());

  @Autowired
  private SchemaCrawlerShellState state;

  @ShellMethodAvailability
  public Availability isConnected()
  {
    final boolean isConnected = new ConnectCommands(state).isConnected();
    return isConnected? Availability.available(): Availability
      .unavailable("there is no database connection");
  }

  @ShellMethod(value = "Filter database object metadata", prefix = "-")
  public void filter(@ShellOption(defaultValue = "false", help = "Include only tables that have rows of data") final boolean noemptytables,
                     @ShellOption(defaultValue = "0", help = "Number of generations of ancestors for the tables selected by grep") int parents,
                     @ShellOption(defaultValue = "0", help = "Number of generations of descendents for the tables selected by grep") int children)
  {
    try
    {
      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = state
        .getSchemaCrawlerOptionsBuilder();

      schemaCrawlerOptionsBuilder.noEmptyTables(noemptytables);
      schemaCrawlerOptionsBuilder.parentTableFilterDepth(parents);
      schemaCrawlerOptionsBuilder.childTableFilterDepth(children);
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot set filter options", e);
    }
  }

  @ShellMethod(value = "Grep database object metadata", prefix = "-")
  public void grep(@ShellOption(defaultValue = "", help = "grep for tables with column names matching pattern") final String grepcolumns,
                   @ShellOption(defaultValue = "", help = "grep for routines with parameter names matching pattern") final String grepinout,
                   @ShellOption(defaultValue = "", help = "grep for tables definitions containing pattern") final String grepdef,
                   @ShellOption(value = "-invert-match", defaultValue = "false", help = "Invert the grep match") final boolean invertMatch,
                   @ShellOption(value = "-only-matching", defaultValue = "false", help = "Show only matching tables, and not foreign keys that reference other non-matching tables") final boolean onlyMatching)
  {
    try
    {
      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = state
        .getSchemaCrawlerOptionsBuilder();

      schemaCrawlerOptionsBuilder
        .includeGreppedColumns(new RegularExpressionInclusionRule(grepcolumns));
      schemaCrawlerOptionsBuilder
        .includeGreppedRoutineColumns(new RegularExpressionInclusionRule(grepinout));
      schemaCrawlerOptionsBuilder
        .includeGreppedDefinitions(new RegularExpressionInclusionRule(grepdef));

      schemaCrawlerOptionsBuilder.invertGrepMatch(invertMatch);
      schemaCrawlerOptionsBuilder.grepOnlyMatching(onlyMatching);
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot set grep options", e);
    }
  }

}
