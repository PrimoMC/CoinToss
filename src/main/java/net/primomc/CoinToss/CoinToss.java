package net.primomc.CoinToss;

import net.milkbowl.vault.economy.Economy;
import net.primomc.CoinToss.Messages.MessageHandler;
import net.primomc.CoinToss.Messages.MsgWrapper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
public class CoinToss extends JavaPlugin implements CommandExecutor, Listener
{

    private Economy economy;
    private LossLimitHandler lossLimit;
    private double tax;
    private double minbet;
    private double maxbet;
    private int expirationtime;
    private Random rand = new Random();
    private MessageHandler messageHandler;

    @Override
    public void onEnable()
    {
        this.getCommand( "cointoss" ).setExecutor( this );
        loadConfig();
        if ( !setupEconomy() )
        {
            getLogger().severe( "Vault not enabled. This plugin requires Vault to work. Disabling.." );
            this.getPluginLoader().disablePlugin( this );
        }
    }

    private void loadConfig()
    {
        saveDefaultConfig();
        initLossLimit();
        tax = getConfig().getDouble( "tax", 0.05 );
        minbet = getConfig().getDouble( "minbet", 100 );
        maxbet = getConfig().getDouble( "maxbet", 1000 );
        expirationtime = getConfig().getInt( "expirationtime", 30 );
        messageHandler = new MessageHandler( this );
    }

    private void initLossLimit()
    {
        boolean enabled = getConfig().getBoolean( "losslimit.enabled", true );
        double max = getConfig().getDouble( "losslimit.ratingmax", 50000 );
        double min = getConfig().getDouble( "losslimit.ratingmin", 10000 );
        int downrateMinutes = getConfig().getInt( "losslimit.downrate.minutes", 18 );
        double downrateAmount = getConfig().getDouble( "losslimit.downrate.amount", 1000 );
        if ( lossLimit != null )
        {
            lossLimit.reset();
        }
        this.lossLimit = new LossLimitHandler( this, enabled, max, min, downrateMinutes, downrateAmount );
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration( net.milkbowl.vault.economy.Economy.class );
        if ( economyProvider != null )
        {
            economy = economyProvider.getProvider();
        }
        return ( economy != null );
    }

    @Override
    public void onDisable()
    {

    }

    Map<UUID, Triple<UUID, Long, Double>> challenges = new HashMap<>();

    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
    {
        if ( command.getName().equalsIgnoreCase( "cointoss" ) && isPlayer( sender ) )
        {
            Player player = getPlayer( sender );
            if ( args.length < 1 )
            {
                MsgWrapper.sendMessage( player, "help" );
                return true;
            }
            if ( args[0].equalsIgnoreCase( "challenge" ) )
            {
                ChallengeCommand( player, Arrays.copyOfRange( args, 1, args.length ) );
            }
            if ( args[0].equalsIgnoreCase( "accept" ) )
            {
                AcceptCommand( player, Arrays.copyOfRange( args, 1, args.length ) );
            }
            if ( args[0].equalsIgnoreCase( "deny" ) )
            {
                DenyCommand( player, Arrays.copyOfRange( args, 1, args.length ) );
            }
            return true;
        }
        return false;
    }

    private void DenyCommand( Player player, String[] args )
    {
        Map<UUID, Triple<UUID, Long, Double>> challengeCopy = this.challenges;
        for ( UUID key : challengeCopy.keySet() )
        {
            Triple<UUID, Long, Double> challenge = challengeCopy.get( key );
            if ( isExpired( challenge.getSecond() ) )
            {
                this.challenges.remove( key );
            }
        }

        if ( !this.challenges.containsKey( player.getUniqueId() ) )
        {
            MsgWrapper.sendMessagePrefix( player, "notchallenged" );
            return;
        }

        Triple<UUID, Long, Double> challenge = this.challenges.get( player.getUniqueId() );

        Player otherPlayer = Bukkit.getPlayer( challenge.getFirst() );
        MsgWrapper.sendMessagePrefix( otherPlayer, "challengedeniedother", player.getName() );
        MsgWrapper.sendMessagePrefix( player, "challengedenied", otherPlayer.getName() );
        this.challenges.remove( player.getUniqueId() );
    }

    private void AcceptCommand( Player player, String[] args )
    {
        Map<UUID, Triple<UUID, Long, Double>> challengeCopy = this.challenges;
        for ( UUID key : challengeCopy.keySet() )
        {
            Triple<UUID, Long, Double> challenge = challengeCopy.get( key );
            if ( isExpired( challenge.getSecond() ) )
            {
                this.challenges.remove( key );
            }
        }
        if ( args.length < 1 || !args[0].trim().toLowerCase().matches( "heads|tails" ) )
        {
            MsgWrapper.sendMessagePrefix( player, "nocoinside" );
            return;
        }
        if ( !challenges.containsKey( player.getUniqueId() ) )
        {
            MsgWrapper.sendMessagePrefix( player, "notchallenged" );
            return;
        }
        Triple<UUID, Long, Double> challenge = challenges.get( player.getUniqueId() );
        if ( isExpired( challenge.getSecond() ) )
        {
            MsgWrapper.sendMessagePrefix( player, "challengeexpired" );
            return;
        }
        if ( challenge.getThird() > economy.getBalance( player ) )
        {
            MsgWrapper.sendMessagePrefix( player, "notenoughmoney" );
            return;
        }

        if ( !lossLimit.canPlayerPlay( player.getUniqueId() ) )
        {
            MsgWrapper.sendMessagePrefix( player, "overlimit" );
            this.challenges.remove( player.getUniqueId() );
            return;
        }
        challenges.remove( player.getUniqueId() );
        startToss( player, Bukkit.getPlayer( challenge.getFirst() ), challenge.getThird(), args[0].trim().equalsIgnoreCase( "heads" ) );
    }

    private boolean isExpired( Long time )
    {
        return System.currentTimeMillis() - time > expirationtime * 1000;
    }

    private void ChallengeCommand( Player player, String[] args )
    {
        if ( args.length < 2 )
        {
            MsgWrapper.sendMessagePrefix( player, "notenougharguments", "/cointoss challenge <player> <bet>" );
            return;
        }
        Player otherPlayer = Bukkit.getServer().getPlayer( args[0] );
        if ( otherPlayer == null )
        {
            MsgWrapper.sendMessagePrefix( player, "playernotfound", args[0] );
            return;
        }
        if ( otherPlayer.equals( player ) )
        {

            MsgWrapper.sendMessagePrefix( player, "cantchallengeyourself" );
            return;
        }
        if ( !TypeUtil.isDouble( args[1] ) )
        {
            MsgWrapper.sendMessagePrefix( player, "notanumber", "<bet>" );
            return;
        }
        double bet = TypeUtil.getDouble( args[1] );
        if ( bet > maxbet )
        {
            MsgWrapper.sendMessagePrefix( player, "bettoohigh", maxbet + "" );
            return;
        }
        if ( bet < minbet )
        {
            MsgWrapper.sendMessagePrefix( player, "bettoolow", minbet + "" );
            return;
        }
        if ( bet > economy.getBalance( player ) )
        {
            MsgWrapper.sendMessagePrefix( player, "notenoughmoney" );
            return;
        }
        if ( challenges.containsKey( otherPlayer.getUniqueId() ) )
        {
            MsgWrapper.sendMessagePrefix( player, "alreadychallenged" );
            return;
        }
        if ( !lossLimit.canPlayerPlay( player.getUniqueId() ) )
        {
            MsgWrapper.sendMessagePrefix( player, "overlimit" );
        }
        challenges.put( otherPlayer.getUniqueId(), new Triple<>( player.getUniqueId(), System.currentTimeMillis(), bet ) );
        MsgWrapper.sendMessagePrefix( player, "youchallenged", otherPlayer.getName(), bet + "" );
        MsgWrapper.sendMessagePrefix( otherPlayer, "challenged", player.getName(), bet + "" );
    }

    public boolean isPlayer( CommandSender sender )
    {
        return sender instanceof Player;
    }

    public Player getPlayer( CommandSender sender )
    {
        return (Player) sender;
    }

    public double getTax()
    {
        return tax;
    }

    public double getMinbet()
    {
        return minbet;
    }

    public double getMaxbet()
    {
        return maxbet;
    }

    public int getExpirationtime()
    {
        return expirationtime;
    }

    public void startToss( Player player, Player otherPlayer, double bet, boolean playerSide )
    {
        this.challenges.remove( player.getUniqueId() );
        final double preTax = bet;
        economy.withdrawPlayer( player, bet );
        economy.withdrawPlayer( otherPlayer, bet );
        MsgWrapper.sendMessagePrefix( new Player[]{ player, otherPlayer }, "moneyremoved", bet + "" );
        String sideName = playerSide ? "heads" : "tails";
        String otherSide = playerSide ? "tails" : "heads";
        double taxed = bet * tax;
        MsgWrapper.sendMessagePrefix( player, "cointossinformation", sideName, bet + "", taxed + "" );
        MsgWrapper.sendMessagePrefix( otherPlayer, "cointossinformation", otherSide, bet + "", taxed + "" );
        MsgWrapper.sendMessagePrefix( new Player[]{ player, otherPlayer }, "startcointoss" );
        bet -= taxed;
        boolean side = rand.nextBoolean();
        final String winningSideName = side ? "heads" : "tails";
        final Player winner;
        final Player loser;
        if ( side == playerSide )
        {
            winner = player;
            loser = otherPlayer;
        }
        else
        {
            winner = otherPlayer;
            loser = player;
        }
        final double finalBet = bet;
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                MsgWrapper.sendMessagePrefix( new Player[]{ winner, loser }, "coinside", winningSideName );
                MsgWrapper.sendMessagePrefix( winner, "youwon" );
                MsgWrapper.sendMessagePrefix( winner, "moneyadded", ( finalBet * 2 ) + "" );
                MsgWrapper.sendMessagePrefix( loser, "youlost" );
                lossLimit.addRating( loser.getUniqueId(), preTax );
                economy.depositPlayer( winner, finalBet * 2 );
            }
        }.runTaskLater( this, 20 );
    }
}
