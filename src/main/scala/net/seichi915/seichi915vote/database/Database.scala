package net.seichi915.seichi915vote.database

import net.seichi915.seichi915vote.Seichi915Vote
import net.seichi915.seichi915vote.data.votebonus.VoteBonusData
import net.seichi915.seichi915vote.util.Util
import scalikejdbc._

import java.io.{File, FileOutputStream}
import java.util.UUID

object Database {
  Class.forName("org.sqlite.JDBC")

  private val dbName =
    Seichi915Vote.instance.getDescription.getName.toLowerCase

  ConnectionPool.add(
    dbName,
    s"jdbc:sqlite:${Seichi915Vote.instance.getDataFolder.getAbsolutePath}/database.db",
    "",
    ""
  )

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = false
  )

  def saveDefaultDatabase: Boolean =
    try {
      if (!Seichi915Vote.instance.getDataFolder.exists())
        Seichi915Vote.instance.getDataFolder.mkdir()
      val databaseFile =
        new File(Seichi915Vote.instance.getDataFolder, "database.db")
      if (!databaseFile.exists()) {
        val inputStream =
          Seichi915Vote.instance.getResource("database.db")
        val outputStream = new FileOutputStream(databaseFile)
        val bytes = new Array[Byte](1024)
        var read = 0
        while ({
          read = inputStream.read(bytes)
          read
        } != -1) outputStream.write(bytes, 0, read)
        inputStream.close()
        outputStream.close()
      }
      true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        false
    }

  def cleanUpDatabase(): Unit = {
    val bonusNames = NamedDB(dbName) localTx { implicit session =>
      sql"SELECT name FROM bonus"
        .map(_.string("name"))
        .list()
        .apply()
    }
    var namesToDelete = List[String]()
    bonusNames.foreach { bonusName =>
      if (
        NamedDB(dbName) localTx { implicit session =>
          sql"SELECT data FROM bonus_data WHERE name = $bonusName"
            .map(_.string("data"))
            .list()
            .apply()
            .isEmpty
        }
      )
        namesToDelete = namesToDelete.appended(bonusName)
    }
    namesToDelete.foreach { nameToDelete =>
      NamedDB(dbName) localTx { implicit session =>
        sql"DELETE FROM bonus WHERE name = $nameToDelete"
          .update()
          .apply()
      }
    }
  }

  def getVoteBonus: List[VoteBonusData] =
    NamedDB(dbName) localTx { implicit session =>
      sql"SELECT * FROM bonus_data"
        .map { resultSet =>
          VoteBonusData(
            resultSet.string("name"),
            Util.decodeItem(resultSet.string("data")),
            resultSet.int("count")
          )
        }
        .list()
        .apply()
    }

  def getCount(uuid: UUID, voteBonusData: VoteBonusData): Option[Int] =
    NamedDB(dbName) localTx { implicit session =>
      sql"SELECT count FROM bonus WHERE uuid = $uuid AND name = ${voteBonusData.getName}"
        .map(_.int("count"))
        .list()
        .apply()
        .headOption
    }

  def insertVoteBonusCount(
      uuid: UUID,
      voteBonusData: VoteBonusData
  ): Unit =
    NamedDB(dbName) localTx { implicit session =>
      sql"INSERT INTO bonus (uuid, name, count) VALUES ($uuid, ${voteBonusData.getName}, ${voteBonusData.getCount})"
        .update()
        .apply()
    }

  def setVoteBonusCount(uuid: UUID, name: String, count: Int): Unit =
    NamedDB(dbName) localTx { implicit session =>
      sql"UPDATE bonus SET count = $count WHERE uuid = $uuid AND name = $name"
        .update()
        .apply()
    }

  def getNames: List[String] =
    NamedDB(dbName) localTx { implicit session =>
      sql"SELECT name FROM bonus_data"
        .map(_.string("name"))
        .list()
        .apply()
    }

  def addVoteBonus(voteBonusData: VoteBonusData): Unit =
    NamedDB(dbName) localTx { implicit session =>
      sql"INSERT INTO bonus_data (name, data, count) VALUES (${voteBonusData.getName}, ${Util
        .encodeItem(voteBonusData.getItemStack)}, ${voteBonusData.getCount})"
        .update()
        .apply()
    }

  def setVoteBonusCount(name: String, count: Int): Unit =
    NamedDB(dbName) localTx { implicit session =>
      sql"UPDATE bonus_data SET count = $count WHERE name = $name"
        .update()
        .apply()
    }

  def deleteVoteBonus(name: String): Unit =
    NamedDB(dbName) localTx { implicit session =>
      sql"DELETE FROM bonus_data WHERE name = $name"
        .update()
        .apply()
    }
}
