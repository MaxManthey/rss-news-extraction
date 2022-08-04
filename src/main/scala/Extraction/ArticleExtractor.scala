//package Extraction
//
//import DbClasses.{Article, NewsDate, NewsSource, WordFrequency}
//import Extraction.JsonNewsProtocol.jsonNewsFormat
//import com.typesafe.scalalogging.Logger
//import de.l3s.boilerpipe.extractors.CanolaExtractor
//import spray.json._
//
//import java.io.File
//import java.time.LocalDateTime
//import scala.collection.mutable.ArrayBuffer
//
//
//case class ArticleExtractor(dirName: String) extends Iterable[Article] {
//  private val logger: Logger = Logger("ArticleExtractor Logger")
////  private val moin = LocalDateTime.parse("2022-07-20T19:18:35.073119").toLocalDate
////  private val arti = List(Article("a", moin, Map("a"->2)), Article("b", moin, Map("b"->2)))
//
////  getAllFileNamesFromDir
//  /*
//      Idee
//      Liste mit allen Dateinamen
//      iterator -> liste erzeugen
//      Siehe artikel https://medium.com/omarelgabrys-blog/iterators-98ab5541ce6a#id_token=eyJhbGciOiJSUzI1NiIsImtpZCI6IjE1NDllMGFlZjU3NGQxYzdiZGQxMzZjMjAyYjhkMjkwNTgwYjE2NWMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJuYmYiOjE2NTk2Mzc1NTIsImF1ZCI6IjIxNjI5NjAzNTgzNC1rMWs2cWUwNjBzMnRwMmEyamFtNGxqZGNtczAwc3R0Zy5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjEwNDI2OTA2NDc1NTUzMzEyNDg0MiIsImVtYWlsIjoibWFudGhleW1heDIzQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiIyMTYyOTYwMzU4MzQtazFrNnFlMDYwczJ0cDJhMmphbTRsamRjbXMwMHN0dGcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJuYW1lIjoiTWF4aW1pbGlhbiBNYW50aGV5IiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FJdGJ2bWtlNG4wbTFvZVVHUGFpZjlyRDFZZGY5TGhxZjhUSHlpVURFNmFnPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Ik1heGltaWxpYW4iLCJmYW1pbHlfbmFtZSI6Ik1hbnRoZXkiLCJpYXQiOjE2NTk2Mzc4NTIsImV4cCI6MTY1OTY0MTQ1MiwianRpIjoiNWJlYWFmM2E0YTU0ZDJiYzUxM2E1MDI2NDVhMTU3Y2MxZDYyZDIzNSJ9.bhJp2X4XhMAQOVEl1eIa2mU7nTcOuKNMZ5tgBdDzhu3JIN8BJ3X4dUunoq56AmvizZj41n7ymkWHtxG_2IXtLN7Y5mBmyuywkE_jitwmrq46M6T7JPUsyohp91kgpU0S9jkD-Nmpv08z2XfOWcs5nrRyr7QAxjgd0JWDizRCQXABYQFPcmXNTIy9o1hozHbv6uJnTdBAtRVBlqFOAsLVZET7Za586iJZx2r-wJX6IBtPMNiqPw7CRpe0_oHFf_VwKZZzWhrTeMj_vzA5mD9KudjipQw3QlsR1XMBV0noYCzQdNvTRWlqigvIYhSWOB_a0jiMrpyqmSejMGdKSR_uUA
//      next -> Liste aktuellen eintrag poppen und zurückgeben
//      has Next = anzahl files in liste
//       */
//
//
//  override def iterator: Iterator[Article] = {
//    val arti = ArrayBuffer[Article]()
//    arti.addOne(Article("newsObj.source", LocalDateTime.parse("2022-07-20T19:18:35.073119").toLocalDate, Map("b"->2)))
//    arti.addOne(Article("newsObj.source", LocalDateTime.parse("2022-07-20T19:18:35.073119").toLocalDate, Map("b"->23)))
////    val test: LazyList[Article] = LazyList()
//    val iter: Iterator[Article] = Iterator()
//
//    val filterWords = scala.io.Source.fromFile("src/main/resources/FilterWords.txt")
//    val lines = try filterWords.mkString.split("\n").map(line => line.split(", ")) finally filterWords.close()
//    val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))
//
////    val fileName = getAllFileNamesFromDir(0)
////    println(fileName)
//    for(fileName <- getAllFileNamesFromDir) {
//      //TODO stream
//      var newsObj: News = null
//
//      val article = getNewsObject(fileName) match {
//        case Some(value) =>
//          newsObj = value
//          Some(stripHtml(value.article, stoppwortList, miscList))
//        case None => None
//      }
//
//      if(newsObj.source.length > longestS) longestS = newsObj.source.length
//      println(longestS)
//
//      val wordsMap = article match {
//        case Some(value) => Some(wordsByFrequency(value))
//        case None => None
//      }
//
//      wordsMap match {
//        case Some(value) =>
//          val article = Article(newsObj.source, LocalDateTime.parse(newsObj.dateTime).toLocalDate, value)
//
//
////          arti.addOne(Article(newsObj.source, LocalDateTime.parse(newsObj.dateTime).toLocalDate, value))
//        case None => logger.error("Failed to add article " + newsObj.source + " to DB")
//      }
//    }
//
////    arti.iterator
//  }
//
//
//  def getAllFileNamesFromDir: Array[File] =
//    new File(dirName).listFiles.filter(_.isFile).filter(_.getName.endsWith(".json"))
//
//
//  def getNewsObject(fileName: File): Option[News] = {
//    val source = scala.io.Source.fromFile(fileName)
//    try {
//      val sourceString = source.mkString
//      Some(sourceString.parseJson.convertTo[News])
//    } catch {
//      case e: Exception =>
//        logger.error("Extracting file: " + fileName + " has failed" + e.getCause)
//        None
//    } finally source.close()
//  }
//
//
//  def stripHtml(htmlArticle: String, stoppwortList: Array[String], miscList: Array[Char]): Array[String] =
//    CanolaExtractor.INSTANCE.getText(htmlArticle)
//      .split('\n').mkString("", " ", "").split(" ")
//      .map(el => {
//        if(el.nonEmpty && miscList.contains(el(el.length-1))) el.dropRight(1)
//        else el
//      })
//      .map(el => if(el.nonEmpty && miscList.contains(el.head)) el.drop(1) else el)
//      .filter(el => el.length > 1 && !stoppwortList.contains(el.toLowerCase))
//      .map(el => el.toLowerCase())
//
//
//  def wordsByFrequency(article: Array[String]): Map[String, Int] =
//    article.groupBy(identity).transform((k, v) => v.size)
//}
//
//
////class Article(source: String, date: LocalDate, words: Map[String, Int]) {...}
////ArticleExtraction(dirname).foreach(a => dao.save(a))
