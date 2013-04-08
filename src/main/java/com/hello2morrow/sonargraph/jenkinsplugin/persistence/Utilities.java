package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import java.util.List;

import com.hello2morrow.sonargraph.jenkinsplugin.xsd.XsdAttribute;
import com.hello2morrow.sonargraph.jenkinsplugin.xsd.XsdAttributeCategory;

/**
 * Utilities to process the xml sonargraph report.
 * @author esteban
 *
 */
public class Utilities
{
    
    /** Default build unit name. */
    public static final String DEFAULT_BUILD_UNIT = "(Default Build Unit)";
    
//    /** Unknown build unit name. */
//    private static final String UNKNOWN = "<UNKNOWN>";
    
    /**
     * Returns the value of an attribute given a name.
     * @param list XsdAttribute list.
     * @param name Name of the attribute to look for in the list.
     * @return Value of the attribute for the given name.
     */
    public static String getAttribute(List<XsdAttribute> list, String name)
    {
        String value = null;

        for (XsdAttribute attr : list)
        {
            if (attr.getStandardName().equals(name))
            {
                value = attr.getValue();
                break;
            }
        }
        return value;
    }
    
    /**
     * @return the category from the list that has the matching name
     */
    public static XsdAttributeCategory getAttributeCategory(List<XsdAttributeCategory> categories, String name)
    {
    	for (XsdAttributeCategory category : categories)
    	{
    		if (category.getName().equals(name))
    		{
    			return category;
    		}
    	}
    	return null;
    }
}
