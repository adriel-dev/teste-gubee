package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.response.HeroResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class HeroRepository {

    private static final String CREATE_HERO_QUERY = "INSERT INTO hero" +
        " (name, race, power_stats_id)" +
        " VALUES (:name, :race, :powerStatsId) RETURNING id";

    private static final String FIND_ALL_HEROES = "SELECT hero.id, hero.name, hero.race, " +
            "power_stats.strength, power_stats.agility, power_stats.dexterity, power_stats.intelligence " +
            "FROM hero " +
            "INNER JOIN power_stats ON hero.power_stats_id = power_stats.id";
    private static final String FIND_HERO_BY_ID_QUERY = "SELECT hero.id, hero.name, hero.race, " +
            "power_stats.strength, power_stats.agility, power_stats.dexterity, power_stats.intelligence " +
            "FROM hero " +
            "INNER JOIN power_stats ON hero.power_stats_id = power_stats.id " +
            "WHERE hero.id = :heroId";

    private static final String FIND_HERO_BY_NAME = "SELECT hero.id, hero.name, hero.race, " +
            "power_stats.strength, power_stats.agility, power_stats.dexterity, power_stats.intelligence " +
            "FROM hero " +
            "INNER JOIN power_stats ON hero.power_stats_id = power_stats.id " +
            "WHERE hero.name LIKE :heroName";

    private static final String FIND_POWER_STATS_ID = "SELECT hero.power_stats_id FROM hero WHERE hero.id = :heroId";

    private static final String DELETE_HERO_QUERY = "DELETE FROM hero WHERE hero.id = :id";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    UUID create(Hero hero) {
        final Map<String, Object> params = Map.of("name", hero.getName(),
            "race", hero.getRace().name(),
            "powerStatsId", hero.getPowerStatsId());

        return namedParameterJdbcTemplate.queryForObject(
            CREATE_HERO_QUERY,
            params,
            UUID.class);
    }

    public List<HeroResponse> findAllHeroes() {
        return namedParameterJdbcTemplate.query(FIND_ALL_HEROES, (rs, rowNum) -> new HeroResponse(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                Race.valueOf(rs.getString("race")),
                rs.getInt("strength"),
                rs.getInt("agility"),
                rs.getInt("dexterity"),
                rs.getInt("intelligence")
        ));
    }

    public HeroResponse findHeroById(UUID id) {
        final Map<String, Object> params = Map.of("heroId", id);
        List<HeroResponse> heroes = namedParameterJdbcTemplate.query(FIND_HERO_BY_ID_QUERY, params, (rs, rowNum) -> new HeroResponse(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                Race.valueOf(rs.getString("race")),
                rs.getInt("strength"),
                rs.getInt("agility"),
                rs.getInt("dexterity"),
                rs.getInt("intelligence")
        ));
        return heroes.stream().findFirst().orElseThrow(() -> new NoSuchElementException(String.format("No hero with UUID: '%s' was found.", id)));
    }

    public HeroResponse findHeroByName(String name) {
        final Map<String, Object> params = Map.of("heroName", name);
        List<HeroResponse> heroes = namedParameterJdbcTemplate.query(FIND_HERO_BY_NAME, params, (rs, rowNum) -> new HeroResponse(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                Race.valueOf(rs.getString("race")),
                rs.getInt("strength"),
                rs.getInt("agility"),
                rs.getInt("dexterity"),
                rs.getInt("intelligence")
        ));
        return heroes.stream().findFirst().orElseThrow(NoSuchElementException::new);
    }

    public UUID findHeroPowerStatsId(UUID id) {
        Map<String, Object> params = Map.of("heroId", id);
        try{
            return namedParameterJdbcTemplate.queryForObject(FIND_POWER_STATS_ID, params, UUID.class);
        }catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException();
        }
    }

    public UUID update(Hero hero) {
        try{
            String query = createUpdateQuery(hero);
            if(query != null){
                Map<String, Object> params = createParamsList(hero);
                return namedParameterJdbcTemplate.queryForObject(query, params, UUID.class);
            }else{
                return findHeroPowerStatsId(hero.getId());
            }
        }catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException();
        }
    }

    private String createUpdateQuery(Hero hero) {
        String name = hero.getName() != null ? "name = :name," : "";
        String race = hero.getRace() != null ? "race = :race," : "";
        String query = "UPDATE hero " +
                "SET "+name+race+" updated_at = :updatedAt " +
                "WHERE id = :id RETURNING power_stats_id";
        return name.equals("") && race.equals("") ? null : query;
    }

    private Map<String, Object> createParamsList(Hero hero) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", hero.getId());
        if(hero.getName() != null) params.put("name", hero.getName());
        if(hero.getRace() != null) params.put("race", hero.getRace().name());
        params.put("updatedAt", Timestamp.from(hero.getUpdatedAt()));
        return params;
    }

    public void delete(UUID id) {
        Map<String, UUID> params = Map.of("id", id);
        final int rowsAffected = namedParameterJdbcTemplate.update(DELETE_HERO_QUERY, params);
        if(rowsAffected == 0) throw new NoSuchElementException();
    }

    public List<HeroResponse> compare(UUID heroId1, UUID heroId2) {
        HeroResponse hero1 = findHeroById(heroId1);
        HeroResponse hero2 = findHeroById(heroId2);
        return List.of(hero1, hero2);
    }

}
