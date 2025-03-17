package candle.protocol;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@EqualsAndHashCode
@ToString
public class ConnectionState {
  private static final ArrayList<ConnectionState> STATES = new ArrayList<>();

  public static final ConnectionState HANDSHAKING = ConnectionState.create("Handshaking");
  public static final ConnectionState CONFIGURATION = ConnectionState.create("Configuration");
  public static final ConnectionState LOGIN = ConnectionState.create("Login");
  public static final ConnectionState PLAY = ConnectionState.create("Play");
  public static final ConnectionState STATUS = ConnectionState.create("Status");

  private final String name;

  ConnectionState(String name) {
    this.name = name;
  }

  public static ConnectionState create( String name ) {
    ConnectionState state = new ConnectionState(name);
    STATES.add(state);
    return state;
  }
}
