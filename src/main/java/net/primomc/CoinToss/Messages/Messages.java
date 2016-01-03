package net.primomc.CoinToss.Messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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

    public static JChat jsonReplacer( String message )
    {
        message = message.replaceAll( "\\n", "||||" );
        JChat jchat = new JChat( "" );
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
                jchat.then( split[0] );
                if ( split.length == 2 )
                {
                    remainder = split[1];
                }
                String param = bbcode.replaceFirst( regex, "$2" ).replaceAll( "\\|\\|\\|\\|", "\n" );
                String text = bbcode.replaceFirst( regex, "$3" ).replaceAll( "\\|\\|\\|\\|", "\n" );
                jchat.then( text );
                if ( bbcode.toLowerCase().contains( "[click-command=" ) )
                {
                    jchat.command( param );
                }
                else if ( bbcode.toLowerCase().contains( "[click-url=" ) )
                {
                    jchat.link( param );
                }
                else if ( bbcode.toLowerCase().contains( "[hover-text=" ) )
                {
                    jchat.tooltip( param );
                }
                else if ( bbcode.toLowerCase().contains( "[hover-command=" ) )
                {
                    String[] params = param.split( "====" );
                    if ( params.length == 2 )
                    {
                        jchat.tooltip( params[0] ).command( params[1] );
                    }
                }
                else if ( bbcode.toLowerCase().contains( "[hover-url=" ) )
                {
                    String[] params = param.split( "====" );
                    if ( params.length == 2 )
                    {
                        jchat.tooltip( params[0] ).link( params[1] );
                    }
                }
            }
            jchat.then( remainder );
        }
        else
        {
            jchat = jchat.then( message.replaceAll( "\\|\\|\\|\\|", "\n" ) );
        }
        return jchat;
    }

    public static String getMessageJson( String key, String... values )
    {
        return jsonReplacer( msg( false, key, values ) ).toJSONString();
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
        JChat jchat = jsonReplacer( msg( true, key, values ) );
        for ( Player player : players )
        {
            try
            {
                jchat.send( player );
            }
            catch ( Exception ignored )
            {
            }
        }
    }

    public static void sendMessage( boolean hasPrefix, Player[] players, String key, String... values )
    {
        JChat jchat = jsonReplacer( msg( hasPrefix, key, values ) );
        for ( Player player : players )
        {
            try
            {
                jchat.send( player );
            }
            catch ( Exception ignored )
            {
            }
        }
    }

    public static void setMessages( Map<String, String> messages )
    {
        Messages.messages = messages;
    }
}
