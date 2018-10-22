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

package schemacrawler.shell.test.unit;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.standard.StandardMethodTargetRegistrar;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import schemacrawler.shell.commands.ConnectCommands;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.shell.test.BaseSchemaCrawlerShellTest;
import schemacrawler.shell.test.TestSchemaCrawlerShellState;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
                                  TestSchemaCrawlerShellState.class,
                                  ConnectCommands.class })
public class ConnectCommandsTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = ConnectCommands.class;

  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();
  @Autowired
  private SchemaCrawlerShellState state;
  @Autowired
  private ApplicationContext context;

  @Test
  public void connect()
    throws SQLException
  {
    final String command = "connect";
    final String commandMethod = "connect";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("1. Database Connection Commands"));
    assertThat(commandTarget.getHelp(),
               is("Connect to a database, using a server specification"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             String.class,
                             String.class,
                             int.class,
                             String.class,
                             String.class,
                             String.class,
                             String.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));
    assertThat(invoke(commandTarget,
                      "hsqldb",
                      "",
                      0,
                      "schemacrawler",
                      "",
                      "sa",
                      ""),
               is(true));

    assertThat(state.getDataSource(), notNullValue());
    try (final Connection connection = state.getDataSource().getConnection();)
    {
      assertThat(connection, notNullValue());
      assertThat(connection.getCatalog(), is("PUBLIC"));
    }
  }

  @Test
  public void connectUrl()
    throws SQLException
  {
    final String command = "connect-url";
    final String commandMethod = "connectUrl";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("1. Database Connection Commands"));
    assertThat(commandTarget.getHelp(),
               is("Connect to a database, using a connection URL"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             String.class,
                             String.class,
                             String.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));
    assertThat(invoke(commandTarget,
                      "jdbc:hsqldb:hsql://localhost:9001/schemacrawler",
                      "sa",
                      ""),
               is(true));

    assertThat(state.getDataSource(), notNullValue());
    try (final Connection connection = state.getDataSource().getConnection();)
    {
      assertThat(connection, notNullValue());
      assertThat(connection.getCatalog(), is("PUBLIC"));
    }
  }

  @Before
  public void setup()
  {
    final StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
    registrar.setApplicationContext(context);
    registrar.register(registry);
  }

}
