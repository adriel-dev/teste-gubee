package br.com.gubee.interview.core.features.hero;

import br.com.gubee.interview.model.request.CreateHeroRequest;
import br.com.gubee.interview.model.request.UpdateHeroRequest;
import br.com.gubee.interview.model.response.CompareHeroesResponse;
import br.com.gubee.interview.model.response.HeroResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/heroes", produces = APPLICATION_JSON_VALUE)
@CrossOrigin
public class HeroController {

    private final HeroService heroService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@Validated
                                       @RequestBody CreateHeroRequest createHeroRequest) {
        final UUID id = heroService.create(createHeroRequest);
        return created(URI.create(format("/api/v1/heroes/%s", id))).build();
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<HeroResponse>> findAllHeroes() {
        try {
            return ok().body(heroService.findAllHeroes());
        } catch (NoSuchElementException e) {
            return notFound().build();
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<HeroResponse> findHeroById(@PathVariable UUID id) {
        try{
            return ok().body(heroService.findHeroById(id));
        }catch (NoSuchElementException e) {
            return notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<HeroResponse> findHeroByName(@RequestParam(required = true) String name) {
        try{
            return ok().body(heroService.findHeroByName(name));
        }catch (NoSuchElementException e) {
            return ok().build();
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<HeroResponse> update(@PathVariable UUID id, @RequestBody UpdateHeroRequest updateHeroRequest) {
        try {
            return ok().body(heroService.update(id, updateHeroRequest));
        }catch (NoSuchElementException e) {
            return notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            heroService.delete(id);
            return ok().build();
        }catch (NoSuchElementException e) {
            return notFound().build();
        }
    }

    @GetMapping(value = "/compare/{id1}/{id2}")
    public ResponseEntity<CompareHeroesResponse> compare(@PathVariable(value = "id1") UUID heroId1, @PathVariable(value = "id2") UUID heroId2) {
        try {
            return ok().body(heroService.compare(heroId1, heroId2));
        } catch (NoSuchElementException e) {
            return notFound().build();
        }
    }

}
