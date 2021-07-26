package com.wynnlab.minestom

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.condition.CommandCondition

const val PERM_SERVER_STOP = "server.stop"
const val PERM_SERVER_PERMISSIONS = "server.permissions"

fun CommandSender.isAllowed(permission: String) = isConsole || isPlayer && asPlayer().permissionLevel >= 4 || hasPermission(permission)

fun isAllowed(permission: String) = CommandCondition { sender, _ -> sender.isAllowed(permission) }