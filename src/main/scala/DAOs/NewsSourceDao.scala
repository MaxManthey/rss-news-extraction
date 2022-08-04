package DAOs

import DbClasses.{DbConnectionFactory, NewsSource}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, SQLException}


class NewsSourceDao(val dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("NewsSourceDAO Logger")
  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO news_sources(source, date_id) VALUES(?, ?);"
  )
  private val preparedFindId = getConnection.prepareStatement(
    "SELECT * FROM news_sources WHERE source = ?"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def save(newsSource: NewsSource): Unit = {
    try {
      preparedSave.setString(1, newsSource.source)
      preparedSave.setInt(2, newsSource.dateId)
      preparedSave.execute
    } catch {
      case e: SQLException => logger.error(s"Error trying to add source: ${newsSource.source} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add source: ${newsSource.source} ${e.getCause}")
    }
  }


  def findId(source: String): Int = {
    try {
      preparedFindId.setString(1, source)
      val resultSet = preparedFindId.executeQuery
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to find source: $source ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to find source: $source ${e.getCause}")
    }
    -1
  }


  def closePrepared(): Unit = {
    preparedSave.close()
    preparedFindId.close()
  }
}
