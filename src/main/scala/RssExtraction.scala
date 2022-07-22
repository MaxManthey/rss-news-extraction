import JsonNewsProtocol.jsonNewsFormat
import com.typesafe.scalalogging.Logger
import java.io.File
import spray.json._
import de.l3s.boilerpipe.extractors._

object RssExtraction {

  private val logger: Logger = Logger("RssDownloader Logger")
  private val newsFilesFolderPath = "../news-files/"


  def main(args: Array[String]): Unit = {
    //TODO add logger
    for(fileName <- getAllFileNamesFromDir) {
      val newsObj = getNewsObject(fileName)
      val article = stripHtml(newsObj.article)
      val wordsMap = wordsByAmount(article)
      //persist words with amount, source and date to DB
      //TODO Date from string to LocalDateTime
    }
  }

  def getAllFileNamesFromDir: Array[File] =
    new File(newsFilesFolderPath).listFiles.filter(_.isFile).filter(_.getName.endsWith(".json"))


  def getNewsObject(fileName: File): News = {
    val source = scala.io.Source.fromFile(fileName)
    val lines = try source.mkString finally source.close()
    lines.parseJson.convertTo[News]
  }


  def stripHtml(htlmlArticle: String): Array[String] =
    CanolaExtractor.INSTANCE.getText(htlmlArticle)
      .split('\n').mkString("", " ", "").split(" ")
      .map(el => {
        if(el.nonEmpty && Stoppwort.miscList.contains(el(el.length-1))) el.dropRight(1)
        else el
      })
      .map(el => if(el.nonEmpty && Stoppwort.miscList.contains(el.head)) el.drop(1) else el)
      .filter(el => el.nonEmpty && !Stoppwort.stoppwortList.contains(el.toLowerCase))


  def wordsByAmount(article: Array[String]): Map[String, Int] = {
    println("\n\n" + article.length + " " + article.mkString("(",", ",")"))
    val wordsMap = article.groupBy(identity).transform((k, v) => v.size)
    println(wordsMap.size + " " + wordsMap)
    wordsMap
  }
}


object Stoppwort {
  val stoppwortList = List("ich", "du", "er", "sie", "es", "wir", "ihr", "sie", "ab", "aber", "alle", "allem", "allen", "aller", "allerdings", "als", "also", "am", "an", "andere", "anderem", "anderen", "anderer", "andernfalls", "anders", "andersherum", "anfangs", "anhand", "anschließend", "ansonsten", "anstatt", "auch", "auf", "aufgrund", "aus", "außerdem", "befindet", "bei", "beide", "beim", "beispielsweise", "bereits", "besonders", "besteht", "bestimmte", "bestimmten", "bestimmter", "bevor", "bietet", "bis", "bleiben", "bringen", "bringt", "bsp", "bzw", "d.h", "da", "dabei", "dafür", "daher", "damit", "danach", "dann", "dar", "daran", "darauf", "daraus", "darf", "darstellt", "darüber", "das", "dass", "davon", "dazu", "dem", "demzufolge", "den", "denen", "denn", "der", "deren", "des", "dessen", "desto", "die", "dies", "diese", "diesem", "diesen", "dieser", "dieses", "doch", "dort", "durch", "ebenfalls", "eher", "eigenen", "eigentlich", "ein", "eine", "einem", "einen", "einer", "eines", "einigen", "einiges", "einmal", "einzelnen", "entscheidend", "entweder", "er", "erstmals", "es", "etc", "etwas", "euch", "folgende", "folgendem", "folgenden", "folgender", "folgendes", "folgt", "für", "ganz", "gegen", "gehen", "gemacht", "genannte", "genannten", "gerade", "gerne", "gibt", "gilt", "gleich", "gleichen", "gleichzeitig", "habe", "haben", "hält", "hat", "hatte", "hätte", "hauptsächlich", "her", "heutigen", "hier", "hierbei", "hierfür", "hin", "hingegen", "hinzu", "hoch", "ihn", "ihr", "ihre", "ihren", "ihrer", "im", "immer", "immerhin", "in", "indem", "insgesamt", "ist", "ja", "je", "jede", "jedem", "jeder", "jedes", "jedoch", "jetzt", "jeweilige", "jeweiligen", "jeweils", "kam", "kann", "keine", "kommen", "kommt", "können", "konnte", "könnte", "konnten", "lassen", "lässt", "lautet", "lediglich", "leider", "letztendlich", "letztere", "letzteres", "liegt", "machen", "macht", "mal", "man", "mehr", "mehrere", "meine", "meinem", "meisten", "mich", "mit", "mithilfe", "mittels", "möchte", "möglich", "möglichst", "momentan", "muss", "müssen", "musste", "nach", "nachdem", "nächsten", "nahezu", "nämlich", "natürlich", "neue", "neuen", "nicht", "nichts", "noch", "nun", "nur", "ob", "obwohl", "oder", "oftmals", "ohne", "per", "sämtliche", "scheint", "schon", "sehr", "sein", "seine", "seinem", "seinen", "sich", "sicherlich", "sie", "siehe", "sind", "so", "sobald", "sofern", "solche", "solchen", "soll", "sollen", "sollte", "sollten", "somit", "sondern", "sorgt", "sowie", "sowohl", "später", "sprich", "statt", "trotz", "über", "überhaupt", "um", "und", "uns", "unter", "usw", "viel", "viele", "vielen", "völlig", "vom", "von", "vor", "vorerst", "vorher", "während", "war", "wäre", "waren", "warum", "was", "weil", "weitere", "weiteren", "weiterer", "weiteres", "weiterhin", "welche", "welchen", "welcher", "welches", "wenn", "wer", "werden", "wesentlich", "wichtige", "wichtigsten", "wie", "wieder", "wiederum", "will", "wir", "wird", "wirklich", "wo", "wobei", "worden", "wurde", "wurden", "z.b", "z.B", "ziemlich", "zu", "zuerst", "zum", "zur", "zusätzlich", "zuvor", "zwar", "zwecks")
  val miscList = List('.', '!', '?', ',', ':', '(', ')', '\"', ';', '-', '/', '„', '“')
}