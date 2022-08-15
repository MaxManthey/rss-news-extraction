import DAO.ArticleDao
import DbClasses.{Article, DbConnectionFactory}
import Extraction.ArticleExtractor
import com.typesafe.scalalogging.Logger


object RssNewsPersistence {
  private val logger: Logger = Logger("RssNewsPersistence Logger")

  def main(args: Array[String]): Unit = {
    if(args.length != 2) {
      logger.error("Amount of args incorrect. For more details, please refer to the readme")
      sys.exit(1)
    }

    val connectionFactory = DbConnectionFactory(args(0))
    val articleDao = ArticleDao(connectionFactory)

    ArticleExtractor(args(1)).foreach {
      article: Article => articleDao.save(article)
    }

    articleDao.closePrepared()
    connectionFactory.close()
  }
}
