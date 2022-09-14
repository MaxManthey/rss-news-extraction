package persistence.DbClasses

import java.time.LocalDate


case class SourceDate(date: LocalDate, source: String, hashedSource: String)
