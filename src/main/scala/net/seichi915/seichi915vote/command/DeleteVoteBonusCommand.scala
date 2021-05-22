package net.seichi915.seichi915vote.command

import cats.effect.IO
import net.seichi915.seichi915vote.configuration.Configuration
import net.seichi915.seichi915vote.database.Database
import net.seichi915.seichi915vote.util.Implicits._
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}

import java.util
import java.util.Collections
import java.util.stream.Collectors
import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

class DeleteVoteBonusCommand extends CommandExecutor with TabExecutor {
  override def onCommand(
      sender: CommandSender,
      command: Command,
      label: String,
      args: Array[String]
  ): Boolean = {
    if (!Configuration.isGUIEnabled) {
      sender.sendMessage("このサーバーではこのコマンドを使用できません。".toErrorMessage)
      return true
    }
    if (args.length != 1) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    val name = args.head
    val task = IO {
      if (Database.getNames.contains(name)) {
        Database.deleteVoteBonus(name)
        Database.cleanUpDatabase()
        sender.sendMessage("削除しました。".toSuccessMessage)
      } else
        sender.sendMessage("不明な投票ボーナスです。".toErrorMessage)
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
  ): util.List[String] = {
    val completions: util.List[String] =
      args.length match {
        case 1 =>
          Database.getNames.asJava
            .stream()
            .filter(value => value.startsWith(args(0)))
            .collect(Collectors.toList[String])
        case _ => Collections.emptyList()
      }
    Collections.sort(completions)
    completions
  }
}
