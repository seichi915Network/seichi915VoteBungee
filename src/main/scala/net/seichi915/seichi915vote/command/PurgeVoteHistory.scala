package net.seichi915.seichi915vote.command

import cats.effect.IO
import net.seichi915.seichi915vote.logger.VoteHistoryLogger
import net.seichi915.seichi915vote.util.Implicits._
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}

import java.util
import java.util.Collections
import scala.concurrent.ExecutionContext

class PurgeVoteHistory extends CommandExecutor with TabExecutor {
  override def onCommand(
      sender: CommandSender,
      command: Command,
      label: String,
      args: Array[String]
  ): Boolean = {
    if (args.nonEmpty) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    sender.sendMessage("投票履歴を削除しています...".toNormalMessage)
    val task = IO {
      VoteHistoryLogger.purge()
      sender.sendMessage("投票履歴を削除しました。".toSuccessMessage)
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
    true
  }

  override def onTabComplete(
      sender: CommandSender,
      command: Command,
      alias: String,
      args: Array[String]
  ): util.List[String] = Collections.emptyList()
}
