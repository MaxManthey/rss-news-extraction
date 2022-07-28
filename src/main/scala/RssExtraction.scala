import com.typesafe.scalalogging.Logger

object RssExtraction {

  private val logger: Logger = Logger("RssExtraction Logger")
  private val articleExtraction = new ArticleExtraction
  private val dbConnection = new DbConnection


  def main(args: Array[String]): Unit = {
    dbConnection.openDbConnection()

    dbConnection.dropWordOccurrenceTable()
    dbConnection.createWordOccurrenceTable()

    val source = scala.io.Source.fromFile("FilterWords.txt")
    val lines = try source.mkString.split("\n").map(line => line.split(", ")) finally source.close()
    val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))

    for(fileName <- articleExtraction.getAllFileNamesFromDir) {
      var newsObj: News = null

      val article = articleExtraction.getNewsObject(fileName) match {
        case Some(value) =>
          newsObj = value
          Some(articleExtraction.stripHtml(value.article, stoppwortList, miscList))
        case None => None
      }

      val wordsMap = article match {
        case Some(value) => Some(articleExtraction.wordsByAmount(value))
        case None => None
      }

      wordsMap match {
        case Some(value) =>
          logger.info("Successfully added article " + newsObj.source + " to DB")
          dbConnection.addWordsMapToDb(newsObj.source, newsObj.dateTime, value)
        case None => logger.error("Failed to add article " + newsObj.source + " to DB")
      }
    }

    dbConnection.closeDbConnection()
  }
}
