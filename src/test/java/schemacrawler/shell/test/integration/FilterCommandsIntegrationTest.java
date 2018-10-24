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
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
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

import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InclusionRuleWithRegularExpression;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.shell.commands.FilterCommands;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.shell.test.BaseSchemaCrawlerShellTest;
import schemacrawler.shell.test.TestSchemaCrawlerShellState;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {
                               InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED
                               + "=" + false })
@ContextConfiguration(classes = TestSchemaCrawlerShellState.class)
public class FilterCommandsIntegrationTest
  extends BaseSchemaCrawlerShellTest
{

  private static final Class<?> COMMANDS_CLASS_UNDER_TEST = FilterCommands.class;

  @Autowired
  private SchemaCrawlerShellState state;
  @Autowired
  private Shell shell;

  @After
  public void disconnect()
  {
    state.disconnect();
  }

  @Before
  public void connect()
  {
    assertThat(shell
      .evaluate(() -> "connect -server hsqldb -user sa -database schemacrawler"),
               is(true));
  }

  @Test
  public void filter()
  {
    final String command = "filter";
    final String commandMethod = "filter";

    final MethodTarget commandTarget = lookupCommand(shell, command);
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

    assertThat(shell
      .evaluate(() -> command + " -noemptytables -parents 1 -children 1"),
               not(instanceOf(Throwable.class)));

    // Check state after invoking command
    final SchemaCrawlerOptions postOptions = state
      .getSchemaCrawlerOptionsBuilder().toOptions();
    assertThat(postOptions.isNoEmptyTables(), is(true));
    assertThat(postOptions.getChildTableFilterDepth(), is(1));
    assertThat(postOptions.getParentTableFilterDepth(), is(1));
  }

  @Test
  public void grep()
  {
    final String command = "grep";
    final String commandMethod = "grep";

    final MethodTarget commandTarget = lookupCommand(shell, command);
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

    assertThat(shell
      .evaluate(() -> command
                      + " -grepcolumns t.*t -grepinout t.*t -grepdef t.*t -invert-match true -only-matching true"),
               not(instanceOf(Throwable.class)));

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
  {
    final String command = "limit";
    final String commandMethod = "limit";

    final MethodTarget commandTarget = lookupCommand(shell, command);
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

    shell.evaluate(() -> command + " -schemas .*");
    assertThat(shell
      .evaluate(() -> command
                      + " -schemas t.*t -tabletypes XX -tables t.*t -excludecolumns t.*t -routinetypes YY -routines t.*t -excludeinout t.*t -synonyms t.*t -sequences t.*t"),
               not(instanceOf(Throwable.class)));

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
