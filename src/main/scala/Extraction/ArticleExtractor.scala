package Extraction

import DbClasses.Article
import Extraction.JsonNewsProtocol.jsonNewsFormat
import com.typesafe.scalalogging.Logger
import de.l3s.boilerpipe.extractors.CanolaExtractor
import spray.json._
import java.io.File
import java.time.LocalDateTime
import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}


case class ArticleExtractor(dirName: String) extends Iterable[Article] {
  private val logger: Logger = Logger("ArticleExtractor Logger")

  private val filterWords = scala.io.Source.fromFile("src/main/resources/FilterWords.txt")
  private val lines = try filterWords.mkString.split("\n").map(line => line.split(", ")) finally filterWords.close()
  private val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))
  private val articleIterator = ArrayBuffer[Article]()


  override def iterator: Iterator[Article] = {
    for(fileName <- getAllFileNamesFromDir) {
      breakable {
        val newsObjOption = getNewsObject(fileName)
        val newsObj = if(newsObjOption.isDefined) newsObjOption.get else {
          logger.error("JSON file could not be parsed")
          break
        }

        val article = stripHtml(newsObj.article)
        if(article.isEmpty) {
          logger.error(s"Article in ${newsObj.source} is empty")
          break
        }

        val wordsMap = wordsByFrequency(article)
        if(wordsMap.isEmpty) {
          logger.error(s"Error trying to create wordsMap from article: ${newsObj.article}")
          break
        }

        articleIterator.addOne(Article(newsObj.source, LocalDateTime.parse(newsObj.dateTime).toLocalDate, wordsMap))
      }
    }
    articleIterator.iterator
  }


  private def getAllFileNamesFromDir: Array[File] =
    new File(dirName).listFiles.filter(_.isFile).filter(_.getName.endsWith(".json"))


  private def getNewsObject(fileName: File): Option[News] = {
    val source = scala.io.Source.fromFile(fileName)
    try {
      val sourceString = source.mkString
      Some(sourceString.parseJson.convertTo[News])
    } catch {
      case e: Exception =>
        logger.error("Extracting file: " + fileName + " has failed" + e.getCause)
        None
    } finally source.close()
  }


  private def stripHtml(htmlArticle: String): Array[String] =
    CanolaExtractor.INSTANCE.getText(htmlArticle)
      .split('\n').mkString("", " ", "").split(" ")
      .map(el => {
        var newEl = el
        while(newEl.nonEmpty && miscList.contains(newEl(newEl.length-1))) newEl = newEl.dropRight(1)
        newEl
      })
      .map(el => {
        var newEl = el
        while(newEl.nonEmpty && miscList.contains(newEl.head)) newEl = newEl.drop(1)
        newEl
      })
      .filter(el => el.length > 1 && !stoppwortList.contains(el.toLowerCase))
      .map(el => el.toLowerCase())


  private def wordsByFrequency(article: Array[String]): Map[String, Int] =
    article.groupBy(identity).transform((k, v) => v.size)
}
