package com.mycompany.a2.commands;

import com.codename1.ui.Command;
import com.codename1.ui.events.ActionEvent;
import com.mycompany.a2.GameWorld;

public class MoveToAstronautCommand extends Command {
    GameWorld gw;
    
    public MoveToAstronautCommand(String command, GameWorld gw) {
        super(command);
        this.gw = gw;
    }
    
    public void actionPerformed(ActionEvent e) {
        System.out.println("Move to Astronaut");
        gw.moveSpaceshipToAstronaut();
    }
}