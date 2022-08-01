package DAOs

import DbClasses.{DbConnectionFactory, NewsSource}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, PreparedStatement, SQLException}


class NewsSourceDAO {
  private val logger: Logger = Logger("NewsSourceDAO Logger")
  private var prepared: PreparedStatement = _


  @throws[SQLException]
  private def getConnection: Connection = DbConnectionFactory.getInstance.getConnection


  def add(newsSource: NewsSource): Unit = {
    try {
      val addNewsDateQuery = "INSERT INTO news_sources(source, date_id) VALUES(?, ?);"
      prepared = getConnection.prepareStatement(addNewsDateQuery)
      prepared.setString(1, newsSource.source)
      prepared.setInt(2, newsSource.dateId)
      prepared.execute()
    } catch {
      case e: SQLException => logger.error(s"Error trying to add source: ${newsSource.source} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add source: ${newsSource.source} ${e.getCause}")
    }
  }


  def findId(source: String): Int = {
    try {
      val findNewsDateQuery = s"SELECT * FROM news_sources WHERE source = '$source'"
      val resultSet = getConnection.prepareStatement(findNewsDateQuery).executeQuery
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to find source: $source ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to find source: $source ${e.getCause}")
    }
    -1
  }
}
