/*
 * ======================================================================== SchemaCrawler
 * http://www.schemacrawler.com Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>. All
 * rights reserved. ------------------------------------------------------------------------
 *
 * SchemaCrawler is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * SchemaCrawler and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0, GNU General Public License v3 or GNU Lesser General Public License v3.
 *
 * You may elect to redistribute this code under any of these licenses.
 *
 * The Eclipse Public License is available at: http://www.eclipse.org/legal/epl-v10.html
 *
 * The GNU General Public License v3 and the GNU Lesser General Public License v3 are available at:
 * http://www.gnu.org/licenses/
 *
 * ========================================================================
 */

package schemacrawler.shell;


import java.sql.Connection;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import sf.util.SchemaCrawlerLogger;

@ShellComponent
@ShellCommandGroup("3. SchemaCrawler Commands")
public class ExecuteCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ExecuteCommand.class.getName());

  @Autowired
  private SchemaCrawlerShellState state;

  @ShellMethod(value = "List available SchemaCrawler commands", prefix = "-")
  public void commands()
    throws Exception
  {
    final CommandRegistry registry = new CommandRegistry();
    for (final String command: registry)
    {
      System.out.println(command);
    }
  }

  @ShellMethod(value = "Execute a SchemaCrawler command", prefix = "-")
  public void execute(@ShellOption @NotNull final String command)
    throws Exception
  {
    try (Connection connection = state.getDataSource().getConnection();)
    {
      final SchemaCrawlerOptions schemaCrawlerOptions = state
        .getSchemaCrawlerOptionsBuilder().toOptions();
      final SchemaRetrievalOptions schemaRetrievalOptions = state
        .getSchemaRetrievalOptionsBuilder().toOptions();
      final OutputOptions outputOptions = OutputOptionsBuilder
        .newOutputOptions();

      final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
      // Configure
      executable.setOutputOptions(outputOptions);
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(state.getAdditionalConfiguration());
      executable.setConnection(connection);
      executable.setSchemaRetrievalOptions(schemaRetrievalOptions);
      executable.execute();
    }
  }

  @ShellMethodAvailability
  public Availability isLoaded()
  {
    final boolean isConnected = new LoadCommand(state).isLoaded();
    return isConnected? Availability.available(): Availability
      .unavailable("there is no schema metadata loaded");
  }

}
