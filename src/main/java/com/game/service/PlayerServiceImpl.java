package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    private void setLevelAndExperienceBeforeSaving(Player requestPlayer) {
        int experience = requestPlayer.getExperience();
        int level = (int) (Math.sqrt(2500+200*experience)-50)/100;
        requestPlayer.setLevel(level);
        requestPlayer.setUntilNextLevel(50*(level+1)*(level+2) - experience);
    }

    @Override
    public Specification<Player> filterName(String name) {
        return new Specification<Player>() {
            @Override
            public Predicate toPredicate(Root<Player> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (name == null) return null;
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }

    //  The method structure is as previous one, but uses lambda and ternary operator
    @Override
    public Specification<Player> filterTitle(String title) {
        return (root, query, criteriaBuilder) -> title == null ? null :
                criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> filterBirthday(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null) return null;
            if (after == null) return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before));
            if (before == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after));
            return criteriaBuilder.between(root.get("birthday"), new Date(after), new Date(before));
        };
    }

    @Override
    public Specification<Player> filterExperience(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    @Override
    public Specification<Player> filterLevel(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            if (max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    @Override
    public Specification<Player> filterRace(Race race) {
        return (root, query, criteriaBuilder) -> race == null ? null :
                criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> filterProfession(Profession profession) {
        return (root, query, criteriaBuilder) -> profession == null ? null :
                criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> filterBanned(Boolean isBanned) {
        return (root, query, criteriaBuilder) -> {
            if (isBanned == null) return null;
            if (isBanned) return criteriaBuilder.isTrue(root.get("banned"));
            return criteriaBuilder.isFalse(root.get("banned"));
        };
    }

    @Override
    public List<Player> showPlayersList(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public Page<Player> showPlayersList(Specification<Player> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public Player getPlayer(Long id) {
        return playerRepository.findById(id).isPresent() ? playerRepository.findById(id).get() : null;
    }

    @Override
    public Player createPlayer(Player requestPlayer) {
        if (requestPlayer.isBanned() == null) requestPlayer.setBanned(false);
        setLevelAndExperienceBeforeSaving (requestPlayer);
        return playerRepository.saveAndFlush(requestPlayer);
    }

    @Override
    public Player updatePlayer(Long id, Player requestPlayer) {
        Player responsePlayer = getPlayer(id);
        if (responsePlayer == null) return null;

        if (requestPlayer.getName() != null) responsePlayer.setName(requestPlayer.getName());
        if (requestPlayer.getTitle() != null) responsePlayer.setTitle(requestPlayer.getTitle());
        if (requestPlayer.getRace() != null) responsePlayer.setRace(requestPlayer.getRace());
        if (requestPlayer.getProfession() != null) responsePlayer.setProfession(requestPlayer.getProfession());
        if (requestPlayer.getBirthday() != null) responsePlayer.setBirthday(requestPlayer.getBirthday());
        if (requestPlayer.isBanned() != null) responsePlayer.setBanned(requestPlayer.isBanned());
        if (requestPlayer.getExperience() != null) responsePlayer.setExperience(requestPlayer.getExperience());

        setLevelAndExperienceBeforeSaving(responsePlayer);
        return playerRepository.save(responsePlayer);
    }

    @Override
    public boolean deletePlayer(Long id) {
        Player deletedPlayer = getPlayer(id);
        if (deletedPlayer == null) return false;
        playerRepository.delete(deletedPlayer);
        return true;
    }
}
