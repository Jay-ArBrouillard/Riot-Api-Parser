import endpoints.EndpointType;
import endpoints.Endpoints;
import io.github.cdimascio.dotenv.Dotenv;
import kong.unirest.*;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    private static JSONObject summonerJsonObject;
    private static JSONArray playersJsonArray;
    private static JSONObject matchDetails;
    private static JSONArray matchlistArray;
    private static Endpoints endpoints;

    public static void main(String[] args) throws InterruptedException, IOException {
        matchDetails = new JSONObject(); //Contains a list of matches
        summonerJsonObject = new JSONObject(); //Contains a list of matches
        playersJsonArray = new JSONArray();
        matchlistArray = new JSONArray();
        Dotenv dotenv = Dotenv.load();
        endpoints = new Endpoints(dotenv.get("RIOT_API_TOKEN"));
        collectSummoners();

        //Reading Summoner.js and produces Matchlist.json
        String jsonData = readFile("summoner.json");
        JSONArray jarr = new JSONArray(new JSONObject(jsonData).getJSONArray("summoners").toString());
        for(int i = 0; i < jarr.length(); i++) {
            JSONArray mArr = new JSONArray(((JSONObject) jarr.get(i)).getJSONArray("matches").toString());
            for (int j = 0; j < mArr.length(); j++) {
                matchlistArray.put((JSONObject) handleExceptions(EndpointType.MATCH, ((JSONObject)mArr.get(j)).getString("gameId"), null));
            }
            System.out.printf("%d: %.4f%% complete of %d total\n", i, (Double.valueOf(i) / jarr.length()) * 100, jarr.length());
        }
        matchDetails.put("matchList", matchlistArray);
        writeJson(matchDetails, "matchlist.json");
    }

    public static String readFile(String filename) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return result;
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

            if (response.getStatus() == 200) {
                json = response.getBody().getObject();
                arr = response.getBody().getArray();
            }
            else { //ERROR
                 if (response.getStatus() == 429 && response.getHeaders().containsKey("Retry-After")) { //Rate limit exceeded
                     String retry = response.getHeaders().getFirst("Retry-After");
                     if (!retry.isEmpty()) {
                         System.out.println("Rate limit exceeded sleeping " + retry + " seconds(s)...");
                         Thread.sleep(Integer.parseInt(retry)  * 1000);
                     } else {
                         System.out.println("Rate limit exceeded sleeping 1 minute...");
                         Thread.sleep(60000); //Sleep a minute
                     }
                 }
                 else {
                     System.out.println(response.getStatusText() + "(" + response.getStatus() + ") sleeping 20 seconds");
                     Thread.sleep(20000); //Sleep 20
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
            JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
            JSONArray matches = new JSONArray();
            for (int j = 0; j < matchlistArray.length(); j++) {
                JSONObject match = (JSONObject) matchlistArray.get(j);
                matches.put(match);
                if (j == 2) break; //only save the first 3 games
            }
            curr.put("matches", matches);
            playersJsonArray.put(curr);
            if (i % 10 == 0) {
                System.out.printf("Challenger Players: %.2f%% complete of %d total\n", (Double.valueOf(i) / challenger.length()) * 100, challenger.length());
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
            JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
            JSONArray matches = new JSONArray();
            for (int j = 0; j < matchlistArray.length(); j++) {
                JSONObject match = (JSONObject) matchlistArray.get(j);
                matches.put(match);
                if (j == 2) break; //only save the first 3 games
            }
            curr.put("matches", matches);
            playersJsonArray.put(curr);
            if (i % 10 == 0) {
                System.out.printf("Grandmaster Players: %.2f%% complete of %d total\n", (Double.valueOf(i) / grandmaster.length()) * 100, grandmaster.length());
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
            JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
            JSONArray matches = new JSONArray();
            for (int j = 0; j < matchlistArray.length(); j++) {
                JSONObject match = (JSONObject) matchlistArray.get(j);
                matches.put(match);
                if (j == 2) break; //only save the first 3 games
            }
            curr.put("matches", matches);
            playersJsonArray.put(curr);
            if (i % 10 == 0) {
                System.out.printf("Master Players: %.2f%% complete of %d total\n", (Double.valueOf(i) / master.length()) * 100, master.length());
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
                JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
                JSONArray matches = new JSONArray();
                for (int j = 0; j < matchlistArray.length(); j++) {
                    JSONObject match = (JSONObject) matchlistArray.get(j);
                    matches.put(match);
                    if (j == 2) break; //only save the first 3 games
                }
                summoner.put("matches", matches);
                playersJsonArray.put(summoner);
                if (i % 10 == 0) {
                    System.out.printf("Diamond %s Players: %.2f%% complete of %d total\n", division, (Double.valueOf(i) / curr.length()) * 100, curr.length());
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
                JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
                JSONArray matches = new JSONArray();
                for (int j = 0; j < matchlistArray.length(); j++) {
                    JSONObject match = (JSONObject) matchlistArray.get(j);
                    matches.put(match);
                    if (j == 2) break; //only save the first 3 games
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.printf("Platinum %s Players: %.2f%% complete of %d total\n", division, (Double.valueOf(i) / curr.length()) * 100, curr.length());
                }
                i++;
            }
            System.out.println("Finished Platinum " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Gold " + division + " players...");
            JSONArray curr = (JSONArray) handleExceptions(EndpointType.LEAGUE_QUEUE, "GOLD", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
                JSONArray matches = new JSONArray();
                for (int j = 0; j < matchlistArray.length(); j++) {
                    JSONObject match = (JSONObject) matchlistArray.get(j);
                    matches.put(match);
                    if (j == 2) break; //only save the first 3 games
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.printf("Gold %s Players: %.2f%% complete of %d total\n", division, (Double.valueOf(i) / curr.length()) * 100, curr.length());
                }
                i++;
            }
            System.out.println("Finished Gold " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Silver " + division + " players...");
            JSONArray curr = (JSONArray) handleExceptions(EndpointType.LEAGUE_QUEUE, "SILVER", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
                JSONArray matches = new JSONArray();
                for (int j = 0; j < matchlistArray.length(); j++) {
                    JSONObject match = (JSONObject) matchlistArray.get(j);
                    matches.put(match);
                    if (j == 2) break; //only save the first 3 games
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.printf("Silver %s Players: %.2f%% complete of %d total\n", division, (Double.valueOf(i) / curr.length()) * 100, curr.length());
                }
                i++;
            }
            System.out.println("Finished Silver " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Bronze " + division + " players...");
            JSONArray curr = (JSONArray) handleExceptions(EndpointType.LEAGUE_QUEUE, "BRONZE", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
                JSONArray matches = new JSONArray();
                for (int j = 0; j < matchlistArray.length(); j++) {
                    JSONObject match = (JSONObject) matchlistArray.get(j);
                    matches.put(match);
                    if (j == 2) break; //only save the first 3 games
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.printf("Bronze %s Players: %.2f%% complete of %d total\n", division, (Double.valueOf(i) / curr.length()) * 100, curr.length());
                }
                i++;
            }
            System.out.println("Finished Bronze " + division + " players");
        }

        for (String division : divisions) {
            System.out.println("Processing Iron " + division + " players...");
            JSONArray curr = (JSONArray) handleExceptions(EndpointType.LEAGUE_QUEUE, "IRON", division);
            int i = 0;
            for (Object o : curr) {
                JSONObject summoner = ((JSONObject) o);
                String summonerId = summoner.getString("summonerId");
                JSONObject summonerObject = (JSONObject) handleExceptions(EndpointType.SUMMONER, summonerId, null);
                String accountId = summonerObject.getString("accountId");
                summoner.put("accountId", accountId);
                summoner.put("summonerLevel", summonerObject.getString("summonerLevel"));
                JSONArray matchlistArray = ((JSONObject) handleExceptions(EndpointType.MATCHLIST, accountId, null)).getJSONArray("matches");
                JSONArray matches = new JSONArray();
                for (int j = 0; j < matchlistArray.length(); j++) {
                    JSONObject match = (JSONObject) matchlistArray.get(j);
                    matches.put(match);
                    if (j == 2) break; //only save the first 3 games
                }
                summoner.put("matches", matches);
                playersJsonArray.put((JSONObject) o);
                if (i % 10 == 0) {
                    System.out.printf("Iron %s Players: %.2f%% complete of %d total\n", division, (Double.valueOf(i) / curr.length()) * 100, curr.length());
                }
                i++;
            }
            System.out.println("Finished Iron " + division + " players");
        }
        summonerJsonObject.put("summoners", playersJsonArray);
        writeJson(summonerJsonObject, "summoner.json");
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
