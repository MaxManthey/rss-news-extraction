package persistence.Extraction

import org.scalatest.PrivateMethodTester
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import java.io.File


class ArticleExtractorTest extends AnyFunSuite with Matchers with PrivateMethodTester {
  private val resourcesPath = "src/test/resources"
  private val mockNewsArticlePath = resourcesPath + "/MockNewsArticle.json"
  private val mockTestJsonFilePath = resourcesPath + "/TestJsonFile.json"
  private val sampleNewsArticlePath = resourcesPath + "/SampleNewsArticle.json"

  private val articleExtractor = ArticleExtractor(resourcesPath)
  private val getAllFileNamesFromDir = PrivateMethod[Array[File]](Symbol("getAllFileNamesFromDir"))
  private val getNewsObject = PrivateMethod[Option[News]](Symbol("getNewsObject"))
  private val stripHtml = PrivateMethod[Array[String]](Symbol("stripHtml"))
  private val wordsByFrequency = PrivateMethod[Map[String, Int]](Symbol("wordsByFrequency"))



  test("getAllFileNamesFromDir returns correct files") {
    val expectedResult = Array(new File(mockNewsArticlePath),
      new File(sampleNewsArticlePath),
      new File(mockTestJsonFilePath))

    val extractedJsonFiles = articleExtractor invokePrivate getAllFileNamesFromDir()

    assert(extractedJsonFiles sameElements expectedResult)
  }


  test("getNewsObject returns correct News Object") {
    val expectedResult = News("<html><head></head><body><p>test</p></body></html>",
      "http://info.cern.ch",
      "2022-07-20T19:18:33.339024"
    )

    val newsObj = articleExtractor invokePrivate getNewsObject(new File(mockNewsArticlePath))

    assert(newsObj.value == expectedResult)
  }


  test("getNewsObject with wrong layout returns None") {
    val newsObj = articleExtractor invokePrivate getNewsObject(new File(mockTestJsonFilePath))
    assert(newsObj.isEmpty)
  }


  test("stripHtml returns correct result") {
    val expectedResult = Array("liebt", "dj", "simone", "thomalla", "neuer", "flirt", "einfach", "herz", "sprechen",
      "tv-star", "simone", "thomalla", "57", "frühling", "seit", "guten", "jahr", "single", "september", "hatten",
      "handballer", "silvio", "heinevetter", "37", "trennung", "zwölf", "jahren", "beziehung", "verkündet", "thomalla",
      "januar", "bams", "zeit", "mann", "einlasse", "zeit", "gekommen", "gesehen", "bild-anfrage", "simone", "sagt",
      "erfahren", "bildplus")

    val newsObj = articleExtractor invokePrivate getNewsObject(new File(sampleNewsArticlePath))
    val article = newsObj.value.article

    val strippedHtml = articleExtractor invokePrivate stripHtml(article)

    assert(strippedHtml sameElements expectedResult)
  }


  test("wordsByFrequency returns correct result") {
    val expectedResult = Map("hallo" -> 2, "moin" -> 2, "servus" -> 1)

    val input = Array("hallo", "moin", "servus", "hallo", "moin")
    val wordsMap = articleExtractor invokePrivate wordsByFrequency(input)
    wordsMap should equal (expectedResult)
  }


  test("wordsByFrequency should work with different types of inputs") {
    val expectedResult = Map("👍" -> 2, "moin" -> 2, "§¢@" -> 1)

    val input = Array("👍", "moin", "👍", "moin", "§¢@")
    val wordsMap = articleExtractor invokePrivate wordsByFrequency(input)
    wordsMap should equal (expectedResult)
  }


  test("wordsByFrequency should work with empty input") {
    val expectedResult = Map()

    val input: Array[String] = Array()
    val wordsMap = articleExtractor invokePrivate wordsByFrequency(input)
    wordsMap should equal (expectedResult)
  }
}
