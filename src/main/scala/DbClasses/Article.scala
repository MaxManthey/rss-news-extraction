package DbClasses

import java.time.LocalDate


case class Article(source: String, date: LocalDate, words: Map[String, Int])
