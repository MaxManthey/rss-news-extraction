package DbClasses

import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ArrayBuffer


class DbConnectionFactoryTest extends AnyFunSuite with BeforeAndAfter {
  val dbConnectionPath = "./src/test/db"
  var dbConnectionFactory: DbConnectionFactory = _


  before{
    dbConnectionFactory = DbConnectionFactory(dbConnectionPath)
  }
  after {
    dbConnectionFactory.close()
  }


  test("Db connection is working") {
    assert(dbConnectionFactory.getConnection.toString == "conn0: url=jdbc:h2:./src/test/db/rss_news_articles user=SA")
  }


  test("all tables exist") {
    val expectedResult = List("NEWS_WORD", "SOURCE_DATE", "WORD_FREQUENCY")
    val dbMetaData = dbConnectionFactory.getConnection.getMetaData
    val tables = dbMetaData.getTables(null, null, "%", null)
    val tableNames = ArrayBuffer[String]()

    while (tables.next) {
      val name = tables.getString(3)
      if(name == "NEWS_WORD" || name == "SOURCE_DATE" || name == "WORD_FREQUENCY") tableNames.addOne(name)
    }
    assert(expectedResult == tableNames)
  }
}
