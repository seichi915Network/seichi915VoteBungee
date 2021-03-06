package net.seichi915.seichi915vote.logger

import net.seichi915.seichi915vote.Seichi915Vote
import net.seichi915.seichi915vote.data.vote.VoteData

import java.io.{File, FileWriter}
import scala.util.chaining._

object VoteHistoryLogger {
  private val logFile =
    new File(Seichi915Vote.instance.getDataFolder, "vote-history.log")

  def initialize(): Unit =
    if (!logFile.exists()) {
      logFile.createNewFile()
      new FileWriter(logFile)
        .tap(_.write("========== Vote history log ==========\n"))
        .tap(_.close())
    }

  def log(voteData: VoteData): Unit =
    new FileWriter(logFile, true)
      .tap(
        _.write(
          s"[${voteData.getTimeStamp}] Address: ${voteData.getAddress}, Service: ${voteData.getServiceName}, Username: ${voteData.getUsername}, TimeStamp: ${voteData.getTimeStamp}\n"
        )
      )
      .tap(_.close())

  def purge(): Unit =
    new FileWriter(logFile)
      .tap(_.write("========== Vote history log ==========\n"))
      .tap(_.close())
}
