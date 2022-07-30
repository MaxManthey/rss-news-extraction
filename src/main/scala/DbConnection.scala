import com.typesafe.scalalogging.Logger
import java.sql.{Connection, DriverManager, PreparedStatement}


class DbConnection {

  private val logger: Logger = Logger("DbConnection Logger")
  private var prepared: PreparedStatement = _
  private var connection: Connection = _
  private val driver = "org.h2.Driver"
  private val url = "jdbc:h2:./db/rss_news_words"
  private val username = "sa"
  private val password = ""

  /*
  TODO normalize db
    - table words / frequency / foreign key source / foreign key date
    - table source / foreign key date
    - table date (without time)
   */

  def open(): Boolean = {
    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      dropWordFrequencyTable()
      createWordFrequencyTable()
      val insert = s"INSERT INTO word_frequency(word, frequency, source, date_time) VALUES(?, ?, ?, ?);"
      prepared = connection.prepareStatement(insert);
      true
    } catch {
      case e: Throwable =>
        logger.error("Db connection failed: " + e.getCause)
        false
    }
  }


  def close(): Unit = {
    prepared.close()
    connection.close()
  }


  private def dropWordFrequencyTable(): Unit = {
    try {
      connection.createStatement().execute("DROP TABLE IF EXISTS word_frequency;")
    } catch {
      case e: Throwable => logger.error("Table word_occurrence could not be dropped." + e.getCause)
    }
  }


  private def createWordFrequencyTable(): Unit = {
    val createTableStatement = "CREATE TABLE IF NOT EXISTS word_frequency(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "word VARCHAR(80)," +
      "frequency SMALLINT," +
      "source VARCHAR(1000)," +
      "date_time DATETIME" +
      ");"
    try {
      connection.createStatement().execute(createTableStatement)
    } catch {
      case e: Throwable => logger.error("Table word_occurrence could not be created." + e.getCause)
    }
  }


  def addWordsMapToDb(source: String, dateTime: String, wordsMap: Map[String, Int]): Unit =
    wordsMap.keys.foreach{ key => addWordToDb(key, wordsMap(key), source, dateTime) }


  def addWordToDb(word: String, frequency: Int, source: String, dateTime: String): Unit = {
    try {
      prepared.setString(1, word)
      prepared.setInt(2, frequency)
      prepared.setString(3, source)
      prepared.setString(4, dateTime)
      prepared.execute()
    } catch {
      case e: Throwable => logger.error(s"Error trying to save word: $word; ${e.getCause}")
    }
  }
}
