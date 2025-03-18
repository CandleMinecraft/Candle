package candle.types.resource;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
@EqualsAndHashCode
public class Identifier {
  private static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9_.-]+");
  private static final Pattern VALID_PATH = Pattern.compile("[a-z0-9_/.-]+");

  private final String namespace;
  private final String path;

  public Identifier( String namespace, String path ) {
    if ( !isValidNamespace(namespace) ) {
      throw new IllegalArgumentException("Invalid namespace: " + namespace);
    }
    if ( !isValidPath(path) ) {
      throw new IllegalArgumentException("Invalid path: " + path);
    }
    this.namespace = namespace;
    this.path = path;
  }

  // Parses "namespace:path". Defaults to "minecraft" if no colon is present.
  public static Identifier parse( String combined ) {
    String[] parts = combined.split(":", 2);
    if ( parts.length == 1 ) {
      return new Identifier("minecraft", parts[0]);
    }
    return new Identifier(parts[0], parts[1]);
  }

  public static Identifier of( String namespacedIdentifier ) {
    String[] splitIdentifier = namespacedIdentifier.split(":", 1);
    return new Identifier(splitIdentifier[0], splitIdentifier[1]);
  }

  public static Identifier of( String namespace, String identifier ) {
    return new Identifier(namespace, identifier);
  }

  private boolean isValidNamespace( String ns ) {
    return VALID_NAMESPACE.matcher(ns).matches();
  }

  private boolean isValidPath( String path ) {
    return VALID_PATH.matcher(path).matches();
  }

  @Override
  public String toString() {
    return namespace + ":" + path;
  }
}
