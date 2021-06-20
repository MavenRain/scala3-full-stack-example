package example

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class CreateNote(title: String, content: String)

object CreateNote:
  given Codec[CreateNote] = deriveCodec[CreateNote]
