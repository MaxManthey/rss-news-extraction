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

    val timer = System.nanoTime

    articleDao.preAggregateSources()

    val duration = (System.nanoTime - timer) / 1e9d
    println(duration + " Sec")

    articleDao.closePrepared()
    connectionFactory.close()
  }
}
