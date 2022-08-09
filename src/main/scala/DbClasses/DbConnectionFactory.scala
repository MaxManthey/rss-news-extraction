package DbClasses

import java.sql.{Connection, DriverManager, SQLException}

//TODO pathToDb, username, pw in constructor
class DbConnectionFactory private() {
  private val connectionUrl = "jdbc:h2:../rss_news_words"
  private val username = "sa"
  private val password = ""

  private val connection: Connection = DriverManager.getConnection(connectionUrl, username, password)
  dropTables()
  createTables()


  def close(): Unit = connection.close()


  @throws[SQLException]
  private def dropTables(): Unit = {
    val dropTablesQuery = "DROP TABLE IF EXISTS word_frequency;" +
    "DROP TABLE IF EXISTS news_words;" +
    "DROP TABLE IF EXISTS source_date;"
    connection.createStatement().execute(dropTablesQuery)
  }

  @throws[SQLException]
  private def createTables(): Unit = {
    val createSourceDateTable = "CREATE TABLE IF NOT EXISTS source_date(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "date DATE," +
      "source VARCHAR(10000)," +
      "hashed_source VARCHAR(500) UNIQUE);"
    val createNewsWordsTable = "CREATE TABLE IF NOT EXISTS news_words(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "word VARCHAR(500) UNIQUE);"
    val createWordFrequencyTable = "CREATE TABLE IF NOT EXISTS word_frequency(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "frequency SMALLINT," +
      "news_words_id INT," +
      "source_date_id INT," +
      "FOREIGN KEY(news_words_id) REFERENCES news_words(id)," +
      "FOREIGN KEY(source_date_id) REFERENCES source_date(id)," +
      "UNIQUE KEY `word_source_date` (`news_words_id`,`source_date_id`));"
    connection.createStatement().execute(createSourceDateTable + createNewsWordsTable + createWordFrequencyTable)
  }


  @throws[SQLException]
  def getConnection: Connection = connection
}


object DbConnectionFactory {
  private var dbConnectionFactory: DbConnectionFactory = _

  def getInstance: DbConnectionFactory = {
    if (dbConnectionFactory == null) dbConnectionFactory = new DbConnectionFactory
    dbConnectionFactory
  }
}
