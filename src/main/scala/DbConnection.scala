import com.typesafe.scalalogging.Logger
import java.sql.{Connection, DriverManager, PreparedStatement}
import java.time.LocalDate
import java.sql.Date


class DbConnection {

  private val logger: Logger = Logger("DbConnection Logger")
  private var preparedDates: PreparedStatement = _
  private var preparedSources: PreparedStatement = _
  private var preparedWordFrequency: PreparedStatement = _
  private var connection: Connection = _
  private val driver = "org.h2.Driver"
  private val url = "jdbc:h2:./db/rss_news_words"
  private val username = "sa"
  private val password = ""


  def open(): Boolean = {
    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      dropTables()
      createTables()
      val insertNewsDates = "INSERT INTO news_dates(date) VALUES(?);"
      val insertNewsSources = "INSERT INTO news_sources(source, date_id) VALUES(?, ?);"
      val insertWordFrequency = "INSERT INTO word_frequency(word, frequency, source_id, date_id) VALUES(?, ?, ?, ?);"
      preparedDates = connection.prepareStatement(insertNewsDates)
      preparedSources = connection.prepareStatement(insertNewsSources)
      preparedWordFrequency = connection.prepareStatement(insertWordFrequency)
      true
    } catch {
      case e: Throwable =>
        logger.error("Db connection failed: " + e.getCause)
        false
    }
  }


  def close(): Unit = {
    preparedDates.close()
    preparedSources.close()
    preparedWordFrequency.close()
    connection.close()
  }


  private def dropTables(): Unit = {
    val dropTablesQuery = "DROP TABLE IF EXISTS word_frequency;" +
      "DROP TABLE IF EXISTS news_sources;" +
      "DROP TABLE IF EXISTS news_dates;"
    try {
      connection.createStatement().execute(dropTablesQuery)
    } catch {
      case e: Throwable => logger.error("Table word_occurrence could not be dropped." + e.getCause)
    }
  }


  private def createTables(): Unit = {
    val createNewsDatesTable = "CREATE TABLE news_dates(" +
        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
        "date DATE UNIQUE);"
    val createNewsSourcesTable = "CREATE TABLE news_sources(" +
        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
        "source VARCHAR(10000)," +
        "date_id INT," +
        "FOREIGN KEY(date_id) REFERENCES news_dates(id));"
    val createWordFrequencyTable = "CREATE TABLE word_frequency(" +
        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
        "word VARCHAR(80)," +
        "frequency SMALLINT," +
        "source_id INT," +
        "date_id INT," +
        "FOREIGN KEY(source_id) REFERENCES news_sources(id)," +
        "FOREIGN KEY(date_id) REFERENCES news_dates(id));"
    try {
      //TODO alle in einem probieren
      connection.createStatement().execute(createNewsDatesTable)
      connection.createStatement().execute(createNewsSourcesTable)
      connection.createStatement().execute(createWordFrequencyTable)
    } catch {
      case e: Throwable => logger.error("Table word_occurrence could not be created." + e.getCause)
    }
  }


  def getFileDateId(date: LocalDate): Int = {
    val existingDate = getDateId(date)
    if(existingDate > 0) existingDate
    else {
      saveDate(date)
      getDateId(date)
    }
  }


  private def saveDate(date: LocalDate): Unit = {
    try {
      preparedDates.setDate(1, Date.valueOf(date))
      preparedDates.execute()
    } catch {
      case e: Throwable => logger.error(s"Error trying to save date: $date; ${e.getCause}")
    }
  }


  private def getDateId(date: LocalDate): Int = {
    try {
      val query = s"SELECT * FROM news_dates WHERE date = '$date'"
      val resultSet = connection.createStatement().executeQuery(query)
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
      -1
    } catch {
      case e: Throwable =>
        logger.error(s"Error trying to get date: $date; ${e.getCause}")
        -1
    }
  }


  def getFileSourceId(source: String, dateId: Int): Int = {
    val existingSource = getSourceId(source)
    if(existingSource > 0) existingSource
    else {
      saveSource(source, dateId)
      getSourceId(source)
    }
  }


  private def saveSource(source: String, dateId: Int): Unit = {
    try {
      preparedSources.setString(1, source)
      preparedSources.setInt(2, dateId)
      preparedSources.execute()
    } catch {
      case e: Throwable => logger.error(s"Error trying to save source: $source; ${e.getCause}")
    }
  }


  private def getSourceId(source: String): Int = {
    try {
      val query = s"SELECT * FROM news_sources WHERE source = '$source'"
      val resultSet = connection.createStatement().executeQuery(query)
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
      -1
    } catch {
      case e: Throwable =>
        logger.error(s"Error trying to get source: $source; ${e.getCause}")
        -1
    }
  }


  def addWordsMapToWordFrequencyTable(sourceId: Int, dateId: Int, wordsMap: Map[String, Int]): Unit =
    wordsMap.keys.foreach{ key => addWordToDb(key, wordsMap(key), sourceId, dateId) }


  def addWordToDb(word: String, frequency: Int, sourceId: Int, dateId: Int): Unit = {
    try {
      preparedWordFrequency.setString(1, word)
      preparedWordFrequency.setInt(2, frequency)
      preparedWordFrequency.setInt(3, sourceId)
      preparedWordFrequency.setInt(4, dateId)
      preparedWordFrequency.execute()
    } catch {
      case e: Throwable => logger.error(s"Error trying to save word: $word; ${e.getCause}")
    }
  }
}
