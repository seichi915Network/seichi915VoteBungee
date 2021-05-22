package net.seichi915.seichi915vote.meta.menu

import org.bukkit.entity.Player

trait RightClickAction {
  def onClick(player: Player): Unit
}
