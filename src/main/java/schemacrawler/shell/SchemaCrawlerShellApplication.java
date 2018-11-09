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


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
public class SchemaCrawlerShellApplication
  implements CommandLineRunner
{

  public static void main(final String[] args)
  {
    final SpringApplication application = new SpringApplication(SchemaCrawlerShellApplication.class);
    application.setBanner(new SchemaCrawlerBanner());
    application.run(args);
  }

  @Override
  public void run(final String... args)
    throws Exception
  {
    // No-op
  }

  @Bean
  public PromptProvider schemaCrawlerShellPromptProvider()
  {
    return new SchemaCrawlerShellPromptProvider();
  }

}
