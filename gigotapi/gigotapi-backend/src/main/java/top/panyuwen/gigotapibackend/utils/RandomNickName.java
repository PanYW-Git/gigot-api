package top.panyuwen.gigotapibackend.utils;

import java.util.Random;

public class RandomNickName {

    private static final String[] ALL_NAMES = {
            "Teemo", "Jinx", "Zed", "Lux", "Yasuo", "Ahri", "Ashe", "Darius", "Garen", "Sona",
            "Valhein", "Lubu", "Yorn", "Krixi", "Nakroth", "Murad", "Raz", "Zill", "Violet", "Thane",
            "Slayer", "Gunner", "Mage", "Priest", "Thief", "Knight", "Lancer", "Fighter", "Dark Knight", "Creator",
            "KillerZone", "HeadshotMaster", "SurvivorX", "ChickenDinner", "DeathDealer", "SniperKing", "WarriorPrime", "LootHunter", "BattleRoyale", "WinnerWinner"
    };

    public static String getRandomName() {
        return ALL_NAMES[new Random().nextInt(ALL_NAMES.length)];
    }
}
