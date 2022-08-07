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
  private val arti = ArrayBuffer[Article]()


  override def iterator: Iterator[Article] = {
    for(fileName <- getAllFileNamesFromDir) {
      var newsObj: News = null //todo somehow to val

      val article = getNewsObject(fileName) match {
        case Some(value) =>
          newsObj = value
          Some(stripHtml(value.article, stoppwortList, miscList))
        case None => None
      }


      val wordsMap = article match {
        case Some(value) => Some(wordsByFrequency(value))
        case None => None
      }

      wordsMap match {
        case Some(value) =>
          arti.addOne(Article(newsObj.source, LocalDateTime.parse(newsObj.dateTime).toLocalDate, value))
        case None => logger.error("Failed to add article " + newsObj.source + " to DB")
      }
    }

    arti.iterator
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


  private def stripHtml(htmlArticle: String, stoppwortList: Array[String], miscList: Array[Char]): Array[String] =
    CanolaExtractor.INSTANCE.getText(htmlArticle)
      .split('\n').mkString("", " ", "").split(" ")
      .map(el => {
        if(el.nonEmpty && miscList.contains(el(el.length-1))) el.dropRight(1)
        else el
      })
      .map(el => if(el.nonEmpty && miscList.contains(el.head)) el.drop(1) else el)
      .filter(el => el.length > 1 && !stoppwortList.contains(el.toLowerCase))
      .map(el => el.toLowerCase())


  private def wordsByFrequency(article: Array[String]): Map[String, Int] =
    article.groupBy(identity).transform((k, v) => v.size)
}
