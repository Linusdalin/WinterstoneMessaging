package campaigns;

import action.ActionInterface;
import action.GiveCoinAction;
import action.NotificationAction;
import core.PlayerInfo;
import remoteData.dataObjects.User;
import response.ResponseStat;
import rewards.Reward;
import rewards.RewardRepository;

import java.sql.Timestamp;


/************************************************************************'
 *
 *
 *
 */

public class LevelUpTuesdayReward extends AbstractCampaign implements CampaignInterface {

    // Campaign config data
    private static final String Name = "LevelUpTuesdayReward";
    private static final int CoolDown_Days = 5;            // Just once per game
    private int[] MessageIds = {2 ,

    };

    private static final int[] baseLevelUps = {
            0,200,200,200,250,300,350,450,600,750,900,
            1050,1200,1350,1500,1650,1800,1950,2100,2250,2400,2550,2700,2850,3000,3150,3300,3450,3600,3750,3900,4050,4200,4350,4500,4650,4800,4950,5100,5250,5400,5550,5700,5850,6000,6150,6300,6450,6600,6750,6900,7050,7200,7350,7500,7650,7800,7950,8100,8250,8400,8550,8700,8850,9000,9150,9300,9450,9600,9750,9900,10050,10200,10350,10500,10650,10800,10950,11100,11250,11400,11550,11700,11850,12000,12150,12300,12450,12600,12750,12900,13050,13200,13350,13500,13650,13800,13950,14100,20000,20000,20000};


    private Reward reward = null;
    private static final String RewardDay =   "onsdag";   // Swedish due to locale on test computer


    // Trigger specific config data
    private static final int Min_Level = 1;
    private static final int Min_Sessions = 0;

    public LevelUpTuesdayReward(int priority, CampaignState active){

        super(Name, priority, active);
        setCoolDown(CoolDown_Days);
        registerMessageIds( MessageIds );
    }


    /********************************************************************
     *
     *              Decide on the campaign
     *
     *
     *
     *
     * @param playerInfo             - the user to evaluate
     */


    public ActionInterface evaluate(PlayerInfo playerInfo, Timestamp executionTime, double responseFactor, ResponseStat response) {

        Timestamp executionDay = getDay(executionTime);
        User user = playerInfo.getUser();

        String wednesdayRestriction    = isSpecificDay(executionTime, false, RewardDay);

        if(wednesdayRestriction != null){

            System.out.println("    -- Campaign " + Name + " not firing. Wrong day.");
            return null;
        }

        if(playerInfo.getLevelUp() == 0){

            System.out.println("    -- Campaign " + Name + " not firing. The user has not levelled up. ( Still " + user.level +")" );
            return null;

        }

        int coins = getCoinsForLevel(user.level);

        System.out.println("    -- Campaign " + Name + " firing. The user levelled up ( To " + user.level +")" );
        return new NotificationAction("Congratulation. You reached level " + user.level + " yesterday and we have added " + coins + " coins to your account. Click here to continue to play!",
                user, executionTime, getPriority(), getTag(),  Name, 1, getState(), responseFactor)
                .attach(new GiveCoinAction(coins, user, executionTime, getPriority(), Name, 6, getState(), responseFactor)
                                .forceAction());


    }

    public int getCoinsForLevel(int level) {

        if(level < 97)
            return baseLevelUps[level - 1];

        int bonus = (int)(Math.min(10000 + Math.pow(1.038, (level - 102)) * 5000,200000));
        bonus = (bonus/100)*100;

        return bonus;
    }


    /*********************************************************************
     *
     *              Campaign timing restrictions
     *
     * @param executionTime     - time of execution
     * @return                  - messgage or null if ok.
     */

    public String testFailCalendarRestriction(Timestamp executionTime, boolean overrideTime) {

        String specificWeekDay = isSpecificDay(executionTime, false, RewardDay);

        if(specificWeekDay != null)
            return specificWeekDay;


        return isTooEarly(executionTime, overrideTime);

    }

    protected Reward decideReward(User user) {

        if(isHighSpender(user))
            return RewardRepository.OS2345High;

        if(isPaying(user))
            return RewardRepository.OS2345Paying;

        if(isFrequent(user))
            return RewardRepository.OS2345Frequent;

        return RewardRepository.OS2345Rest;
    }


    /*
    package com.slotamerica.level
{
	import com.slotamerica.common.DebugConsole;
	import com.slotamerica.common.SlotSignals;

	public class LevelController
	{
		private var xpLevels:Array = [0];
		private var bonusAmounts:Array = [0];
		private static const oldXPLevels:Array = [0,1000,6000,12000,20000,35000,55000,85000,125000,175000,235000,305000,385000,475000,575000,685000,805000,935000,1075000,1225000,1385000,1555000,1735000,1925000,2125000,2335000,2555000,2785000,3025000,3275000,3535000,3805000,4085000,4375000,4675000,4985000,5305000,5635000,5975000,6325000,6685000,7055000,7435000,7825000,8225000,8635000,9055000,9485000,9925000,10375000,10835000,11305000,11785000,12275000,12775000,13285000,13805000,14335000,14875000,15425000,15985000,16555000,17135000,17725000,18325000,18935000,19555000,20185000,20825000,21475000,22135000,22805000,23485000,24175000,24875000,25585000,26305000,27035000,27775000,28525000,29285000,30055000,30835000,31625000,32425000,33235000,34055000,34885000,35725000,36575000,37435000,38305000,39185000,40075000,40975000,41885000,42805000,43735000,44675000,60000000,80000000,100000000];
		private static const oldBonusAmounts:Array = [0,200,200,200,250,300,350,450,600,750,900,1050,1200,1350,1500,1650,1800,1950,2100,2250,2400,2550,2700,2850,3000,3150,3300,3450,3600,3750,3900,4050,4200,4350,4500,4650,4800,4950,5100,5250,5400,5550,5700,5850,6000,6150,6300,6450,6600,6750,6900,7050,7200,7350,7500,7650,7800,7950,8100,8250,8400,8550,8700,8850,9000,9150,9300,9450,9600,9750,9900,10050,10200,10350,10500,10650,10800,10950,11100,11250,11400,11550,11700,11850,12000,12150,12300,12450,12600,12750,12900,13050,13200,13350,13500,13650,13800,13950,14100,20000,20000,20000];

		private var previousXP:Number = 0;
		private var xpBalance:Number = 0;
		private var currentLevel:Number = 1;
		private var initialUpdate:Boolean = true;

		private var debugConsole:DebugConsole = DebugConsole.getInstance();

		public function LevelController() {
			for (var i:int = 0; i < 1500; i ++) {
				var level:Object = getLevelData(i);
				var requiredXP:Number = level.xp;
				xpLevels.push(requiredXP+previousXP);
				bonusAmounts.push(level.bonus);
				previousXP += requiredXP;
			}

			SlotSignals.updateXP.add(addXP);
		}

		private function getLevelData(level:int):Object {
			var nextXpRequired:Number;
			var bonus:Number;
			if (level >= 97 ) {
				nextXpRequired = Math.min(1000000 + (Math.pow(1.038, (level - 100)) * 100000), 100000000);
				nextXpRequired = parseInt((nextXpRequired / 1000).toFixed(0)) * 1000;
				bonus = Math.min(10000 + Math.pow(1.038, (level - 100)) * 5000,200000);
				bonus = parseInt((bonus / 100).toFixed(0)) * 100;
			}
			else {
				nextXpRequired = oldXPLevels[level + 1] - oldXPLevels[level];
				bonus = oldBonusAmounts[level + 1];
			}
			return { bonus: bonus, xp: nextXpRequired };
		}

		public function reset():void {
			xpBalance = 0;
			currentLevel = 1;
			initialUpdate = true;
		}

		public function addXP(xp:Number):void {
			xpBalance += xp;
			determineLevel(xpBalance);
		}

		public function geCurrenttLevel():Number {
			return currentLevel;
		}

		private function determineLevel(totalWager:Number, updateOnly:Boolean = false):void {
			var levelResult:LevelData = getLevel(totalWager);
			var level:Number = levelResult.getLevel();

			if (level > currentLevel) {
				debugConsole.debug("levelUp level:" + level + " bonus" + levelResult.getBonus());
				currentLevel = levelResult.getLevel();
				if ( !initialUpdate && !updateOnly ) {
					try {
						SlotSignals.levelUp.dispatch(level, levelResult.getBonus());
					} catch (e:Error) {
						DebugConsole.getInstance().debug(e.getStackTrace());
					}
				}
			}

			debugConsole.debug("current level: " + currentLevel);

			initialUpdate = false;

			var xpBalance:Number = totalWager - levelResult.getXpRequired();
			var percentDecimals:Number = xpBalance /(levelResult.getNextXpRequired()-levelResult.getXpRequired());

			SlotSignals.updateNextLevelBonus.dispatch(currentLevel, bonusAmounts[currentLevel], percentDecimals, totalWager, levelResult.getNextXpRequired());
		}

		private function getLevel(totalWager:Number):LevelData {
			var level:int = 0;
			var bonus:Number = 0;
			var nextXpRequired:Number = 0;
			var xpRequired:Number = 0;

			for (var i:int = 0; i < xpLevels.length; i++) {
				if (totalWager >= xpLevels[i]) {
					level++;
					if (level > currentLevel) {
						bonus += bonusAmounts[i];
					}
				}
			}

			xpRequired = xpLevels[level-1];
			nextXpRequired = xpLevels[level];

			return new LevelData(level, bonus, xpRequired, nextXpRequired);
		}
	}
}


     */



}
