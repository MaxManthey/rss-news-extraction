package DAOs

import DbClasses.{Article, DbConnectionFactory}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, SQLException}


case class ArticleDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("ArticleDao Logger")
  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO x(x) VALUES(?);"
  ) //TODO adjust insert


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def save(article: Article): Unit = { //TODO adjust save
    try {
      preparedSave.setString(1, article.source)
      preparedSave.execute
    } catch {
      case e: SQLException => logger.error("Error trying to save article: " + e.getCause)
      case e: Exception => logger.error("Error trying to save article: " + e.getCause)
    }
  }


  def closePrepared(): Unit = preparedSave.close()
}
