package gg.rsmod.plugins.content.skills.woodcutting

import gg.rsmod.plugins.api.ext.getInteractingGameObj
import gg.rsmod.plugins.content.skills.woodcutting.Woodcutting.Tree

val trees = arrayListOf(
        Tree(TreeType.TREE, obj = 1278, trunk = 1342),
        Tree(TreeType.TREE, obj = 1276, trunk = 1342),
        Tree(TreeType.TREE, obj = 1286, trunk = 1351), // Dead tree
        Tree(TreeType.TREE, obj = 1282, trunk = 1347), // Dead tree
        Tree(TreeType.TREE, obj = 1383, trunk = 1358), // Dead tree
        Tree(TreeType.TREE, obj = 1289, trunk = 1353), // Dead tree
        Tree(TreeType.TREE, obj = 2091, trunk = 1342), // Evergreen

        Tree(TreeType.REDWOOD, obj = 29668, trunk = 29669),
        Tree(TreeType.REDWOOD, obj = 29670, trunk = 29671)
)

trees.forEach { tree ->
    on_obj_option(obj = tree.obj, option = "chop down") {
        val obj = it.getInteractingGameObj()
        it.suspendable {
            Woodcutting.chopDownTree(it, obj, tree.type, tree.trunk)
        }
    }
}