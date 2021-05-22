package net.seichi915.seichi915vote.command

import net.seichi915.seichi915vote.configuration.Configuration
import net.seichi915.seichi915vote.menu.VoteBonusMenu
import net.seichi915.seichi915vote.util.Implicits._
import org.bukkit.command.{Command, CommandExecutor, CommandSender, TabExecutor}
import org.bukkit.entity.Player

import java.util
import java.util.Collections

class VoteBonusCommand extends CommandExecutor with TabExecutor {
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
    if (args.nonEmpty) {
      sender.sendMessage("コマンドの使用法が間違っています。".toErrorMessage)
      return true
    }
    VoteBonusMenu.open(sender.asInstanceOf[Player])
    true
  }

  override def onTabComplete(
      sender: CommandSender,
      command: Command,
      alias: String,
      args: Array[String]
  ): util.List[String] = Collections.emptyList()
}
