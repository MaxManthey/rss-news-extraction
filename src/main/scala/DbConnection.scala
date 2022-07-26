import java.sql.{Connection, DriverManager, SQLInput}
import java.time.LocalDateTime


case class DbConnection(word: String, occurrenceAmount: Int, source: String, dateTime: LocalDateTime){

  private val driver = "com.mysql.cj.jdbc.Driver"
  private val url = "jdbc:mysql://localhost/rss_news_words"
  private val username = "root"
  private val password = ""

  def addWordToDb(): Unit = {
    var connection:Connection = null

    try {
      Class.forName(driver)
      connection = DriverManager.getConnection(url, username, password)
      val statement = connection.createStatement()
      statement.execute(s"INSERT INTO word_occurrence(word, occurrence_amount, source, date_time) VALUES('$word', $occurrenceAmount, '$source', '$dateTime');")
    } catch {
      case e: Throwable => e.printStackTrace()
    }

    connection.close()
  }
}
