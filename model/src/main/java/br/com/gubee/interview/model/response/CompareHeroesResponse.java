package br.com.gubee.interview.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CompareHeroesResponse {

    private UUID firstId;
    private UUID secondId;
    private int strengthDiff;
    private int agilityDiff;
    private int dexterityDiff;
    private int intelligenceDiff;

}
