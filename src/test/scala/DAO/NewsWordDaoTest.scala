package DAO

import DbClasses.{DbConnectionFactory, NewsWord}
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import org.scalatest.funsuite.AnyFunSuite
import java.sql.Connection


class NewsWordDaoTest extends AnyFunSuite with BeforeAndAfter with PrivateMethodTester {
  private val dbConnectionPath = "./src/test/db"
  private var dbConnectionFactory: DbConnectionFactory = _
  private var newsWordDao: NewsWordDao = _
  private val getConnection = PrivateMethod[Connection](Symbol("getConnection"))


  before{
    dbConnectionFactory = DbConnectionFactory(dbConnectionPath)
    newsWordDao = NewsWordDao(dbConnectionFactory)
  }
  after {
    newsWordDao.closePrepared()
    dbConnectionFactory.close()
  }


  test("connection is working") {
    assert((newsWordDao invokePrivate getConnection()).toString == "conn0: url=jdbc:h2:./src/test/db/rss_news_articles user=SA")
  }


  test("nonexistent column returns -1") {
    assert(newsWordDao.findId(NewsWord("nonexistent")).isEmpty)
  }


  test("saving and finding is working") {
    val newsWord = NewsWord("savingTest")
    newsWordDao.save(newsWord)
    assert(newsWordDao.findId(newsWord).isDefined)
  }


  test("saveIfNotExists is working"){
    val newsWord = NewsWord("saveIfNotExists")
    newsWordDao.saveIfNotExists(newsWord)
    newsWordDao.saveIfNotExists(newsWord)
    assert(newsWordDao.findId(newsWord).isDefined)
  }
}
