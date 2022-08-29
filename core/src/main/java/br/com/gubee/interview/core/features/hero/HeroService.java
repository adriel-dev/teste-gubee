package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.core.features.powerstats.PowerStatsRepository;
import br.com.gubee.interview.model.Hero;
import br.com.gubee.interview.model.PowerStats;
import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import br.com.gubee.interview.model.response.CompareHeroesResponse;
import br.com.gubee.interview.model.response.HeroResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HeroService {

    private final HeroRepository heroRepository;

    private final PowerStatsRepository powerStatsRepository;

    @Transactional
    public UUID create(CreateHeroRequest createHeroRequest) {
        final UUID powerStatsId = powerStatsRepository.create(new PowerStats(createHeroRequest));
        return heroRepository.create(new Hero(createHeroRequest, powerStatsId));
    }

    public HeroResponse findHeroById(UUID id) throws NoSuchElementException {
        return heroRepository.findHeroById(id);
    }

    public HeroResponse findHeroByName(String name) throws NoSuchElementException {
        return heroRepository.findHeroByName(name);
    }

    @Transactional
    public HeroResponse update(UUID id, UpdateHeroRequest updateHeroRequest) throws NoSuchElementException {
        Hero hero = new Hero(
                id,
                updateHeroRequest.getName(),
                updateHeroRequest.getRace(),
                null,
                null,
                Instant.now(),
                true
        );
        UUID powerStatsId = heroRepository.update(hero);
        PowerStats powerStats = new PowerStats(
                powerStatsId,
                updateHeroRequest.getStrength(),
                updateHeroRequest.getAgility(),
                updateHeroRequest.getDexterity(),
                updateHeroRequest.getIntelligence(),
                null,
                Instant.now()
        );
        powerStatsRepository.update(powerStats);
        return this.findHeroById(id);
    }

    @Transactional
    public void delete(UUID id) throws NoSuchElementException {
        UUID powerStatsId = heroRepository.findHeroPowerStatsId(id);
        heroRepository.delete(id);
        powerStatsRepository.delete(powerStatsId);
    }

    public CompareHeroesResponse compare(UUID heroId1, UUID heroId2) throws NoSuchElementException {
        List<HeroResponse> heroes = heroRepository.compare(heroId1, heroId2);
        HeroResponse hero1 = heroes.get(0);
        HeroResponse hero2 = heroes.get(1);
        return new CompareHeroesResponse(
                heroId1,
                heroId2,
                hero1.getStrength() - hero2.getStrength(),
                hero1.getAgility() - hero2.getAgility(),
                hero1.getDexterity() - hero2.getDexterity(),
                hero1.getIntelligence() - hero2.getIntelligence()
        );
    }

}
