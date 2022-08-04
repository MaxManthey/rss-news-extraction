package DAOs

import DbClasses.{DbConnectionFactory, NewsDate}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, Date, PreparedStatement, SQLException}
import java.time.LocalDate


class NewsDateDao(val dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("NewsDateDAO Logger")
  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO news_dates(date) VALUES(?);"
  )
  private val preparedFindId = getConnection.prepareStatement(
    "SELECT * FROM news_dates WHERE date = ?"
  )



  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def save(newsDate: NewsDate): Unit = {
    try {
      preparedSave.setDate(1, Date.valueOf(newsDate.date))
      preparedSave.execute
    } catch {
      case e: SQLException => logger.error("Error trying to save date: " + e.getCause)
      case e: Exception => logger.error("Error trying to save date: " + e.getCause)
    }
  }


  def findId(date: LocalDate): Int = {
    try {
      preparedFindId.setDate(1, Date.valueOf(date))
      val resultSet = preparedFindId.executeQuery
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
    } catch {
      case e: SQLException =>
        logger.error(s"Error trying to get date: $date; ${e.getCause}")
        println(e.printStackTrace())
      case e: Throwable => logger.error(s"Error trying to get date: $date; ${e.getCause}")
        println(e.printStackTrace())
    }
    -1
  }

  def closePrepared(): Unit = {
    preparedSave.close()
    preparedFindId.close()
  }
}
