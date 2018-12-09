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

package schemacrawler.shell.test;


import static org.springframework.util.ReflectionUtils.invokeMethod;

import javax.validation.constraints.NotNull;

import org.springframework.shell.CommandRegistry;
import org.springframework.shell.MethodTarget;

import schemacrawler.test.utility.BaseDatabaseTest;

public abstract class BaseSchemaCrawlerShellTest
  extends BaseDatabaseTest
{

  protected <T> T invoke(final MethodTarget methodTarget, final Object... args)
  {
    return (T) invokeMethod(methodTarget.getMethod(),
                            methodTarget.getBean(),
                            args);
  }

  protected MethodTarget lookupCommand(@NotNull final CommandRegistry registry,
                                       @NotNull final String command)
  {
    return registry.listCommands().get(command);
  }

}
