package com.hello2morrow.sonargraph.jenkinsplugin.foundation;

public class StringUtility
{
    public static String convertConstantNameToMixedCaseString(String input, boolean capitalizeFirstLetter, boolean insertSpace)
    {
        assert input != null : "'input' must not be null";
        assert input.length() > 0 : "'input' must not be empty";

        StringBuilder builder = new StringBuilder();
        boolean previousWasUnderscore = capitalizeFirstLetter;
        boolean currentIsUnderscore;

        for (int i = 0; i < input.length(); i++)
        {
            char nextChar = input.charAt(i);
            currentIsUnderscore = nextChar == '_';
            if (!currentIsUnderscore)
            {
                if (previousWasUnderscore)
                {
                    if (insertSpace && builder.length() > 0)
                    {
                        builder.append(' ');
                    }
                    builder.append(Character.toUpperCase(nextChar));
                }
                else
                {
                    builder.append(Character.toLowerCase(nextChar));
                }
            }
            previousWasUnderscore = currentIsUnderscore;
        }
        return builder.toString();
    }

    public static String convertMixedCaseStringToConstantName(String input)
    {
        assert input != null : "'input' must not be null";
        assert input.length() > 0 : "'input' must not be empty";

        StringBuilder builder = new StringBuilder();

        char previousChar = input.charAt(0);
        builder.append(Character.toUpperCase(previousChar));

        for (int i = 1; i < input.length(); i++)
        {
            char nextChar = input.charAt(i);
            if (Character.isUpperCase(nextChar) || (Character.isDigit(nextChar) && !Character.isDigit(previousChar)))
            {
                builder.append('_');
            }
            builder.append(Character.toUpperCase(nextChar));
            previousChar = nextChar;
        }
        return builder.toString();
    }

    public static boolean validateNotNullAndRegexp(String value, String pattern)
    {
        if (value == null)
        {
            return false;
        }

        if (!value.matches(pattern))
        {
            return false;
        }

        return true;
    }

}
