package tags // TODO: change!

import io.circe.generic.auto._
import io.circe.parser._
import sttp.client3._

object Random {

  case class Dog(fileSizeBytes: Int, url: String)

  def dog(): Option[Dog] = {
    val json = getJSON("https://random.dog/woof.json")
    json.map(decode[Dog](_)) match {
      case Some(Right(dog)) => Some(dog)
      case _ => None
    }
  }

  case class Quote(_id: String, tags: List[String], content: String, author: String, length: Int)

  def quote(): Option[Quote] = {
    val json = getJSON("https://api.quotable.io/random")
    json.map(decode[Quote](_)) match {
      case Some(Right(quote)) => Some(quote)
      case _ => None
    }
  }

  def getJSON(url: String): Option[String] = {
    val request = basicRequest.get(uri"$url")
    val backend = HttpURLConnectionBackend()
    request.send(backend).body match {
      case Right(success) => Some(success)
      case _ => None
    }
  }

}
