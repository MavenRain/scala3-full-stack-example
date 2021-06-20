package example

import cats.syntax.either.catsSyntaxEither
import io.circe.Printer
import io.circe.parser.decode
import io.circe.scalajs.decodeJs
import io.circe.syntax.EncoderOps
import java.io.IOException
import org.scalajs.dom.experimental.{Fetch, HttpMethod, Request, Response}
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.{Any => JsAny, Dictionary}

class HttpClient(using ExecutionContext) extends NoteService:
  private val printer = Printer(
    dropNullValues = true,
    indent = ""
  )

  def getAllNotes(): Future[Seq[Note]] =
    for
      resp <- Fetch.fetch("./api/notes").toFuture
      json <- resp.jsonOrFailure
    yield decodeJs[Seq[Note]](json).valueOr(throw _)

  def createNote(title: String, content: String): Future[Note] =
    val request = Request(
      "./api/notes",
      new {
        method = HttpMethod.POST
        headers = Dictionary("Content-Type" -> "application/json")
        body = printer.print(CreateNote(title, content).asJson)
      }
    )
    for
      resp <- Fetch.fetch(request).toFuture
      json <- resp.jsonOrFailure
    yield decodeJs[Note](json).valueOr(throw _)

  extension (resp: Response)
    private def jsonOrFailure: Future[JsAny] =
      if resp.ok
      then resp.json.toFuture
      else Future.failed(IOException(resp.statusText))
