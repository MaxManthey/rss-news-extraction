import com.typesafe.scalalogging.Logger

import java.time.LocalDateTime

object RssExtraction {

  private val logger: Logger = Logger("RssExtraction Logger")
  private val articleExtraction = new ArticleExtraction

  //TODO remove println
  def main(args: Array[String]): Unit = {
    //TODO drop old db

    val source = scala.io.Source.fromFile("FilterWords.txt")
    val lines = try source.mkString.split("\n").map(line => line.split(", ")) finally source.close()
    val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))

    for(fileName <- articleExtraction.getAllFileNamesFromDir) {
      val article = articleExtraction.getNewsObject(fileName) match {
        case Some(value) => Some(articleExtraction.stripHtml(value.article, stoppwortList, miscList))
        case None => None
      }

      val wordsMap = article match {
        case Some(value) => Some(articleExtraction.wordsByAmount(value))
        case None => None
      }
      //TODO Date from string to LocalDateTime
      //TODO persist words with amount, source and date to DB in lowercase
      //TODO add log for success
    }

    //test local connection
    DbConnection("moin", 1, "abc", LocalDateTime.now()).addWordToDb()
  }
}
