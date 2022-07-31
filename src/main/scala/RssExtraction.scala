import com.typesafe.scalalogging.Logger
import java.time.LocalDateTime


object RssExtraction {

  private val logger: Logger = Logger("RssExtraction Logger")
  private val articleExtraction = new ArticleExtraction
  private val dbConnection = new DbConnection


  def main(args: Array[String]): Unit = {
    if(!dbConnection.open()) System.exit(1)

    val source = scala.io.Source.fromFile("src/main/resources/FilterWords.txt")
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

      val dateId = dbConnection.getFileDateId(LocalDateTime.parse(newsObj.dateTime).toLocalDate)
      val sourceId = dbConnection.getFileSourceId(newsObj.source, dateId)

      val wordsMap = article match {
        case Some(value) => Some(articleExtraction.wordsByFrequency(value))
        case None => None
      }

      wordsMap match {
        case Some(value) =>
          logger.info("Successfully added article " + newsObj.source + " to DB")
          dbConnection.addWordsMapToWordFrequencyTable(sourceId, dateId, value)
        case None => logger.error("Failed to add article " + newsObj.source + " to DB")
      }
    }

    dbConnection.close()
  }
}
