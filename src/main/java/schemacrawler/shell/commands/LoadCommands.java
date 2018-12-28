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

package schemacrawler.shell.commands;


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.util.logging.Level;

import javax.validation.constraints.NotNull;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.shell.state.SchemaCrawlerShellState;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.options.OutputOptionsBuilder;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

@ShellComponent
@ShellCommandGroup("3. Catalog Load Commands")
public class LoadCommands
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(LoadCommands.class.getName());

  @Autowired
  private final SchemaCrawlerShellState state;

  public LoadCommands(final SchemaCrawlerShellState state)
  {
    this.state = state;
  }

  @ShellMethodAvailability
  public Availability isConnected()
  {
    final boolean isConnected = state.isConnected();
    return isConnected? Availability.available(): Availability
      .unavailable("there is no database connection");
  }

  @ShellMethod(value = "Check if the catalog is loaded")
  public boolean isLoaded()
  {
    return state.isLoaded();
  }

  @ShellMethod(value = "Load a catalog", prefix = "-")
  public AttributedString loadCatalog(@ShellOption(value = "-infolevel", help = "Determine the amount of database metadata retrieved") @NotNull final InfoLevel infoLevel)
  {
    try (final Connection connection = state.getDataSource().getConnection();)
    {
      LOGGER.log(Level.INFO, new StringFormat("infoLevel=%s", infoLevel));

      loadOutputOptionsBuilder();

      final Config additionalConfiguration = state.getAdditionalConfiguration();
      final SchemaRetrievalOptions schemaRetrievalOptions = state
        .getSchemaRetrievalOptionsBuilder().toOptions();
      final SchemaCrawlerOptions schemaCrawlerOptions = state
        .getSchemaCrawlerOptionsBuilder().toOptions();

      final CatalogLoaderRegistry catalogLoaderRegistry = new CatalogLoaderRegistry();
      final CatalogLoader catalogLoader = catalogLoaderRegistry
        .lookupCatalogLoader(schemaRetrievalOptions.getDatabaseServerType()
          .getDatabaseSystemIdentifier());
      LOGGER
        .log(Level.CONFIG,
             new StringFormat("Catalog loader: %s", this.getClass().getName()));

      catalogLoader.setAdditionalConfiguration(additionalConfiguration);
      catalogLoader.setConnection(connection);
      catalogLoader.setSchemaRetrievalOptions(schemaRetrievalOptions);
      catalogLoader.setSchemaCrawlerOptions(schemaCrawlerOptions);

      final Catalog catalog = catalogLoader.loadCatalog();
      requireNonNull(catalog, "Catalog could not be retrieved");

      state.setCatalog(catalog);
      LOGGER.log(Level.INFO, "Loaded catalog");

      return success();
    }
    catch (final Exception e)
    {
      throw new RuntimeException("Cannot load catalog", e);
    }
  }

  private void loadOutputOptionsBuilder()
  {
    final Config config = state.getAdditionalConfiguration();
    final OutputOptionsBuilder outputOptionsBuilder = OutputOptionsBuilder
      .builder();
    outputOptionsBuilder.fromConfig(config);
    state.setOutputOptionsBuilder(outputOptionsBuilder);
  }

  private AttributedString success()
  {
    if (isLoaded())
    {
      return new AttributedString("Loaded catalog",
                                  AttributedStyle.DEFAULT
                                    .foreground(AttributedStyle.CYAN));
    }
    else
    {
      return new AttributedString("Did not load catalog",
                                  AttributedStyle.DEFAULT
                                    .foreground(AttributedStyle.RED));
    }
  }

}
