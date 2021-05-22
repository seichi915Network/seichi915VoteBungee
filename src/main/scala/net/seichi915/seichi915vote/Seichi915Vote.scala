package net.seichi915.seichi915vote

import net.seichi915.seichi915vote.command._
import net.seichi915.seichi915vote.configuration.Configuration
import net.seichi915.seichi915vote.database.Database
import net.seichi915.seichi915vote.listener._
import net.seichi915.seichi915vote.logger.VoteHistoryLogger
import net.seichi915.seichi915vote.meta.menu.{ClickAction, RightClickAction}
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin

import java.util.UUID
import scala.collection.mutable

object Seichi915Vote {
  var instance: Seichi915Vote = _

  var clickActionMap: mutable.HashMap[UUID, ClickAction] = mutable.HashMap()
  var rightClickActionMap: mutable.HashMap[UUID, RightClickAction] =
    mutable.HashMap()
}

class Seichi915Vote extends JavaPlugin {
  Seichi915Vote.instance = this

  override def onEnable(): Unit = {
    Configuration.saveDefaultConfig()
    if (!Database.saveDefaultDatabase) {
      getLogger.severe("デフォルトのデータベースファイルのコピーに失敗しました。サーバーを停止します。")
      Bukkit.shutdown()
      return
    }
    Database.cleanUpDatabase()
    VoteHistoryLogger.initialize()
    Seq(
      new InventoryClickListener,
      new VoteListener,
      new VotifierListener
    ).foreach(Bukkit.getPluginManager.registerEvents(_, this))
    Map(
      "addvotebonus" -> new AddVoteBonusCommand,
      "deletevotebonus" -> new DeleteVoteBonusCommand,
      "purgevotehistory" -> new PurgeVoteHistory,
      "setvotebonusamount" -> new SetVoteBonusAmountCommand,
      "votebonus" -> new VoteBonusCommand
    ).foreach {
      case (commandName: String, commandExecutor: CommandExecutor) =>
        Bukkit.getPluginCommand(commandName).setExecutor(commandExecutor)
        Bukkit.getPluginCommand(commandName).setTabCompleter(commandExecutor)
    }

    getLogger.info("seichi915Voteが有効になりました。")
  }

  override def onDisable(): Unit = {
    getLogger.info("seichi915Voteが無効になりました。")
  }
}
