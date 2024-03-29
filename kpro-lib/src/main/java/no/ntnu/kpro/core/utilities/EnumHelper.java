/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.ntnu.kpro.core.utilities;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aleksandersjafjell
 */
public class EnumHelper {

    public static <E extends Enum<?>> ArrayAdapter getEnumAdapterByEnumInstance(E someEnum, Context context, int textViewResourceId) {
        return getEnumAdapterByEnumClass(someEnum.getClass(), context, textViewResourceId);

    }

    public static <E extends Enum<?>> ArrayAdapter getEnumAdapterByEnumClass(Class<E> c, Context context, int textViewResourceId) {
        List<String> enumList = new ArrayList<String>();

        for (E enumValue : c.getEnumConstants()) {
            enumList.add(enumValue.toString());
        }
        return new ArrayAdapter(context, textViewResourceId, enumList);
    }

    public static <E extends Enum<?>> void populateSpinnerWithEnumValues(Spinner spinner, Context context, Class<E> someEnum) {
        ArrayAdapter adapter = EnumHelper.getEnumAdapterByEnumClass(someEnum, context, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    /**
     *
     * @param enumClass The class of the enum we want to find a specific value
     * within.
     * @param enumString[] The strings value of the enum we want to find data
     * of.
     * @return The enums value
     */
    public static <E extends Enum<?>> E[] getEnumValue(Class<E> enumClass, String[] enumString) {
        E[] el = (E[]) Array.newInstance(enumClass, (enumString == null) ? 1 : enumString.length);
        if (enumString == null) {
            el[0] = enumClass.getEnumConstants()[0];
            return el;
        }
        for (int i = 0; i < enumString.length; i++) {
            el[i] = getEnumValue(enumClass, enumString[i]);
        }
        return el;
    }

    /**
     *
     * @param enumClass The class of the enum we want to find a specific value
     * within.
     * @param enumString The string value of the enum we want to find data of.
     * @return The enum value
     */
    public static <E extends Enum<?>> E getEnumValue(Class<E> enumClass, String enumString) {
        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.toString().equalsIgnoreCase(enumString)) {
                return enumValue;
            }
        }
        throw new EnumConstantNotPresentException(enumClass, enumString);
    }
}
