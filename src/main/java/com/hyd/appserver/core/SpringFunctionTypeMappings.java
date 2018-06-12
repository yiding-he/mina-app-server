package com.hyd.appserver.core;

import com.hyd.appserver.Action;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpringFunctionTypeMappings implements FunctionTypeMappings<Action> {

    public SpringFunctionTypeMappings(ApplicationContext applicationContext) {

        if (applicationContext == null) {
            return;
        }

        ArrayList<Action> actions = new ArrayList<>(applicationContext.getBeansOfType(Action.class).values());
        for (Action action : actions) {
            mappings.put(action.getFullFunctionPath(), action.getClass());
        }
    }

    private Map<String, Class<? extends Action>> mappings = new HashMap<>();

    @Override
    public Class<? extends Action> find(String functionPath) {
        return mappings.get(functionPath);
    }
}
