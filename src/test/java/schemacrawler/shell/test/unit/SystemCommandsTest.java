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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

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

import schemacrawler.shell.commands.SystemCommands;
import schemacrawler.shell.test.BaseSchemaCrawlerShellTest;
import schemacrawler.shell.test.TestSchemaCrawlerShellState;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
                                  TestSchemaCrawlerShellState.class,
                                  SystemCommands.class })
public class SystemCommandsTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = SystemCommands.class;

  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();
  @Autowired
  private ApplicationContext context;

  @Before
  public void setup()
  {
    final StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
    registrar.setApplicationContext(context);
    registrar.register(registry);
  }

  @Test
  public void systemInfo()
  {
    final String command = "system-info";
    final String commandMethod = "systemInfo";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("4. System Commands"));
    assertThat(commandTarget.getHelp(), is("System version information"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST, commandMethod)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));
    assertThat(invoke(commandTarget), nullValue());
  }

  @Test
  public void version()
  {
    final String command = "version";
    final String commandMethod = "version";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("4. System Commands"));
    assertThat(commandTarget.getHelp(),
               is("SchemaCrawler version information"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST, commandMethod)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));
    assertThat(invoke(commandTarget), nullValue());
  }

}
