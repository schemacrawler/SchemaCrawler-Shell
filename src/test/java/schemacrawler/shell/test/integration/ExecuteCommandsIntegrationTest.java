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

package schemacrawler.shell.test.integration;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.fileResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jline.utils.AttributedString;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
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
import schemacrawler.test.utility.TestName;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.options.OutputOptions;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {
                               InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED
                               + "=" + false })
@ContextConfiguration(classes = TestSchemaCrawlerShellState.class)
public class ExecuteCommandsIntegrationTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = ExecuteCommands.class;

  @Rule
  public TestName testName = new TestName();

  @Autowired
  private Shell shell;
  @Autowired
  private SchemaCrawlerShellState state;

  private TestOutputStream out;
  private TestOutputStream err;

  @After
  public void cleanUpStreams()
  {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void commands()
    throws Exception
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

    assertThat(fileResource(out),
               hasSameContentAs(classpathResource(testName
                 .currentMethodFullName())));
    assertThat(fileResource(err), hasNoContent());
  }

  @Test
  public void execute()
    throws Exception
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
                             String.class,
                             String.class,
                             String.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    // Check state before invoking command
    final OutputOptions preOutputOptions = state.getOutputOptionsBuilder()
      .toOptions();
    assertThat(preOutputOptions.getOutputFormatValue(), is("text"));

    final Object returnValue = shell
      .evaluate(() -> command + " -command schema -fmt text");

    // Check state after invoking command
    final OutputOptions postOutputOptions = state.getOutputOptionsBuilder()
      .toOptions();
    assertThat(postOutputOptions.getOutputFormatValue(), is("text"));

    assertThat(returnValue, notNullValue());
    assertThat(returnValue, is(instanceOf(AttributedString.class)));
    assertThat(returnValue.toString(), startsWith("Completed"));

    assertThat(fileResource(out),
               hasSameContentAs(classpathResource(testName
                 .currentMethodFullName())));
    assertThat(fileResource(err), hasNoContent());
  }

  @Before
  public void setup()
  {
    connect();
    loadCatalog();
  }

  @Before
  public void setUpStreams()
    throws IOException
  {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
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
