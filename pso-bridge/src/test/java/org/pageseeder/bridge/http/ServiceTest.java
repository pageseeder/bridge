package org.pageseeder.bridge.http;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.junit.Test;

public final class ServiceTest {

  /**
   * Sample variables of testing path.
   */
  private List<Object[]> variables = Arrays.asList(
    new Object[]{},
    new Object[]{"1"},
    new Object[]{"1","2"},
    new Object[]{"1","2","3"},
    new Object[]{"1","2","3","4"}
  );

  /**
   * Ensure that all templates for the services are valid
   */
  @Test
  public void testValidTemplate() {
    for (Service service : Service.values()) {
      // the factory method will throw an IllegalArgumentException if invalid
      ServicePath.newServicePath(service.template());
    }
  }

  /**
   * Ensure that we can construct paths for all services.
   */
  @Test
  public void testToPaths() {
    for (Service service : Service.values()) {
      int count = service.countVariables();
      Object[] vars = this.variables.get(count);
      service.toPath(vars);
    }
  }


  /**
   * Ensure that we can construct paths for all services.
   */
  @Test
  public void findDuplicates() {
    EnumMap<Service, String> paths = new EnumMap<>(Service.class);
    Set<String> duplicates = new HashSet<>();
    for (Service service : Service.values()) {
      int count = service.countVariables();
      Object[] vars = this.variables.get(count);
      String path = service.toPath(vars);
      if (paths.containsValue(path)) {
        duplicates.add(path);
      }
      paths.put(service, path);
    }
    for (String duplicate : duplicates) {
      Set<Service> services = getServicesByPath(paths, duplicate);
      System.out.println("Duplicate path `"+services.iterator().next().template()+"`");
      for (Service service : services) {
        System.out.println(" - "+service);
      }
    }
  }

  private static Set<Service> getServicesByPath(EnumMap<Service, String> map, String value) {
    EnumSet<Service> services = EnumSet.noneOf(Service.class);
    for (Entry<Service, String> entry : map.entrySet()) {
      if (Objects.equals(value, entry.getValue())) {
        services.add(entry.getKey());
      }
    }
    return services;
}
}
