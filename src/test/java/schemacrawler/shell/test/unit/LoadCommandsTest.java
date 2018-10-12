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

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.standard.StandardMethodTargetRegistrar;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.shell.commands.ConnectCommands;
import schemacrawler.shell.commands.LoadCommands;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.shell.test.BaseSchemaCrawlerShellTest;
import schemacrawler.tools.options.InfoLevel;

public class LoadCommandsTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = LoadCommands.class;

  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();
  private SchemaCrawlerShellState state;

  @Test
  public void loadCatalog()
    throws SQLException
  {
    final String command = "load-catalog";
    final String commandMethod = "loadCatalog";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("3. Catalog Load Commands"));
    assertThat(commandTarget.getHelp(), is("Load a catalog"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             InfoLevel.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));
    assertThat(invoke(commandTarget, InfoLevel.standard), is(true));

    assertThat(state.getCatalog(), notNullValue());
    assertThat(state.getCatalog().getTables().size(), is(19));
  }

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

  @After
  public void sweep()
  {
    state.sweep();
  }

}
