import DAOs.{NewsDateDao, NewsSourceDao, WordFrequencyDao}
import DbClasses.DbConnectionFactory
import com.typesafe.scalalogging.Logger
import java.sql.SQLException

object RssNewsPersistence {
  private val logger: Logger = Logger("RssNewsPersistence Logger")


  def main(args: Array[String]): Unit = {
    val connectionFactory = DbConnectionFactory.getInstance
    try{
      connectionFactory.open()
    } catch {
      case e: SQLException => logger.info("SQLException trying to connect to db: " + e.getCause)
        System.exit(1)
      case e: Exception => logger.info("Exception trying to connect to db: " + e.getCause)
        System.exit(1)
    }

    val dateDao = new NewsDateDao(connectionFactory)
    val sourceDao = new NewsSourceDao(connectionFactory)
    val wordFrequencyDao = new WordFrequencyDao(connectionFactory)

    val source = scala.io.Source.fromFile("src/main/resources/FilterWords.txt")
    val lines = try source.mkString.split("\n").map(line => line.split(", ")) finally source.close()
    val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))

    PersistenceHandler.getInstance().persistExistingFiles(stoppwortList, miscList, dateDao, sourceDao, wordFrequencyDao)

    try{
      connectionFactory.close()
    } catch {
      case e: SQLException => logger.info("SQLException trying to close db: " + e.getCause)
        System.exit(1)
      case e: Exception => logger.info("Exception trying to close db: " + e.getCause)
        System.exit(1)
    }
  }
}
