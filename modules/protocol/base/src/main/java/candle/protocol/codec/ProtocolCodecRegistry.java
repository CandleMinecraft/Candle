package candle.protocol.codec;

import candle.types.resource.Identifier;
import candle.types.resource.Registry;

public class ProtocolCodecRegistry extends Registry<AbstractDataCodec> {
  public ProtocolCodecRegistry() {
    super(Identifier.of("candlemc", "protocol_codec"));
  }
}
