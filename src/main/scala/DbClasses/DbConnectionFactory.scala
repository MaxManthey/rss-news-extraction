package DbClasses

import com.typesafe.scalalogging.Logger

import java.sql.{Connection, DriverManager, PreparedStatement, SQLException}


class DbConnectionFactory private() {
  private val logger: Logger = Logger("DbConnectionFactory Logger")
  private val driverClassName = "org.h2.Driver"
  private val connectionUrl = "jdbc:h2:./db/rss_news_words"
  private val username = "sa"
  private val password = ""

  private var connection: Connection = _


  try Class.forName(driverClassName)
  catch {
    case e: ClassNotFoundException => logger.error("Db driver could not be found: " + e.getCause)
  }

  @throws[SQLException]
  def open(): Unit = {
    connection = DriverManager.getConnection(connectionUrl, username, password)
    dropTables()
    createTables()
  }


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
    connection.createStatement().execute(createNewsDatesTable + createNewsSourcesTable + createWordFrequencyTable)
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
