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
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

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

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.shell.commands.TextOutputCommands;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.shell.test.BaseSchemaCrawlerShellTest;
import schemacrawler.shell.test.TestSchemaCrawlerShellState;
import schemacrawler.tools.text.base.CommonTextOptions;
import schemacrawler.tools.text.base.CommonTextOptionsBuilder;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {
                               InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED
                               + "=" + false })
@ContextConfiguration(classes = TestSchemaCrawlerShellState.class)
public class TextOutputCommandsIntegrationTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = TextOutputCommands.class;

  @Autowired
  private Shell shell;
  @Autowired
  private SchemaCrawlerShellState state;

  @Test
  public void output()
  {
    final String command = "output";
    final String commandMethod = "output";

    final MethodTarget commandTarget = lookupCommand(shell, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("4. Text Output Commands"));
    assertThat(commandTarget.getHelp(), is("Set output options"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             String.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    // Check state before invoking command
    final SchemaCrawlerOptions preOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(preOptions.getTitle(), is(""));

    assertThat(shell.evaluate(() -> command + " -title title"),
               not(instanceOf(Throwable.class)));

    // Check state after invoking command
    final SchemaCrawlerOptions postOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(postOptions.getTitle(), is("title"));
  }

  @Before
  public void setup()
  {
    connect();
    loadCatalog();
  }

  @Test
  public void show()
  {
    final String command = "show";
    final String commandMethod = "show";

    final MethodTarget commandTarget = lookupCommand(shell, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("4. Text Output Commands"));
    assertThat(commandTarget.getHelp(), is("Show output"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             boolean.class,
                             boolean.class,
                             boolean.class,
                             boolean.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    // Check state before invoking command
    final Config preConfig = state.getAdditionalConfiguration();
    final SchemaTextOptions preOptions = SchemaTextOptionsBuilder.builder()
      .fromConfig(preConfig).toOptions();
    assertThat(preOptions.isNoInfo(), is(false));
    assertThat(preOptions.isHideRemarks(), is(false));
    assertThat(preOptions.isShowWeakAssociations(), is(false));
    assertThat(preOptions.isHideIndexNames(), is(false));

    assertThat(shell
      .evaluate(() -> command
                      + " -portablenames true -noinfo true -noremarks true -weakassociations true"),
               not(instanceOf(Throwable.class)));

    // Check state after invoking command
    final Config postConfig = state.getAdditionalConfiguration();
    final SchemaTextOptions postOptions = SchemaTextOptionsBuilder.builder()
      .fromConfig(postConfig).toOptions();
    assertThat(postOptions.isNoInfo(), is(true));
    assertThat(postOptions.isHideRemarks(), is(true));
    assertThat(postOptions.isShowWeakAssociations(), is(true));
    assertThat(postOptions.isHideIndexNames(), is(true));
  }

  @Test
  public void sort()
  {
    final String command = "sort";
    final String commandMethod = "sort";

    final MethodTarget commandTarget = lookupCommand(shell, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("4. Text Output Commands"));
    assertThat(commandTarget.getHelp(), is("Sort output"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             boolean.class,
                             boolean.class,
                             boolean.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    // Check state before invoking command
    final Config preConfig = state.getAdditionalConfiguration();
    final CommonTextOptions preOptions = CommonTextOptionsBuilder.builder()
      .fromConfig(preConfig).toOptions();
    assertThat(preOptions.isAlphabeticalSortForTables(), is(true));
    assertThat(preOptions.isAlphabeticalSortForTableColumns(), is(false));
    assertThat(preOptions.isAlphabeticalSortForRoutineColumns(), is(false));

    assertThat(shell
      .evaluate(() -> command
                      + " -sorttables false -sortcolumns true -sortinout true"),
               not(instanceOf(Throwable.class)));

    // Check state after invoking command
    final Config postConfig = state.getAdditionalConfiguration();
    final CommonTextOptions postOptions = CommonTextOptionsBuilder.builder()
      .fromConfig(postConfig).toOptions();
    assertThat(postOptions.isAlphabeticalSortForTables(), is(false));
    assertThat(postOptions.isAlphabeticalSortForTableColumns(), is(true));
    assertThat(postOptions.isAlphabeticalSortForRoutineColumns(), is(true));
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
