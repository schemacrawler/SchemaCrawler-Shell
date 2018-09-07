package schemacrawler.shell;


import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaRetrievalOptionsBuilder;

@Component("state")
public class SchemaCrawlerShellState
{

  private Catalog catalog;
  private DataSource dataSource;
  private Config additionalConfiguration;
  private SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder;
  private SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder;

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

  public void setSchemaCrawlerOptionsBuilder(final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder)
  {
    this.schemaCrawlerOptionsBuilder = schemaCrawlerOptionsBuilder;
  }

  public void setSchemaRetrievalOptionsBuilder(final SchemaRetrievalOptionsBuilder schemaRetrievalOptionsBuilder)
  {
    this.schemaRetrievalOptionsBuilder = schemaRetrievalOptionsBuilder;
  }

}
