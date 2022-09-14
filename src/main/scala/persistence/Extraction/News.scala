package persistence.Extraction

import spray.json.{DefaultJsonProtocol, RootJsonFormat}


case class News(article: String, source: String, dateTime: String)


object JsonNewsProtocol extends DefaultJsonProtocol {
  implicit val jsonNewsFormat: RootJsonFormat[News] = jsonFormat3(News)
}
