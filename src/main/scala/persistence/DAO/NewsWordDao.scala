package persistence.DAO

import persistence.DbClasses.{DbConnectionFactory, NewsWord}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, SQLException}


case class NewsWordDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("NewsWordDao Logger")

  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO news_word(word) VALUES(?);"
  )
  private val preparedFindId = getConnection.prepareStatement(
    "SELECT * FROM news_word WHERE word = ?;"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def saveIfNotExists(newsWord: NewsWord): Unit = if(findId(newsWord).isEmpty) save(newsWord)


  def save(newsWord: NewsWord): Unit = {
    try {
      preparedSave.setString(1, newsWord.word)
      preparedSave.execute
    } catch {
      case e: SQLException => logger.error(s"Error trying to add word: ${newsWord.word} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add word: ${newsWord.word} ${e.getCause}")
    }
  }


  def findId(newsWord: NewsWord): Option[Int] = {
    try {
      preparedFindId.setString(1, newsWord.word)
      val resultSet = preparedFindId.executeQuery
      if(resultSet.next()) {
        return Some(resultSet.getInt("id"))
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to find word: ${newsWord.word} - ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to find word: ${newsWord.word} - ${e.getCause}")
    }
    None
  }


  def closePrepared(): Unit = {
    preparedSave.close()
    preparedFindId.close()
  }
}
