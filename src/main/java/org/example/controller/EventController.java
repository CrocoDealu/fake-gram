package org.example.controller;


import org.example.utils.Event;

import java.util.HashMap;
import java.util.Map;

public class EventController {
    private final Map<String, MainController> mainControllers = new HashMap<>();

    public EventController() {

    }

    public void addController(MainController mainController) {
        this.mainControllers.put(mainController.getUsername(), mainController);
    }

    public void removeController(MainController mainController) {
        this.mainControllers.remove(mainController.getUsername());
    }

    public void handleEvent (Event event) {
        String parm1 = event.getParameters().getFirst();
        if (mainControllers.containsKey(parm1)) {
            mainControllers.get(parm1).handleEvent(event);
        }
    }

    public void closeAll() {
        for (MainController mainController : mainControllers.values()) {
            mainController.closeScene();
        }
    }
}
