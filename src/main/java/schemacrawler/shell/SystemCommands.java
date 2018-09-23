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

package schemacrawler.shell;


import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import schemacrawler.JvmSystemInfo;
import schemacrawler.OperatingSystemInfo;
import schemacrawler.SchemaCrawlerInfo;
import sf.util.SchemaCrawlerLogger;

@ShellComponent
@ShellCommandGroup("4. System Commands")
public class SystemCommands
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SystemCommands.class.getName());

  @ShellMethod(value = "System version information")
  public void systemInfo()
  {
    final SchemaCrawlerInfo scInfo = new SchemaCrawlerInfo();
    System.out.println(scInfo);
    final OperatingSystemInfo osInfo = new OperatingSystemInfo();
    System.out.println(osInfo);
    final JvmSystemInfo jvmInfo = new JvmSystemInfo();
    System.out.println(jvmInfo);
  }

  @ShellMethod(value = "SchemaCrawler version information")
  public void version()
  {
    final SchemaCrawlerInfo scInfo = new SchemaCrawlerInfo();
    System.out.println(scInfo);
  }

}
