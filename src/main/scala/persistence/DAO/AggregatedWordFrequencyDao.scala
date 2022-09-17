package persistence.DAO

import com.typesafe.scalalogging.Logger
import persistence.DbClasses.{AggregatedWordFrequency, DbConnectionFactory}
import java.sql.{Connection, SQLException}


case class AggregatedWordFrequencyDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger = Logger("AggregatedWordFrequencyDao Logger")

  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO aggregated_word_frequency(frequency, news_word_id, date) VALUES(?, ?, ?);"
  )
  private val preparedFindId = getConnection.prepareStatement(
    "SELECT * FROM aggregated_word_frequency WHERE frequency = ? AND news_word_id = ? AND date = ?;"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def saveIfNotExists(aggregatedWordFrequency: AggregatedWordFrequency): Unit =
    if(findId(aggregatedWordFrequency).isEmpty) save(aggregatedWordFrequency)


  def save(aggregatedWordFrequency: AggregatedWordFrequency): Unit = {
    try {
      preparedSave.setInt(1, aggregatedWordFrequency.frequency)
      preparedSave.setInt(2, aggregatedWordFrequency.newsWordId)
      preparedSave.setDate(3, aggregatedWordFrequency.date)
      preparedSave.execute
    } catch {
      case e: SQLException =>
        logger.error(s"Error trying to save aggregatedWordFrequency: ${aggregatedWordFrequency.toString} ${e.getCause}")
      case e: Exception =>
        logger.error(s"Error trying to save aggregatedWordFrequency: ${aggregatedWordFrequency.toString} ${e.getCause}")
    }
  }


  def findId(aggregatedWordFrequency: AggregatedWordFrequency): Option[Int] = {
    try {
      preparedFindId.setInt(1, aggregatedWordFrequency.frequency)
      preparedFindId.setInt(2, aggregatedWordFrequency.newsWordId)
      preparedFindId.setDate(3, aggregatedWordFrequency.date)
      val resultSet = preparedFindId.executeQuery
      if(resultSet.next()) {
        return Some(resultSet.getInt("id"))
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to find aggregatedWordFrequency: ${aggregatedWordFrequency.toString} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to find aggregatedWordFrequency: ${aggregatedWordFrequency.toString} ${e.getCause}")
    }
    None
  }


  def closePrepared(): Unit = {
    preparedSave.close()
    preparedFindId.close()
  }
}
