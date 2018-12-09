/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.shell.commands;


import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

@ShellComponent
@ShellCommandGroup("2. Filter Commands")
public class FilterCommands
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(FilterCommands.class.getName());

  @Autowired
  private SchemaCrawlerShellState state;

  @ShellMethod(value = "Filter database object metadata", prefix = "-")
  public void filter(@ShellOption(defaultValue = "false", help = "Include only tables that have rows of data") final boolean noemptytables,
                     @ShellOption(defaultValue = "0", help = "Number of generations of ancestors for the tables selected by grep") final int parents,
                     @ShellOption(defaultValue = "0", help = "Number of generations of descendents for the tables selected by grep") final int children)
  {
    try
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("noemptytables=%b, parents=%d, children=%d",
                                  noemptytables,
                                  parents,
                                  children));

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
                   @ShellOption(value = "-invert-match", arity = 1, defaultValue = "false", help = "Invert the grep match") final boolean invertMatch,
                   @ShellOption(value = "-only-matching", arity = 1, defaultValue = "false", help = "Show only matching tables, and not foreign keys that reference other non-matching tables") final boolean onlyMatching)
  {
    try
    {
      LOGGER
        .log(Level.INFO,
             new StringFormat("grepcolumns=%s, grepinout=%s, grepdef=%s, invertMatch=%b, onlyMatching=%b",
                              grepcolumns,
                              grepinout,
                              grepdef,
                              invertMatch,
                              onlyMatching));

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

  @ShellMethodAvailability
  public Availability isConnected()
  {
    final boolean isConnected = state.isConnected();
    LOGGER.log(Level.INFO, new StringFormat("isConnected=%b", isConnected));

    return isConnected? Availability.available(): Availability
      .unavailable("there is no database connection");
  }

  @ShellMethod(value = "Limit database object metadata", prefix = "-")
  public void limit(@ShellOption(defaultValue = ".*", help = "Regular expression to match fully qualified names of schemas to include") final String schemas,
                    @ShellOption(defaultValue = "", help = "Comma-separated list of table types") final String tabletypes,
                    @ShellOption(defaultValue = ".*", help = "Regular expression to match fully qualified names of tables to include") final String tables,
                    @ShellOption(defaultValue = "", help = "Regular expression to match fully qualified names of columns to exclude") final String excludecolumns,
                    @ShellOption(defaultValue = "", help = "Comma-separated list of routine types") final String routinetypes,
                    @ShellOption(defaultValue = "", help = "Regular expression to match fully qualified names of routines to include") final String routines,
                    @ShellOption(defaultValue = "", help = "Regular expression to match fully qualified names of parameters to exclude") final String excludeinout,
                    @ShellOption(defaultValue = "", help = "Regular expression to match fully qualified names of synonyms to include") final String synonyms,
                    @ShellOption(defaultValue = "", help = "Regular expression to match fully qualified names of sequences to include") final String sequences)
  {
    try
    {
      LOGGER
        .log(Level.INFO,
             new StringFormat("schemas=%s, tabletypes=%s, tables=%s, excludecolumns=%, routinetypes=%s, routines=%s, excludeinout=%s, synonyms=%s, sequences=%s",
                              schemas,
                              tabletypes,
                              tables,
                              excludecolumns,
                              routinetypes,
                              routines,
                              excludeinout,
                              synonyms,
                              sequences));

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = state
        .getSchemaCrawlerOptionsBuilder();

      schemaCrawlerOptionsBuilder
        .includeSchemas(new RegularExpressionInclusionRule(schemas));

      schemaCrawlerOptionsBuilder.tableTypes(tabletypes)
        .includeTables(new RegularExpressionInclusionRule(tables))
        .includeColumns(new RegularExpressionExclusionRule(excludecolumns));

      schemaCrawlerOptionsBuilder.routineTypes(routinetypes)
        .includeRoutines(new RegularExpressionInclusionRule(routines))
        .includeRoutineColumns(new RegularExpressionExclusionRule(excludeinout));

      schemaCrawlerOptionsBuilder
        .includeSynonyms(new RegularExpressionInclusionRule(synonyms));

      schemaCrawlerOptionsBuilder
        .includeSequences(new RegularExpressionInclusionRule(sequences));
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot set limit options", e);
    }
  }

}
