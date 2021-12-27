package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PlayerService {

    Specification<Player> filterName(String name);
    Specification<Player> filterTitle(String title);
    Specification<Player> filterBirthday(Long after, Long before);
    Specification<Player> filterExperience(Integer min, Integer max);
    Specification<Player> filterLevel(Integer min, Integer max);
    Specification<Player> filterRace(Race race);
    Specification<Player> filterProfession(Profession profession);
    Specification<Player> filterBanned(Boolean isBanned);

    Page<Player> showPlayersList(Specification<Player> specification, Pageable pageable);
    List<Player> showPlayersList(Specification<Player> specification);
    Player getPlayer(Long id);
    Player createPlayer(Player requestPlayer);
    Player updatePlayer(Long id, Player requestPlayer);
    boolean deletePlayer(Long id);
}
