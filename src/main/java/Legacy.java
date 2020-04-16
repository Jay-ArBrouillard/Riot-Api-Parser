import com.google.gson.Gson;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import net.rithms.riot.api.RiotApi;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.api.endpoints.league.constant.LeagueQueue;
import net.rithms.riot.api.endpoints.league.dto.LeagueEntry;
import net.rithms.riot.api.endpoints.league.dto.LeagueItem;
import net.rithms.riot.api.endpoints.league.dto.LeagueList;
import net.rithms.riot.api.endpoints.match.dto.*;
import net.rithms.riot.api.endpoints.summoner.dto.Summoner;
import net.rithms.riot.constant.Platform;

import java.io.FileWriter;
import java.util.List;
import java.util.Set;

public class Legacy {

    private static FileWriter summonerDocument;
    private static FileWriter matchesDocument;
    private static RiotApi api;
    private static JSONObject summonerJsonObject;
    private static JSONArray playersJsonArray;
    private static JSONObject matchDetails;
    private static JSONArray matchDetailList;
    private static final int DELAY = 2000; //milliseconds

    public static void buildSummonerJson() throws RiotApiException, InterruptedException {
        //Iterate through all ranked players in NA
        //Challenger
        System.out.println("Processing challenger players...");
        LeagueList challenger = null;
        try {
            challenger = api.getChallengerLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
        } catch (Exception e) {
            while (challenger == null) {
                Thread.sleep(60000);
                challenger = api.getChallengerLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
            }
        }
        getSummoners(challenger);
        System.out.println("Finished challenger players");

        System.out.println("Processing grandmaster players...");
        Thread.sleep(DELAY);
        LeagueList grandmaster = null;
        try {
            grandmaster = api.getGrandmasterLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
        } catch (Exception e) {
            while (grandmaster == null) {
                Thread.sleep(60000);
                grandmaster = api.getGrandmasterLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
            }
        }
        getSummoners(grandmaster);
        System.out.println("Finished grandmaster players");

        System.out.println("Processing master players...");
        Thread.sleep(DELAY);
        LeagueList master = null;
        try {
            master = api.getMasterLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
        } catch (Exception e) {
            while (master == null) {
                Thread.sleep(60000);
                master = api.getMasterLeagueByQueue(Platform.NA, LeagueQueue.RANKED_SOLO_5x5);
            }
        }
        getSummoners(master);
        System.out.println("Finished master players");

        String [] divisions = new String[] { "I", "II", "III", "IV"};

        for (String division : divisions) {
            System.out.println("Processing Diamond " + division + " players...");
            Thread.sleep(DELAY);
            Set<LeagueEntry> diamond = null;
            try {
                diamond = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "DIAMOND", division);
            } catch (Exception e) {
                while (diamond == null) {
                    Thread.sleep(60000);
                    diamond = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "DIAMOND", division);
                }
            }
            getSummoners(diamond);
            System.out.println("Finished Diamond " + division + " players");
        }
        System.out.println("Finished Diamond players");

        for (String division : divisions) {
            System.out.println("Processing Platinum " + division + " players...");
            Thread.sleep(DELAY);
            Set<LeagueEntry> platinum = null;
            try {
                platinum = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "PLATINUM", division);
            } catch (Exception e) {
                while (platinum == null) {
                    Thread.sleep(60000);
                    platinum = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "PLATINUM", division);
                }
            }
            getSummoners(platinum);
            System.out.println("Finished Platinum " + division + " players");
        }
        System.out.println("Finished platinum players");

        for (String division : divisions) {
            System.out.println("Processing Gold " + division + " players...");
            Thread.sleep(DELAY);
            Set<LeagueEntry> gold = null;
            try {
                gold = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "GOLD", division);
            } catch (Exception e) {
                while (gold == null) {
                    Thread.sleep(60000);
                    gold = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "GOLD", division);
                }
            }
            getSummoners(gold);
            System.out.println("Finished Gold " + division + " players...");
        }
        System.out.println("Finished gold players");

        for (String division : divisions) {
            System.out.println("Processing Silver " + division + " players...");
            Thread.sleep(DELAY);
            Set<LeagueEntry> silver = null;
            try {
                silver = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "SILVER", division);
            } catch (Exception e) {
                while (silver == null) {
                    Thread.sleep(60000);
                    silver = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "SILVER", division);
                }
            }
            getSummoners(silver);
            System.out.println("Finished Silver " + division + " players...");
        }
        System.out.println("Finished silver players");

        for (String division : divisions) {
            System.out.println("Processing Bronze " + division + " players...");
            Thread.sleep(DELAY);
            Set<LeagueEntry> bronze = null;
            try {
                bronze = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "BRONZE", division);
            } catch (Exception e) {
                while (bronze == null) {
                    Thread.sleep(60000);
                    bronze = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "BRONZE", division);
                }
            }
            getSummoners(bronze);
            System.out.println("Finished Bronze " + division + " players...");
        }
        System.out.println("Finished bronze players");

        for (String division : divisions) {
            System.out.println("Processing Iron " + division + " players...");
            Thread.sleep(DELAY);
            Set<LeagueEntry> iron = null;
            try {
                iron = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "IRON", division);
            } catch (Exception e) {
                while (iron == null) {
                    Thread.sleep(60000);
                    iron = api.getLeagueEntries(Platform.NA, "RANKED_SOLO_5x5", "IRON", division);
                }
            }
            getSummoners(iron);
            System.out.println("Finished Iron " + division + " players...");
        }
        System.out.println("Finished iron players");

        //Iterate through all summoners to add each summoners match list
        System.out.println("Processing matches...");
        for (int i = 0; i < playersJsonArray.length(); i++) {
            String accountId = ((JSONObject) playersJsonArray.get(i)).getString("accountId");
            MatchList matchList = null;
            try {
                matchList = api.getMatchListByAccountId(Platform.NA, accountId);
            } catch (Exception e) {
                while (matchList == null) {
                    Thread.sleep(60000);
                    matchList = api.getMatchListByAccountId(Platform.NA, accountId);
                }
            }
            // We can now iterate over the match list to access the matches they were in
            JSONArray matches = new JSONArray();
            for (MatchReference match : matchList.getMatches()) {
                JSONObject m = new JSONObject();
                m.put("gameId", match.getGameId());
                buildMatchDetailsJson(match.getGameId());
                m.put("champion", match.getChampion());
                m.put("timestamp", match.getTimestamp());
                m.put("role", match.getRole());
                m.put("lane", match.getLane());
                matches.put(m);
            }
            ((JSONObject)(playersJsonArray.get(i))).put("matches", matches);
        }
        summonerJsonObject.put("summoners", playersJsonArray);
        matchDetails.put("matches", matchDetailList);
        System.out.println("Finished matches and match details");
    }
    private static void handleRequestCount() throws InterruptedException {
        //Development api key
        Thread.sleep(DELAY);
        //Personal api key or production api key
//        requestCount++;
//        if (requestCount+1 == 20) {
//            requestCount = 0;
//            Thread.sleep(800);
//        }
    }

    private static void buildMatchDetailsJson(Long pMatchId) throws RiotApiException, InterruptedException {
        JSONObject json = new JSONObject();
        Thread.sleep(DELAY);
        Match match = null;
        try {
            match = api.getMatch(Platform.NA, pMatchId);
        } catch (Exception e) {
            while (match == null) {
                Thread.sleep(60000);
                match = api.getMatch(Platform.NA, pMatchId);
            }
        }
        json.put("gameCreation", match.getGameCreation());
        json.put("gameDuration", match.getGameDuration());
        json.put("season", match.getSeasonId());
        json.put("gameMode", match.getGameMode());
        json.put("gameType", match.getGameType());
        //Teams
        JSONArray teams = new JSONArray();
        for (TeamStats ts : match.getTeams()) {
            JSONObject team = new JSONObject();
            team.put("teamId", ts.getTeamId());
            team.put("win", ts.getWin());
            team.put("firstBlood", ts.isFirstBlood());
            team.put("firstTower", ts.isFirstTower());
            team.put("firstInhibitor", ts.isFirstInhibitor());
            team.put("firstBaron", ts.isFirstBaron());
            team.put("firstDragon", ts.isFirstDragon());
            team.put("firstRiftHerald", ts.isFirstRiftHerald());
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
            participant.put("stats", handleStats(p));
            participant.put("timeline", handleTimeLine(p));
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

    public static void getSummoners (LeagueList list) throws InterruptedException, RiotApiException {
        List<LeagueItem> entries = list.getEntries();
        for (LeagueItem item : entries) {
            JSONObject i = new JSONObject();
            Summoner summoner = null;
            int len = playersJsonArray.length();
            try {
                summoner = api.getSummoner(Platform.NA, item.getSummonerId());
            } catch (Exception e) {
                Thread.sleep(60000);
                while (playersJsonArray.length() == len) {
                    summoner = api.getSummoner(Platform.NA, item.getSummonerId());
                    if (summoner != null) {
                        i.put("id", summoner.getId());
                        i.put("accountId", summoner.getAccountId());
                        i.put("name", summoner.getName());
                        i.put("summonerLevel", summoner.getSummonerLevel());
                        i.put("tier", list.getTier());
                        i.put("leaguePoints", item.getLeaguePoints());
                        i.put("rank", item.getRank());
                        i.put("wins", item.getWins());
                        i.put("losses", item.getLosses());
                        i.put("veteran", item.isVeteran());
                        i.put("freshBlood", item.isFreshBlood());
                        i.put("hotStreak", item.isHotStreak());
                        i.put("inactive", item.isInactive());
                        playersJsonArray.put(i);
                    }
                }
            }
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
            i.put("veteran", item.isVeteran());
            i.put("freshBlood", item.isFreshBlood());
            i.put("hotStreak", item.isHotStreak());
            i.put("inactive", item.isInactive());
            playersJsonArray.put(i);
        }
    }

    public static void getSummoners (Set<LeagueEntry> list) throws InterruptedException, RiotApiException {
        for (LeagueEntry item : list) {
            JSONObject i = new JSONObject();
            Summoner summoner = null;
            int len = playersJsonArray.length();
            try {
                summoner = api.getSummoner(Platform.NA, item.getSummonerId());
            } catch (Exception e) {
                Thread.sleep(60000);
                while (playersJsonArray.length() == len) {
                    summoner = api.getSummoner(Platform.NA, item.getSummonerId());
                    if (summoner != null) {
                        i.put("id", summoner.getId());
                        i.put("accountId", summoner.getAccountId());
                        i.put("name", summoner.getName());
                        i.put("summonerLevel", summoner.getSummonerLevel());
                        i.put("tier", item.getTier());
                        i.put("leaguePoints", item.getLeaguePoints());
                        i.put("rank", item.getRank());
                        i.put("wins", item.getWins());
                        i.put("losses", item.getLosses());
                        i.put("veteran", item.isVeteran());
                        i.put("freshBlood", item.isFreshBlood());
                        i.put("hotStreak", item.isHotStreak());
                        i.put("inactive", item.isInactive());
                        playersJsonArray.put(i);
                    }
                }
            }
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
            i.put("veteran", item.isVeteran());
            i.put("freshBlood", item.isFreshBlood());
            i.put("hotStreak", item.isHotStreak());
            i.put("inactive", item.isInactive());
            playersJsonArray.put(i);
        }
    }

    public static JSONObject handleStats(Participant p) {
        JSONObject stats = new JSONObject();
        stats.put("participantId", p.getStats().getParticipantId());
        stats.put("win", p.getStats().isWin());
        stats.put("item0", p.getStats().getItem0());
        stats.put("item1", p.getStats().getItem1());
        stats.put("item2", p.getStats().getItem2());
        stats.put("item3", p.getStats().getItem3());
        stats.put("item4", p.getStats().getItem4());
        stats.put("item5", p.getStats().getItem5());
        stats.put("item6", p.getStats().getItem6());
        stats.put("kills", p.getStats().getKills());
        stats.put("deaths", p.getStats().getDeaths());
        stats.put("assists", p.getStats().getAssists());
        stats.put("largestKillingSpree", p.getStats().getLargestKillingSpree());
        stats.put("largestMultiKill", p.getStats().getLargestMultiKill());
        stats.put("killingSprees", p.getStats().getKillingSprees());
        stats.put("longestTimeSpentLiving", p.getStats().getLongestTimeSpentLiving());
        stats.put("doubleKills", p.getStats().getDoubleKills());
        stats.put("tripleKills", p.getStats().getTripleKills());
        stats.put("quadraKills", p.getStats().getQuadraKills());
        stats.put("pentaKills", p.getStats().getPentaKills());
        stats.put("unrealKills", p.getStats().getUnrealKills());
        stats.put("totalDamageDealt", p.getStats().getTotalDamageDealt());
        stats.put("magicDamageDealt", p.getStats().getMagicDamageDealt());
        stats.put("physicalDamageDealt", p.getStats().getPhysicalDamageDealt());
        stats.put("trueDamageDealt", p.getStats().getTrueDamageDealt());
        stats.put("largestCriticalStrike", p.getStats().getLargestCriticalStrike());
        stats.put("totalDamageDealtToChampions", p.getStats().getTotalDamageDealtToChampions());
        stats.put("magicDamageDealtToChampions", p.getStats().getMagicDamageDealtToChampions());
        stats.put("physicalDamageDealtToChampions", p.getStats().getPhysicalDamageDealtToChampions());
        stats.put("trueDamageDealtToChampions", p.getStats().getTrueDamageDealtToChampions());
        stats.put("totalHeal", p.getStats().getTotalHeal());
        stats.put("totalUnitsHealed", p.getStats().getTotalUnitsHealed());
        stats.put("damageSelfMitigated", p.getStats().getDamageSelfMitigated());
        stats.put("damageDealtToObjectives", p.getStats().getDamageDealtToObjectives());
        stats.put("damageDealtToTurrets", p.getStats().getDamageDealtToTurrets());
        stats.put("visionScore", p.getStats().getVisionScore());
        stats.put("timeCCingOthers", p.getStats().getTimeCCingOthers());
        stats.put("totalDamageTaken", p.getStats().getTotalDamageTaken());
        stats.put("magicalDamageTaken", p.getStats().getMagicalDamageTaken());
        stats.put("physicalDamageTaken", p.getStats().getPhysicalDamageTaken());
        stats.put("trueDamageTaken", p.getStats().getTrueDamageTaken());
        stats.put("goldEarned", p.getStats().getGoldEarned());
        stats.put("goldSpent", p.getStats().getGoldSpent());
        stats.put("turretKills", p.getStats().getTurretKills());
        stats.put("inhibitorKills", p.getStats().getInhibitorKills());
        stats.put("totalMinionsKilled", p.getStats().getTotalMinionsKilled());
        stats.put("neutralMinionsKilled", p.getStats().getNeutralMinionsKilled());
        stats.put("neutralMinionsKilledTeamJungle", p.getStats().getNeutralMinionsKilledTeamJungle());
        stats.put("neutralMinionsKilledEnemyJungle", p.getStats().getNeutralMinionsKilledEnemyJungle());
        stats.put("totalTimeCrowdControlDealt", p.getStats().getTotalTimeCrowdControlDealt());
        stats.put("champLevel", p.getStats().getChampLevel());
        stats.put("visionWardsBoughtInGame", p.getStats().getVisionWardsBoughtInGame());
        stats.put("sightWardsBoughtInGame", p.getStats().getSightWardsBoughtInGame());
        stats.put("wardsPlaced", p.getStats().getWardsPlaced());
        stats.put("wardsKilled", p.getStats().getWardsKilled());
        stats.put("firstBloodKill", p.getStats().isFirstBloodKill());
        stats.put("firstBloodAssist", p.getStats().isFirstBloodAssist());
        stats.put("firstTowerKill", p.getStats().isFirstTowerKill());
        stats.put("firstTowerAssist", p.getStats().isFirstTowerAssist());
        stats.put("firstInhibitorKill", p.getStats().isFirstInhibitorKill());
        stats.put("firstInhibitorAssist", p.getStats().isFirstInhibitorAssist());
        stats.put("combatPlayerScore", p.getStats().getCombatPlayerScore());
        stats.put("objectivePlayerScore", p.getStats().getObjectivePlayerScore());
        stats.put("totalPlayerScore", p.getStats().getTotalPlayerScore());
        stats.put("totalScoreRank", p.getStats().getTotalScoreRank());
        stats.put("playerScore0", p.getStats().getPlayerScore0());
        stats.put("playerScore1", p.getStats().getPlayerScore1());
        stats.put("playerScore2", p.getStats().getPlayerScore2());
        stats.put("playerScore3", p.getStats().getPlayerScore3());
        stats.put("playerScore4", p.getStats().getPlayerScore4());
        stats.put("playerScore5", p.getStats().getPlayerScore5());
        stats.put("playerScore6", p.getStats().getPlayerScore6());
        stats.put("playerScore7", p.getStats().getPlayerScore7());
        stats.put("playerScore8", p.getStats().getPlayerScore8());
        stats.put("playerScore9", p.getStats().getPlayerScore9());
        //TODO ADD PERKS HERE
        return stats;
    }

    private static JSONObject handleTimeLine(Participant p) {
        JSONObject timeline = new JSONObject();
        timeline.put("participantId", p.getParticipantId());
        timeline.put("creepsPerMinDeltas", new JSONObject(String.valueOf(p.getTimeline().getCreepsPerMinDeltas())));
        timeline.put("xpPerMinDeltas", new JSONObject(String.valueOf(p.getTimeline().getXpPerMinDeltas())));
        timeline.put("goldPerMinDeltas", new JSONObject(String.valueOf(p.getTimeline().getGoldPerMinDeltas())));
        timeline.put("csDiffPerMinDeltas", new JSONObject(String.valueOf(p.getTimeline().getCsDiffPerMinDeltas())));
        timeline.put("xpDiffPerMinDeltas", new JSONObject(String.valueOf(p.getTimeline().getXpDiffPerMinDeltas())));
        timeline.put("damageTakenPerMinDeltas", new JSONObject(String.valueOf(p.getTimeline().getDamageTakenPerMinDeltas())));
        timeline.put("damageTakenDiffPerMinDeltas", new JSONObject(String.valueOf(p.getTimeline().getDamageTakenDiffPerMinDeltas())));
        timeline.put("role", new JSONObject(p.getTimeline().getRole()));
        timeline.put("lane", new JSONObject(p.getTimeline().getLane()));
        return timeline;
    }
}
