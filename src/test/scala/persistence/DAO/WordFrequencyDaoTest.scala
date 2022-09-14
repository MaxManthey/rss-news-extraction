package persistence.DAO

import persistence.DbClasses.{DbConnectionFactory, NewsWord, SourceDate, WordFrequency}
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import org.scalatest.funsuite.AnyFunSuite
import java.sql.Connection
import java.time.LocalDate


class WordFrequencyDaoTest extends AnyFunSuite with BeforeAndAfter with PrivateMethodTester {

  private val dbConnectionPath = "./src/test/db"
  private var dbConnectionFactory: DbConnectionFactory = _
  private var wordFrequencyDao: WordFrequencyDao = _
  private val getConnection = PrivateMethod[Connection](Symbol("getConnection"))


  before {
    dbConnectionFactory = DbConnectionFactory(dbConnectionPath)
    wordFrequencyDao = WordFrequencyDao(dbConnectionFactory)
    val newsWordDao = NewsWordDao(dbConnectionFactory)
    newsWordDao.save(NewsWord("moin"))
    newsWordDao.closePrepared()
    val sourceDateDao = SourceDateDao(dbConnectionFactory)
    sourceDateDao.save(SourceDate(LocalDate.of(2021, 2, 8), "a", "b"))
    sourceDateDao.closePrepared()
  }
  after {
    wordFrequencyDao.closePrepared()
    dbConnectionFactory.close()
  }


  test("connection is working") {
    assert((wordFrequencyDao invokePrivate getConnection()).toString == "conn0: url=jdbc:h2:./src/test/db/rss_news_articles user=SA")
  }


  test("nonexistent column returns -1") {
    assert(wordFrequencyDao.findId(WordFrequency(123, 23, 32)).isEmpty)
  }


  test("saving and finding is working") {
    val wordFrequency = WordFrequency(123, 1, 1)
    wordFrequencyDao.save(wordFrequency)
    assert(wordFrequencyDao.findId(wordFrequency).isDefined)
  }


  test("saveIfNotExists is working") {
    val wordFrequency = WordFrequency(123, 1, 1)
    wordFrequencyDao.saveIfNotExists(wordFrequency)
    wordFrequencyDao.saveIfNotExists(wordFrequency)
    assert(wordFrequencyDao.findId(wordFrequency).isDefined)
  }
}

