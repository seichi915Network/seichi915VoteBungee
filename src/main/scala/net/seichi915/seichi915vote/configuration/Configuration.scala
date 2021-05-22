package net.seichi915.seichi915vote.configuration

import net.seichi915.seichi915vote.Seichi915Vote

import scala.jdk.CollectionConverters._

object Configuration {
  def saveDefaultConfig(): Unit = Seichi915Vote.instance.saveDefaultConfig()

  def getCommandsToRun: List[String] =
    Seichi915Vote.instance.getConfig
      .getStringList("CommandsToRun")
      .asScala
      .toList

  def isGUIEnabled: Boolean =
    Seichi915Vote.instance.getConfig.getBoolean("EnableGUI")
}
