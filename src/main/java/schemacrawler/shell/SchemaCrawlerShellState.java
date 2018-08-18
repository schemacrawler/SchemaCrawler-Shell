package schemacrawler.shell;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component("state")
public class SchemaCrawlerShellState
{

  private final Map<String, Object> state;

  public SchemaCrawlerShellState()
  {
    state = new HashMap<>();
  }

  public boolean containsKey(final String key)
  {
    return state.containsKey(key);
  }

  public <T> T get(final String key)
  {
    return (T) state.get(key);
  }

  public void put(final String key, final Object value)
  {
    state.put(key, value);
  }

  public <T> T remove(final Object key)
  {
    return (T) state.remove(key);
  }

}
