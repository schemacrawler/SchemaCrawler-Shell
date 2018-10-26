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

package schemacrawler.shell.test.functional;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

import java.sql.SQLException;

import org.jline.utils.AttributedString;
import org.junit.After;
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

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.shell.commands.ConnectCommands;
import schemacrawler.shell.commands.ExecuteCommands;
import schemacrawler.shell.commands.LoadCommands;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.shell.test.BaseSchemaCrawlerShellTest;
import schemacrawler.shell.test.TestSchemaCrawlerShellState;
import schemacrawler.tools.options.InfoLevel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
                                  TestSchemaCrawlerShellState.class,
                                  ExecuteCommands.class })
public class ExecuteCommandsTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = ExecuteCommands.class;

  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();
  @Autowired
  private SchemaCrawlerShellState state;
  @Autowired
  private ApplicationContext context;

  @Test
  public void commands()
  {
    final String command = "commands";
    final String commandMethod = "commands";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("5. SchemaCrawler Commands"));
    assertThat(commandTarget.getHelp(),
               is("List available SchemaCrawler commands"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST, commandMethod)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    invoke(commandTarget);
    // TODO: Test output
  }

  @Test
  public void execute()
    throws SQLException
  {
    final String command = "execute";
    final String commandMethod = "execute";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("5. SchemaCrawler Commands"));
    assertThat(commandTarget.getHelp(), is("Execute a SchemaCrawler command"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             String.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    final Object returnValue = invoke(commandTarget, "schema");

    assertThat(returnValue, notNullValue());
    assertThat(returnValue, is(instanceOf(AttributedString.class)));
    assertThat(returnValue.toString(), startsWith("output sent to "));
  }

  @Before
  public void setup()
    throws SchemaCrawlerException, SQLException
  {
    final StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
    registrar.setApplicationContext(context);
    registrar.register(registry);

    // Create a connection
    final ConnectCommands connectCommands = new ConnectCommands(state);
    connectCommands
      .connectUrl("jdbc:hsqldb:hsql://localhost:9001/schemacrawler", "sa", "");

    // Load schema
    final LoadCommands loadCommands = new LoadCommands(state);
    loadCommands.loadCatalog(InfoLevel.minimum);
  }

  @After
  public void sweep()
  {
    state.sweep();
  }

}
