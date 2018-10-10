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

package schemacrawler.shell.test;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.standard.StandardMethodTargetRegistrar;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.shell.ConnectCommands;
import schemacrawler.shell.SchemaCrawlerShellState;
import schemacrawler.shell.TextOutputCommands;
import schemacrawler.tools.text.base.CommonTextOptions;
import schemacrawler.tools.text.base.CommonTextOptionsBuilder;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;

public class TextOutputCommandsTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = TextOutputCommands.class;

  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();
  private SchemaCrawlerShellState state;

  @Before
  public void setup()
    throws SchemaCrawlerException, SQLException
  {
    final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean("state", SchemaCrawlerShellState.class);
    context.register(COMMANDS_CLASS_UNDER_TEST);
    context.refresh();
    state = (SchemaCrawlerShellState) context.getBean("state");

    final StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
    registrar.setApplicationContext(context);
    registrar.register(registry);

    // Create a connection
    final ConnectCommands connectCommands = new ConnectCommands(state);
    connectCommands
      .connectUrl("jdbc:hsqldb:hsql://localhost:9001/schemacrawler", "sa", "");
  }

  @Test
  public void show()
    throws SQLException
  {
    final String command = "show";
    final String commandMethod = "show";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("3. Text Output Commands"));
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

    invoke(commandTarget, true, true, true, true);

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
    throws SQLException
  {
    final String command = "sort";
    final String commandMethod = "sort";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("3. Text Output Commands"));
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

    invoke(commandTarget, false, true, true);

    // Check state after invoking command
    final Config postConfig = state.getAdditionalConfiguration();
    final CommonTextOptions postOptions = CommonTextOptionsBuilder.builder()
      .fromConfig(postConfig).toOptions();
    assertThat(postOptions.isAlphabeticalSortForTables(), is(false));
    assertThat(postOptions.isAlphabeticalSortForTableColumns(), is(true));
    assertThat(postOptions.isAlphabeticalSortForRoutineColumns(), is(true));
  }

}
