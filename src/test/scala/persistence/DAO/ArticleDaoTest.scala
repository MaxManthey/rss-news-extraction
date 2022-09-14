package persistence.DAO

import persistence.DbClasses.{Article, DbConnectionFactory, NewsWord, SourceDate, WordFrequency}
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import java.time.LocalDate


class ArticleDaoTest extends AnyFunSuite with BeforeAndAfter {
  private val dbConnectionPath = "./src/test/db"
  private var dbConnectionFactory: DbConnectionFactory = _
  private var articleDao: ArticleDao = _


  before {
    dbConnectionFactory = DbConnectionFactory(dbConnectionPath)
    articleDao = ArticleDao(dbConnectionFactory)
    articleDao.save(Article("abc.de", LocalDate.of(2021, 2, 8), Map("test" -> 3)))
  }
  after {
    articleDao.closePrepared()
    dbConnectionFactory.close()
  }


  test("NewsWord exists") {
    assert(NewsWordDao(dbConnectionFactory).findId(NewsWord("test")).isDefined)
  }


  test("SourceDate exists") {
    assert(SourceDateDao(dbConnectionFactory).findId(
      SourceDate(LocalDate.of(2021, 2, 8), "abc.de", "a3ccb51e1b5b2dc6a67774dc5c9d27b0"))
      .isDefined)
  }


  test("WordFrequency exists") {
    assert(WordFrequencyDao(dbConnectionFactory).findId(WordFrequency(3, 1, 1)).isDefined)
  }
}
