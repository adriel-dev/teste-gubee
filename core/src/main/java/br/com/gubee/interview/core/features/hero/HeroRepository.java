package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.enums.Race;
import br.com.gubee.interview.model.response.HeroResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HeroRepository {

    private static final String CREATE_HERO_QUERY = "INSERT INTO hero" +
        " (name, race, power_stats_id)" +
        " VALUES (:name, :race, :powerStatsId) RETURNING id";

    private static final String FIND_HERO_BY_ID_QUERY = "SELECT hero.name, hero.race, " +
            "power_stats.strength, power_stats.agility, power_stats.dexterity, power_stats.intelligence " +
            "FROM hero " +
            "INNER JOIN power_stats ON hero.power_stats_id = power_stats.id " +
            "WHERE hero.id = :heroId";

    private static final String FIND_HERO_BY_NAME = "SELECT hero.name, hero.race, " +
            "power_stats.strength, power_stats.agility, power_stats.dexterity, power_stats.intelligence " +
            "FROM hero " +
            "INNER JOIN power_stats ON hero.power_stats_id = power_stats.id " +
            "WHERE hero.name LIKE :heroName";

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

    public HeroResponse findHeroById(UUID id) {
        final Map<String, Object> params = Map.of("heroId", id);
        List<HeroResponse> heroes = namedParameterJdbcTemplate.query(FIND_HERO_BY_ID_QUERY, params, (rs, rowNum) -> new HeroResponse(
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
                rs.getString("name"),
                Race.valueOf(rs.getString("race")),
                rs.getInt("strength"),
                rs.getInt("agility"),
                rs.getInt("dexterity"),
                rs.getInt("intelligence")
        ));
        return heroes.stream().findFirst().orElseThrow(NoSuchElementException::new);
    }

}
