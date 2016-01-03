package net.primomc.CoinToss;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;

import static org.bukkit.configuration.file.YamlConfiguration.loadConfiguration;

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
public class CommonsConfig
{
    protected final File file;
    protected Plugin plugin;
    protected String FILENAME;
    protected File datafolder;
    protected FileConfiguration config;

    public CommonsConfig( Plugin plugin, String filename )
    {
        this.FILENAME = filename;
        this.plugin = plugin;
        this.datafolder = plugin.getDataFolder();
        file = new File( datafolder, FILENAME );
        InputStream inputStream = plugin.getResource( FILENAME );
        if ( inputStream != null && !file.exists() )
        {
            OutputStream outputStream = null;

            try
            {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
                outputStream = new FileOutputStream( file );

                int read;
                byte[] bytes = new byte[1024];

                while ( ( read = inputStream.read( bytes ) ) != -1 )
                {
                    outputStream.write( bytes, 0, read );
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    inputStream.close();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
                if ( outputStream != null )
                {
                    try
                    {
                        outputStream.close();
                    }
                    catch ( IOException e )
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        loadConfig();
    }

    private void createFile()
    {
        try
        {
            file.createNewFile();
        }
        catch ( Exception ex )
        {
            plugin.getLogger().warning( ChatColor.DARK_RED + "Failed to generate the file: " + FILENAME );
        }
    }

    public void loadConfig()
    {
        if ( !file.exists() )
        {
            createFile();
        }
        config = loadConfiguration( file );
    }

    public FileConfiguration getConfig()
    {
        return config;
    }


    public void saveConfig()
    {
        try
        {
            config.save( file );
        }
        catch ( IOException ex )
        {
            plugin.getLogger().warning( ChatColor.DARK_RED + "Could not save the file: " + FILENAME );
        }
    }

    protected void createBackup()
    {
        File file = new File( datafolder, FILENAME + ".bak" );
        try
        {
            FileUtils.copyFile( this.file, file );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
}
