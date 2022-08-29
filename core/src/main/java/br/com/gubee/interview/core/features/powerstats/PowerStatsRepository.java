package br.com.gubee.interview.core.features.powerstats;

import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PowerStatsRepository {

    private static final String CREATE_POWER_STATS_QUERY = "INSERT INTO power_stats" +
        " (strength, agility, dexterity, intelligence)" +
        " VALUES (:strength, :agility, :dexterity, :intelligence) RETURNING id";

    private static final String DELETE_POWER_STATS_QUERY = "DELETE FROM power_stats WHERE power_stats.id = :id";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UUID create(PowerStats powerStats) {
        return namedParameterJdbcTemplate.queryForObject(
            CREATE_POWER_STATS_QUERY,
            new BeanPropertySqlParameterSource(powerStats),
            UUID.class);
    }

    public void update(PowerStats powerStats) {
        String query = createUpdateQuery(powerStats);
        if (query != null) {
            final Map<String, Object> params = createParamsList(powerStats);
            final int rowsAffected = namedParameterJdbcTemplate.update(query, params);
            if(rowsAffected == 0) throw new NoSuchElementException();
        }
    }

    private String createUpdateQuery(PowerStats powerStats) {
        String strength = powerStats.getStrength() != null ? "strength = :strength," : "";
        String agility = powerStats.getAgility() != null ? "agility = :agility," : "";
        String dexterity = powerStats.getDexterity() != null ? "dexterity = :dexterity," : "";
        String intelligence = powerStats.getIntelligence() != null ? "intelligence = :intelligence," : "";
        String query = "UPDATE power_stats " +
                "SET "+strength+agility+dexterity+intelligence+"updated_at = :updatedAt " +
                "WHERE id = :id";
        return strength.equals("")
                && agility.equals("")
                && dexterity.equals("")
                && intelligence.equals("") ? null : query;
    }

    private Map<String, Object> createParamsList(PowerStats powerStats) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", powerStats.getId());
        if(powerStats.getStrength() != null) params.put("strength", powerStats.getStrength());
        if(powerStats.getAgility() != null) params.put("agility", powerStats.getAgility());
        if(powerStats.getDexterity() != null) params.put("dexterity", powerStats.getDexterity());
        if(powerStats.getIntelligence() != null) params.put("intelligence", powerStats.getIntelligence());
        params.put("updatedAt", Timestamp.from(powerStats.getUpdatedAt()));
        return params;
    }

    public void delete(UUID id) {
        Map<String, UUID> params = Map.of("id", id);
        final int rowsAffected = namedParameterJdbcTemplate.update(DELETE_POWER_STATS_QUERY, params);
        if (rowsAffected == 0) throw new NoSuchElementException();
    }

}
