package DAOs

import DbClasses.{DbConnectionFactory, SourceDate}
import com.typesafe.scalalogging.Logger
import java.sql.{Connection, Date, SQLException}


case class SourceDateDao(dbConnectionFactory: DbConnectionFactory) {
  private val logger: Logger = Logger("SourceDateDao Logger")

  private val preparedSave = getConnection.prepareStatement(
    "INSERT INTO source_date(source, hashed_source, date) VALUES(?, ?, ?);"
  )
  private val preparedFindId = getConnection.prepareStatement(
    "SELECT * FROM source_date WHERE source = ? AND hashed_source = ? AND date = ?;"
  )


  @throws[SQLException]
  private def getConnection: Connection = dbConnectionFactory.getConnection


  def saveIfNotExists(sourceDate: SourceDate): Unit = if(findId(sourceDate) == -1) save(sourceDate)


  def save(sourceDate: SourceDate): Unit = {
    try {
      preparedSave.setString(1, sourceDate.source)
      preparedSave.setString(2, sourceDate.hashedSource)
      preparedSave.setDate(3, Date.valueOf(sourceDate.date))
      preparedSave.execute
    } catch {
      case e: SQLException => logger.error(s"Error trying to save sourceDate: ${sourceDate.toString} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to save sourceDate: ${sourceDate.toString} ${e.getCause}")
    }
  }


  def findId(sourceDate: SourceDate): Int = {
    try {
      preparedFindId.setString(1, sourceDate.source)
      preparedFindId.setString(2, sourceDate.hashedSource)
      preparedFindId.setDate(3, Date.valueOf(sourceDate.date))
      val resultSet = preparedFindId.executeQuery
      while(resultSet.next()) {
        return resultSet.getInt("id")
      }
    } catch {
      case e: SQLException => logger.error(s"Error trying to find sourceDate: ${sourceDate.toString} ${e.getCause}")
      case e: Exception => logger.error(s"Error trying to find sourceDate: ${sourceDate.toString} ${e.getCause}")
    }
    -1
  }


  def closePrepared(): Unit = {
    preparedSave.close()
    preparedFindId.close()
  }
}
