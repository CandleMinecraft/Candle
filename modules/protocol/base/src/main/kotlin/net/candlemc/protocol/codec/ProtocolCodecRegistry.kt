package net.candlemc.protocol.codec

import net.candlemc.types.resource.Identifier
import net.candlemc.types.resource.Registry

class ProtocolCodecRegistry : Registry<AbstractDataCodec?>(Identifier.of("candlemc", "protocol_codec"))
