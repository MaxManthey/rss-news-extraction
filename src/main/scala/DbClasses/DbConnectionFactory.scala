package DbClasses

import java.sql.{Connection, DriverManager, SQLException}


class DbConnectionFactory private() {
  private val connectionUrl = "jdbc:h2:./db/rss_news_words"
  private val username = "sa"
  private val password = ""

  private var connection: Connection = _

  connection = DriverManager.getConnection(connectionUrl, username, password)
  dropTables()
  createTables()


  def close(): Unit = connection.close()


  @throws[SQLException]
  private def dropTables(): Unit = {
    val dropTablesQuery = "DROP TABLE IF EXISTS word_frequency;" +
      "DROP TABLE IF EXISTS news_sources;" +
      "DROP TABLE IF EXISTS news_dates;"
    connection.createStatement().execute(dropTablesQuery)
  }

  @throws[SQLException]
  private def createTables(): Unit = {
    val createSourceDateTable = "CREATE TABLE source_date(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "date DATE UNIQUE UNIQUE," +
      "source VARCHAR(10000)," +
      "hashed_source VARCHAR(500) UNIQUE);"
    val createNewsWordsTable = "CREATE TABLE news_words(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "word VARCHAR(500) UNIQUE);"
    val createWordFrequencyTable = "CREATE TABLE word_frequency(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "frequency SMALLINT," +
      "news_words_id INT," +
      "sources_dates_id INT," +
      "FOREIGN KEY(news_words_id) REFERENCES news_words(id)," +
      "FOREIGN KEY(sources_dates_id) REFERENCES sources_dates(id)," +
      "UNIQUE KEY `word_source_date` (`news_words_id`,`sources_dates_id`));"
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
