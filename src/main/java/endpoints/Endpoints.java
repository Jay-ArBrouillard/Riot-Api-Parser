package endpoints;

import kong.unirest.*;

public class Endpoints {

    public final String challengerQueueUri = "https://na1.api.riotgames.com/lol/league/v4/challengerleagues/by-queue/RANKED_SOLO_5x5";
    public final String grandmasterQueueUri = "https://na1.api.riotgames.com/lol/league/v4/grandmasterleagues/by-queue/RANKED_SOLO_5x5";
    public final String masterQueueUri = "https://na1.api.riotgames.com/lol/league/v4/masterleagues/by-queue/RANKED_SOLO_5x5";
    public String leagueEntryQueueUri = "https://na1.api.riotgames.com/lol/league/v4/entries/RANKED_SOLO_5x5/";
    public final String summonerByIdUri = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/";
    public final String matchListUri = "https://na1.api.riotgames.com/lol/match/v4/matchlists/by-account/";
    public static String matchUri = "https://na1.api.riotgames.com/lol/match/v4/matches/";

    public Endpoints(String key) {
        Unirest.config().socketTimeout(600000).connectTimeout(100000).addDefaultHeader("X-Riot-Token", key).automaticRetries(true);
    }

    public HttpResponse<JsonNode> getChallengerQueue() {
        HttpResponse<JsonNode> response = Unirest.get(challengerQueueUri).asJson();
        return response;
    }

    public HttpResponse<JsonNode> getGrandmasterQueue() {
        HttpResponse<JsonNode> response = Unirest.get(grandmasterQueueUri).asJson();
        return response;
    }

    public HttpResponse<JsonNode> getMasterQueue() {
        HttpResponse<JsonNode> response = Unirest.get(masterQueueUri).asJson();
        return response;
    }

    public HttpResponse<JsonNode> getLeagueEntry(String tier, String division) {
        HttpResponse<JsonNode> response = Unirest.get(leagueEntryQueueUri+tier+"/"+division+"?page=1").header("accept", "application/json").asJson();
        return response;
    }

    public HttpResponse<JsonNode> getSummonerById(String id) {
        HttpResponse<JsonNode> response = Unirest.get(summonerByIdUri+id).asJson();
        return response;
    }

    public HttpResponse<JsonNode> getMatchList(String accountId) {
        HttpResponse<JsonNode> response = Unirest.get(matchListUri+accountId).asJson();
        return response;
    }

    public HttpResponse<JsonNode> getMatch(String gameId) {
        HttpResponse<JsonNode> response = Unirest.get(matchUri+gameId).asJson();
        return response;
    }

}
