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
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.util.ReflectionUtils.findMethod;

import java.sql.SQLException;

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

import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InclusionRuleWithRegularExpression;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.shell.commands.ConnectCommands;
import schemacrawler.shell.commands.FilterCommands;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.shell.test.BaseSchemaCrawlerShellTest;
import schemacrawler.shell.test.TestSchemaCrawlerShellState;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
                                  TestSchemaCrawlerShellState.class,
                                  FilterCommands.class })
public class FilterCommandsTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = FilterCommands.class;

  private final ConfigurableCommandRegistry registry = new ConfigurableCommandRegistry();
  @Autowired
  private SchemaCrawlerShellState state;
  @Autowired
  private ApplicationContext context;

  @After
  public void disconnect()
  {
    state.disconnect();
  }

  @Test
  public void filter()
    throws SQLException
  {
    final String command = "filter";
    final String commandMethod = "filter";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("2. Filter Commands"));
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
    assertThat(commandTarget.getGroup(), is("2. Filter Commands"));
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

    assertThat(getInclusionPattern(postOptions.getGrepColumnInclusionRule()
      .get()), is("t.*t"));
    assertThat(getInclusionPattern(postOptions
      .getGrepRoutineColumnInclusionRule().get()), is("t.*t"));
    assertThat(getInclusionPattern(postOptions.getGrepDefinitionInclusionRule()
      .get()), is("t.*t"));
  }

  @Test
  public void limit()
    throws SQLException
  {
    final String command = "limit";
    final String commandMethod = "limit";

    final MethodTarget commandTarget = lookupCommand(registry, command);
    assertThat(commandTarget, notNullValue());
    assertThat(commandTarget.getGroup(), is("2. Filter Commands"));
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
    assertThat(getInclusionPattern(preOptions.getSchemaInclusionRule()),
               is(".*"));
    assertThat(preOptions.getTableTypes(), hasItem("VIEW"));
    assertThat(getInclusionPattern(preOptions.getTableInclusionRule()),
               is(".*"));
    assertThat(getExclusionPattern(preOptions.getColumnInclusionRule()),
               is(""));
    assertThat(preOptions.getRoutineTypes(), hasItem(RoutineType.function));
    assertThat(getInclusionPattern(preOptions.getRoutineInclusionRule()),
               is(""));
    assertThat(getExclusionPattern(preOptions.getRoutineColumnInclusionRule()),
               is(""));
    assertThat(getInclusionPattern(preOptions.getSynonymInclusionRule()),
               is(""));
    assertThat(getInclusionPattern(preOptions.getSequenceInclusionRule()),
               is(""));

    invoke(commandTarget,
           "t.*t",
           "XX",
           "t.*t",
           "t.*t",
           "YY",
           "t.*t",
           "t.*t",
           "t.*t",
           "t.*t");

    // Check state after invoking command
    final SchemaCrawlerOptions postOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(getInclusionPattern(postOptions.getSchemaInclusionRule()),
               is("t.*t"));
    assertThat(postOptions.getTableTypes(), hasItem("XX"));
    assertThat(getInclusionPattern(postOptions.getTableInclusionRule()),
               is("t.*t"));
    assertThat(getExclusionPattern(postOptions.getColumnInclusionRule()),
               is("t.*t"));
    assertThat(postOptions.getRoutineTypes(), hasItem(RoutineType.unknown));
    assertThat(getInclusionPattern(postOptions.getRoutineInclusionRule()),
               is("t.*t"));
    assertThat(getExclusionPattern(postOptions.getRoutineColumnInclusionRule()),
               is("t.*t"));
    assertThat(getInclusionPattern(postOptions.getSynonymInclusionRule()),
               is("t.*t"));
    assertThat(getInclusionPattern(postOptions.getSequenceInclusionRule()),
               is("t.*t"));
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
  }

  private String getExclusionPattern(final InclusionRule inclusionRule)
  {
    return ((InclusionRuleWithRegularExpression) inclusionRule)
      .getExclusionPattern().pattern();
  }

  private String getInclusionPattern(final InclusionRule inclusionRule)
  {
    return ((InclusionRuleWithRegularExpression) inclusionRule)
      .getInclusionPattern().pattern();
  }

}
