package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class PlayerService {

    PlayerRepository repository;

    @Autowired
    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public List<Player> getPlayers(String name, String title, Race race, Profession profession,
                                   Long after, Long before,
                                   int minExp, int maxExp, int minLevel, int maxLevel,
                                   Boolean banned, PlayerOrder order,
                                   int pageNumber, int pageSize) {

        List<Player> players = filterPlayers(getPlayers(), name, title, race, profession,
                after, before, minExp, maxExp, minLevel, maxLevel, banned);

        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min((pageNumber + 1) * pageSize, players.size());

        players = players.subList(fromIndex, toIndex);

        sortPlayers(players, order);
        return players;
    }

    public Player getPlayerById(long id) {
        return repository.findById(id).orElse(null);
    }

    public int getPlayersSize(String name, String title, Race race, Profession profession,
                              Long after, Long before,
                              int minExp, int maxExp, int minLevel, int maxLevel,
                              Boolean banned) {

        return filterPlayers(getPlayers(), name, title, race, profession,
                after, before, minExp, maxExp, minLevel, maxLevel, banned).size();
    }

    public List<Player> getPlayers() {
        return repository.findAll();
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    public void save(Player player) {
        preparePlayer(player);
        repository.save(player);
    }

    public void update(long id, Player player) {
        player.setId(id);
        Player sourcePlayer = getPlayerById(id);
        if (player.getName() == null) player.setName(sourcePlayer.getName());
        if (player.getTitle() == null) player.setTitle(sourcePlayer.getTitle());
        if (player.getRace() == null) player.setRace(sourcePlayer.getRace());
        if (player.getProfession() == null) player.setProfession(sourcePlayer.getProfession());
        if (player.getBirthday() == null) player.setBirthday(sourcePlayer.getBirthday());
        if (player.getExperience() == null) player.setExperience(sourcePlayer.getExperience());
        if (player.getBanned() == null) player.setBanned(sourcePlayer.getBanned());
        preparePlayer(player);
        repository.save(player);
    }

    private List<Player> filterPlayers(List<Player> players,
                                       String name, String title, Race race, Profession profession,
                                       Long after, Long before,
                                       int minExp, int maxExp, int minLevel, int maxLevel,
                                       Boolean banned) {

        Stream<Player> playersStream = players.stream();

        if (name != null && !name.isEmpty()) {
            playersStream = playersStream.filter(p -> p.getName().contains(name));
        }
        if (title != null && !title.isEmpty()) {
            playersStream = playersStream.filter(p -> p.getTitle().contains(title));
        }
        if (race != null) {
            playersStream = playersStream.filter(p -> p.getRace().equals(race));
        }
        if (profession != null) {
            playersStream = playersStream.filter(p -> p.getProfession().equals(profession));
        }
        if (after != null) {
            playersStream = playersStream.filter(p -> p.getBirthday().getTime() >= after);
        }
        if (before != null) {
            playersStream = playersStream.filter(p -> p.getBirthday().getTime() <= before);
        }
        if (minExp != -1) {
            playersStream = playersStream.filter(p -> p.getExperience() >= minExp);
        }
        if (maxExp != -1) {
            playersStream = playersStream.filter(p -> p.getExperience() <= maxExp);
        }
        if (minLevel != -1) {
            playersStream = playersStream.filter(p -> p.getLevel() >= minLevel);
        }
        if (maxLevel != -1) {
            playersStream = playersStream.filter(p -> p.getLevel() <= maxLevel);
        }
        if (banned != null) {
            playersStream = playersStream.filter(p -> p.getBanned().equals(banned));
        }
        return playersStream.collect(Collectors.toList());
    }

    private void sortPlayers(List<Player> players, PlayerOrder order) {
        switch (order) {
            case ID: {
                players.sort((o1, o2) -> (int) (o1.getId() - o2.getId()));
                break;
            }
            case NAME: {
                players.sort(Comparator.comparing(Player::getName));
                break;
            }
            case BIRTHDAY: {
                players.sort(Comparator.comparing(Player::getBirthday));
                break;
            }
            case EXPERIENCE: {
                players.sort(Comparator.comparingInt(Player::getExperience));
                break;
            }
            case LEVEL: {
                players.sort(Comparator.comparingInt(Player::getLevel));
                break;
            }
        }
    }

    private void preparePlayer(Player player) {
        if (player.getBanned() == null) player.setBanned(false);
        player.setLevel((int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100));
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
    }

}
