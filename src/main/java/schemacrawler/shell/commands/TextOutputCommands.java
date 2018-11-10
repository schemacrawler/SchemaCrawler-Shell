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

package schemacrawler.shell.commands;


import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.tools.text.base.CommonTextOptionsBuilder;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

@ShellComponent
@ShellCommandGroup("4. Text Output Commands")
public class TextOutputCommands
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(TextOutputCommands.class.getName());

  @Autowired
  private SchemaCrawlerShellState state;

  @ShellMethodAvailability
  public Availability isLoaded()
  {
    final boolean isLoaded = state.isLoaded();
    return isLoaded? Availability.available(): Availability
      .unavailable("there is no schema metadata loaded");
  }

  @ShellMethod(value = "Set output options", prefix = "-")
  public void output(@ShellOption(defaultValue = "", help = "Title text on output") final String title)
  {
    try
    {
      LOGGER.log(Level.INFO, new StringFormat("title=%s", title));

      final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = state
        .getSchemaCrawlerOptionsBuilder();

      schemaCrawlerOptionsBuilder.title(title);
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot set output options", e);
    }
  }

  @ShellMethod(value = "Show output", prefix = "-")
  public void show(@ShellOption(arity = 1, help = "Whether to show database information") final boolean noinfo,
                   @ShellOption(arity = 1, help = "Whether to sort remarks") final boolean noremarks,
                   @ShellOption(arity = 1, help = "Whether to weak associations") final boolean weakassociations,
                   @ShellOption(arity = 1, help = "Whether to use portable names") final boolean portablenames)
  {
    try
    {
      LOGGER
        .log(Level.INFO,
             new StringFormat("noinfo=%b, noremarks=%b, weakassociations=%b, portablenames=%b",
                              noinfo,
                              noremarks,
                              weakassociations,
                              portablenames));

      final Config config = state.getAdditionalConfiguration();

      final SchemaTextOptionsBuilder textOptionsBuilder = SchemaTextOptionsBuilder
        .builder().fromConfig(config);
      textOptionsBuilder.noInfo(noinfo).noRemarks(noremarks)
        .weakAssociations(weakassociations).portableNames(portablenames);
      config.putAll(textOptionsBuilder.toConfig());

      state.setAdditionalConfiguration(config);
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot set show output options", e);
    }
  }

  @ShellMethod(value = "Sort output", prefix = "-")
  public void sort(@ShellOption(defaultValue = "false", arity = 1, help = "Whether to sort tables") final boolean sorttables,
                   @ShellOption(defaultValue = "false", arity = 1, help = "Whether to sort table columns") final boolean sortcolumns,
                   @ShellOption(defaultValue = "false", arity = 1, help = "Whether to routine parameters") final boolean sortinout)
  {
    try
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("sorttables=%b, sortcolumns=%b, sortinout=%b",
                                  sorttables,
                                  sortcolumns,
                                  sortinout));

      final Config config = state.getAdditionalConfiguration();

      final CommonTextOptionsBuilder textOptionsBuilder = CommonTextOptionsBuilder
        .builder().fromConfig(config);
      textOptionsBuilder.sortTables(sorttables).sortTableColumns(sortcolumns)
        .sortInOut(sortinout);
      config.putAll(textOptionsBuilder.toConfig());

      state.setAdditionalConfiguration(config);
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot set sort options", e);
    }
  }

}
