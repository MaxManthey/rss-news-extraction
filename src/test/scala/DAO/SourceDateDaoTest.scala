package DAO

import DbClasses.{DbConnectionFactory, SourceDate}
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import org.scalatest.funsuite.AnyFunSuite
import java.sql.Connection
import java.time.LocalDate


class SourceDateDaoTest extends AnyFunSuite with BeforeAndAfter with PrivateMethodTester {
  private val dbConnectionPath = "./src/test/db"
  private var dbConnectionFactory: DbConnectionFactory = _
  private var sourceDateDao: SourceDateDao = _
  private val getConnection = PrivateMethod[Connection](Symbol("getConnection"))


  before{
    dbConnectionFactory = DbConnectionFactory(dbConnectionPath)
    sourceDateDao = SourceDateDao(dbConnectionFactory)
  }
  after {
    sourceDateDao.closePrepared()
    dbConnectionFactory.close()
  }


  test("connection is working") {
    assert((sourceDateDao invokePrivate getConnection()).toString == "conn0: url=jdbc:h2:./src/test/db/rss_news_articles user=SA")
  }


  test("nonexistent column returns -1") {
    assert(sourceDateDao
      .findId(SourceDate(LocalDate.of(2022, 1, 8), "nonexistent", "123"))
      .isEmpty)
  }


  test("saving and finding is working") {
    val sourceDate = SourceDate(LocalDate.of(2021, 2, 8), "saving", "321")
    sourceDateDao.save(sourceDate)
    assert(sourceDateDao.findId(sourceDate).isDefined)
  }


  test("saveIfNotExists is working"){
    val sourceDate = SourceDate(LocalDate.of(2019, 3, 8), "saveIfNotExists", "213")
    sourceDateDao.saveIfNotExists(sourceDate)
    sourceDateDao.saveIfNotExists(sourceDate)
    assert(sourceDateDao.findId(sourceDate).isDefined)
  }
}
