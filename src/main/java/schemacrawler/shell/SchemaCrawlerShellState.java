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


import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;
import schemacrawler.tools.options.OutputOptionsBuilder;

@Component("state")
public class SchemaCrawlerShellState
{

  private Catalog catalog;
  private DataSource dataSource;
  private Config additionalConfiguration;
  private SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder;
  private SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder;
  private OutputOptionsBuilder outputOptionsBuilder;

  public void disconnect()
  {
    if (dataSource instanceof AutoCloseable)
    {
      try
      {
        ((AutoCloseable) dataSource).close();
      }
      catch (final Exception e)
      {
        // Ignore errors
      }
    }
    dataSource = null;
  }

  public Config getAdditionalConfiguration()
  {
    return additionalConfiguration;
  }

  public Catalog getCatalog()
  {
    return catalog;
  }

  public DataSource getDataSource()
  {
    return dataSource;
  }

  public OutputOptionsBuilder getOutputOptionsBuilder()
  {
    return outputOptionsBuilder;
  }

  public SchemaCrawlerOptionsBuilder getSchemaCrawlerOptionsBuilder()
  {
    return schemaCrawlerOptionsBuilder;
  }

  public SchemaRetrievalOptionsBuilder getSchemaRetrievalOptionsBuilder()
  {
    return schemaRetrievalOptionsBuilder;
  }

  public void setAdditionalConfiguration(final Config additionalConfiguration)
  {
    this.additionalConfiguration = additionalConfiguration;
  }

  public void setCatalog(final Catalog catalog)
  {
    this.catalog = catalog;
  }

  public void setDataSource(final DataSource dataSource)
  {
    this.dataSource = dataSource;
  }

  public void setOutputOptionsBuilder(final OutputOptionsBuilder outputOptionsBuilder)
  {
    this.outputOptionsBuilder = outputOptionsBuilder;
  }

  public void setSchemaCrawlerOptionsBuilder(final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder)
  {
    this.schemaCrawlerOptionsBuilder = schemaCrawlerOptionsBuilder;
  }

  public void setSchemaRetrievalOptionsBuilder(final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder)
  {
    this.schemaRetrievalOptionsBuilder = schemaRetrievalOptionsBuilder;
  }

  public void sweep()
  {
    catalog = null;
    additionalConfiguration = null;
    schemaCrawlerOptionsBuilder = null;
    schemaRetrievalOptionsBuilder = null;
    outputOptionsBuilder = null;

    disconnect();
  }

}
