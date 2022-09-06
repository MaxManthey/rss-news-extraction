package Extraction

import DbClasses.Article
import Extraction.JsonNewsProtocol.jsonNewsFormat
import com.typesafe.scalalogging.Logger
import de.l3s.boilerpipe.extractors.CanolaExtractor
import spray.json._
import java.io.File
import java.time.LocalDateTime
import scala.collection.mutable.ArrayBuffer


case class ArticleExtractor(dirName: String) extends Iterable[Article] {
  private val logger: Logger = Logger("ArticleExtractor Logger")

  private val filterWords = scala.io.Source.fromFile("src/main/resources/FilterWords.txt")
  private val lines = try filterWords.mkString.split("\n").map(line => line.split(", ")) finally filterWords.close()
  private val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))


  override def iterator: Iterator[Article] =
    getAllFileNamesFromDir.iterator.collect(file => createArticle(file) match {
      case Some(article) => article
    })


  private def createArticle(fileName: File): Option[Article] = {
    val newsObjOption = getNewsObject(fileName)
    val newsObj = if(newsObjOption.isDefined) newsObjOption.get else {
      logger.error("JSON file could not be parsed")
      return None
    }

    val article = stripHtml(newsObj.article)
    if(article.isEmpty) {
      logger.error(s"Article in ${newsObj.source} is empty")
      return None
    }

    val wordsMap = wordsByFrequency(article)
    if(wordsMap.isEmpty) {
      logger.error(s"Error trying to create wordsMap from article: ${newsObj.article}")
      return None
    }

    Some(Article(newsObj.source, LocalDateTime.parse(newsObj.dateTime).toLocalDate, wordsMap))
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
        if(el.nonEmpty) {
          val newEl = el.split("").map(x => x.charAt(0)).to(ArrayBuffer)
          while(newEl.nonEmpty && miscList.contains(newEl(newEl.length-1))) newEl.remove(newEl.length-1)
          newEl.mkString("")
        } else el
      })
      .map(el => {
        if(el.nonEmpty) {
          val newEl = el.split("").map(x => x.charAt(0)).to(ArrayBuffer)
          while(newEl.nonEmpty && miscList.contains(newEl.head)) newEl.remove(0)
          newEl.mkString("")
        } else el
      })
      .filter(el => el.length > 1 && !stoppwortList.contains(el.toLowerCase))
      .map(el => el.toLowerCase())


  private def wordsByFrequency(article: Array[String]): Map[String, Int] =
    article.groupBy(identity).transform((k, v) => v.length)
}
