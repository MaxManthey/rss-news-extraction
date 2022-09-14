package persistence.DbClasses

import java.time.LocalDate


case class Article(source: String, date: LocalDate, wordsMap: Map[String, Int])
