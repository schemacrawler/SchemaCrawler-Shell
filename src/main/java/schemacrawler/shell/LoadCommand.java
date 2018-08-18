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


import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.logging.Level;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaRetrievalOptions;
import schemacrawler.tools.catalogloader.CatalogLoader;
import schemacrawler.tools.catalogloader.CatalogLoaderRegistry;
import schemacrawler.tools.iosource.FileInputResource;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.utility.PropertiesUtility;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

@ShellComponent
@ShellCommandGroup("Catalog Load Commands")
public class LoadCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(LoadCommand.class.getName());

  @Autowired
  private SchemaCrawlerShellState state;

  @ShellMethod(value = "Load a config properties file", prefix = "-")
  public boolean config(@NotNull final Path configFilePath)
  {
    try
    {
      final Config config = new Config();
      config.putAll(PropertiesUtility
        .loadConfig(new FileInputResource(configFilePath)));
      state.put("config", config);
      return true;
    }
    catch (final IOException e)
    {
      return false;
    }
  }

  @ShellMethod(key = "isloaded", value = "Check if the schema is loaded")
  public boolean isLoaded()
  {
    final Catalog catalog = state.get("catalog");
    return catalog != null;
  }

  @ShellMethod(value = "Load a catalog", prefix = "-")
  public boolean loadCatalog(@ShellOption(value = "-infolevel") @NotNull final InfoLevel infoLevel)
  {
    try
    {
      final Connection connection = state.get("connection");

      final Config additionalConfiguration = state
        .get("additionalConfiguration");
      final SchemaRetrievalOptions schemaRetrievalOptions = state
        .get("schemaRetrievalOptions");
      final SchemaCrawlerOptions schemaCrawlerOptions = state
        .get("schemaCrawlerOptions");

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

      state.put("catalog", catalog);
      LOGGER.log(Level.INFO, "Loaded catalog");
    }
    catch (final Exception e)
    {
      state.remove("catalog");
    }

    return isLoaded();
  }

}
