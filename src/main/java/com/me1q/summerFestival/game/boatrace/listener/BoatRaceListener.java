package com.me1q.summerFestival.game.boatrace.listener;

import com.me1q.summerFestival.game.boatrace.goal.GoalLineDetector;
import com.me1q.summerFestival.game.boatrace.session.BoatRaceSession;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

public class BoatRaceListener implements Listener {

    private final BoatRaceSession raceSession;
    private final GoalLineDetector goalLineDetector;

    public BoatRaceListener(BoatRaceSession raceSession) {
        this.raceSession = raceSession;
        this.goalLineDetector = new GoalLineDetector();
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (!(event.getVehicle() instanceof Boat boat)) {
            return;
        }

        if (boat.getPassengers().isEmpty()) {
            return;
        }

        Entity passenger = boat.getPassengers().getFirst();
        if (!(passenger instanceof Player player)) {
            return;
        }

        if (!raceSession.isActive() || !raceSession.isRaceStarted()) {
            return;
        }

        if (!raceSession.isParticipant(player) || raceSession.hasFinished(player)) {
            return;
        }

        Location fromLocation = event.getFrom();
        Location toLocation = event.getTo();

        if (goalLineDetector.hasPassedGoalLine(fromLocation, toLocation,
            raceSession.getGoalLine())) {
            raceSession.recordFinish(player);
        }
    }
}
