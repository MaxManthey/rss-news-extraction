package Extraction

import DbClasses.Article
import Extraction.JsonNewsProtocol.jsonNewsFormat
import com.typesafe.scalalogging.Logger
import de.l3s.boilerpipe.extractors.CanolaExtractor
import spray.json._
import java.io.File
import java.time.LocalDateTime


case class ArticleExtractor(dirName: String) extends Iterable[Article] {
  private val logger: Logger = Logger("ArticleExtractor Logger")

  private val filterWords = scala.io.Source.fromFile("src/main/resources/FilterWords.txt")
  private val lines = try filterWords.mkString.split("\n").map(line => line.split(", ")) finally filterWords.close()
  private val (stoppwortList, specialCharList) = (lines(0), lines(1).map(el => el.charAt(0)))


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
      .map(el => removeSpecialCharacters(el))
      .filter(el => el.length > 1 && !stoppwortList.contains(el.toLowerCase))
      .map(el => el.toLowerCase())


  def removeSpecialCharacters(text: String) = {
    val from = if(text.length > 1 && isSpecialCharacter(text.charAt(0))) {
      val indicesOfNonSpecialChars = for(i <- 0 until text.length; if !isSpecialCharacter(text(i))) yield i
      if(indicesOfNonSpecialChars.isEmpty || indicesOfNonSpecialChars(0) == text.length) 1
      else indicesOfNonSpecialChars(0)
    } else 0

    val to = if(text.length > 1 && isSpecialCharacter(text.charAt(text.length - 1))) {
      val indicesOfNonSpecialChars = for(i <- text.length-1 to 0 by -1; if !isSpecialCharacter(text(i))) yield i
      if(indicesOfNonSpecialChars.isEmpty || indicesOfNonSpecialChars(0) == 0) 1
      else indicesOfNonSpecialChars(0)+1
    } else text.length

    text.substring(from, to)
  }


  private def isSpecialCharacter(c: Char) = specialCharList.contains(c)


  private def wordsByFrequency(article: Array[String]): Map[String, Int] =
    article.groupBy(identity).transform((k, v) => v.length)
}
