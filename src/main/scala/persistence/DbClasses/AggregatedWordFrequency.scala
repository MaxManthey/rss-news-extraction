package persistence.DbClasses

import java.sql.Date

case class AggregatedWordFrequency(frequency: Int, newsWordId: Int, date: Date)
