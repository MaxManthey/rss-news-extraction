package persistence.DAO

import com.typesafe.scalalogging.Logger
import persistence.DbClasses.DbConnectionFactory
import java.sql.{Connection, SQLException}


case class PreAggregateArticleDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("ArticleExtractor Logger")

  private val preparedInsertAllWords = getConnection.prepareStatement(
    """INSERT INTO aggregated_word_frequency(news_word_id, frequency, date) (
      |SELECT NW.id as news_word_id, SUM(WF.frequency) as frequency, SD.date
      |FROM WORD_FREQUENCY WF
      |JOIN NEWS_WORD NW on WF.NEWS_WORD_ID = NW.ID
      |JOIN SOURCE_DATE SD on WF.SOURCE_DATE_ID = SD.ID
      |GROUP BY SD.date, news_word_id ORDER BY DATE);""".stripMargin
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def preAggregateArticle(): Unit = {
    try {
      preparedInsertAllWords.execute
    } catch {
      case e: SQLException => logger.error(s"Error trying to add preAggregateArticle ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add preAggregateArticle ${e.getCause}")
    }
  }


  def closePrepared(): Unit = {
    preparedInsertAllWords.close()
  }
}
