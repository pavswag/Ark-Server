package io.kyros.content.pet.duel;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.cache.definitions.NpcDefinition;
import io.kyros.content.pet.Pet;
import io.kyros.model.entity.npc.pets.PetHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PetAnimationRepository {

    @Getter
    private static final Map<Integer, Integer> attackAnimations = new HashMap<>();

    private static List<PetAttackAnimation> loadPetsFromFile(String filePath) {
        List<PetAttackAnimation> petAttackAnimations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("#"))
                    continue;
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int npcId = Integer.parseInt(parts[0].trim());  // NPC ID
                    int attackAnimation = Integer.parseInt(parts[1].trim());  // Attack animation
                    petAttackAnimations.add(new PetAttackAnimation(npcId, attackAnimation));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return petAttackAnimations;
    }

    @PostInit
    public static void initRepository() {
        loadPetsFromFile(Server.getDataDirectory() + "/cfg/npc/pet_attack_anims.kyros").forEach(petAttackAnimation -> {
            getAttackAnimations().put(petAttackAnimation.getNpcId(), petAttackAnimation.getAttackAnimation());
        });

        log.info("Loaded {} pet attack animations.", getAttackAnimations().size());
    }
}
