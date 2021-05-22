package net.seichi915.seichi915vote.util

import net.seichi915.seichi915vote.Seichi915Vote
import net.seichi915.seichi915vote.inventory.Seichi915VoteInventoryHolder
import net.seichi915.seichi915vote.meta.menu.{ClickAction, RightClickAction}
import org.bukkit.entity.Player
import org.bukkit.{ChatColor, Material, NamespacedKey}
import org.bukkit.inventory.{Inventory, ItemStack}
import org.bukkit.persistence.PersistentDataType

import java.util.UUID
import scala.jdk.CollectionConverters._

object Implicits {
  implicit class AnyOps(any: Any) {
    def isNull: Boolean = Option(any).flatMap(_ => Some(false)).getOrElse(true)

    def nonNull: Boolean = !isNull
  }

  implicit class StringOps(string: String) {
    def toNormalMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.WHITE}seichi915Vote${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toSuccessMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.GREEN}seichi915Vote${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toWarningMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.GOLD}seichi915Vote${ChatColor.AQUA}]${ChatColor.RESET} $string"

    def toErrorMessage: String =
      s"${ChatColor.AQUA}[${ChatColor.RED}seichi915Vote${ChatColor.AQUA}]${ChatColor.RESET} $string"
  }

  implicit class InventoryOps(inventory: Inventory) {
    def isSeichi915VoteInventory: Boolean =
      inventory.getHolder.nonNull && inventory.getHolder
        .isInstanceOf[Seichi915VoteInventoryHolder]
  }

  implicit class PlayerOps(player: Player) {
    def giveItemSafety(itemStacks: ItemStack*): Unit =
      player.getInventory
        .addItem(itemStacks: _*)
        .values()
        .asScala
        .filter(_.getType != Material.AIR)
        .foreach(player.getWorld.dropItem(player.getLocation, _))
  }

  implicit class ItemStackOps(itemStack: ItemStack) {
    def setClickAction(clickAction: ClickAction): Unit = {
      val itemMeta = itemStack.getItemMeta
      val uuid = UUID.randomUUID()
      itemMeta.getPersistentDataContainer.set(
        new NamespacedKey(Seichi915Vote.instance, "click_action"),
        PersistentDataType.STRING,
        uuid.toString
      )
      itemStack.setItemMeta(itemMeta)
      Seichi915Vote.clickActionMap += uuid -> clickAction
    }

    def setRightClickAction(rightClickAction: RightClickAction): Unit = {
      val itemMeta = itemStack.getItemMeta
      val uuid = UUID.randomUUID()
      itemMeta.getPersistentDataContainer.set(
        new NamespacedKey(Seichi915Vote.instance, "right_click_action"),
        PersistentDataType.STRING,
        uuid.toString
      )
      itemStack.setItemMeta(itemMeta)
      Seichi915Vote.rightClickActionMap += uuid -> rightClickAction
    }
  }
}
