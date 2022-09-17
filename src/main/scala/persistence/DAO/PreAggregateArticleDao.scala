package persistence.DAO

import com.typesafe.scalalogging.Logger
import persistence.DbClasses.{AggregatedWordFrequency, DbConnectionFactory}
import java.sql.{Connection, SQLException}


case class PreAggregateArticleDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("ArticleExtractor Logger")

  private val aggregatedWordFrequencyDao = AggregatedWordFrequencyDao(dbConnectionFactory)

  private val preparedDistinctWords = getConnection.prepareStatement(
    "SELECT * FROM NEWS_WORD;"
  )
  private val preparedAggregatedWordFrequency = getConnection.prepareStatement(
    """SELECT NW.word, SUM(WF.frequency) as frequency, SD.DATE
      |FROM WORD_FREQUENCY WF
      |JOIN NEWS_WORD NW on WF.NEWS_WORD_ID = NW.ID
      |JOIN SOURCE_DATE SD on WF.SOURCE_DATE_ID = SD.ID
      |WHERE NW.word = ? GROUP BY SD.DATE, NW.word ORDER BY DATE;""".stripMargin
  )
  private val preparedInsertAllWords = getConnection.prepareStatement(
    """INSERT INTO aggregated_word_frequency2(news_word_id, frequency, date) (
      |SELECT NW.id as news_word_id, SUM(WF.frequency) as frequency, SD.date
      |FROM WORD_FREQUENCY WF
      |JOIN NEWS_WORD NW on WF.NEWS_WORD_ID = NW.ID
      |JOIN SOURCE_DATE SD on WF.SOURCE_DATE_ID = SD.ID
      |GROUP BY SD.date, news_word_id ORDER BY DATE);""".stripMargin
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def preAggregateSources(): Unit = {
    val distinctWords = preparedDistinctWords.executeQuery
    while(distinctWords.next()) {
      val id = distinctWords.getInt("id")
      val word = distinctWords.getString("word")

      preparedAggregatedWordFrequency.setString(1, word)
      val wordFrequencyPerDay = preparedAggregatedWordFrequency.executeQuery
      while(wordFrequencyPerDay.next()) {
        val aggregatedWordFrequency = AggregatedWordFrequency(
          wordFrequencyPerDay.getInt("frequency"),
          id,
          wordFrequencyPerDay.getDate("date")
        )
        aggregatedWordFrequencyDao.saveIfNotExists(aggregatedWordFrequency)
      }
    }
  }

  def preAggregateArticle(): Unit = {
    try {
      preparedInsertAllWords.execute
    } catch {
      case e: SQLException => logger.error(s"Error trying to add preAggregateArticle ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to add preAggregateArticle ${e.getCause}")
    }
  }


  def closePrepared(): Unit = {
    aggregatedWordFrequencyDao.closePrepared()
    preparedDistinctWords.close()
    preparedAggregatedWordFrequency.close()
  }
}
