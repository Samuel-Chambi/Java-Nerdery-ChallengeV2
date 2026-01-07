/* (C)2024 */
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mocks.CallCostObject;
import mocks.CallSummary;
import mocks.CardWinner;
import mocks.TotalSummary;

public class ChallengeStream {

    /**
     * One stack containing five numbered cards from 0-9 are given to both players. Calculate which hand has winning number.
     * The winning number is calculated by which hard produces the highest two-digit number.
     *
     * calculateWinningHand([2, 5, 2, 6, 9], [3, 7, 3, 1, 2]) ➞ true
     *  P1 can make the number 96
     *  P2 can make the number 73
     *  P1 win the round since 96 > 73
     *
     * The function must return which player hand is the winner and the two-digit number produced. The solution must contain streams.
     *
     * @param player1  hand, player2 hand
     */
    public CardWinner calculateWinningHand(List<Integer> player1, List<Integer> player2) {
        Integer player1Val = getMaxValue(player1);
        Integer player2Val = getMaxValue(player2);
        if(player1Val.compareTo(player2Val) == 0)
            return new CardWinner("TIE", player1Val);
        else if(player1Val.compareTo(player2Val) < 0)
            return new CardWinner("P2" , player2Val);
        else
            return new CardWinner("P1" , player1Val);
    }

    public static Integer getMaxValue(List<Integer> values){
        StringBuilder val = new StringBuilder();
        values.stream()
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .forEach(val::append);
        return Integer.parseInt(val.toString());
    }
    /**
     * Design a solution to calculate what to pay for a set of phone calls. The function must receive an
     * array of objects that will contain the identifier, type and duration attributes. For the type attribute,
     * the only valid values are: National, International and Local
     *
     * The criteria for calculating the cost of each call is as follows:
     *
     * International: first 3 minutes $ 7.56 -> $ 3.03 for each additional minute
     * National: first 3 minutes $ 1.20 -> $ 0.48 per additional minute
     * Local: $ 0.2 per minute.
     *
     * The function must return the total calls, the details of each call (the detail received + the cost of the call)
     * and the total to pay taking into account all calls. The solution must be done only using streams.
     *
     * @param {Call[]} calls - Call's information to be processed
     *
     * @returns {CallsResponse}  - Processed information
     */
    public TotalSummary calculateCost(List<CallCostObject> costObjectList) {
        // YOUR CODE HERE...
        /*
         * My first approach is to separates all callSummaries by type and in this form the cost of each call is calculated in a easier way.
         * The 'filterByType' function is for this.
         * So for the next, build the 'TotalSummary' object using streams on the 3 lists that are obtained in the filter.
        */
        List<CallSummary> internationals = filterByType(costObjectList , "International");
        List<CallSummary> nationals      = filterByType(costObjectList , "National");
        List<CallSummary> locals         = filterByType(costObjectList , "Local");

        TotalSummary totalSummary = new TotalSummary();
        totalSummary.setCalls(Stream.of(internationals , nationals , locals)
                .flatMap(List::stream)
                .collect(Collectors.toList()));
        totalSummary.setTotalCalls(Stream.of(internationals, nationals, locals)
                .mapToInt(List::size)
                .sum());
        totalSummary.setTotalCost(Stream.of(internationals, nationals, locals)
                .flatMap(List::stream)
                .mapToDouble(CallSummary::getTotalCost)
                .sum());
        return totalSummary;

    }
    public static List<CallSummary> filterByType(List<CallCostObject> calls, String type){
        double initCost;
        double additionalCost;
        int initDuration;
        if(type.equals("International")){initDuration = 3; initCost = 7.56; additionalCost = 3.03;}
        else if(type.equals("National")){initDuration = 3; initCost = 1.20; additionalCost = 0.48;}
        else                            {initDuration = 0; initCost = 0.00; additionalCost = 0.20;}
        return calls.stream()
                .filter(call -> call.getType().equals(type))
                .map(call -> new CallSummary(call,
                        (initCost * Integer.min(initDuration , call.getDuration())) + (additionalCost * (call.getDuration() - Integer.min(initDuration , call.getDuration())))))
                .collect(Collectors.toList());
    }
}
