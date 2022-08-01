package DAOs

import DbClasses.{DbConnectionFactory, NewsDate}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, Date, PreparedStatement, SQLException}
import java.time.LocalDate


class NewsDateDao(val dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("NewsDateDAO Logger")
  private var prepared: PreparedStatement = _


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def save(newsDate: NewsDate): Unit = {
    try {
      val addNewsDateQuery = "INSERT INTO news_dates(date) VALUES(?);"
      prepared = getConnection.prepareStatement(addNewsDateQuery)
      prepared.setDate(1, Date.valueOf(newsDate.date))
      prepared.execute()
    } catch {
      case e: SQLException => logger.error("Error trying to save date: " + e.getCause)
      case e: Exception => logger.error("Error trying to save date: " + e.getCause)
    }
  }


  def findId(date: LocalDate): Int = {
    try {
      val findNewsDateQuery = s"SELECT * FROM news_dates WHERE date = '$date'"
      val resultSet = getConnection.prepareStatement(findNewsDateQuery).executeQuery
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to get date: $date; ${e.getCause}")
      case e: Throwable => logger.error(s"Error trying to get date: $date; ${e.getCause}")
    }
    -1
  }
}
