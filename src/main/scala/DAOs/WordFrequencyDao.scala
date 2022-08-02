package DAOs

import DbClasses.{DbConnectionFactory, WordFrequency}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, PreparedStatement, SQLException}


class WordFrequencyDao(val dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("NewsSourceDAO Logger")
  private var prepared: PreparedStatement = _


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def save(wordFrequency: WordFrequency): Unit = {
    try {
      val addNewsDateQuery = "INSERT INTO word_frequency(word, frequency, source_id, date_id) VALUES(?, ?, ?, ?);"
      prepared = getConnection.prepareStatement(addNewsDateQuery)
      prepared.setString(1, wordFrequency.word)
      prepared.setInt(2, wordFrequency.frequency)
      prepared.setInt(3, wordFrequency.sourceId)
      prepared.setInt(4, wordFrequency.dateId)
      prepared.execute()
    } catch {
      case e: SQLException => logger.error(s"Error trying to add word: ${wordFrequency.word} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add word: ${wordFrequency.word} ${e.getCause}")
    }
  }

  def saveAll(wordFrequencyArr: Array[WordFrequency]): Unit =
    for(wordFrequency <- wordFrequencyArr) save(wordFrequency)
}


