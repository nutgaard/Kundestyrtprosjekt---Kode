/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.helpers;

import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aleksandersjafjell
 */
public class EnumHelper {

    public static <E extends Enum<?>> ArrayAdapter getEnumAdapterByEnumInstance(E e, Context context, int textViewResourceId ){        
        return getEnumAdapterByEnumClass(e.getClass(), context, textViewResourceId);        
        
    }
    
    public static <E extends Enum<?>> ArrayAdapter getEnumAdapterByEnumClass(Class<E> c, Context context, int textViewResourceId ){
        List<String> enumList = new ArrayList<String>();
        
        for (E enumValue : c.getEnumConstants()) {
            enumList.add(enumValue.toString());
        }               
        return new ArrayAdapter(context, textViewResourceId, enumList);        
    }    
   
}
