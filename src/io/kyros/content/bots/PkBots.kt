package io.kyros.content.bots

import io.kyros.content.bots.pkbot.*
import io.kyros.model.Items
import io.kyros.model.entity.player.Player
import io.kyros.model.entity.player.Right
import io.kyros.model.entity.player.mode.Mode
import io.kyros.model.entity.player.mode.ModeType
import io.kyros.util.Misc
import java.security.SecureRandom

class PkBots {

    fun startPKBots() {
        val player = Player.createBot(generateBotName(), Right.PLAYER)

        if (generateMode() == ModeType.IRON_MAN) {
            player.setMode(Mode.forType(ModeType.IRON_MAN))
            player.rights.primary = Right.IRONMAN

            player.items.equipItem(Items.IRONMAN_HELM, 1, Player.playerHat)
            player.items.equipItem(Items.IRONMAN_PLATEBODY, 1, Player.playerChest)
            player.items.equipItem(Items.IRONMAN_PLATELEGS, 1, Player.playerLegs)
            player.items.equipItem(1323, 1, Player.playerWeapon)
        } else {
            player.setMode(Mode.forType(ModeType.STANDARD))

            player.items.equipItem(1323, 1, Player.playerWeapon)
            player.items.equipItem(1153, 1, Player.playerHat)
            player.items.equipItem(1115, 1, Player.playerChest)
            player.items.equipItem(1067, 1, Player.playerLegs)
        }

        player.autoRet = 1

        val style = Misc.random(0, 9)

        when (style) {
            0 -> OneDefencePure(player)
            1 -> DefencePureBot(player)
            2 -> ObsidianPureBot(player)
            3 -> GraniteMaulPureBot(player)
            4 -> Black13DefencePureBot(player)
            5 -> InitiateRuneBerserkerPureBot(player)
            6 -> VoidPureBot(player)
            7 -> MagePureTankBot(player)
            8 -> RangePureTankBot(player)
            9 -> BarrowsPureBot(player)
        }
    }

    private val PREFIXES: Array<String> = arrayOf(
        "Silver", "Shadow", "Golden", "Mystic", "Spirit", "Crystal", "Starlight", "Dragon", "Emerald", "Thunder",
        "Fire", "Ice", "Moon", "Sun", "Sky", "Ocean", "Forest", "Mountain", "Storm", "Wind",
        "Sword", "Shield", "Arrow", "Bow", "Spear", "Hammer", "Axe", "Staff", "Wand", "Dagger",
        "Rune", "Grim", "Dark", "Light", "Bane", "Frost", "Flame", "Stone", "Blood", "Soul",
        "Dream", "Swift"
    )

    private val SUFFIXES: Array<String> = arrayOf(
        "Blade", "Wanderer", "Seeker", "Hero", "Guardian", "Knight", "Mage", "Sorcerer", "Ranger", "Hunter",
        "Adventurer", "Drifter", "Champion", "Warrior", "Defender", "Slayer", "Archer", "Wizard", "Warlock", "Shaman",
        "Assassin", "Paladin", "Barbarian", "Crusader", "Rogue", "Bard", "Monk", "Necromancer", "Cleric", "Rogue",
        "Fury", "Savage", "Scout", "Sentinel", "Serpent", "Phoenix", "Bear", "Wolf", "Eagle", "Lion",
        "Tiger", "Dragon", "Falcon", "Viper", "Hawk", "Panther", "Cobra", "Griffin", "Raven", "Owl",
        "Wraith", "Golem", "Titan", "Hydra", "Phoenix", "Elemental", "Centaur", "Gargoyle", "Basilisk", "Minotaur",
        "Yeti", "Gryphon", "Cyclops", "Sphinx", "Medusa", "Kraken", "Harpy", "Werewolf", "Vampire", "Zombie",
        "Ghoul", "Wraith", "Specter", "Spirit", "Phantom", "Reaper", "Lich", "Nemesis", "Shadow", "Death",
        "Eclipse", "Illusion", "Mirage", "Mystery", "Whisper", "Echo", "Shade", "Veil", "Nightmare", "Fury"
    )

    private var usedNames: MutableSet<String> = HashSet()

    private fun generateBotName(): String {
        val random = SecureRandom()

        var name: String
        do {
            // Randomly choose a prefix and suffix
            val prefix = PREFIXES[random.nextInt(PREFIXES.size)]
            val suffix = SUFFIXES[random.nextInt(SUFFIXES.size)]

            // Combine the elements to create the username
            name = "$prefix $suffix"
        } while (usedNames.contains(name)) // Check if the name is already used

        usedNames.add(name) // Add the name to used names set
        return name
    }

    private fun generateMode(): ModeType {
        val rng = Misc.random(0, 10)

        if (rng > 0 && rng < 5) {
            return ModeType.IRON_MAN
        } else if (rng > 5) {
            return ModeType.STANDARD
        }

        return ModeType.STANDARD
    }
}
