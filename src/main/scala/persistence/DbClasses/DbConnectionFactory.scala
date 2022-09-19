package persistence.DbClasses

import java.sql.{Connection, DriverManager, SQLException}


case class DbConnectionFactory(pathToDb: String) {
  private val connectionUrl = "jdbc:h2:" + pathToDb + "/rss_news_articles"
  private val username = "sa"
  private val password = ""

  private val connection: Connection = DriverManager.getConnection(connectionUrl, username, password)
  dropTables()
  createTables()


  def close(): Unit = connection.close()


  @throws[SQLException]
  private def dropTables(): Unit = {
    val dropTablesQuery = "DROP TABLE IF EXISTS word_frequency;" +
    "DROP TABLE IF EXISTS news_word;" +
    "DROP TABLE IF EXISTS source_date;" +
    "DROP TABLE IF EXISTS aggregated_word_frequency;" +
    "DROP TABLE IF EXISTS aggregated_date;"
    connection.createStatement().execute(dropTablesQuery)
  }

  @throws[SQLException]
  private def createTables(): Unit = {
    val createSourceDateTable = "CREATE TABLE IF NOT EXISTS source_date(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "date DATE," +
      "source CLOB," +
      "hashed_source VARCHAR(32) UNIQUE);"
    val createNewsWordsTable = "CREATE TABLE IF NOT EXISTS news_word(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "word VARCHAR(500) UNIQUE);"
    val createWordFrequencyTable = "CREATE TABLE IF NOT EXISTS word_frequency(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "frequency SMALLINT," +
      "news_word_id INT," +
      "source_date_id INT," +
      "FOREIGN KEY(news_word_id) REFERENCES news_word(id)," +
      "FOREIGN KEY(source_date_id) REFERENCES source_date(id)," +
      "UNIQUE KEY `word_source_date` (`news_word_id`,`source_date_id`));"
    val createAggregatedDateTable = "CREATE TABLE IF NOT EXISTS aggregated_date(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "date DATE UNIQUE);"
    val createAggregatedWorFrequencyTable = "CREATE TABLE IF NOT EXISTS aggregated_word_frequency(" +
      "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
      "frequency SMALLINT," +
      "news_word_id INT," +
      "date DATE," +
      "FOREIGN KEY(news_word_id) REFERENCES news_word(id)," +
      "UNIQUE KEY `aggregated_word_date` (`news_word_id`,`date`));"
    connection.createStatement().execute(
      createSourceDateTable +
      createNewsWordsTable +
      createWordFrequencyTable +
      createAggregatedDateTable +
      createAggregatedWorFrequencyTable
    )
  }


  @throws[SQLException]
  def getConnection: Connection = connection
}
