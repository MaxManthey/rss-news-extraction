package persistence.DAO

import persistence.DbClasses.{AggregatedDate, AggregatedWordFrequency, DbConnectionFactory}
import java.sql.{Connection, SQLException}


case class PreAggregateArticleDao(dbConnectionFactory: DbConnectionFactory) {
  private val aggregatedDateDao = AggregatedDateDao(dbConnectionFactory)
  private val aggregatedWordFrequencyDao = AggregatedWordFrequencyDao(dbConnectionFactory)

  private val preparedDistinctDates = getConnection.prepareStatement(
    "SELECT DISTINCT(date) FROM SOURCE_DATE ORDER BY DATE;"
  )
  private val preparedDistinctWords = getConnection.prepareStatement(
    "SELECT * FROM NEWS_WORD;"
  )
  private val preparedAggregatedWordFrequency = getConnection.prepareStatement(
    "SELECT NW.word, SUM(WF.frequency) as \"frequency\", SD.DATE FROM WORD_FREQUENCY WF JOIN NEWS_WORD NW on WF.NEWS_WORD_ID = NW.ID JOIN SOURCE_DATE SD on WF.SOURCE_DATE_ID = SD.ID WHERE NW.word = ? GROUP BY SD.DATE, NW.word ORDER BY DATE;"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def preAggregateSources(): Unit = {
    val distinctDates = preparedDistinctDates.executeQuery
    while(distinctDates.next()) {
      aggregatedDateDao.save(AggregatedDate(distinctDates.getDate("date")))
    }

    val distinctWords = preparedDistinctWords.executeQuery
    while(distinctWords.next()) {
      val id = distinctWords.getInt("id")
      val word = distinctWords.getString("word")

      preparedAggregatedWordFrequency.setString(1, word)
      val wordFrequencyPerDay = preparedAggregatedWordFrequency.executeQuery
      while(wordFrequencyPerDay.next()) {
        val dateId = aggregatedDateDao.findId(AggregatedDate(wordFrequencyPerDay.getDate("date"))) match {
          case Some(dateId) => dateId
          case None => -1
        }
        val aggregatedWordFrequency = AggregatedWordFrequency(
          wordFrequencyPerDay.getInt("frequency"),
          id,
          dateId
        )
        aggregatedWordFrequencyDao.saveIfNotExists(aggregatedWordFrequency)
      }
    }
  }


  def closePrepared(): Unit = {
    aggregatedDateDao.closePrepared()
    aggregatedWordFrequencyDao.closePrepared()
    preparedDistinctDates.close()
    preparedDistinctWords.close()
    preparedAggregatedWordFrequency.close()
  }
}
