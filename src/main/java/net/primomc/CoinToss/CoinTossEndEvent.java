package net.primomc.CoinToss;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/*
 * Copyright 2016 Luuk Jacobs

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CoinTossEndEvent extends Event
{
    private final Player winner;
    private final Player loser;
    private final double tax;
    private final double bet;
    private static final HandlerList handlers = new HandlerList();
    public CoinTossEndEvent( Player winner, Player loser, double tax, double bet )
    {
        this.winner = winner;
        this.loser = loser;
        this.tax = tax;
        this.bet = bet;
    }

    /**
     * The amount of money that was taken by tax per person. (so to get total tax you have to multiply by 2)
     * @return tax
     */
    public double getTax()
    {
        return tax;
    }

    /**
     * The amount of money that was spent per person. (So to get total bet you have to multiply by 2)
     * @return tax
     */
    public double getBet()
    {
        return bet;
    }

    /**
     * The loser of the coin toss.
     * @return loser
     */
    public Player getLoser()
    {
        return loser;
    }

    /**
     * The winner of the coin toss.
     * @return winner
     */
    public Player getWinner()
    {
        return winner;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
}
