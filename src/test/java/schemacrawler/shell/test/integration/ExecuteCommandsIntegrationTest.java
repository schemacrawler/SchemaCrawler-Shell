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

package schemacrawler.shell.test.integration;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

import org.jline.utils.AttributedString;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import schemacrawler.shell.commands.ExecuteCommands;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.shell.test.BaseSchemaCrawlerShellTest;
import schemacrawler.shell.test.TestSchemaCrawlerShellState;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {
                               InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED
                               + "=" + false })
@ContextConfiguration(classes = TestSchemaCrawlerShellState.class)
public class ExecuteCommandsIntegrationTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = ExecuteCommands.class;

  @Autowired
  private Shell shell;
  @Autowired
  private SchemaCrawlerShellState state;

  @Test
  public void execute()
  {
    final String command = "execute";
    final String commandMethod = "execute";

    final MethodTarget commandTarget = lookupCommand(shell, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("5. SchemaCrawler Commands"));
    assertThat(commandTarget.getHelp(), is("Execute a SchemaCrawler command"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             String.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    final Object returnValue = shell
      .evaluate(() -> command + " -command schema");

    assertThat(returnValue, notNullValue());
    assertThat(returnValue, is(instanceOf(AttributedString.class)));
    assertThat(returnValue.toString(), startsWith("output sent to "));
  }

  @Test
  public void commands()
  {
    final String command = "commands";
    final String commandMethod = "commands";

    final MethodTarget commandTarget = lookupCommand(shell, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("5. SchemaCrawler Commands"));
    assertThat(commandTarget.getHelp(),
               is("List available SchemaCrawler commands"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST, commandMethod)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    final Object returnValue = shell.evaluate(() -> command);

    assertThat(returnValue, nullValue());
    assertThat(returnValue, not(instanceOf(Throwable.class)));
  }

  @Before
  public void setup()
  {
    connect();
    loadCatalog();
  }

  @After
  public void sweep()
  {
    assertThat(shell.evaluate(() -> "sweep"), nullValue());
    assertThat(shell.evaluate(() -> "is-connected"), is(false));
  }

  private void connect()
  {
    shell
      .evaluate(() -> "connect -server hsqldb -user sa -database schemacrawler");
    assertThat(state.isConnected(), is(true));
  }

  private void loadCatalog()
  {
    shell.evaluate(() -> "load-catalog -infolevel minimum");
    assertThat(state.isLoaded(), is(true));
  }

}
