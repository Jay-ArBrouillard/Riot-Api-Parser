import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.rithms.riot.api.ApiConfig;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.league.constant.LeagueQueue;
import net.rithms.riot.api.endpoints.league.dto.LeagueEntry;
import net.rithms.riot.api.endpoints.league.dto.LeagueItem;
import net.rithms.riot.api.endpoints.league.dto.LeagueList;
import net.rithms.riot.api.endpoints.match.dto.*;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Main {

    private static FileWriter summonerDocument;
    private static FileWriter matchesDocument;
    private static RiotApi api;
    private static JSONObject matchDetails;
    private static JSONArray matchDetailList;
    private static final int DELAY = 2000; //2000ms

    public static void main(String[] args) throws RiotApiException, InterruptedException {
        ApiConfig config = new ApiConfig().setKey("API_KEY");
        api = new RiotApi(config);

        matchDetails = new JSONObject(); //Contains a list of matches
        matchDetailList = new JSONArray();
        JSONObject summonerJson = buildSummonerJson();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(summonerJson.toString());
        String prettySummonerJsonString = gson.toJson(je);
        je = jp.parse(matchDetails.toString());
        String prettyMatchesJsonString = gson.toJson(je);

        try {
            summonerDocument = new FileWriter("Summoner.json");
            summonerDocument.write(prettySummonerJsonString);
            matchesDocument = new FileWriter("Matches.json");
            matchesDocument.write(prettyMatchesJsonString);
            System.out.println("Finished");
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                summonerDocument.flush();
                summonerDocument.close();
                matchesDocument.flush();
                matchesDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONObject buildSummonerJson() throws RiotApiException, InterruptedException {
        JSONObject json = new JSONObject();
        //Iterate through all ranked players in NA
        //Challenger
        JSONArray players = new JSONArray();
        LeagueList challenger = api.getChallengerLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
        Thread.sleep(DELAY);
        getSummoners(challenger, players);
        System.out.println("Finished challenger players");

        LeagueList grandmaster = api.getGrandmasterLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
        Thread.sleep(DELAY);
        getSummoners(grandmaster, players);
        System.out.println("Finished grandmaster players");

        LeagueList master = api.getMasterLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
        Thread.sleep(DELAY);
        getSummoners(master, players);
        System.out.println("Finished master players");

        String [] divisions = new String[] { "I", "II", "III", "IV"};

        for (String division : divisions) {
            Set<LeagueEntry> diamond = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "DIAMOND", division);
            Thread.sleep(DELAY);
            getSummoners(diamond, players);
        }
        Set<LeagueEntry> diamond = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "DIAMOND", "I");
        Thread.sleep(DELAY);
        getSummoners(diamond, players);
        System.out.println("Finished diamond players");

        for (String division : divisions) {
            Set<LeagueEntry> platinum = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "PLATINUM", division);
            Thread.sleep(DELAY);
            getSummoners(platinum, players);
        }
        System.out.println("Finished platinum players");

        for (String division : divisions) {
            Set<LeagueEntry> gold = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "GOLD", division);
            Thread.sleep(DELAY);
            getSummoners(gold, players);
        }
        System.out.println("Finished gold players");

        for (String division : divisions) {
            Set<LeagueEntry> silver = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "SILVER", division);
            Thread.sleep(DELAY);
            getSummoners(silver, players);
        }
        System.out.println("Finished silver players");

        for (String division : divisions) {
            Set<LeagueEntry> bronze = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "BRONZE", division);
            Thread.sleep(DELAY);
            getSummoners(bronze, players);
        }
        System.out.println("Finished bronze players");

        for (String division : divisions) {
            Set<LeagueEntry> iron = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "IRON", division);
            Thread.sleep(DELAY);
            getSummoners(iron, players);
        }
        System.out.println("Finished iron players");

        //Iterate through all summoners to add each summoners match list
        for (int i = 0; i < players.length(); i++) {
            JSONObject temp = (JSONObject) players.get(i);
            String accountId = temp.getString("accountId");
            MatchList matchList = api.getMatchListByAccountId(Platform.NA, accountId);
            Thread.sleep(DELAY);
            // We can now iterate over the match list to access the matches they were in
            JSONArray matches = new JSONArray();
            for (MatchReference match : matchList.getMatches()) {
                JSONObject m = new JSONObject();
                m.put("gameId", match.getGameId());
                buildMatchDetailsJson(match.getGameId());   //may and may not want to build match details now
                m.put("champion", match.getChampion());
                m.put("timestamp", match.getTimestamp());
                m.put("role", match.getRole());
                m.put("lane", match.getLane());
                matches.put(m);
            }
            temp.put("matches", matches);
        }
        json.put("summoners", players);
        matchDetails.put("matches", matchDetailList);
        return json;
    }

    private static void buildMatchDetailsJson(Long pMatchId) throws RiotApiException, InterruptedException {
        JSONObject json = new JSONObject();
        Match match = api.getMatch(Platform.NA, pMatchId);
        Thread.sleep(DELAY);
        json.put("gameCreation", match.getGameCreation());
        json.put("gameDuration", match.getGameDuration());
        json.put("gameMode", match.getGameMode());
        json.put("gameType", match.getGameType());
        //Teams
        JSONArray teams = new JSONArray();
        for (TeamStats ts : match.getTeams()) {
            JSONObject team = new JSONObject();
            team.put("teamId", ts.getTeamId());
            team.put("win", ts.getWin());
            team.put("towerKills", ts.getTowerKills());
            team.put("inhibitorKills", ts.getInhibitorKills());
            team.put("baronKills", ts.getBaronKills());
            team.put("dragonKills", ts.getDragonKills());
            team.put("riftHeraldKills", ts.getRiftHeraldKills());
            JSONArray bansArr = new JSONArray();
            for (TeamBans ban : ts.getBans()) {
                JSONObject b = new JSONObject();
                b.put("championId", ban.getChampionId());
                b.put("pickTurn", ban.getPickTurn());
                bansArr.put(b);
            }
            team.put("bans", bansArr);
            teams.put(team);
        }
        json.put("teams", teams);
        //Participants
        JSONArray participants = new JSONArray();
        for (Participant p : match.getParticipants()) {
            JSONObject participant = new JSONObject();
            participant.put("participantId", p.getParticipantId());
            participant.put("teamId", p.getTeamId());
            participant.put("championId", p.getChampionId());
            participant.put("spell1Id", p.getSpell1Id());
            participant.put("spell2Id", p.getSpell2Id());
            String statsJsonString = new Gson().toJson(p.getStats());
            participant.put("stats", statsJsonString);
            String timelineJsonString = new Gson().toJson(p.getTimeline());
            participant.put("timeline", timelineJsonString);
            participants.put(participant);
        }
        json.put("participants", participants);
        //Participant identities
        JSONArray participantIdentities = new JSONArray();
        for (ParticipantIdentity p : match.getParticipantIdentities()) {
            JSONObject participant = new JSONObject();
            participant.put("participantId", p.getParticipantId());
            String playerJsonString = new Gson().toJson(p.getPlayer());
            participant.put("player", playerJsonString);
            participantIdentities.put(participant);
        }
        json.put("participantIdentities", participantIdentities);
        matchDetailList.put(json); //Add one match to the list
    }

    public static void getSummoners (LeagueList list, JSONArray players) throws InterruptedException, RiotApiException {
        List<LeagueItem> entries = list.getEntries();
        for (LeagueItem item : entries) {
            JSONObject i = new JSONObject();
            Summoner summoner = api.getSummoner(Platform.NA, item.getSummonerId());
            Thread.sleep(DELAY);
            i.put("id", summoner.getId());
            i.put("accountId", summoner.getAccountId());
            i.put("name", summoner.getName());
            i.put("summonerLevel", summoner.getSummonerLevel());
            i.put("tier", list.getTier());
            i.put("leaguePoints", item.getLeaguePoints());
            i.put("rank", item.getRank());
            i.put("wins", item.getWins());
            i.put("losses", item.getLosses());
            players.put(i);
        }
    }

    public static void getSummoners (Set<LeagueEntry> list, JSONArray players) throws InterruptedException, RiotApiException {
        for (LeagueEntry item : list) {
            JSONObject i = new JSONObject();
            Summoner summoner = api.getSummoner(Platform.NA, item.getSummonerId());
            Thread.sleep(DELAY);
            i.put("id", summoner.getId());
            i.put("accountId", summoner.getAccountId());
            i.put("name", summoner.getName());
            i.put("summonerLevel", summoner.getSummonerLevel());
            i.put("tier", item.getTier());
            i.put("leaguePoints", item.getLeaguePoints());
            i.put("rank", item.getRank());
            i.put("wins", item.getWins());
            i.put("losses", item.getLosses());
            players.put(i);
        }
    }
}
