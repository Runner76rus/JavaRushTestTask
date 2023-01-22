package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerEmptyValidator;
import com.game.service.PlayerService;
import com.game.service.PlayerValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerController {

    private final PlayerService service;
    private final PlayerEmptyValidator playerEmptyValidator;
    private final PlayerValidator playerValidator;

    @Autowired
    public PlayerController(PlayerService service, PlayerEmptyValidator playerEmptyValidator, PlayerValidator playerValidator) {
        this.service = service;
        this.playerEmptyValidator = playerEmptyValidator;
        this.playerValidator = playerValidator;
    }

    @GetMapping()
    @ResponseBody
    public List<Player> getAll(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String title,
                               @RequestParam(required = false) String race,
                               @RequestParam(required = false) String profession,
                               @RequestParam(required = false) Long after,
                               @RequestParam(required = false) Long before,
                               @RequestParam(defaultValue = "-1") int minExperience,
                               @RequestParam(defaultValue = "-1") int maxExperience,
                               @RequestParam(defaultValue = "-1") int minLevel,
                               @RequestParam(defaultValue = "-1") int maxLevel,
                               @RequestParam(required = false) Boolean banned,
                               @RequestParam(defaultValue = "0") int pageNumber,
                               @RequestParam(defaultValue = "3") int pageSize,
                               @RequestParam(defaultValue = "ID") String order) {
        return service.getPlayers(name, title,
                race != null ? Race.valueOf(race) : null,
                profession != null ? Profession.valueOf(profession) : null,
                after, before,
                minExperience, maxExperience,
                minLevel, maxLevel, banned,
                PlayerOrder.valueOf(order),
                pageNumber, pageSize);
    }

    @GetMapping("/count")
    public Integer getCount(@RequestParam(required = false) String name,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) String race,
                            @RequestParam(required = false) String profession,
                            @RequestParam(required = false) Long after,
                            @RequestParam(required = false) Long before,
                            @RequestParam(defaultValue = "-1") int minExperience,
                            @RequestParam(defaultValue = "-1") int maxExperience,
                            @RequestParam(defaultValue = "-1") int minLevel,
                            @RequestParam(defaultValue = "-1") int maxLevel,
                            @RequestParam(required = false) Boolean banned) {
        return service.getPlayersSize(name, title,
                race != null ? Race.valueOf(race) : null,
                profession != null ? Profession.valueOf(profession) : null,
                after, before,
                minExperience, maxExperience,
                minLevel, maxLevel, banned);
    }

    @GetMapping("/{id}")
    public Player getPlayerById(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        Player player = service.getPlayerById(id);
        if (player == null) response.sendError(HttpServletResponse.SC_NOT_FOUND);
        return player;
    }

    @PostMapping
    public Player create(@RequestBody Player player, BindingResult bindingResult, HttpServletResponse response) throws IOException {
        playerEmptyValidator.validate(player, bindingResult);
        if (bindingResult.hasErrors()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        service.save(player);
        return player;
    }

    @PostMapping("/{id}")
    public Player update(@PathVariable("id") long id, @RequestBody Player player, BindingResult bindingResult, HttpServletResponse response) throws IOException {
        if (id <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if (service.getPlayerById(id) == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        playerValidator.validate(player, bindingResult);
        if (bindingResult.hasErrors()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        service.update(id, player);
        return player;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
        if (id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (service.getPlayerById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        service.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
