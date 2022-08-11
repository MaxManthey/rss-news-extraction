import DAO.ArticleDao
import DbClasses.{Article, DbConnectionFactory}
import Extraction.ArticleExtractor


object RssNewsPersistence {
  def main(args: Array[String]): Unit = {
    val connectionFactory = DbConnectionFactory(args(0))
    val articleDao = ArticleDao(connectionFactory)

    ArticleExtractor(args(1)).foreach {
      article: Article => articleDao.save(article)
    }

    articleDao.closePrepared()
    connectionFactory.close()
  }
}
