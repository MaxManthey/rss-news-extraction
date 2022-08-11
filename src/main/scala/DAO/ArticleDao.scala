package DAO

import DbClasses.{Article, DbConnectionFactory, NewsWord, SourceDate, WordFrequency}
import com.typesafe.scalalogging.Logger
import java.security.MessageDigest
import java.sql.SQLException


case class ArticleDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("ArticleDao Logger")

  private val sourceDateDao = SourceDateDao(dbConnectionFactory)
  private val newsWordDao = NewsWordDao(dbConnectionFactory)
  private val wordFrequencyDao = WordFrequencyDao(dbConnectionFactory)


  def save(article: Article): Unit = {
    try {
      val sourceDate = SourceDate(article.date, article.source,
        MessageDigest.getInstance("MD5").digest(article.source.getBytes).map("%02x".format(_)).mkString)
      sourceDateDao.saveIfNotExists(sourceDate)
      val sourceDateId = sourceDateDao.findId(sourceDate)

      for(word <- article.wordsMap.keys) {
        val newsWord = NewsWord(word)
        newsWordDao.saveIfNotExists(newsWord)
        val newsWordId = newsWordDao.findId(newsWord)
        wordFrequencyDao.saveIfNotExists(WordFrequency(article.wordsMap(word), newsWordId, sourceDateId))
      }

      logger.info(s"Successfully saved article: ${article.source}")
    } catch {
      case e: SQLException => logger.error(s"Error trying to save article: ${article.toString}" + e.getCause)
      case e: Exception => logger.error(s"Error trying to save article: ${article.toString}" + e.getCause)
    }
  }

  def closePrepared(): Unit = {
    sourceDateDao.closePrepared()
    newsWordDao.closePrepared()
    wordFrequencyDao.closePrepared()
  }
}
