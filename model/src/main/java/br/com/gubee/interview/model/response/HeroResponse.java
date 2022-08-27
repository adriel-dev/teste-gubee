package br.com.gubee.interview.model.response;

import br.com.gubee.interview.model.enums.Race;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeroResponse {

    private String name;
    private Race race;
    private int strength;
    private int agility;
    private int dexterity;
    private int intelligence;

}