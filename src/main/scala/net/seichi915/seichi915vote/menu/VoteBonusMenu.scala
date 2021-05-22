package net.seichi915.seichi915vote.menu

import cats.effect.IO
import net.seichi915.seichi915vote.database.Database
import net.seichi915.seichi915vote.inventory.Seichi915VoteInventoryHolder
import net.seichi915.seichi915vote.meta.menu.Menu
import net.seichi915.seichi915vote.util.Implicits._
import org.bukkit.{Bukkit, ChatColor, Material}
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

import scala.concurrent.ExecutionContext
import scala.jdk.CollectionConverters._

object VoteBonusMenu extends Menu {
  override def open(player: Player): Unit = {
    val inventory = Bukkit.createInventory(
      Seichi915VoteInventoryHolder.seichi915VoteInventoryHolder,
      36,
      "seichi915Vote"
    )
    val task = IO {
      Database.getVoteBonus.sortBy(_.getName).foreach { voteBonusData =>
        val itemStack = voteBonusData.getItemStack.clone()
        itemStack.setAmount(voteBonusData.getCount)
        val itemMeta = itemStack.getItemMeta
        val newLore = List(
          "",
          s"${ChatColor.GREEN}所持数: ${ChatColor.WHITE}${Database.getCount(player.getUniqueId, voteBonusData).getOrElse(0)}個",
          "",
          s"${ChatColor.WHITE}左クリック: 64個取り出す",
          s"${ChatColor.WHITE}右クリック: 1個取り出す"
        )
        if (itemMeta.hasLore)
          itemMeta.setLore(itemMeta.getLore.asScala.concat(newLore).asJava)
        else
          itemMeta.setLore(newLore.asJava)
        itemStack.setItemMeta(itemMeta)
        itemStack.setClickAction { player =>
          val count =
            Database.getCount(player.getUniqueId, voteBonusData).getOrElse(0)
          if (!(count <= 0))
            if (count >= 64) {
              val bonusItem = voteBonusData.getItemStack.clone()
              bonusItem.setAmount(64)
              player.giveItemSafety(bonusItem)
              Database.setVoteBonusCount(
                player.getUniqueId,
                voteBonusData.getName,
                count - 64
              )
            } else {
              val bonusItem = voteBonusData.getItemStack.clone()
              bonusItem.setAmount(count)
              player.giveItemSafety(bonusItem)
              Database.setVoteBonusCount(
                player.getUniqueId,
                voteBonusData.getName,
                0
              )
            }
          VoteBonusMenu.open(player)
        }
        itemStack.setRightClickAction { player =>
          val count =
            Database.getCount(player.getUniqueId, voteBonusData).getOrElse(0)
          if (!(count <= 0)) {
            val bonusItem = voteBonusData.getItemStack.clone()
            player.giveItemSafety(bonusItem)
            Database.setVoteBonusCount(
              player.getUniqueId,
              voteBonusData.getName,
              count - 1
            )
          }
          VoteBonusMenu.open(player)
        }
        inventory.addItem(itemStack)
      }
    }
    val contextShift = IO.contextShift(ExecutionContext.global)
    IO.shift(contextShift).flatMap(_ => task).unsafeRunAsyncAndForget()
    val closeButton = new ItemStack(Material.BARRIER)
    val closeButtonMeta = closeButton.getItemMeta
    closeButtonMeta.setDisplayName(s"${ChatColor.RED}閉じる")
    closeButton.setItemMeta(closeButtonMeta)
    closeButton.setClickAction(_.closeInventory())
    inventory.setItem(35, closeButton)
    player.openInventory(inventory)
  }
}
