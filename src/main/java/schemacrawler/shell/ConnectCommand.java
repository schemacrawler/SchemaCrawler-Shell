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


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.validation.constraints.NotNull;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseConfigConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;
import sf.util.SchemaCrawlerLogger;
import us.fatehi.commandlineparser.CommandLineUtility;

@ShellComponent
@ShellCommandGroup("Database Connection Commands")
public class ConnectCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ConnectCommand.class.getName());

  @Autowired
  private SchemaCrawlerShellState state;
  private Config config;
  private DatabaseConnector databaseConnector;

  @ShellMethod(value = "Connect to a database, using a connection URL specification", prefix = "-")
  public boolean connectUrl(@ShellOption(value = "-url") @NotNull final String connectionUrl,
                            @NotNull final String user,
                            @ShellOption(defaultValue = "") final String password)
    throws SchemaCrawlerException, SQLException
  {

    LOGGER.log(Level.INFO, "Database connection URL: " + connectionUrl);

    LOGGER.log(Level.INFO, "Looking up database connector");
    lookupDatabaseConnectorFromUrl(connectionUrl);
    LOGGER.log(Level.INFO, "Loading config");
    loadConfig();
    LOGGER.log(Level.INFO, "Loading SchemaCrawler options");
    loadSchemaCrawlerOptionsBuilder();
    LOGGER.log(Level.INFO, "Creating data source");
    createDataSource(connectionUrl, user, password);
    LOGGER.log(Level.INFO, "Loading schema retrieval options");
    loadSchemaRetrievalOptionsBuilder();

    LOGGER.log(Level.INFO, "Checking connection");
    return isConnected();
  }

  @ShellMethod(value = "Connect to a database, using a server specification", prefix = "-")
  public boolean connect(@ShellOption(value = "-server") @NotNull final String databaseSystemIdentifier,
                         @ShellOption(defaultValue = "") final String host,
                         @ShellOption(defaultValue = "") final String port,
                         @ShellOption(defaultValue = "") final String database,
                         @ShellOption(defaultValue = "") final String urlx,
                         @NotNull final String user,
                         @ShellOption(defaultValue = "") final String password)
    throws SchemaCrawlerException, SQLException
  {
    final SingleUseUserCredentials userCredentials = new SingleUseUserCredentials(user,
                                                                                  password);
    final DatabaseConfigConnectionOptions connectionOptions = new DatabaseConfigConnectionOptions(userCredentials,
                                                                                                  config);
    final String connectionUrl = connectionOptions.getConnectionUrl();

    return connectUrl(connectionUrl, user, password);
  }

  @ShellMethod(value = "Check if there is a connection to the database")
  public boolean isConnected()
  {
    try (final Connection connection = state.getDataSource().getConnection();)
    {
      LOGGER
        .log(Level.INFO,
             "Connected to: "
                         + connection.getMetaData().getDatabaseProductName());
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      return false;
    }
    return true;
  }

  private void createDataSource(final String connectionUrl,
                                final String user,
                                final String password)
  {
    final BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUsername(user);
    dataSource.setPassword(password);
    dataSource.setUrl(connectionUrl);
    dataSource.setDefaultAutoCommit(false);
    dataSource.setInitialSize(1);
    dataSource.setMaxTotal(1);

    state.setDataSource(dataSource);
  }

  private void loadConfig()
    throws SchemaCrawlerException
  {
    // TODO: Find a way to get command-line arguments
    final Config argsMap = new Config();
    config = CommandLineUtility.loadConfig(argsMap, databaseConnector);

    state.setAdditionalConfiguration(config);
  }

  private void loadSchemaCrawlerOptionsBuilder()
  {
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder = SchemaCrawlerOptionsBuilder
      .builder();
    schemaCrawlerOptionsBuilder.fromConfig(config);
    state.setSchemaCrawlerOptionsBuilder(schemaCrawlerOptionsBuilder);
  }

  private void loadSchemaRetrievalOptionsBuilder()
    throws SQLException
  {
    try (final Connection connection = state.getDataSource().getConnection();)
    {
      final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder = databaseConnector
        .getSchemaRetrievalOptionsBuilder(connection);
      schemaRetrievalOptionsBuilder.fromConfig(config);
      state.setSchemaRetrievalOptionsBuilder(schemaRetrievalOptionsBuilder);
    }
  }

  private void lookupDatabaseConnectorFromUrl(final String connectionUrl)
    throws SchemaCrawlerException
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    databaseConnector = registry.lookupDatabaseConnectorFromUrl(connectionUrl);
  }

}
