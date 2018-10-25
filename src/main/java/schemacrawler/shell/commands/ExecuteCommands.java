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


import java.sql.Connection;

import javax.validation.constraints.NotNull;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import sf.util.SchemaCrawlerLogger;

@ShellComponent
@ShellCommandGroup("5. SchemaCrawler Commands")
public class ExecuteCommands
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ExecuteCommands.class.getName());

  @Autowired
  private SchemaCrawlerShellState state;

  @ShellMethod(value = "List available SchemaCrawler commands", prefix = "-")
  public void commands()
  {
    try
    {
      final CommandRegistry registry = new CommandRegistry();
      for (final CommandDescription command: registry)
      {
        System.out.println(command);
      }
    }
    catch (final SchemaCrawlerException e)
    {
      throw new RuntimeException("Cannot find SchemaCrawler commands", e);
    }
  }

  @ShellMethod(value = "Execute a SchemaCrawler command", prefix = "-")
  public AttributedString execute(@NotNull @ShellOption(help = "SchemaCrawler command") final String command)
  {
    try (Connection connection = state.getDataSource().getConnection();)
    {
      final SchemaCrawlerOptions schemaCrawlerOptions = state
        .getSchemaCrawlerOptionsBuilder().toOptions();
      final SchemaRetrievalOptions schemaRetrievalOptions = state
        .getSchemaRetrievalOptionsBuilder().toOptions();
      final OutputOptions outputOptions = state.getOutputOptionsBuilder()
        .toOptions();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      // Configure
      executable.setOutputOptions(outputOptions);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(state.getAdditionalConfiguration());
      executable.setConnection(connection);
      executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
      executable.execute();

      return new AttributedString(String
        .format("Output sent to ", outputOptions.getOutputResource()),
                                  AttributedStyle.DEFAULT
                                    .foreground(AttributedStyle.GREEN));
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot execute SchemaCrawler command", e);
    }
  }

  @ShellMethodAvailability
  public Availability isLoaded()
  {
    final boolean isConnected = new LoadCommands(state).isLoaded();
    return isConnected? Availability.available(): Availability
      .unavailable("there is no schema metadata loaded");
  }

}
