package example

import org.scalajs.dom.document.{createElement => createDocumentElement}
import org.scalajs.dom.html.{
  Button, Div, Element, Heading, Input, Paragraph, TextArea
}
import scala.compiletime.constValue
import scala.util.chaining.scalaUtilChainingOps

private type ElementType[E <: String & Singleton] =
  E match
    case "h1" => Heading
    case "h2" => Heading
    case "p" => Paragraph
    case "button" => Button

private inline def createElement[ElementName <: String & Singleton](content: String) =
  constValue[ElementName]
    .pipe(createDocumentElement)
    .tap(_.textContent = content)
    .asInstanceOf[ElementType[ElementName]]

def div(children: Element*): Div =
  createDocumentElement("div")
    .tap(elem => children.foreach(elem.appendChild))
    .asInstanceOf[Div]
def h1(textContent: String): Heading =
  createElement["h1"](textContent)
def h2(textContent: String): Heading =
  createElement["h2"](textContent)
def p(textContent: String): Paragraph =
  createElement["p"](textContent)
def input(): Input =
  createDocumentElement("input").asInstanceOf[Input]
def textarea(): TextArea =
  createDocumentElement("textarea").asInstanceOf[TextArea]
def button(textContent: String): Button =
  createElement["button"](textContent)
