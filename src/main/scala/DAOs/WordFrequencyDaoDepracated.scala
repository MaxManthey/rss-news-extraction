//TODO DELETE

package DAOs

import DbClasses.{DbConnectionFactory, WordFrequencyDeprecated}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, SQLException}


class WordFrequencyDaoDepracated(val dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("NewsSourceDAO Logger")
  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO word_frequency(word, frequency, source_id) VALUES(?, ?, ?);"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def save(wordFrequency: WordFrequencyDeprecated): Unit = {
    try {
      preparedSave.setString(1, wordFrequency.word)
      preparedSave.setInt(2, wordFrequency.frequency)
      preparedSave.setInt(3, wordFrequency.sourceId)
      preparedSave.execute
    } catch {
      case e: SQLException => logger.error(s"Error trying to add word: ${wordFrequency.word} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add word: ${wordFrequency.word} ${e.getCause}")
    }
  }


  def saveAll(wordFrequencyArr: Array[WordFrequencyDeprecated]): Unit =
    for(wordFrequency <- wordFrequencyArr) save(wordFrequency)


  def closePrepared(): Unit = preparedSave.close()
}


