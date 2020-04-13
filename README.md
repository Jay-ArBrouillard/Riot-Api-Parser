# riot_api_parser
Queries Riot API for all currently ranked summoners. For each summoner grab the matches they have recently played and get the details for each of those matches.
**NOTE**: Due to Riot API rate limitations this program has implemented a delay time between consecutive api call, which means the runtime is in the scope of hours with a default `DELAY` of 2000ms. If you have a production api key then definitely change the `DELAY` variable from 2000ms to 500ms or perhaps 0 and you will see drastically faster runtimes. **NOTE**  
https://developer.riotgames.com/docs/portal#web-apis_rate-limiting

Running this program will create two .json files in the base directory of the project namely, "Summoner.json" and "Matches.json".
I recommend checking out Riot's API to understand the format of these json files fully.
https://developer.riotgames.com/apis

Api endpoints used:  
/lol/league/v4/challengerleagues/by-queue/{queue}  
/lol/league/v4/entries/{queue}/{tier}/{division}  
/lol/league/v4/grandmasterleagues/by-queue/{queue}  
/lol/league/v4/masterleagues/by-queue/{queue}  
/lol/summoner/v4/summoners/{encryptedSummonerId}  
/lol/match/v4/matchlists/by-account/{encryptedAccountId  
/lol/match/v4/matches/{matchId}

Example Format Summoner.json:
```json
{
    "id": "14854",
    "accountId": "24552",
    "name": "BestSummonerNA",
    "profileIconId": 4368,
    "revisionDate": 1586655793000,
    "summonerLevel": 75,
    "matches:" [
        {
            "gameId": "25434",
            "champion": "119",
            "timestamp": "1586654292084",
            "role": "SOLO",
            "lane": "BOTTOM"
        },
        {
            "gameId": 3341365,
            "champion": 164,
            "timestamp": 1585127736810,
            "role": "DUO",
            "lane": "TOP"
        },
        ...
    ]
}
```

Example Format Matches.json:
```json
{
    "gameId": 3362621247,
    "platformId": "NA1",
    "gameCreation": 1586142318993,
    "gameDuration": 1691,
    "queueId": 420,
    "mapId": 11,
    "seasonId": 13,
    "gameVersion": "10.7.314.9802",
    "gameMode": "CLASSIC",
    "gameType": "MATCHED_GAME",
    "teams": [
        {
            "teamId": 100,
            "win": "Fail",
            "firstBlood": true,
            "firstTower": false,
            "firstInhibitor": false,
            "firstBaron": false,
            "firstDragon": false,
            "firstRiftHerald": false,
            "towerKills": 2,
            "inhibitorKills": 0,
            "baronKills": 0,
            "dragonKills": 0,
            "vilemawKills": 0,
            "riftHeraldKills": 0,
            "dominionVictoryScore": 0,
            "bans": [
                {
                    "championId": 20,
                    "pickTurn": 1
                },
                {
                    "championId": 9,
                    "pickTurn": 2
                },
                {
                    "championId": 235,
                    "pickTurn": 3
                },
                {
                    "championId": 58,
                    "pickTurn": 4
                },
                {
                    "championId": 142,
                    "pickTurn": 5
                }
            ]
        },
        {
            "teamId": 200,
            "win": "Win",
            "firstBlood": false,
            "firstTower": true,
            "firstInhibitor": true,
            "firstBaron": true,
            "firstDragon": true,
            "firstRiftHerald": true,
            "towerKills": 9,
            "inhibitorKills": 2,
            "baronKills": 1,
            "dragonKills": 4,
            "vilemawKills": 0,
            "riftHeraldKills": 2,
            "dominionVictoryScore": 0,
            "bans": [
                {
                    "championId": 80,
                    "pickTurn": 6
                },
                {
                    "championId": 350,
                    "pickTurn": 7
                },
                {
                    "championId": 64,
                    "pickTurn": 8
                },
                {
                    "championId": 875,
                    "pickTurn": 9
                },
                {
                    "championId": 122,
                    "pickTurn": 10
                }
            ]
        }
    ],
    "participants": [
        {
            "participantId": 1,
            "teamId": 100,
            "championId": 92,
            "spell1Id": 12,
            "spell2Id": 4,
            "stats": {
                "participantId": 1,
                "win": false,
                "item0": 3812,
                "item1": 3035,
                "item2": 3071,
                "item3": 3123,
                "item4": 0,
                "item5": 3047,
                "item6": 3340,
                "kills": 4,
                "deaths": 7,
                "assists": 4,
                "largestKillingSpree": 2,
                "largestMultiKill": 1,
                "killingSprees": 2,
                "longestTimeSpentLiving": 334,
                "doubleKills": 0,
                "tripleKills": 0,
                "quadraKills": 0,
                "pentaKills": 0,
                "unrealKills": 0,
                "totalDamageDealt": 130088,
                "magicDamageDealt": 0,
                "physicalDamageDealt": 116099,
                "trueDamageDealt": 13989,
                "largestCriticalStrike": 0,
                "totalDamageDealtToChampions": 11079,
                "magicDamageDealtToChampions": 0,
                "physicalDamageDealtToChampions": 10943,
                "trueDamageDealtToChampions": 136,
                "totalHeal": 3292,
                "totalUnitsHealed": 1,
                "damageSelfMitigated": 23227,
                "damageDealtToObjectives": 0,
                "damageDealtToTurrets": 0,
                "visionScore": 28,
                "timeCCingOthers": 28,
                "totalDamageTaken": 21283,
                "magicalDamageTaken": 4908,
                "physicalDamageTaken": 14667,
                "trueDamageTaken": 1708,
                "goldEarned": 10721,
                "goldSpent": 10450,
                "turretKills": 0,
                "inhibitorKills": 0,
                "totalMinionsKilled": 179,
                "neutralMinionsKilled": 16,
                "neutralMinionsKilledTeamJungle": 12,
                "neutralMinionsKilledEnemyJungle": 0,
                "totalTimeCrowdControlDealt": 156,
                "champLevel": 14,
                "visionWardsBoughtInGame": 2,
                "sightWardsBoughtInGame": 0,
                "wardsPlaced": 11,
                "wardsKilled": 2,
                "firstBloodKill": false,
                "firstBloodAssist": false,
                "firstTowerKill": false,
                "firstTowerAssist": false,
                "firstInhibitorKill": false,
                "firstInhibitorAssist": false,
                "combatPlayerScore": 0,
                "objectivePlayerScore": 0,
                "totalPlayerScore": 0,
                "totalScoreRank": 0,
                "playerScore0": 0,
                "playerScore1": 0,
                "playerScore2": 0,
                "playerScore3": 0,
                "playerScore4": 0,
                "playerScore5": 0,
                "playerScore6": 0,
                "playerScore7": 0,
                "playerScore8": 0,
                "playerScore9": 0,
                "perk0": 8010,
                "perk0Var1": 411,
                "perk0Var2": 0,
                "perk0Var3": 0,
                "perk1": 9111,
                "perk1Var1": 522,
                "perk1Var2": 160,
                "perk1Var3": 0,
                "perk2": 9104,
                "perk2Var1": 19,
                "perk2Var2": 30,
                "perk2Var3": 0,
                "perk3": 8014,
                "perk3Var1": 235,
                "perk3Var2": 0,
                "perk3Var3": 0,
                "perk4": 8345,
                "perk4Var1": 3,
                "perk4Var2": 0,
                "perk4Var3": 0,
                "perk5": 8352,
                "perk5Var1": 202,
                "perk5Var2": 1294,
                "perk5Var3": 0,
                "perkPrimaryStyle": 8000,
                "perkSubStyle": 8300,
                "statPerk0": 5007,
                "statPerk1": 5008,
                "statPerk2": 5002
            },
            "timeline": {
                "participantId": 1,
                "creepsPerMinDeltas": {
                    "10-20": 6.7,
                    "0-10": 5.5
                },
                "xpPerMinDeltas": {
                    "10-20": 473.59999999999997,
                    "0-10": 390.7
                },
                "goldPerMinDeltas": {
                    "10-20": 464.5,
                    "0-10": 232.4
                },
                "csDiffPerMinDeltas": {
                    "10-20": -0.9999999999999996,
                    "0-10": -1.7000000000000002
                },
                "xpDiffPerMinDeltas": {
                    "10-20": -63.60000000000008,
                    "0-10": -75.30000000000001
                },
                "damageTakenPerMinDeltas": {
                    "10-20": 868.8,
                    "0-10": 511
                },
                "damageTakenDiffPerMinDeltas": {
                    "10-20": 177.5,
                    "0-10": 26.90000000000002
                },
                "role": "SOLO",
                "lane": "TOP"
            }
        },
        ... 
    ],
    "participantIdentities": [
        {
            "participantId": 1,
            "player": {
                "platformId": "NA1",
                "accountId": "2H2JumQmjpDNePJn1jnNPF-WxitY6FddwoXIPM2GSpmu8d8",
                "summonerName": "Ssumdayday",
                "summonerId": "5cn25D2DX5kEwHTyBTOY4-2iawFfR8jNF5ygkolRUwJl18s",
                "currentPlatformId": "NA1",
                "currentAccountId": "2H2JumQmjpDNePJn1jnNPF-WxitY6FddwoXIPM2GSpmu8d8",
                "matchHistoryUri": "/v1/stats/player_history/NA1/236691073",
                "profileIcon": 3229
            }
        },
        {
            "participantId": 2,
            "player": {
                "platformId": "NA1",
                "accountId": "Y3nQ28mE-Y0Kefb5KeEBUFjo_ZAxOcTIDeSGgU3Ia4MNlmcjAnSWOm7I",
                "summonerName": "LOSERS Q ONLY",
                "summonerId": "zZ3PUP98c7zkxmYoHCyX7GowZTgJd5K2hLbfOo8oWovM8bFL",
                "currentPlatformId": "NA1",
                "currentAccountId": "Y3nQ28mE-Y0Kefb5KeEBUFjo_ZAxOcTIDeSGgU3Ia4MNlmcjAnSWOm7I",
                "matchHistoryUri": "/v1/stats/player_history/NA1/2374456137321344",
                "profileIcon": 29
            }
        },
        ...
    ]
}
```
