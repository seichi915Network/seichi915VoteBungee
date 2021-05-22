package net.seichi915.seichi915vote.listener

import cats.effect.IO
import net.seichi915.seichi915vote.Seichi915Vote
import net.seichi915.seichi915vote.configuration.Configuration
import net.seichi915.seichi915vote.database.Database
import net.seichi915.seichi915vote.event.VoteEvent
import net.seichi915.seichi915vote.logger.VoteHistoryLogger
import org.bukkit.Bukkit
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.ExecutionContext

class VoteListener extends Listener {
  @EventHandler
  def onVote(event: VoteEvent): Unit = {
    val task = IO {
      VoteHistoryLogger.log(event.getVoteData)
      if (
        Bukkit.getOfflinePlayers
          .map(_.getName)
          .toSet
          .contains(event.getVoteData.getUsername)
      ) Database.getVoteBonus.foreach { voteBonus =>
        val offlinePlayer = Bukkit.getOfflinePlayers
          .map(offlinePlayer => offlinePlayer.getName -> offlinePlayer)
          .toMap
          .apply(event.getVoteData.getUsername)
        Database.getCount(offlinePlayer.getUniqueId, voteBonus) match {
          case Some(value) =>
            Database.setVoteBonusCount(
              offlinePlayer.getUniqueId,
              voteBonus.getName,
              value + voteBonus.getCount
            )
          case None =>
            Database.insertVoteBonusCount(
              offlinePlayer.getUniqueId,
              voteBonus
            )
        }
      }
      else
        Seichi915Vote.instance.getLogger
          .warning(s"${event.getVoteData.getUsername} さんのUUIDを取得できませんでした。")
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
    Configuration.getCommandsToRun.foreach { command =>
      Bukkit.getServer.dispatchCommand(
        Bukkit.getServer.getConsoleSender,
        command
          .replaceAll("TIME", event.getVoteData.getTimeStamp)
          .replaceAll("NAME", event.getVoteData.getUsername)
          .replaceAll("SERVICE", event.getVoteData.getServiceName)
          .replaceAll("ADDRESS", event.getVoteData.getAddress)
      )
    }
  }
}
