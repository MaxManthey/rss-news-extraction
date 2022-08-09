import DAOs.ArticleDao
import DbClasses.{Article, DbConnectionFactory}
import Extraction.ArticleExtractor


object RssNewsPersistence {
  def main(args: Array[String]): Unit = {
    //TODO remove db
    val connectionFactory = DbConnectionFactory.getInstance
    val articleDao = ArticleDao(connectionFactory)

    ArticleExtractor("../test-news-file/").foreach {
      article: Article => articleDao.save(article)
    }

    articleDao.closePrepared()
    connectionFactory.close()
  }
}
