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

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.ConfigurableCommandRegistry;
import org.springframework.shell.MethodTarget;
import org.springframework.shell.standard.StandardMethodTargetRegistrar;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import schemacrawler.shell.ConnectCommands;
import schemacrawler.shell.SchemaCrawlerShellState;
import schemacrawler.shell.SystemCommands;

@RunWith(SpringJUnit4ClassRunner.class)
public class ConnectCommandsTest
  extends BaseSchemaCrawlerShellTest
{

  private final StandardMethodTargetRegistrar registrar = new StandardMethodTargetRegistrar();
  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();

  @Before
  public void setUp()
  {
    final ApplicationContext context = new AnnotationConfigApplicationContext(ConnectCommands.class);
    registrar.setApplicationContext(context);
    registrar.register(registry);
  }

  @Bean
  public SchemaCrawlerShellState state()
  {
    return new SchemaCrawlerShellState();
  }

  @Ignore("Under development")
  @Test
  public void connect()
  {
    final String command = "connect";
    final String commandMethod = "connect";

    final Map<String, MethodTarget> commands = registry.listCommands();
    final MethodTarget commandTarget = commands.get(command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("1. Database Connection Commands"));
    assertThat(commandTarget.getHelp(),
               is("Connect to a database, using a server specification"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(SystemCommands.class, commandMethod)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));
    assertThat(invoke(commandTarget), is(true));
  }

}
