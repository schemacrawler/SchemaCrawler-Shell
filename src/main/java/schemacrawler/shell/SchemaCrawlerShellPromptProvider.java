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


import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.shell.jline.PromptProvider;

import schemacrawler.shell.state.SchemaCrawlerShellState;

@Configurable
public class SchemaCrawlerShellPromptProvider
  implements PromptProvider
{

  @Autowired
  private SchemaCrawlerShellState state;

  @Override
  public AttributedString getPrompt()
  {

    final int foregroundColor;
    if (state == null)
    {
      foregroundColor = AttributedStyle.WHITE;
    }
    else if (state.isLoaded())
    {
      foregroundColor = AttributedStyle.YELLOW;
    }
    else if (state.isConnected())
    {
      foregroundColor = AttributedStyle.GREEN;
    }
    else
    {
      foregroundColor = AttributedStyle.WHITE;
    }

    return new AttributedString("schemacrawler> ",
                                AttributedStyle.DEFAULT.bold()
                                  .foreground(foregroundColor));
  }

}
