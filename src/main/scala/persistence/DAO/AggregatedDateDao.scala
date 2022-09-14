package persistence.DAO

import com.typesafe.scalalogging.Logger
import persistence.DbClasses.{AggregatedDate, DbConnectionFactory}

import java.sql.{Connection, SQLException}


case class AggregatedDateDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger = Logger("AggregatedDateDao Logger")

  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO aggregated_date(date) VALUES(?);"
  )
  private val preparedFindId = getConnection.prepareStatement(
    "SELECT * FROM aggregated_date WHERE date = ?;"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def saveIfNotExists(aggregatedDate: AggregatedDate): Unit = if(findId(aggregatedDate).isEmpty) save(aggregatedDate)


  def save(aggregatedDate: AggregatedDate): Unit = {
    try {
      preparedSave.setDate(1, aggregatedDate.date)
      preparedSave.execute
    } catch {
      case e: SQLException => logger.error(s"Error trying to add date: ${aggregatedDate.date} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add date: ${aggregatedDate.date} ${e.getCause}")
    }
  }


  def findId(aggregatedDate: AggregatedDate): Option[Int] = {
    try {
      preparedFindId.setDate(1, aggregatedDate.date)
      val resultSet = preparedFindId.executeQuery
      if(resultSet.next()) {
        return Some(resultSet.getInt("id"))
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to find date: ${aggregatedDate.date} - ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to find date: ${aggregatedDate.date} - ${e.getCause}")
    }
    None
  }


  def closePrepared(): Unit = {
    preparedSave.close()
    preparedFindId.close()
  }
}
