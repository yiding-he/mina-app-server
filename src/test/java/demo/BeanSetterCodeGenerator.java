package demo;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

/**
 * (description)
 *
 * @author yiding.he
 */
public class BeanSetterCodeGenerator {
    
    public static void main(String[] args) throws Exception {
    }
    
    /////////////////////////////////////////

    private static String generate(Class type, String varName) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        String result = "";

        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            if (!checkDescriptor(descriptor)) {
                continue;
            }

            result += varName + "." + generateSetter(descriptor) + "\n";
        }

        return result;
    }

    private static boolean checkDescriptor(PropertyDescriptor descriptor) {
        return !descriptor.getName().equals("class");
    }

    private static String generateSetter(PropertyDescriptor descriptor) {
        return descriptor.getWriteMethod().getName() + "();";
    }
}
