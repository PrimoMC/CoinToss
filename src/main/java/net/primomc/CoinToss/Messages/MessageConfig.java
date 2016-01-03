package net.primomc.CoinToss.Messages;

import net.primomc.CoinToss.CommonsConfig;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

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
public class MessageConfig extends CommonsConfig
{
    protected MessageConfig( Plugin plugin )
    {
        super( plugin, "messages.yml" );
    }

    public Map<String, String> loadMessages()
    {
        Map<String, String> messages = new HashMap<>();
        for ( String key : config.getKeys( false ) )
        {
            String message = config.getString( key );
            messages.put( key, message );
        }
        return messages;
    }
}
