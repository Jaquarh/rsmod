package gg.rsmod.plugins.content

/**
 * Closing main modal for players.
 */
set_modal_close_logic {
    val modal = player.interfaces.getModal()
    if (modal != -1) {
        player.closeInterface(modal)
        player.interfaces.setModal(-1)
    }
}

/**
 * Check if the player has a menu opened.
 */
set_menu_open_check {
    player.getInterfaceAt(dest = InterfaceDestination.MAIN_SCREEN) != -1
}

/**
 * Execute when a player logs in.
 */
on_login {
    /**
     * Skill-related logic.
     */
    if (player.getSkills().getMaxLevel(Skills.HITPOINTS) < 10) {
        player.getSkills().setBaseLevel(Skills.HITPOINTS, 10)
    }
    player.calculateAndSetCombatLevel()
    player.sendWeaponComponentInformation()
    player.sendCombatLevelText()

    /**
     * Interface-related logic.
     */
    player.openOverlayInterface(player.interfaces.displayMode)
    InterfaceDestination.values.filter { pane -> pane.interfaceId != -1 }.forEach { pane ->
        if (pane == InterfaceDestination.XP_COUNTER && player.getVarbit(OSRSGameframe.XP_DROPS_VISIBLE_VARBIT) == 0) {
            return@forEach
        } else if (pane == InterfaceDestination.MINI_MAP && player.getVarbit(OSRSGameframe.HIDE_DATA_ORBS_VARBIT) == 1) {
            return@forEach
        }
        player.openInterface(pane.interfaceId, pane)
    }

    /**
     * Inform the client whether or not we have a display name.
     */
    val displayName = player.username.isNotEmpty()
    player.runClientScript(1105, if (displayName) 1 else 0) // Has display name
    player.runClientScript(423, player.username)
    if (player.getVarp(1055) == 0 && displayName) {
        player.syncVarp(1055)
    }
    player.setVarbit(8119, 1) // Has display name

    /**
     * Game-related logic.
     */
    player.sendRunEnergy(player.runEnergy.toInt())
    player.message("Welcome to ${player.world.gameContext.name}.", ChatMessageType.SERVER)
}