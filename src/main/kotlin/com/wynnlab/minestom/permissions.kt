package com.wynnlab.minestom

import com.wynnlab.minestom.labs.Lab
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.condition.CommandCondition
import net.minestom.server.entity.Player

const val PERM_SERVER_STOP = "server.stop"
const val PERM_SERVER_SAVE_ALL = "server.save-all"
const val PERM_SERVER_KICK = "server.kick"
const val PERM_SERVER_KILL_OTHERS = "server.kill"
const val PERM_SERVER_VANISH = "server.vanish"
const val PERM_SERVER_GAMEMODE = "server.gamemode"
const val PERM_SERVER_GIVE = "server.give"
const val PERM_SERVER_SETBLOCK = "server.setblock"
const val PERM_SERVER_TP = "server.teleport"
const val PERM_SERVER_EFFECT = "server.effect"
const val PERM_SERVER_EXECUTE = "server.execute"
const val PERM_SERVER_PERMISSIONS = "server.permissions"

fun CommandSender.isAllowed(permission: String, level: Int = 3) = hasLevel(level)
        || hasPermission(permission)

fun CommandSender.hasLevel(level: Int) = isConsole
        || this is Player && (permissionLevel >= level || instance is Lab && (instance as Lab).getPermissions(this) >= level)

fun isAllowed(permission: String, level: Int = 3) = CommandCondition { sender, _ -> sender.isAllowed(permission, level) }

fun hasLevel(level: Int) = CommandCondition { sender, _ -> sender.hasLevel(level) }

val atLeast1 = hasLevel(1)

val playerAtLeast1 = CommandCondition { sender, _ -> sender.isPlayer && sender.hasLevel(1) }