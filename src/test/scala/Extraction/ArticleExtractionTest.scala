//TODO rewrite tests
//TODO rename class to extractor

//package Extraction
//
//import org.scalatest.OptionValues.convertOptionToValuable
//import org.scalatest.funsuite.AnyFunSuite
//import org.scalatest.matchers.should.Matchers
//import java.io.File
//
//
//class ArticleExtractionTest extends AnyFunSuite with Matchers {
//  private val articleExtraction = new ArticleExtraction
//
//  private val resourcesPath = "src/test/resources"
//  private val filterWordsPath = resourcesPath + "/FilterWords.txt"
//  private val mockNewsArticlePath = resourcesPath + "/MockNewsArticle.json"
//  private val mockTestJsonFilePath = resourcesPath + "/TestJsonFile.json"
//  private val sampleNewsArticlePath = resourcesPath + "/SampleNewsArticle.json"
//
//
//  test("getAllFileNamesFromDir returns correct files") {
//    val expectedResult = Array(new File(mockNewsArticlePath),
//      new File(sampleNewsArticlePath),
//      new File(mockTestJsonFilePath))
//
//    val privateNewsFilesFolderPath = articleExtraction.getClass.getDeclaredField("newsFilesFolderPath")
//    privateNewsFilesFolderPath.setAccessible(true)
//    privateNewsFilesFolderPath.set(articleExtraction, resourcesPath)
//    val extractedJsonFiles = articleExtraction.getAllFileNamesFromDir
//
//    assert(extractedJsonFiles sameElements expectedResult)
//  }
//
//
//  test("getNewsObject returns correct News Object") {
//    val expectedResult = News("<html><head></head><body><p>test</p></body></html>",
//      "http://info.cern.ch",
//      "2022-07-20T19:18:33.339024"
//    )
//
//    val newsObj = articleExtraction.getNewsObject(new File(mockNewsArticlePath))
//
//    assert(newsObj.value == expectedResult)
//  }
//
//
//  test("getNewsObject with wrong layout returns None") {
//    val newsObj = articleExtraction.getNewsObject(new File(mockTestJsonFilePath))
//    assert(newsObj.isEmpty)
//  }
//
//
//  test("stripHtml returns correct result") {
//    val expectedResult = Array("liebt", "dj?", "simone", "thomalla", "neuer", "flirt", "einfach", "herz", "sprechen", "tv-star",
//      "simone", "thomalla", "57", "frühling", "seit", "guten", "jahr", "single", "september", "hatten", "handballer",
//      "silvio", "heinevetter", "37", "trennung", "zwölf", "jahren", "beziehung", "verkündet", "thomalla", "januar",
//      "bams", "zeit", "mann", "einlasse", "zeit", "gekommen", "gesehen", "bild-anfrage", "simone", "sagt",
//      "erfahren", "bildplus")
//
//    val filterWords = scala.io.Source.fromFile(filterWordsPath)
//    val lines = try filterWords.mkString.split("\n").map(line => line.split(", ")) finally filterWords.close()
//    val (stoppwortList, miscList) = (lines(0), lines(1).map(el => el.charAt(0)))
//
//    val article = articleExtraction.getNewsObject(new File(sampleNewsArticlePath)).value.article
//
//
//    val strippedHtml = articleExtraction.stripHtml(article, stoppwortList, miscList)
//
//    assert(strippedHtml sameElements expectedResult)
//  }
//
//
//  test("wordsByFrequency returns correct result") {
//    val expectedResult = Map("hallo" -> 2, "moin" -> 2, "servus" -> 1)
//
//    val input = Array("hallo", "moin", "servus", "hallo", "moin")
//    val wordsMap = articleExtraction.wordsByFrequency(input)
//    wordsMap should equal (expectedResult)
//  }
//
//
//  test("wordsByFrequency should work with different types of inputs") {
//    val expectedResult = Map("👍" -> 2, "moin" -> 2, "§¢@" -> 1)
//
//    val input = Array("👍", "moin", "👍", "moin", "§¢@")
//    val wordsMap = articleExtraction.wordsByFrequency(input)
//    wordsMap should equal (expectedResult)
//  }
//
//
//  test("wordsByFrequency should work with empty input") {
//    val expectedResult = Map()
//
//    val input: Array[String] = Array()
//    val wordsMap = articleExtraction.wordsByFrequency(input)
//    wordsMap should equal (expectedResult)
//  }
//}
