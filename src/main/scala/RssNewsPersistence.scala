import DAOs.{NewsDateDao, NewsSourceDao, WordFrequencyDao}
import DbClasses.{Article, DbConnectionFactory}
//import Extraction.ArticleExtractor
import com.typesafe.scalalogging.Logger

import java.sql.SQLException

object RssNewsPersistence {
  private val logger: Logger = Logger("RssNewsPersistence Logger")


  def main(args: Array[String]): Unit = {
//    ArticleExtractor("../unzipped/news-files/").foreach{ println }
//
//    System.exit(1)

    val connectionFactory = DbConnectionFactory.getInstance

    val dateDao = new NewsDateDao(connectionFactory)
    val sourceDao = new NewsSourceDao(connectionFactory)
    val wordFrequencyDao = new WordFrequencyDao(connectionFactory)

    val filterWords = scala.io.Source.fromFile("src/main/resources/FilterWords.txt")
    val lines = try filterWords.mkString.split("\n").map(line => line.split(", ")) finally filterWords.close()
    val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))

    PersistenceHandler.getInstance().persistExistingFiles(stoppwortList, miscList, dateDao, sourceDao, wordFrequencyDao)

    try{
      dateDao.closePrepared()
      sourceDao.closePrepared()
      wordFrequencyDao.closePrepared()
      connectionFactory.close()
    } catch {
      case e: SQLException => logger.info("SQLException trying to close db: " + e.getCause)
        System.exit(1)
      case e: Exception => logger.info("Exception trying to close db: " + e.getCause)
        System.exit(1)
    }
  }
}
