package net.primomc.CoinToss;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/*
 * Copyright 2015 Luuk Jacobs

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
public class LossLimitHandler
{
    private Plugin plugin;
    private final boolean enabled;
    private final double max;
    private final double min;
    private final int downrateMinutes;
    private final double downrateAmount;
    Map<UUID, Double> playerRatings = new HashMap<UUID, Double>();
    Set<UUID> playersOnCooldown = new HashSet<UUID>();
    private BukkitTask task;

    public LossLimitHandler( Plugin plugin, boolean enabled, double max, double min, int downrateMinutes, double downrateAmount )
    {
        this.plugin = plugin;
        this.enabled = enabled;
        this.max = max;
        this.min = min;
        this.downrateMinutes = downrateMinutes;
        this.downrateAmount = downrateAmount;
        startTimer();
    }

    private void startTimer()
    {
        task = new BukkitRunnable()
        {
            public void run()
            {
                if(!enabled)
                {
                    return;
                }
                for ( UUID player : playerRatings.keySet() )
                {
                    lowerRating( player );
                    maybeRemoveCooldown( player );
                }
            }
        }.runTaskTimer( plugin, 0, downrateMinutes * 60 * 20 );
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void addRating( UUID player, double amount )
    {
        if ( playerRatings.containsKey( player ) )
        {
            double rating = playerRatings.get( player ) + amount;
            playerRatings.put( player, rating );
            if ( rating >= max )
            {
                setOnCooldown( player );
            }
        }
        else
        {
            playerRatings.put( player, amount );
        }
    }

    public void lowerRating( UUID player )
    {
        lowerRating( player, downrateAmount );
    }

    public void lowerRating( UUID player, double amount )
    {
        if ( playerRatings.containsKey( player ) )
        {
            double rating = playerRatings.get( player ) - amount;
            if(rating < 0)
            {
                playerRatings.remove( player );
            }
            playerRatings.put( player, rating );
        }
    }

    public void maybeRemoveCooldown( UUID player )
    {
        if ( !playerRatings.containsKey( player ) )
        {
            playersOnCooldown.remove( player );
        }
        double rating = playerRatings.get( player );
        boolean onCooldown = playersOnCooldown.contains( player );
        if ( !onCooldown )
        {
            return;
        }
        if ( rating < min )
        {
            playersOnCooldown.remove( player );
        }
    }

    public void setOnCooldown( UUID player )
    {
        playersOnCooldown.add( player );
    }

    public boolean canPlayerPlay( UUID player )
    {
        if(!enabled)
        {
            return true;
        }
        if ( !playerRatings.containsKey( player ) )
        {
            return true;
        }
        double rating = playerRatings.get( player );
        if ( rating >= max )
        {
            return false;
        }
        boolean onCooldown = playersOnCooldown.contains( player );
        if ( onCooldown && rating >= min )
        {
            return false;
        }
        return true;
    }

    public void reset()
    {
        task.cancel();
    }
}
