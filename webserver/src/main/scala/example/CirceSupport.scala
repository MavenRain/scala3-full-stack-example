package example

import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.marshalling.{ToEntityMarshaller, Marshaller}
import akka.http.scaladsl.model.{ContentTypes, MediaTypes}
import cats.syntax.either.catsSyntaxEither
import io.circe.{Decoder, Encoder, Printer}
import io.circe.parser.decode
import io.circe.syntax.EncoderOps
import scala.util.chaining.scalaUtilChainingOps

trait CirceSupport:
  private val printer = Printer(
    dropNullValues = true,
    indent = ""
  )

  given [T: Decoder]: FromEntityUnmarshaller[T] =
    Unmarshaller.stringUnmarshaller
      .forContentTypes(ContentTypes.`application/json`)
      .map(decode(_).valueOr(throw _))

  given [T: Encoder]: ToEntityMarshaller[T] =
    Marshaller
      .stringMarshaller(MediaTypes.`application/json`)
      .compose(_.asJson.pipe(printer.print))

object CirceSupport extends CirceSupport
