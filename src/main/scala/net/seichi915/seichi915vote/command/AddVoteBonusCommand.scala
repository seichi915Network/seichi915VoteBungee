package net.seichi915.seichi915vote.command

import cats.effect.IO
import net.seichi915.seichi915vote.configuration.Configuration
import net.seichi915.seichi915vote.data.votebonus.VoteBonusData
import net.seichi915.seichi915vote.database.Database
import net.seichi915.seichi915vote.util.Implicits._
import org.bukkit.Material
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}
import org.bukkit.entity.Player

import java.util
import java.util.Collections
import scala.concurrent.ExecutionContext

class AddVoteBonusCommand extends CommandExecutor with TabExecutor {
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
    if (!sender.isInstanceOf[Player]) {
      sender.sendMessage("このコマンドはプレイヤーのみが実行できます。".toErrorMessage)
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
      if (!Database.getNames.contains(name)) {
        val player = sender.asInstanceOf[Player]
        val itemInMainHand = player.getInventory.getItemInMainHand
        if (itemInMainHand.isNull || itemInMainHand.getType == Material.AIR) {
          sender.sendMessage("投票ボーナスにしたいアイテムをメインハンドに持ってください".toErrorMessage)
          return true
        }
        val clonedItemInMainHand = itemInMainHand.clone()
        clonedItemInMainHand.setAmount(1)
        val voteBonusData = VoteBonusData(name, clonedItemInMainHand, count)
        Database.addVoteBonus(voteBonusData)
        sender.sendMessage("投票ボーナスを追加しました。".toSuccessMessage)
      } else
        sender.sendMessage("既に同じ名前の投票ボーナスが存在します。".toErrorMessage)
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
