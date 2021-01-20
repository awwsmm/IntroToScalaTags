//   https://github.com/awwsmm/IntroToScalaTags

package scalatags

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._


object Index {

  def main (args: Array[String]): Unit = {

    val paragraph = p("This is my paragraph")

    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      val body: HTMLElement = document.body
      body.appendChild(paragraph.render)
    })

    println("Hello, World!")
    System.err.println("Whoopsie!")
  }

}