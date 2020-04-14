import endpoints.EndpointType;
import endpoints.Endpoints;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class Main {

    private static JSONObject summonerJsonObject;
    private static JSONArray playersJsonArray;
    private static JSONObject matchDetails;
    private static JSONArray matchDetailList;
    private static ArrayList<String> gameIdArray;
    private static final int DELAY = 2000; //milliseconds
    private static Endpoints endpoints;

    public static void main(String[] args) throws InterruptedException, IOException {
        matchDetails = new JSONObject(); //Contains a list of matches
        matchDetailList = new JSONArray();
        summonerJsonObject = new JSONObject(); //Contains a list of matches
        playersJsonArray = new JSONArray();
        gameIdArray = new ArrayList<>();
        endpoints = new Endpoints();
        collectSummoners();
    }

    public static Object handleExceptions(EndpointType e, String data1, String data2) throws InterruptedException {
        JSONObject json = null;
        JSONArray arr = null;
        HttpResponse<JsonNode> response = null;
        do {
            switch (e) {
                case CHALLENGER_QUEUE:
                    response = endpoints.getChallengerQueue();
                    break;
                case GRANDMASTER_QUEUE:
                    response = endpoints.getGrandmasterQueue();
                    break;
                case MASTER_QUEUE:
                    response = endpoints.getMasterQueue();
                    break;
                case LEAGUE_QUEUE:
                    response = endpoints.getLeagueEntry(data1, data2);
                    break;
                case SUMMONER:
                    response = endpoints.getSummonerById(data1);
                    break;
                case MATCH:
                    response = endpoints.getMatch(data1);
                    break;
                case MATCHLIST:
                    response = endpoints.getMatchList(data1);
                    break;
            }

            if (response.getHeaders().containsKey("X-App-Rate-Limit-Count") && response.getHeaders().containsKey("X-Method-Rate-Limit-Count")) {
                String appRateLimitCount = response.getHeaders().getFirst("X-App-Rate-Limit-Count");
                int reqAppPerSec = Integer.parseInt(appRateLimitCount.substring(0, appRateLimitCount.indexOf(":")));
                int reqAppPer120Sec = Integer.parseInt(appRateLimitCount.substring(appRateLimitCount.indexOf(",")+1, appRateLimitCount.lastIndexOf(":")));
                String methodRateLimitCount = response.getHeaders().getFirst("X-Method-Rate-Limit-Count");
                int reqMethodPer10Sec = Integer.parseInt(methodRateLimitCount.substring(0, methodRateLimitCount.indexOf(":")));
                int reqMethodPer600Sec = Integer.parseInt(methodRateLimitCount.substring(methodRateLimitCount.indexOf(",")+1, methodRateLimitCount.lastIndexOf(":")));
                if (reqMethodPer600Sec+1 >= 600) {
                    Thread.sleep(	600000);
                }
                else if (reqAppPer120Sec+1 >= 120) {
                    Thread.sleep(	120000);
                }
                else if (reqMethodPer10Sec+1 >= 30) {
                    Thread.sleep(	10000);
                }
                else if (reqAppPerSec+1 >= 20) {
                    Thread.sleep(2000);
                }
            }

            if (response.getStatus() == 200) {
                json = response.getBody().getObject();
                arr = response.getBody().getArray();
            }
            else { //ERROR
                 if (response.getStatus() == 429 && response.getHeaders().containsKey("Retry-After")) { //Rate limit exceeded
                     String retry = response.getHeaders().getFirst("Retry-After");
                     if (!retry.isEmpty()) {
                         Thread.sleep(Integer.parseInt(retry)  * 1000);
                     } else {
                         Thread.sleep(60000); //Sleep a minute
                     }
                 }
                 else {
                    endpoints.shutdown();
                    endpoints = new Endpoints();
                    Thread.sleep(60000); //Sleep a minute
                 }
            }
        } while (json == null && arr == null);

        if (json != null) {
            return json;
        }
        return arr;
    }

    /**
     * Collect basic summoner data from each tier into playersJsonArray
     */
    public static void collectSummoners() throws InterruptedException, IOException {
        String [] divisions = new String[] { "I", "II", "III", "IV"};
        JSONObject challengerQueue = (JSONObject) handleExceptions(EndpointType.CHALLENGER_QUEUE, null, null);
        JSONObject grandmasterQueue = (JSONObject) handleExceptions(EndpointType.GRANDMASTER_QUEUE, null, null);
        JSONObject masterQueue = (JSONObject) handleExceptions(EndpointType.MASTER_QUEUE, null, null);

        JSONArray challenger = challengerQueue.getJSONArray("entries");
        System.out.println("Processing challenger players...");
        for (int i = 0; i < challenger.length(); i++) {
            JSONObject curr = challenger.getJSONObject(i);
            curr.put("tier", "CHALLENGER");
            String summonerId = curr.getString("summonerId");
            JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
            String accountId = summonerObject.getString("accountId");
            curr.put("accountId", accountId);
            curr.put("summonerLevel", summonerObject.getString("summonerLevel"));
            JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
            JSONArray matches = new JSONArray();
            for (Object o : matchlistObject.getJSONArray("matches")) {
                JSONObject match = (JSONObject) o;
                gameIdArray.add(match.getString("gameId"));
                matches.put(match);
            }
            curr.put("matches", matches);
            playersJsonArray.put(curr);
            if (i % 10 == 0) {
                System.out.println((Double.valueOf(i) / challenger.length()) * 100 + "% complete of " + challenger.length() + " with challenger players");
            }
        }

        JSONArray grandmaster = grandmasterQueue.getJSONArray("entries");
        System.out.println("Processing grandmaster players...");
        for (int i = 0; i < grandmaster.length(); i++) {
            JSONObject curr = grandmaster.getJSONObject(i);
            curr.put("tier", "GRANDMASTER");
            String summonerId = curr.getString("summonerId");
            JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
            String accountId = summonerObject.getString("accountId");
            curr.put("accountId", accountId);
            curr.put("summonerLevel", summonerObject.getString("summonerLevel"));
            JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
            JSONArray matches = new JSONArray();
            for (Object o : matchlistObject.getJSONArray("matches")) {
                JSONObject match = (JSONObject) o;
                gameIdArray.add(match.getString("gameId"));
                matches.put(match);
            }
            curr.put("matches", matches);
            playersJsonArray.put(curr);
            if (i % 10 == 0) {
                System.out.println((Double.valueOf(i) / grandmaster.length()) * 100 + "% complete of " + grandmaster.length() + " with grandmaster players");
            }
        }

        JSONArray master = masterQueue.getJSONArray("entries");
        System.out.println("Processing master players...");
        for (int i = 0; i < master.length(); i++) {
            JSONObject curr = master.getJSONObject(i);
            curr.put("tier", "MASTER");
            String summonerId = curr.getString("summonerId");
            JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
            String accountId = summonerObject.getString("accountId");
            curr.put("accountId", accountId);
            curr.put("summonerLevel", summonerObject.getString("summonerLevel"));
            JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
            JSONArray matches = new JSONArray();
            for (Object o : matchlistObject.getJSONArray("matches")) {
                JSONObject match = (JSONObject) o;
                gameIdArray.add(match.getString("gameId"));
                matches.put(match);
            }
            curr.put("matches", matches);
            playersJsonArray.put(curr);
            if (i % 10 == 0) {
                System.out.println((Double.valueOf(i) / master.length()) * 100 + "% complete of " + master.length() + " with master players");
            }
        }

        for (String division : divisions) {
            System.out.println("Processing Diamond " + division + " players...");
            JSONArray curr = (JSONArray) handleExceptions(EndpointType.LEAGUE_QUEUE, "DIAMOND", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
                JSONArray matches = new JSONArray();
                for (Object m : matchlistObject.getJSONArray("matches")) {
                    JSONObject match = (JSONObject) m;
                    gameIdArray.add(match.getString("gameId"));
                    matches.put(match);
                }
                summoner.put("matches", matches);
                playersJsonArray.put(summoner);
                if (i % 10 == 0) {
                    System.out.println((Double.valueOf(i) / curr.length()) * 100 + "% complete of " +  curr.length() + " with diamond " + division + " players");
                }
                i++;
            }
            System.out.println("Finished Diamond " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Platinum " + division + " players...");
            JSONArray curr = (JSONArray) handleExceptions(EndpointType.LEAGUE_QUEUE, "PLATINUM", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
                JSONArray matches = new JSONArray();
                for (Object m : matchlistObject.getJSONArray("matches")) {
                    JSONObject match = (JSONObject) m;
                    gameIdArray.add(match.getString("gameId"));
                    matches.put(match);
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.println((Double.valueOf(i) / curr.length()) * 100 + "% complete of " +  curr.length() + " with platinum " + division + " players");
                }
                i++;
            }
            System.out.println("Finished Platinum " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Gold " + division + " players...");
            JSONArray curr = (JSONArray)  handleExceptions(EndpointType.LEAGUE_QUEUE, "GOLD", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
                JSONArray matches = new JSONArray();
                for (Object m : matchlistObject.getJSONArray("matches")) {
                    JSONObject match = (JSONObject) m;
                    gameIdArray.add(match.getString("gameId"));
                    matches.put(match);
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.println((Double.valueOf(i) / curr.length()) * 100 + "% complete of " +  curr.length() + " with gold " + division + " players");
                }
                i++;
            }
            System.out.println("Finished Gold " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Silver " + division + " players...");
            JSONArray curr = (JSONArray)  handleExceptions(EndpointType.LEAGUE_QUEUE, "SILVER", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
                JSONArray matches = new JSONArray();
                for (Object m : matchlistObject.getJSONArray("matches")) {
                    JSONObject match = (JSONObject) m;
                    gameIdArray.add(match.getString("gameId"));
                    matches.put(match);
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.println((Double.valueOf(i) / curr.length()) * 100 + "% complete of " +  curr.length() + " with silver " + division + " players");
                }
                i++;
            }
            System.out.println("Finished Silver " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Bronze " + division + " players...");
            JSONArray curr = (JSONArray)  handleExceptions(EndpointType.LEAGUE_QUEUE, "BRONZE", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
                JSONArray matches = new JSONArray();
                for (Object m : matchlistObject.getJSONArray("matches")) {
                    JSONObject match = (JSONObject) m;
                    gameIdArray.add(match.getString("gameId"));
                    matches.put(match);
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.println((Double.valueOf(i) / master.length()) * 100 + "% complete of " +  curr.length() + " with bronze " + division + " players");
                }
                i++;
            }
            System.out.println("Finished Bronze " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Iron " + division + " players...");
            JSONArray curr = (JSONArray)  handleExceptions(EndpointType.LEAGUE_QUEUE, "IRON", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONObject matchlistObject = (JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null);
                JSONArray matches = new JSONArray();
                for (Object m : matchlistObject.getJSONArray("matches")) {
                    JSONObject match = (JSONObject) m;
                    gameIdArray.add(match.getString("gameId"));
                    matches.put(match);
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.println((Double.valueOf(i) / curr.length()) * 100 + "% complete of " +  curr.length() + " with iron " + division + " players");
                }
                i++;
            }
            System.out.println("Finished Iron " + division + " players");
        }
        summonerJsonObject.put("summoners", playersJsonArray);
        writeJson(summonerJsonObject, "summoner.json");

        //Process Matches.json
        System.out.println("Processing matches.json");
        long size = gameIdArray.size();
        for (int i = 0; i < size; i++) {
           JSONObject curr = (JSONObject) handleExceptions(EndpointType.MATCH, gameIdArray.get(i), null);
           matchDetailList.put(curr);
           if (i % 10 == 0) {
               System.out.println((Double.valueOf(i) / size) * 100 + "% complete of " + size + " with matches");
           }
        }
        matchDetails.put("matches", matchDetailList);
        writeJson(matchDetails, "matches.json");
    }

    private static void writeJson(JSONObject toWrite, String filename) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(filename);
            writer.write(toWrite.toString(2));
            writer.flush();
            System.out.println("Completed " + filename + "!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
