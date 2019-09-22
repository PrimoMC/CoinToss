package net.primomc.CoinToss.Messages;

import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright 2015 Luuk Jacobs
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Messages
{
    private static Map<String, String> messages = new HashMap<>();

    private static String msg( boolean hasPrefix, String modifier, String... values )
    {
        String message;
        if ( !messages.containsKey( modifier ) )
        {
            return ChatColor.RED + "String doesn't exist in config.";
        }
        else
        {
            message = messages.get( modifier );
        }
        String prefix = "";
        if ( hasPrefix && messages.containsKey( "prefix" ) )
        {
            prefix = messages.get( "prefix" );
        }
        int i = 0;
        for ( String value : values )
        {
            message = message.replaceAll( "\\{" + i + "\\}", value );
            i++;
        }
        message = prefix + message;
        message = ChatColor.translateAlternateColorCodes( '&', message );
        return message;
    }

    private static String msg( String message )
    {
        message = ChatColor.translateAlternateColorCodes( '&', message );
        return message;
    }

    public static BaseComponent[] jsonReplacer( String message )
    {
        message = message.replaceAll( "\\n", "||||" );
        ComponentBuilder jchat = new ComponentBuilder( "" );
        String regex = "(?i)\\[(hover-text|click-url|click-command|hover-command|hover-url)=(.+)\\](.+)\\[/\\1\\]";
        Pattern pattern = Pattern.compile( regex );
        Matcher matcher = pattern.matcher( message );
        List<String> bbcodes = new ArrayList<>();
        while ( matcher.find() )
        {
            bbcodes.add( matcher.group() );
        }
        if ( bbcodes.size() > 0 )
        {
            String remainder = message;
            for ( String bbcode : bbcodes )
            {
                String[] split = remainder.split( Pattern.quote( bbcode ), 2 );
                jchat.append( split[0] );
                if ( split.length == 2 )
                {
                    remainder = split[1];
                }
                String param = bbcode.replaceFirst( regex, "$2" ).replaceAll( "\\|\\|\\|\\|", "\n" );
                String text = bbcode.replaceFirst( regex, "$3" ).replaceAll( "\\|\\|\\|\\|", "\n" );
                jchat.append( text );
                if ( bbcode.toLowerCase().contains( "[click-command=" ) )
                {
                    jchat.event( new ClickEvent( ClickEvent.Action.RUN_COMMAND, param ) );
                }
                else if ( bbcode.toLowerCase().contains( "[click-url=" ) )
                {
                    jchat.event( new ClickEvent( ClickEvent.Action.OPEN_URL, param ) );
                }
                else if ( bbcode.toLowerCase().contains( "[hover-text=" ) )
                {
                    jchat.event( new HoverEvent( HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText( param ) ) );
                }
                else if ( bbcode.toLowerCase().contains( "[hover-command=" ) )
                {
                    String[] params = param.split( "====" );
                    if ( params.length == 2 )
                    {
                        jchat.event( new HoverEvent( HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText( params[0] ) ) ).event( new ClickEvent( ClickEvent.Action.RUN_COMMAND, params[1] ) );
                    }
                }
                else if ( bbcode.toLowerCase().contains( "[hover-url=" ) )
                {
                    String[] params = param.split( "====" );
                    if ( params.length == 2 )
                    {
                        jchat.event( new HoverEvent( HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText( params[0] ) ) ).event( new ClickEvent( ClickEvent.Action.OPEN_URL, params[1] ) );
                    }
                }
            }
            jchat.append( remainder );
        }
        else
        {
            jchat = jchat.append( message.replaceAll( "\\|\\|\\|\\|", "\n" ) );
        }
        return jchat.create();
    }

    public static String getMessageJson( String key, String... values )
    {
        return ComponentSerializer.toString( jsonReplacer( msg( false, key, values ) ) );
    }

    public static String getBasicMessage( String message )
    {
        return msg( message );
    }

    public static String getBasicMessage( boolean hasPrefix, String key, String... values )
    {
        return msg( hasPrefix, key, values );
    }

    public static void sendMessage( Player[] players, String key, String... values )
    {
        BaseComponent[] jchat = jsonReplacer( msg( true, key, values ) );
        for ( Player player : players )
        {
            player.spigot().sendMessage( jchat );
        }
    }

    public static void sendMessage( boolean hasPrefix, Player[] players, String key, String... values )
    {
        BaseComponent[] jchat = jsonReplacer( msg( hasPrefix, key, values ) );
        for ( Player player : players )
        {
            player.spigot().sendMessage( jchat );

        }
    }

    public static void sendMessage( Player player, String key, String[] values )
    {
        sendMessage( new Player[]{ player }, key, values );
    }

    public static void sendMessage( CommandSender sender, String key, String[] values )
    {
        if ( sender instanceof Player )
        {
            sendMessage( new Player[]{ (Player) sender }, key, values );
        }
        else
        {
            sender.sendMessage( msg( true, key, values ) );
        }
    }

    public static void setMessages( Map<String, String> messages )
    {
        Messages.messages = messages;
    }
}
