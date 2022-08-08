package DAOs

import DbClasses.{DbConnectionFactory, NewsWord}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, SQLException}


case class NewsWordDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("NewsWordDao Logger")

  private val preparedSave = getConnection.prepareStatement(
    "INSERT IGNORE INTO news_words(word) VALUES(?);"
  )
  private val preparedFindId = getConnection.prepareStatement(
    "SELECT * FROM news_words WHERE word = ?;"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def save(newsWord: NewsWord): Unit = {
    try {
      preparedSave.setString(1, newsWord.word)
      preparedSave.execute
    } catch {
      case e: SQLException => logger.error(s"Error trying to add word: ${newsWord.word} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add word: ${newsWord.word} ${e.getCause}")
    }
  }


  def findId(word: String): Int = {
    try {
      preparedFindId.setString(1, word)
      val resultSet = preparedFindId.executeQuery
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to find word: $word ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to find word: $word ${e.getCause}")
    }
    -1
  }


  def closePrepared(): Unit = {
    preparedSave.close()
    preparedFindId.close()
  }
}
