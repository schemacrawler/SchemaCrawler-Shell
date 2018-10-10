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

import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InclusionRuleWithRegularExpression;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.shell.ConnectCommands;
import schemacrawler.shell.FilterCommands;
import schemacrawler.shell.SchemaCrawlerShellState;

public class FilterCommandsTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = FilterCommands.class;

  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();
  private SchemaCrawlerShellState state;

  @Test
  public void filter()
    throws SQLException
  {
    final String command = "filter";
    final String commandMethod = "filter";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("3. Filter Commands"));
    assertThat(commandTarget.getHelp(), is("Filter database object metadata"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             boolean.class,
                             int.class,
                             int.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    // Check state before invoking command
    final SchemaCrawlerOptions preOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(preOptions.isNoEmptyTables(), is(false));
    assertThat(preOptions.getChildTableFilterDepth(), is(0));
    assertThat(preOptions.getParentTableFilterDepth(), is(0));

    invoke(commandTarget, true, 1, 1);

    // Check state after invoking command
    final SchemaCrawlerOptions postOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(postOptions.isNoEmptyTables(), is(true));
    assertThat(postOptions.getChildTableFilterDepth(), is(1));
    assertThat(postOptions.getParentTableFilterDepth(), is(1));
  }

  @Test
  public void grep()
    throws SQLException
  {
    final String command = "grep";
    final String commandMethod = "grep";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("3. Filter Commands"));
    assertThat(commandTarget.getHelp(), is("Grep database object metadata"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             String.class,
                             String.class,
                             String.class,
                             boolean.class,
                             boolean.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    // Check state before invoking command
    final SchemaCrawlerOptions preOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(preOptions.isGrepColumns(), is(false));
    assertThat(preOptions.isGrepRoutineColumns(), is(false));
    assertThat(preOptions.isGrepDefinitions(), is(false));
    assertThat(preOptions.isGrepInvertMatch(), is(false));
    assertThat(preOptions.isGrepOnlyMatching(), is(false));

    invoke(commandTarget, "t.*t", "t.*t", "t.*t", true, true);

    // Check state after invoking command
    final SchemaCrawlerOptions postOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(postOptions.isGrepColumns(), is(true));
    assertThat(postOptions.isGrepRoutineColumns(), is(true));
    assertThat(postOptions.isGrepDefinitions(), is(true));
    assertThat(postOptions.isGrepInvertMatch(), is(true));
    assertThat(postOptions.isGrepOnlyMatching(), is(true));

    assertThat(getPattern(postOptions.getGrepColumnInclusionRule().get()),
               is("t.*t"));
    assertThat(getPattern(postOptions.getGrepRoutineColumnInclusionRule()
      .get()), is("t.*t"));
    assertThat(getPattern(postOptions.getGrepDefinitionInclusionRule().get()),
               is("t.*t"));
  }

  @Test
  public void limit()
    throws SQLException
  {
    final String command = "limit";
    final String commandMethod = "limit";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("3. Filter Commands"));
    assertThat(commandTarget.getHelp(), is("Limit database object metadata"));
    assertThat(commandTarget.getMethod(),
               is(findMethod(COMMANDS_CLASS_UNDER_TEST,
                             commandMethod,
                             String.class,
                             String.class,
                             String.class,
                             String.class,
                             String.class,
                             String.class,
                             String.class,
                             String.class,
                             String.class)));
    assertThat(commandTarget.getAvailability().isAvailable(), is(true));

    // Check state before invoking command
    final SchemaCrawlerOptions preOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(getPattern(preOptions.getSchemaInclusionRule()), is(".*"));
    assertThat(getPattern(preOptions.getTableInclusionRule()), is(".*"));
    assertThat(getPattern(preOptions.getRoutineInclusionRule()), is(""));
    assertThat(getPattern(preOptions.getSynonymInclusionRule()), is(""));
    assertThat(getPattern(preOptions.getSequenceInclusionRule()), is(""));

    invoke(commandTarget,
           "t.*t",
           "t.*t",
           "t.*t",
           "t.*t",
           "t.*t",
           "t.*t",
           "t.*t",
           "t.*t",
           "t.*t");

    // Check state after invoking command
    final SchemaCrawlerOptions postOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(getPattern(postOptions.getSchemaInclusionRule()), is("t.*t"));
    assertThat(getPattern(postOptions.getTableInclusionRule()), is("t.*t"));
    assertThat(getPattern(postOptions.getRoutineInclusionRule()), is("t.*t"));
    assertThat(getPattern(postOptions.getSynonymInclusionRule()), is("t.*t"));
    assertThat(getPattern(postOptions.getSequenceInclusionRule()), is("t.*t"));
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

  private String getPattern(final InclusionRule inclusionRule)
  {
    return ((InclusionRuleWithRegularExpression) inclusionRule)
      .getInclusionPattern().pattern();
  }

}
