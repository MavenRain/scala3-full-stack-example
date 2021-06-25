package example

import org.scalajs.dom.document.body
import org.scalajs.dom.html.{Div, Element}
import scala.concurrent.ExecutionContext
import scala.util.chaining.scalaUtilChainingOps
import scala.util.control.NonFatal

object WebPage:
  given ExecutionContext = ExecutionContext.global
  val service = HttpClient()
  val titleInput = input()
  val contentTextArea = textarea()
  val saveButton =
    button("Create Note")
      .tap(_.onclick = _ =>
        service
          .createNote(titleInput.value, contentTextArea.value)
          .map(addNote)
      )
  val form = div(
    titleInput,
    contentTextArea,
    saveButton
  ).tap(_.className = "note-form")

  val appContainer = div(
    h1("My Notepad"),
    form
  ).tap(_.id = "app-container")

  def addNote(note: Note): Unit =
    val elem = div(
      h2(note.title),
      p(note.content)
    ).tap(_.className = "note")
    appContainer.appendChild(elem)

  @main def start: Unit =
    body.appendChild(appContainer)
    for
      notes <- service.getAllNotes()
      note <- notes
    do addNote(note)
