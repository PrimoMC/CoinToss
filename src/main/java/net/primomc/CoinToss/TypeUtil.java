package net.primomc.CoinToss;

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
public class TypeUtil
{
    public static boolean isInt( final String sInt )
    {
        try
        {
            Integer.parseInt( sInt );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
        return true;
    }

    public static int getInt( final String sInt )
    {
        try
        {
            return Integer.parseInt( sInt );
        }
        catch ( NumberFormatException e )
        {
            return 0;
        }
    }

    public static boolean isDouble( final String sDouble )
    {
        try
        {
            Double.parseDouble( sDouble );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
        return true;
    }

    public static double getDouble( final String sDouble )
    {
        try
        {
            return Double.parseDouble( sDouble );
        }
        catch ( NumberFormatException e )
        {
            return 0;
        }
    }

    public static boolean isBoolean( String sBoolean )
    {
        return sBoolean.equalsIgnoreCase( "true" ) || sBoolean.equalsIgnoreCase( "false" ) || sBoolean.equalsIgnoreCase( "yes" ) || sBoolean.equalsIgnoreCase( "no" ) || sBoolean.equalsIgnoreCase( "1" ) || sBoolean.equalsIgnoreCase( "0" );
    }

    public static boolean getBoolean( String sBoolean )
    {
        sBoolean = sBoolean.toLowerCase();
        sBoolean = sBoolean.replaceAll( "(yes|1)", "true" );
        sBoolean = sBoolean.replaceAll( "(no|0)", "false" );
        return Boolean.parseBoolean( sBoolean );
    }
}
