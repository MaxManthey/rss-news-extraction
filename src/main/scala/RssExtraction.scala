import DAOs.{NewsDateDAO, NewsSourceDAO, WordFrequencyDAO}
import DbClasses.{NewsDate, NewsSource, WordFrequency}
import com.typesafe.scalalogging.Logger
import java.time.LocalDateTime


object RssExtraction {

  private val logger: Logger = Logger("RssExtraction Logger")
  private val articleExtraction = new ArticleExtraction
  private val dbConnection = new DbConnection


  def main(args: Array[String]): Unit = {
    val testDate = NewsDate(LocalDateTime.parse("2022-07-31T19:18:34.179159").toLocalDate)
    val newsDao = new NewsDateDAO()
    newsDao.add(testDate)
    val res = newsDao.findId(testDate.date)
    println("res: " + res)

    val testSource = "www.test.de"
    val sourceDao = new NewsSourceDAO()
    sourceDao.add(NewsSource(testSource, 1))
    val res2 = sourceDao.findId(testSource)
    println("res2: " + res2)

    val wfDao = new WordFrequencyDAO()
    wfDao.add(WordFrequency("moin", 75, 44, 2))

    System.exit(1)

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
