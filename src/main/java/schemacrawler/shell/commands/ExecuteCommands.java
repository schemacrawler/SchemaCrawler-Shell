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


import static sf.util.Utility.isBlank;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

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

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.tools.executable.CommandDaisyChain;
import schemacrawler.tools.executable.CommandDescription;
import schemacrawler.tools.executable.CommandRegistry;
import schemacrawler.tools.executable.SchemaCrawlerCommand;
import schemacrawler.tools.integration.graph.GraphOutputFormat;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

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
      LOGGER.log(Level.INFO, "commands");

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
  public AttributedString execute(@NotNull @ShellOption(help = "SchemaCrawler command") final String command,
                                  @ShellOption(value = {
                                                         "-o",
                                                         "-outputfile" }, defaultValue = "", help = "Output file name") final String outputfile,
                                  @ShellOption(value = {
                                                         "-fmt",
                                                         "-outputformat" }, defaultValue = "", help = "Format of the SchemaCrawler output") final String outputformat)
  {

    Connection connection = null;
    try
    {
      if (state.isConnected())
      {
        connection = state.getDataSource().getConnection();
      }
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.FINE, e.getMessage(), e);
      connection = null;
    }

    try
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("command=%s, outputfile=%s, outputformat=%s",
                                  command,
                                  outputfile,
                                  outputformat));

      final OutputOptionsBuilder outputOptionsBuilder = state
        .getOutputOptionsBuilder();
      if (!isBlank(outputfile))
      {
        outputOptionsBuilder.withOutputFile(Paths.get(outputfile));
      }
      else
      {
        outputOptionsBuilder.withConsoleOutput();
      }
      outputOptionsBuilder.withOutputFormatValue(outputformat);

      final SchemaCrawlerOptions schemaCrawlerOptions = state
        .getSchemaCrawlerOptionsBuilder().toOptions();
      final SchemaRetrievalOptions schemaRetrievalOptions = state
        .getSchemaRetrievalOptionsBuilder().toOptions();
      final OutputOptions outputOptions = outputOptionsBuilder.toOptions();
      final Config additionalConfiguration = state.getAdditionalConfiguration();

      // Output file name has to be specified for diagrams
      // (Check after output options have been built)
      if (GraphOutputFormat
        .isSupportedFormat(outputOptions.getOutputFormatValue())
          && isBlank(outputfile))
      {
        throw new RuntimeException("Output file has to be specified for schema diagrams");
      }

      final Catalog catalog = state.getCatalog();

      // NOTE: The daisy chain command may change the provided output
      // options for each chained command
      final SchemaCrawlerCommand scCommand = new CommandDaisyChain(command);
      scCommand.setSchemaCrawlerOptions(schemaCrawlerOptions);
      scCommand.setOutputOptions(outputOptions);
      scCommand.setAdditionalConfiguration(additionalConfiguration);
      scCommand.setIdentifiers(schemaRetrievalOptions.getIdentifiers());

      scCommand.setConnection(connection);
      scCommand.setCatalog(catalog);

      scCommand.execute();

      final String message;
      if (isBlank(outputfile))
      {
        message = "Completed";
      }
      else
      {
        message = String.format("Output sent to %s", outputfile);
      }
      return new AttributedString(message,
                                  AttributedStyle.DEFAULT
                                    .foreground(AttributedStyle.CYAN));
    }
    catch (final RuntimeException e)
    {
      throw e;
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot execute SchemaCrawler command", e);
    }
    finally
    {
      if (connection != null)
      {
        try
        {
          connection.close();
        }
        catch (final SQLException e)
        {
          throw new RuntimeException("Cannot execute SchemaCrawler command", e);
        }
      }
    }
  }

  @ShellMethodAvailability
  public Availability isLoaded()
  {
    LOGGER.log(Level.INFO, "commands");

    final boolean isLoaded = state.isLoaded();
    return isLoaded? Availability.available(): Availability
      .unavailable("there is no schema metadata loaded");
  }

}
