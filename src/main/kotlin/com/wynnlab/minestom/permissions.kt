package com.wynnlab.minestom

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.condition.CommandCondition

const val PERM_SERVER_STOP = "server.stop"
const val PERM_SERVER_SAVE_ALL = "server.save-all"
const val PERM_SERVER_KICK = "server.kick"
const val PERM_SERVER_KILL_OTHERS = "server.kill"
const val PERM_SERVER_VANISH = "server.vanish"
const val PERM_SERVER_GAMEMODE = "server.gamemode"
const val PERM_SERVER_GIVE = "server.give"
const val PERM_SERVER_SETBLOCK = "server.setblock"
const val PERM_SERVER_EFFECT = "server.effect"
const val PERM_SERVER_EXECUTE = "server.execute"
const val PERM_SERVER_PERMISSIONS = "server.permissions"

fun CommandSender.isAllowed(permission: String, level: Int = 3) = isConsole || isPlayer && asPlayer().permissionLevel >= level || hasPermission(permission)

fun isAllowed(permission: String, level: Int = 3) = CommandCondition { sender, _ -> sender.isAllowed(permission, level) }