package net.primomc.CoinToss.Messages;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
public class MsgWrapper
{
    public static void sendMessage( CommandSender sender, String key, String... values )
    {
        if ( sender instanceof Player )
        {
            sendMessage( (Player) sender, key, values );
        }
        else
        {
            sender.sendMessage( noPrefix( key, values ) );
        }
    }

    public static void sendMessagePrefix( CommandSender sender, String key, String... values )
    {
        if ( sender instanceof Player )
        {
            sendMessagePrefix( (Player) sender, key, values );
        }
        else
        {
            sender.sendMessage( withPrefix( key, values ) );
        }
    }

    public static void sendMessage( Player player, String key, String... values )
    {
        sendMessage( new Player[]{ player }, key, values );
    }

    public static void sendMessage( Player[] players, String key, String... values )
    {
        Messages.sendMessage( false, players, key, values );
    }

    public static void sendMessagePrefix( Player player, String key, String... values )
    {
        sendMessagePrefix( new Player[]{ player }, key, values );
    }

    public static void sendMessagePrefix( Player[] players, String key, String... values )
    {
        Messages.sendMessage( true, players, key, values );
    }

    public static String noPrefix( String key, String... values )
    {
        return Messages.getBasicMessage( false, key, values );
    }

    public static String str( String key, String... values )
    {
        return noPrefix( key, values );
    }

    public static String withPrefix( String key, String... values )
    {
        return Messages.getBasicMessage( true, key, values );
    }

    public static String yesno( boolean bool )
    {
        if ( bool )
        {
            return noPrefix( "stringyes" );
        }
        else
        {
            return noPrefix( "stringno" );
        }
    }
}
