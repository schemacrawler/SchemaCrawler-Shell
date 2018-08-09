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

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import sf.util.SchemaCrawlerLogger;

@ShellComponent
public class ConnectCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(ConnectCommand.class.getName());

  private DataSource dataSource;

  @ShellMethod(value = "Connect to a database, using a server spefication", prefix = "-")
  public boolean connect(@ShellOption(value = "-url") final String connectionUrl,
                         final String user,
                         @ShellOption(defaultValue = "") final String password)
  {
    final BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUsername(user);
    dataSource.setPassword(password);
    dataSource.setUrl(connectionUrl);
    dataSource.setDefaultAutoCommit(false);
    dataSource.setInitialSize(1);
    dataSource.setMaxTotal(1);

    this.dataSource = dataSource;
    LOGGER.log(Level.INFO, "Database connection URL: " + connectionUrl);

    return isConnected();
  }

  @ShellMethod(key = "isconnected", value = "Check if there is a connection to the database")
  public boolean isConnected()
  {
    try (final Connection connection = dataSource.getConnection();)
    {
      LOGGER
        .log(Level.INFO,
             "Connected to: "
                         + connection.getMetaData().getDatabaseProductName());
    }
    catch (SQLException e)
    {
      LOGGER.log(Level.WARNING, e.getMessage(), e);
      return false;
    }
    return true;
  }

}
