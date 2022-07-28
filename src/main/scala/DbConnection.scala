import com.typesafe.scalalogging.Logger
import java.sql.{Connection, DriverManager, PreparedStatement}


class DbConnection {

  private val logger: Logger = Logger("DbConnection Logger")
  private var prepared: PreparedStatement = _
  private var connection: Connection = _
  private val driver = "com.mysql.cj.jdbc.Driver"
  private val url = "jdbc:mysql://localhost/rss_news_words"
  private val username = "root"
  private val password = ""


  def openDbConnection(): Unit = {
    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      val insert = s"INSERT INTO word_occurrence(word, occurrence_amount, source, date_time) VALUES(?, ?, ?, ?);"
      prepared = connection.prepareStatement(insert);
    } catch {
      case e: Throwable => logger.error("Db connection failed: " + e.getCause)
    }
  }


  def closeDbConnection(): Unit = {
    prepared.close()
    connection.close()
  }


  def dropWordOccurrenceTable(): Unit = {
    try {
      connection.createStatement().execute("drop table word_occurrence;")
    } catch {
      case e: Throwable => logger.error("Table word_occurrence could not be dropped." + e.getCause)
    }
  }


  def createWordOccurrenceTable(): Unit = {
    val createTableStatement = "CREATE TABLE word_occurrence(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "word VARCHAR(80) CHARACTER SET utf8mb4," +
      "occurrence_amount SMALLINT," +
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


  def addWordToDb(word: String, occurrenceAmount: Int, source: String, dateTime: String): Unit = {
    try {
      prepared.setString(1, word)
      prepared.setInt(2, occurrenceAmount)
      prepared.setString(3, source)
      prepared.setString(4, dateTime)
      prepared.execute()
    } catch {
      case e: Throwable => logger.error(s"Error trying to save word: $word; ${e.getCause}")
    }
  }
}
