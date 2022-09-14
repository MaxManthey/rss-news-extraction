package persistence

import com.typesafe.scalalogging.Logger
import persistence.DAO.ArticleDao
import persistence.DbClasses.{Article, DbConnectionFactory}
import persistence.Extraction.ArticleExtractor

object RssNewsPersistence {
  private val logger: Logger = Logger("RssNewsPersistence Logger")

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      println("Amount of args incorrect. For more details, please refer to the readme.")
      logger.error("Amount of args incorrect. For more details, please refer to the readme")
      sys.exit(1)
    }

    val connectionFactory = DbConnectionFactory(args(0))
    val articleDao = ArticleDao(connectionFactory)

    ArticleExtractor(args(1)).foreach {
      article: Article => articleDao.save(article)
    }

    articleDao.preAggregateSources()

    articleDao.closePrepared()
    connectionFactory.close()
  }
}
