package DAOs

import DbClasses.{DbConnectionFactory, WordFrequency}
import com.typesafe.scalalogging.Logger

import java.sql.{Connection, Date, SQLException}


case class WordFrequencyDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("SourceDateDao Logger")

  private val preparedSave = getConnection.prepareStatement(
    "INSERT IGNORE INTO word_frequency(frequency, news_words_id, sources_dates_id) VALUES(?, ?, ?);"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


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


  def closePrepared(): Unit = preparedSave.close()
}
