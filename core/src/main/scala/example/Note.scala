package example

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class Note(id: String, title: String, content: String)

object Note:
  given Codec[Note] = deriveCodec[Note]
