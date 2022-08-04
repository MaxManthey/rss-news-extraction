import DAOs.{NewsDateDao, NewsSourceDao, WordFrequencyDao}
import DbClasses.{NewsDate, NewsSource, WordFrequency}
import Extraction.{ArticleExtraction, News}
import com.typesafe.scalalogging.Logger
import java.time.LocalDateTime


class PersistenceHandler private() {
  private val logger: Logger = Logger("PersistenceHandler Logger")
  private val articleExtraction = new ArticleExtraction


  def persistExistingFiles(stoppwortList: Array[String],
                           miscList: Array[Char],
                           dateDao: NewsDateDao,
                           sourceDao: NewsSourceDao,
                           wordFrequencyDao: WordFrequencyDao): Unit = {
    for(fileName <- articleExtraction.getAllFileNamesFromDir) {
      var newsObj: News = null

      val article = articleExtraction.getNewsObject(fileName) match {
        case Some(value) =>
          newsObj = value
          Some(articleExtraction.stripHtml(value.article, stoppwortList, miscList))
        case None => None
      }

      val dateId = getDateIdIfNotExistsSave(NewsDate(LocalDateTime.parse(newsObj.dateTime).toLocalDate), dateDao)
      val sourceId = getSourceIdIfNotExistsSave(NewsSource(newsObj.source, dateId), sourceDao)

      val wordsMap = article match {
        case Some(value) => Some(articleExtraction.wordsByFrequency(value))
        case None => None
      }

      wordsMap match {
        case Some(value) =>
          wordFrequencyDao.saveAll(value.toArray.map(wf => WordFrequency(wf._1, wf._2, sourceId)))
          logger.info("Successfully added article " + newsObj.source + " to DB")
        case None => logger.error("Failed to add article " + newsObj.source + " to DB")
      }
    }
  }


  def getDateIdIfNotExistsSave(newsDate: NewsDate, dateDao: NewsDateDao): Int = {
    val existingDate = dateDao.findId(newsDate.date)
    if(existingDate > 0) existingDate
    else {
      dateDao.save(newsDate)
      dateDao.findId(newsDate.date)
    }
  }


  def getSourceIdIfNotExistsSave(newsSource: NewsSource, sourceDao: NewsSourceDao): Int = {
    val existingSource = sourceDao.findId(newsSource.source)
    if(existingSource > 0) existingSource
    else {
      sourceDao.save(newsSource)
      sourceDao.findId(newsSource.source)
    }
  }
}


object PersistenceHandler {
  private var persistenceHandler: PersistenceHandler = _

  def getInstance(): PersistenceHandler = {
    if (persistenceHandler == null) persistenceHandler = new PersistenceHandler()
    persistenceHandler
  }
}