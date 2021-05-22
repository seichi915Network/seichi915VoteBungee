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

class SetVoteBonusAmountCommand extends CommandExecutor with TabExecutor {
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
    if (args.length != 2) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    val name = args.head
    val count = args(1).toIntOption match {
      case Some(value) =>
        if (value > 64) {
          sender.sendMessage("個数は64個以下で指定してください。".toErrorMessage)
          return true
        }
        if (value <= 0) {
          sender.sendMessage("個数は1個以上で指定してください。".toErrorMessage)
          return true
        }
        value
      case None =>
        sender.sendMessage("個数は数字で指定してください。".toErrorMessage)
        return true
    }
    val task = IO {
      if (Database.getNames.contains(name)) {
        Database.setVoteBonusCount(name, count)
        sender.sendMessage("個数を変更しました。".toSuccessMessage)
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
