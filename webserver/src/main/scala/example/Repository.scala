package example

import cats.syntax.either.catsSyntaxEither
import io.circe.syntax.EncoderOps
import io.circe.parser.decode
import io.circe.Printer
import java.nio.file.{Path, Paths, Files, StandardOpenOption}
import java.util.UUID
import scala.concurrent.{Future, ExecutionContext}
import scala.jdk.CollectionConverters.*
import scala.util.chaining.scalaUtilChainingOps

trait Repository extends NoteService[Future]

object Repository:
  private val printer: Printer = Printer(
    dropNullValues = true,
    indent = ""
  )

  def apply(directory: Path)(using ExecutionContext): Repository =
    if !Files.exists(directory) then Files.createDirectory(directory)
    FileRepository(directory)

  private class FileRepository(directory: Path)(using ExecutionContext) extends Repository:
    def getAllNotes(): Future[Seq[Note]] = Future {
      Files
        .list(directory)
        .iterator
        .asScala
        .filter(_.toString.endsWith(".json"))
        .map(
          _
            .pipe(Files.readAllBytes(_))
            .pipe(String(_))
            .pipe(decode[Note](_))
            .valueOr(throw _)
        )
        .toSeq
    }

    def createNote(title: String, content: String): Future[Note] = Future {
      val id = UUID.randomUUID().toString
      val note = Note(id, title, content)
      val file = directory.resolve(s"$id.json")
      val bytes = printer.print(note.asJson).getBytes
      Files.write(file, bytes, StandardOpenOption.CREATE)
      note
    }
