package DbClasses

import com.typesafe.scalalogging.Logger
import java.sql.{Connection, DriverManager, SQLException}


class DbConnectionFactory private() {
  //TODO drop und create tables hinzufügen
  //TODO close connection
  //TODO close prepared in Daos
  //Connection Factory DAOs übergeben
  private val logger: Logger = Logger("DbConnectionFactory Logger")
  private val driverClassName = "org.h2.Driver"
  private val connectionUrl = "jdbc:h2:./db/rss_news_words"
  private val username = "sa"
  private val password = ""

  try Class.forName(driverClassName)
  catch {
    case e: ClassNotFoundException => logger.error("Db driver could not be found: " + e.getCause)
  }

  @throws[SQLException]
  def getConnection: Connection = {
    val test = DriverManager.getConnection(connectionUrl, username, password)
    test
  }
}


object DbConnectionFactory {
  private var dbConnectionFactory: DbConnectionFactory = _

  def getInstance: DbConnectionFactory = {
    if (dbConnectionFactory == null) dbConnectionFactory = new DbConnectionFactory
    dbConnectionFactory
  }
}
