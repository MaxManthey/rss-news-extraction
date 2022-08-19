package DAO

import DbClasses.{DbConnectionFactory, WordFrequency}
import com.typesafe.scalalogging.Logger

import java.sql.{Connection, Date, SQLException}


case class WordFrequencyDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("SourceDateDao Logger")

  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO word_frequency(frequency, news_word_id, source_date_id) VALUES(?, ?, ?);"
  )
  private val preparedFindId = getConnection.prepareStatement(
    "SELECT * FROM word_frequency WHERE frequency = ? AND news_word_id = ? AND source_date_id = ?;"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def saveIfNotExists(wordFrequency: WordFrequency): Unit = if(findId(wordFrequency) == -1) save(wordFrequency)


  def save(wordFrequency: WordFrequency): Unit = {
    try {
      preparedSave.setInt(1, wordFrequency.frequency)
      preparedSave.setInt(2, wordFrequency.newsWordsId)
      preparedSave.setInt(3, wordFrequency.sourceDateId)
      preparedSave.execute
    } catch {
      case e: SQLException =>
        logger.error(s"Error trying to save wordFrequency: ${wordFrequency.toString} ${e.getCause}")
      case e: Exception =>
        logger.error(s"Error trying to save wordFrequency: ${wordFrequency.toString} ${e.getCause}")
    }
  }


  def findId(wordFrequency: WordFrequency): Int = {
    try {
      preparedFindId.setInt(1, wordFrequency.frequency)
      preparedFindId.setInt(2, wordFrequency.newsWordsId)
      preparedFindId.setInt(3, wordFrequency.sourceDateId)
      val resultSet = preparedFindId.executeQuery
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to find wordFrequency: ${wordFrequency.toString} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to find wordFrequency: ${wordFrequency.toString} ${e.getCause}")
    }
    -1
  }


  def closePrepared(): Unit = {
    preparedSave.close()
  }
}
