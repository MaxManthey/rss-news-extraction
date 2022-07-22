import JsonNewsProtocol.jsonNewsFormat
import com.typesafe.scalalogging.Logger
import java.io.File
import spray.json._
import de.l3s.boilerpipe.extractors._

object RssExtraction {

  private val logger: Logger = Logger("RssExtraction Logger")
  private val newsFilesFolderPath = "../news-files/"

  //TODO remove println
  def main(args: Array[String]): Unit = {
    val source = scala.io.Source.fromFile("FilterWords.txt")
    val lines = try source.mkString.split("\n").map(line => line.split(", ")) finally source.close()
    val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))
    println(miscList.mkString("(", " ", ")"))

    //TODO boilerpipe stuff auslagern
    for(fileName <- getAllFileNamesFromDir) {
      //TODO rework stripHtml file.
//      val htlmlArticle = getNewsObject(fileName)
//      htlmlArticle match {
//        case Some(value) =>
//          println(fileName)
//          println(CanolaExtractor.INSTANCE.getText(value.article).split('\n').mkString("", " ", "").split(" ").mkString("", " ", ""))
//      }

      val article = getNewsObject(fileName) match {
        case Some(value) => Some(stripHtml(value.article, stoppwortList, miscList))
        case None => None
      }

      val wordsMap = article match {
        case Some(value) => Some(wordsByAmount(value))
        case None => None
      }
      println("\n\n")
      //TODO persist words with amount, source and date to DB in lowercase
      //TODO Date from string to LocalDateTime
      //TODO add log for success
    }
  }

  def getAllFileNamesFromDir: Array[File] =
    new File(newsFilesFolderPath).listFiles.filter(_.isFile).filter(_.getName.endsWith(".json"))


  def getNewsObject(fileName: File): Option[News] = {
    val source = scala.io.Source.fromFile(fileName)
    try {
      val sourceString = source.mkString
      Some(sourceString.parseJson.convertTo[News])
    } catch {
      case e: Exception =>
        logger.error("Extracting file: " + fileName + " has failed")
        None
    } finally source.close()
  }


  def stripHtml(htmlArticle: String, stoppwortList: Array[String], miscList: Array[Char]): Array[String] =
    CanolaExtractor.INSTANCE.getText(htmlArticle)
      .split('\n').mkString("", " ", "").split(" ")
      .map(el => {
        if(el.nonEmpty && miscList.contains(el(el.length-1))) el.dropRight(1)
        else el
      })
      .map(el => if(el.nonEmpty && miscList.contains(el.head)) el.drop(1) else el)
      .filter(el => el.nonEmpty && !stoppwortList.contains(el.toLowerCase))


  def wordsByAmount(article: Array[String]): Map[String, Int] = {
    println(article.length + " " + article.mkString("(",", ",")"))
    val wordsMap = article.groupBy(identity).transform((k, v) => v.size)
    println(wordsMap.size + " " + wordsMap)
    wordsMap
  }
}
