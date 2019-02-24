package gg.rsmod.plugins.content.npcs.sheep

import gg.rsmod.game.fs.def.NpcDef
import gg.rsmod.game.model.Tile
import gg.rsmod.game.model.entity.GroundItem
import gg.rsmod.game.model.entity.Npc
import gg.rsmod.game.model.entity.Player
import gg.rsmod.game.plugin.Plugin
import gg.rsmod.plugins.api.cfg.Items
import gg.rsmod.plugins.api.cfg.Npcs
import gg.rsmod.plugins.api.ext.getInteractingNpc
import gg.rsmod.plugins.api.ext.playSound
import gg.rsmod.plugins.api.ext.player

Sheep.SHEEP_NPCS.forEach { sheep ->
    if (world.definitions.get(NpcDef::class.java, sheep).options.contains("Shear")) {

        on_npc_option(npc = sheep, option = "shear") {
            val player = it.player()
            val npc = it.getInteractingNpc()

            player.facePawn(null)
            player.faceTile(npc.tile)
            if (!player.inventory.hasItem(Items.SHEARS)) {
                player.message("You need a set of shears to do this.")
                return@on_npc_option
            }
            it.suspendable { shear(it, player, npc) }
        }
    }
}

suspend fun shear(it: Plugin, p: Player, n: Npc) {
    val flee = n.world.percentChance(15.0)

    p.lock()
    p.animate(893)
    p.playSound(761)
    if (flee) {
        flee(n)
    }
    it.wait(2)
    p.unlock()
    p.playSound(762)
    n.forceChat("Baa!")

    if (!flee) {
        if (p.inventory.hasSpace()) {
            p.inventory.add(item = Items.WOOL)
        } else {
            val ground = GroundItem(item = Items.WOOL, amount = 1, tile = Tile(p.tile), owner = p)
            p.world.spawn(ground)
        }
        p.message("You get some wool.")
        n.executePlugin {
            it.suspendable {
                transmog_sheep(it, n)
            }
        }
    } else {
        p.message("The sheep manages to get away from you!")
    }
}

fun flee(n: Npc) {
    val rx = n.world.random(-n.walkRadius..n.walkRadius)
    val rz = n.world.random(-n.walkRadius..n.walkRadius)

    val start = n.spawnTile
    val dest = start.transform(rx, rz)

    n.walkTo(dest)
}

suspend fun transmog_sheep(it: Plugin, n: Npc) {
    n.setTransmogId(if (n.id == 2803) Npcs.SHEEP_2792 else Npcs.SHEEP_2793)
    it.wait(100)
    n.setTransmogId(n.id)
}