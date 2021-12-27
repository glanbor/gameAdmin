package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayersController {

    private final PlayerService playerService;

    @Autowired
    public PlayersController(PlayerService playerService) {
        this.playerService = playerService;
    }

    private boolean invalidId(Long id) {
        return id <= 0;
    }

    private boolean invalidPlayerFields(Player player) {
        if (player.getName() != null && (player.getName().length() < 1 || player.getName().length() > 12)
                || (player.getTitle() != null && player.getTitle().length() > 30)
                || (player.getBirthday() != null && player.getBirthday().getTime() < 0)
                || (player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10_000_000)))
            return true;
        if (player.getBirthday() != null) {
            Calendar date = Calendar.getInstance();
            date.setTime(player.getBirthday());
            if (date.get(Calendar.YEAR) < 2000 || date.get(Calendar.YEAR) > 3000) return true;
        }
        return false;
    }

    private boolean nullPlayerOrFields(Player player) {
        if (player == null
                || player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null)
            return true;
        return false;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> showPlayersList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Player> playerSpecification = Specification.where(
                playerService.filterName(name)
                        .and(playerService.filterTitle(title))
                        .and(playerService.filterRace(race))
                        .and(playerService.filterProfession(profession))
                        .and(playerService.filterBirthday(after, before))
                        .and(playerService.filterBanned(banned))
                        .and(playerService.filterExperience(minExperience, maxExperience))
                        .and(playerService.filterLevel(minLevel, maxLevel)));

        return playerService.showPlayersList(playerSpecification, pageable).getContent();
    }

    @GetMapping("/players/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer countPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {

        Specification<Player> playerSpecification = Specification.where(
                playerService.filterName(name)
                        .and(playerService.filterTitle(title))
                        .and(playerService.filterRace(race))
                        .and(playerService.filterProfession(profession))
                        .and(playerService.filterBirthday(after, before))
                        .and(playerService.filterBanned(banned))
                        .and(playerService.filterExperience(minExperience, maxExperience))
                        .and(playerService.filterLevel(minLevel, maxLevel)));

        return playerService.showPlayersList(playerSpecification).size();
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long id) {
        if (invalidId(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player player = playerService.getPlayer(id);
        if (player == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player requestPlayer) {
        if (nullPlayerOrFields(requestPlayer) || invalidPlayerFields(requestPlayer))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player responsePlayer = playerService.createPlayer(requestPlayer);
        if (responsePlayer == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else return new ResponseEntity<>(responsePlayer, HttpStatus.OK);
    }

    @PostMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable(name = "id") Long id, @RequestBody Player requestPlayer) {
        if (invalidId(id) || invalidPlayerFields(requestPlayer))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Player responsePlayer = playerService.updatePlayer(id, requestPlayer);
        if (responsePlayer == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(responsePlayer, HttpStatus.OK);
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") Long id) {
        if (invalidId(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (playerService.deletePlayer(id))
            return new ResponseEntity<>(HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
